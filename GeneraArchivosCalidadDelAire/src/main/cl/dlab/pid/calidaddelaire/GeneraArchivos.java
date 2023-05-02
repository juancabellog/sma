package cl.dlab.pid.calidaddelaire;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class GeneraArchivos extends GeneraBase
{
	private static Logger logger = LoggerFactory.getLogger(GeneraArchivos.class);
		
	public FileVO getFile(DataBase db, Integer ufId, String formato) throws Exception
	{
		return getFile(db, ufId, null, formato);
	}
	public FileVO getFile(DataBase db, Integer ufId, Integer dispositivoId, String formato) throws Exception
	{
		String pathArchivos = PropertyUtil.getProperty(db.getKeyPathArchivos());
		boolean isParquet = formato.equals("parquet");
		String type = isParquet ? ".parquet": "";
		String fileNameBase;
		
		if (dispositivoId == null)
		{
			fileNameBase =  db.getNameUfId() + ufId  + type + ".zip";
		}
		else
		{
			fileNameBase = db.getNameUfIdDispositivoId() + ufId  + "_" + dispositivoId + type + (isParquet ? "" : ".zip");
		}
		
		String fileName = pathArchivos  + (dispositivoId == null ? "" : ufId + "/")+ fileNameBase;
		logger.info("Buscando archivo:" + fileName);
		File file = new File(fileName);
		if (!file.exists())
		{
			if (isParquet)
			{
				return null;
			}
			process(new String[] {ufId.toString()}, pathArchivos, db, true);
		}
		file = new File(fileName);
		if (!file.exists())
		{
			return null;
		}
		try(FileInputStream fi = new FileInputStream(file))
		{
			byte[] bytes = new byte[fi.available()];
			fi.read(bytes);
			return new FileVO(fileNameBase, bytes);
		}
	}
	protected Bson getFilter(Integer ufId)
	{
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("UfId", ufId);
		return searchQuery;
	}
	public void process(String[] ufids, DataBase db) throws Exception
	{
		process(ufids, PropertyUtil.getProperty(db.getKeyPathArchivos()), db, true);
	}
	@SuppressWarnings("unchecked")
	public void process(String[] ufids, String pathArchivos, DataBase db, boolean writeSubfiles) throws Exception
	{
		logger.info("***************************************************************************");
		logger.info("Entra a Generar Archivos: " + db + ", pathSalida:" + pathArchivos);
		logger.info("Tipos de archivos a generar: " + db.getNameUfId() + ", " + db.getNameUfIdDispositivoId());
		logger.info("Id's a procesar: " + Arrays.toString(ufids));
		logger.info("Usando ByteArrayWriter ");
		logger.info("***************************************************************************");
		MongoClientURI connectionString = new MongoClientURI(PropertyUtil.getProperty("dlab.pid.mongodb.uri"));
		MongoClient mongoClient = new MongoClient(connectionString);
		try
		{
			MongoDatabase database = mongoClient.getDatabase(db.getDbName());
			MongoCollection<Document> collection = database.getCollection(db.getCollectionName());
	
			String header = PropertyUtil.getProperty("dlab.pid.header");
			
			String[] caApiRestFields = PropertyUtil.getProperty("dlab.pid.caapirest.fields").split(",");
			String[] dispositivoFields = PropertyUtil.getProperty("dlab.pid.dispositivo.fields").split(",");
			String[] parametrosFields = PropertyUtil.getProperty("dlab.pid.parametro.fields").split(",");
			
			
			
			for (String id : ufids)
			{
				long t = System.currentTimeMillis();
				Integer ufId = Integer.parseInt(id.trim());
				

				logger.info("Consultando:" + ufId);
				
				HashMap<String, BytesWriter> writers = new HashMap<String, BytesWriter>();

				FindIterable<Document> find = collection.find(getFilter(ufId));
				MongoCursor<Document> cursor = find.iterator();
				long numRows = 0;
				try
				{
					while (cursor.hasNext())
					{
						++numRows;
						Document doc = cursor.next();
						ArrayList<Document> data = (ArrayList<Document>) doc.get("data");
						
						for (Document dispositivo : data)
						{
							ufId = doc.getInteger("UfId");
							Integer dispositivoId = dispositivo.getInteger("dispositivoId");
							String key = ufId + "_" + dispositivoId;
							BytesWriter wr = writers.get(key);
							if (wr == null)
							{
								writers.put(key, wr = new ByteArrayWriter(db, ufId, dispositivoId));
								wr.write(header).newLine();
							}
							ArrayList<Document> parametros = (ArrayList<Document>)dispositivo.get("Parametros");
							for (Document parametro : parametros)
							{
								String sep = "";
								for (String field : caApiRestFields)
								{
									String value = getValue(doc.get(field));
									wr.write(sep).write(value);
									sep = "|";
								}
								for (String field : dispositivoFields)
								{
									String value = getValue(dispositivo.get(field));
									wr.write(sep).write(value);
								}
								for (String field : parametrosFields)
								{
									String value = getValue(parametro.get(field));
									wr.write(sep).write(value);
								}
								wr.newLine();
								
							}
							
						}
					}
				}
				finally
				{
					cursor.close();
				}
				logger.info("Termina de leer:" + ufId + " en:" + (System.currentTimeMillis() - t) + " ms, numrows:" + numRows);
				
				if (numRows > 0)
				{
				
					String fileName = pathArchivos + db.getNameUfId() + ufId  + ".zip";
					logger.info("Escribiendo:" + fileName);
	
					ZipOutputStream zo = new ZipOutputStream(new FileOutputStream(fileName));
	
					ArrayList<String> keys = new ArrayList<>(writers.keySet());
					for (String key : keys)
					{
						BytesWriter wr = writers.remove(key);
						wr.close();
						
						zo.putNextEntry(new ZipEntry(wr.getFileName() + ".csv"));
						wr.write(zo);	
						zo.closeEntry();
						
						if (writeSubfiles)
						{
							File dir = new File(pathArchivos + wr.getUfId());
							if (!dir.exists())
							{
								dir.mkdirs();
							}
							fileName = pathArchivos + wr.getUfId() + "/" + wr.getFileName() + ".zip";
							logger.info("Escribiendo:" + fileName);
							wr.writeZipFile(new FileOutputStream(fileName));
						}
						wr.clear();
					}
					zo.close();
					writers = null;
					System.gc();
				}
			}
		}
		finally
		{
			mongoClient.close();
		}
		logger.info("***************************************************************************");
		logger.info("Termina de generar archivos");
		logger.info("***************************************************************************");

	}	
	
	public static void main(String[] args) throws Exception
	{
		
		PropertyUtil.load("calidaddelaire.properties");
		new GeneraArchivos().process(PropertyUtil.getProperty("dlab.pid.listaufids").split(","), DataBase.ApiRest);
	}
	
}
