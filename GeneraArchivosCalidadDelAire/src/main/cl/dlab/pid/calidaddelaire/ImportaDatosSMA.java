package cl.dlab.pid.calidaddelaire;

import static com.mongodb.client.model.Aggregates.group;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.bson.Document;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;

public class ImportaDatosSMA {

	private static Logger logger = LoggerFactory.getLogger(ImportaDatosSMA.class);
	private static final SimpleDateFormat FMT = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat FMT2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static final String FECHA_MINIMA = "2022-04-11T05:00:00";
	
	
	private static ImportaDatosSMA instance;
	
	public static ImportaDatosSMA getInstance()
	{
		if (instance == null)
		{
			instance = new ImportaDatosSMA();
		}
		return instance;
	}
	
	private Scheduler scheduler;
	private ImportaDatosSMA() {}
	
	//@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception
	{
		PropertyUtil.load("calidaddelaire.properties");
		
		//new ImportaDatosSMA().importData();
		System.out.println(new ImportaDatosSMA().getUfidsSMA());
		/*String s = new ConsultasSMA().getUrlGetMethod("http://apirestsma.eastus.cloudapp.azure.com/api/v1/unidadfiscalizable/529/proceso/305/fecha/2022-07-09", 1);
		Document doc = Document.parse("{data:" + s + "}");
		ArrayList<Document> list = (ArrayList<Document>)doc.get("data");
		System.out.println(list.size() + "::" + s);*/
		
		
	}
		
	public void shutdownTask() throws SchedulerException
	{
		scheduler.shutdown();
	}
	@SuppressWarnings("unchecked")
	private HashMap<Integer, ArrayList<Integer>> getUfidsSMA() throws Exception
	{
		HashMap<Integer, ArrayList<Integer>> result = new HashMap<Integer, ArrayList<Integer>>();
		String s = new ConsultasSMA().getUrlGetMethod("http://apirestsma.eastus.cloudapp.azure.com/api/v1/unidadfiscalizable/list", 0);
		Document doc = Document.parse("{data:" + s + "}");
		ArrayList<Document> list = (ArrayList<Document>)doc.get("data");
		for (Document ufid : list) {
			Integer idUfId = ufid.getInteger("ufId");
			ArrayList<Integer> procesosIds = result.get(idUfId);
			if (procesosIds == null)
			{
				result.put(idUfId, procesosIds = new ArrayList<Integer>());
			}
			ArrayList<Document> procesos = ufid.get("procesos", ArrayList.class);
			for (Document proceso : procesos) {
				procesosIds.add(proceso.getInteger("procesoId"));
			}
		}
		return result;
	}
	public void programmingTask() throws SchedulerException
	{
		String expression = PropertyUtil.getProperty("dlab.pid.cron.expression");
		System.out.println("Programando trigger:" + expression);
		JobDetail job = JobBuilder.newJob(ImportaDatosSMAJob.class).withIdentity("myTask", "group1").build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity("cronTrigger", "group1").withSchedule(CronScheduleBuilder.cronSchedule(expression)).build();  
		scheduler = new StdSchedulerFactory().getScheduler();
		scheduler.start();  
        scheduler.scheduleJob(job, trigger);  
	}
	private AggregateIterable<Document> getUfids(MongoCollection<Document> collection) throws Exception
	{
		return collection.aggregate(Arrays.asList(group(new Document("ufId", "$UfId").append("procesoId", "$ProcesoId"), Accumulators.max("maxFecha", "$EventEnqueuedUtcTime"))));
	}
	public void importData() throws Exception {
		ConsultasSMA sma = new ConsultasSMA();
		MongoClientURI connectionString = new MongoClientURI(PropertyUtil.getProperty("dlab.pid.mongodb.uri"));
		MongoClient mongoClient = new MongoClient(connectionString);
		try
		{
			MongoDatabase database = mongoClient.getDatabase("ExportData");
			MongoCollection<Document> collection = database.getCollection("CA_ApiRest_SMA");
			
			System.out.println("antes de recuperar datos de CA_ApiRest_SMA");
			AggregateIterable<Document> docs = getUfids(collection);
			System.out.println("Recupera datos de CA_ApiRest_SMA");
			MongoCursor<Document> cursor = docs.cursor();
			HashMap<Integer, ArrayList<Integer>> ufIds = getUfidsSMA();
			while(cursor.hasNext())
			{
				Document doc = cursor.next();
				Document id = doc.get("_id", Document.class);
				Integer ufId = id.getInteger("ufId");
				Integer procesoId = id.getInteger("procesoId");
				ArrayList<Integer> procesos = ufIds.get(ufId);
				if (procesos != null)
				{
					procesos.remove(procesoId);
				}
				importData(collection, sma, ufId, procesoId, doc.getDate("maxFecha"));
			}
			System.out.println("recuperando datos para:" + ufIds);
			Date fechaInicial = FMT2.parse(FECHA_MINIMA);
			for (Integer ufId : ufIds.keySet()) {
				ArrayList<Integer> procesos = ufIds.get(ufId);
				for (Integer procesoId : procesos) {
					importData(collection, sma, ufId, procesoId, fechaInicial);
				}
			}
			logger.info("Termina de insertar datos de forma exitosa");
		}
		finally
		{
			mongoClient.close();
		}
		
	}
	@SuppressWarnings("unchecked")
	private static void importData(MongoCollection<Document> collection, ConsultasSMA sma, int ufId, int procId, Date fechaInicial) throws Exception
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 6);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		long now = cal.getTime().getTime();
		cal.setTime(fechaInicial);
		cal.set(Calendar.HOUR_OF_DAY, 6);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		fechaInicial = cal.getTime();
		while(cal.getTime().getTime() < now)
		{
			cal.add(Calendar.DAY_OF_YEAR, 1);
			String fechaConsulta = FMT.format(cal.getTime());
			logger.info("Consulta datos para ufid:" + ufId + ", con procesoId:" + procId + ", para:" + fechaConsulta);
			if (ufId == 1802 || ufId == 2576 || ufId == 2435)
			{
				logger.info("consultando sma SIN respuesta para ufid:" + ufId + ", con procesoId:" + procId + ", para:" + fechaConsulta);
				continue;
			}
			String s = sma.getUrlGetMethod("http://apirestsma.eastus.cloudapp.azure.com/api/v1/unidadfiscalizable/" + ufId + "/proceso/" + procId + "/fecha/"+ fechaConsulta, 1);
			if (s == null)
			{
				logger.info("consultando sma con ERROR para ufid:" + ufId + ", con procesoId:" + procId + ", para:" + fechaConsulta);
				continue;
			}
			Document doc = Document.parse("{data:" + s + "}");
			ArrayList<Document> list = (ArrayList<Document>)doc.get("data");
			logger.info("Consulta exitosa sma con " + list.size() + " registros a insertar");
			Date stampa = null;
			for (Document item : list) {
				ArrayList<Document> data = (ArrayList<Document>)item.get("data");
				for (Document _doc : data) {
					ArrayList<Document> parametros = (ArrayList<Document>)_doc.get("Parametros");
					for (Document parametro : parametros) {
						String fecha = (String)parametro.getString("estampaTiempo");
						stampa = FMT2.parse(fecha);
						parametro.put("estampaTiempo", stampa);
					}
				}
				String dev = "ApiRest_U1674P34" + ufId + "P" + procId;
				item.put("timestamp", stampa);
				item.put("timestampUTC", stampa);
				item.put("EventProcessedUtcTime", stampa);
				item.append("EventEnqueuedUtcTime", stampa);
				item.append("Sistema", "ApiRest");
				item.append("dev", dev);
				item.append("DeviceId", dev);
			}
			if (list.size() > 0)
			{
				logger.info("Insertando ufId:" + ufId + ", procId:" + procId + ", fecha:" + fechaConsulta);
				collection.insertMany(list);
			}
		}
		
		
	}
	
}
