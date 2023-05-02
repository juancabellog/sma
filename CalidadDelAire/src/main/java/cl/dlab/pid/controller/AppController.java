package cl.dlab.pid.controller;

import java.net.InetAddress;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cl.dlab.pid.calidaddelaire.ConsultaPorFecha;
import cl.dlab.pid.calidaddelaire.ConsultasSMA;
import cl.dlab.pid.calidaddelaire.DataBase;
import cl.dlab.pid.calidaddelaire.FileVO;
import cl.dlab.pid.calidaddelaire.GeneraArchivos;
import cl.dlab.pid.calidaddelaire.GeneraIds;
import cl.dlab.pid.calidaddelaire.ImportaDatosSMA;
import cl.dlab.pid.calidaddelaire.PropertyUtil;


@CrossOrigin(origins="*")
//@CrossOrigin(origins="http://localhost:4200")
@RestController
public class AppController implements ApplicationContextAware {
	
	private static Logger logger = LoggerFactory.getLogger(AppController.class);
	

	@Value("${dlab.pid.path.file.properties}")
	private String pathFileProperties;
	
	
	private ApplicationContext context;

	@Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;
        try
        {
        	PropertyUtil.load(pathFileProperties);
        }
        catch(Exception e)
        {
        	logger.error("Error al leer archivo de propiedades:" + pathFileProperties, e);
        }
        try
        {
        	ImportaDatosSMA.getInstance().programmingTask();
        }
        catch(Exception e)
        {
        	logger.error("Error al inicializar ImportDatosSMA", e);
        }
    }
	
	@GetMapping("/shutdownContext")
	public void shutdownContext() {
		try {
			ImportaDatosSMA.getInstance().shutdownTask();
		} catch (SchedulerException e) {			
			logger.error("Error al detener cronjob", e);
		}
        ((ConfigurableApplicationContext) context).close();
    }
	@GetMapping("/importDataSMA")
	public void importDataSMA() throws Exception {
        ImportaDatosSMA.getInstance().importData();
    }
	
	@SuppressWarnings("rawtypes")
	@GetMapping("/getIdsUnidadFiscalizableDispositivo")
	public @ResponseBody ResponseEntity getIdsUnidadFiscalizableDispositivo() throws Exception
	{
		long t = System.currentTimeMillis();
		try
		{
			return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"IdsUnidadFiscalizableDispositivo.csv\"")
				.body(GeneraIds.getIds());
			
		}
		catch(Exception e)
		{
			logger.error("Error al consultar getCalidadDelAirePorUfId", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		finally
		{
			logger.info("getIdsUnidadFiscalizableDispositivo en generar archivo:" + (System.currentTimeMillis() - t) + " ms");
		}
	}	
	@SuppressWarnings("rawtypes")
	@GetMapping("/getCalidadDelAirePorUfId/{ufId}/{formato}")
	public @ResponseBody ResponseEntity getCalidadDelAirePorUfId(@PathVariable Integer ufId, @PathVariable String formato) throws Exception
	{
		return getCalidadDelAirePorUfId(ufId, formato, "A");
	}
	@SuppressWarnings("rawtypes")
	@GetMapping("/getCalidadDelAirePorUfId/{ufId}/{formato}/{db}")
	public @ResponseBody ResponseEntity getCalidadDelAirePorUfId(@PathVariable Integer ufId, @PathVariable String formato, @PathVariable String db) throws Exception
	{
		long t = System.currentTimeMillis();
		try
		{
			DataBase database = db.toUpperCase().charAt(0) == 'H' ? DataBase.Historica : db.toUpperCase().charAt(0) == 'S' ? DataBase.ApiRest_SMA : DataBase.ApiRest;
			formato = formato == null ? "csv" : formato.toLowerCase();
			logger.info("getCalidadDelAirePorUfId:" + ufId + ", tipo archivo:" + formato + ", db:" + database);
			if (!formato.equals("csv") && !formato.equals("parquet"))
			{
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Formato inválido " + formato + ", solo son permitidos csv y parquet");
			}
			FileVO file = new GeneraArchivos().getFile(database, ufId, formato);
			if (file == null)
			{
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unidad Fiscalizable no encontrada para " + ufId);
			}
			//return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("")).build();
			return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
				.body(file.getBytes());
			
		}
		catch(Exception e)
		{
			logger.error("Error al consultar getCalidadDelAirePorUfId", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		finally
		{
			logger.info("getCalidadDelAirePorUfId en generar archivo:" + (System.currentTimeMillis() - t) + " ms");
		}
	}	

	@SuppressWarnings("rawtypes")
	@GetMapping("/getCalidadDelAirePorUfIdDispositivoId/{ufId}/{dispositivoId}/{formato}")
	public @ResponseBody ResponseEntity getFileCalidadDelAirePorUfIdDispositivoId(@PathVariable Integer ufId, @PathVariable Integer dispositivoId, @PathVariable String formato) throws Exception
	{
		return getFileCalidadDelAirePorUfIdDispositivoId(ufId, dispositivoId, formato, "A");
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping("/getCalidadDelAirePorUfIdDispositivoId/{ufId}/{dispositivoId}/{formato}/{db}")
	public @ResponseBody ResponseEntity getFileCalidadDelAirePorUfIdDispositivoId(@PathVariable Integer ufId, @PathVariable Integer dispositivoId, @PathVariable String formato, @PathVariable String db) throws Exception
	{
		long t = System.currentTimeMillis();
		try
		{
			DataBase database = db.toUpperCase().charAt(0) == 'H' ? DataBase.Historica : DataBase.ApiRest;
			formato = formato == null ? "csv" : formato.toLowerCase();
			logger.info("getFileCalidadDelAirePorUfIdDispositivoId:" + ufId + ",dispositivoId:" + dispositivoId + ", type:" + formato + ", db:" + database);
			if (!formato.equals("csv") && !formato.equals("parquet"))
			{
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Formato inválido " + formato + ", solo son permitidos csv y parquet");
			}
			logger.info("getFileCalidadDelAirePorUfIdDispositivoId:" + InetAddress.getLocalHost().getHostAddress() + ", " + ufId, ", " + dispositivoId);
			FileVO file = new GeneraArchivos().getFile(database, ufId, dispositivoId, formato);
			if (file == null)
			{
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("DispositivoId " + dispositivoId + " no encontrado para UfId " + ufId);
			}
			return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
				.body(file.getBytes());
			
		}
		catch(Exception e)
		{
			logger.error("Error al consultar getFileCalidadDelAirePorUfIdDispositivoId", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		finally
		{
			logger.info("getFileCalidadDelAirePorUfIdDispositivoId en generar archivo:" + (System.currentTimeMillis() - t) + " ms");
		}
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping("/getCalidadDelAirePorUfIdFechaInicioFechaTermino/{ufId}/{fechaInicio}/{fechaTermino}")
	public @ResponseBody ResponseEntity getCalidadDelAirePorUfIdFechaInicioFechaTermino(@PathVariable Integer ufId, @PathVariable String fechaInicio, @PathVariable String fechaTermino) throws Exception
	{
		return getCalidadDelAirePorUfIdFechaInicioFechaTermino(ufId, fechaInicio, fechaTermino, "A");
	}
	@SuppressWarnings("rawtypes")
	@GetMapping("/getCalidadDelAirePorUfIdFechaInicioFechaTermino/{ufId}/{fechaInicio}/{fechaTermino}/{db}")
	public @ResponseBody ResponseEntity getCalidadDelAirePorUfIdFechaInicioFechaTermino(@PathVariable Integer ufId, @PathVariable String fechaInicio, @PathVariable String fechaTermino, @PathVariable String db) throws Exception
	{
		long t = System.currentTimeMillis();
		try
		{
			DataBase database = db.toUpperCase().charAt(0) == 'H' ? DataBase.Historica : DataBase.ApiRest;
			logger.info("getCalidadDelAirePorUfIdFechaInicioFechaTermino:" + ufId + ", fechaInicio:" + fechaInicio + ", fechaTermino:" + fechaTermino + ", db:" + database);
			FileVO file = new ConsultaPorFecha().getFile(database, ufId, fechaInicio, fechaTermino, "csv");
			if (file == null)
			{
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No existen registros para Unidad Fiscalizable:" + ufId + " entre:" + fechaInicio + " y " + fechaTermino);
			}
			return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
				.body(file.getBytes());
			
		}
		catch(Exception e)
		{
			logger.error("Error al consultar getCalidadDelAirePorUfIdFechaInicioFechaTermino", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		finally
		{
			logger.info("getCalidadDelAirePorUfIdFechaInicioFechaTermino en generar archivo:" + (System.currentTimeMillis() - t) + " ms");
		}
	}
	
	@SuppressWarnings("rawtypes")
	@GetMapping("/getUnidadesFiscalizablesListSMA")
	public @ResponseBody ResponseEntity getUnidadesFiscalizablesListSMA() throws Exception
	{
		long t = System.currentTimeMillis();
		try
		{
			logger.info("getUnidadesFiscalizablesListSMA");
			FileVO file = new ConsultasSMA().getUnidadFiscalizableList();
			return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
				.body(file.getBytes());
			
		}
		catch(Exception e)
		{
			logger.error("Error al consultar getUnidadesFiscalizablesListSMA", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		finally
		{
			logger.info("getUnidadesFiscalizablesListSMA en generar archivo:" + (System.currentTimeMillis() - t) + " ms");
		}
	}	
	@SuppressWarnings("rawtypes")
	@GetMapping("/getUnidadFiscalizableUfIdSMA/{ufId}")
	public @ResponseBody ResponseEntity getUnidadFiscalizableUfIdSMA(@PathVariable Integer ufId) throws Exception
	{
		long t = System.currentTimeMillis();
		try
		{
			logger.info("getUnidadFiscalizableUfIdSMA:" + ufId);
			FileVO file = new ConsultasSMA().getUnidadFiscalizableUfId(ufId);
			return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
				.body(file.getBytes());
			
		}
		catch(Exception e)
		{
			logger.error("Error al consultar getUnidadFiscalizableUfIdSMA", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		finally
		{
			logger.info("getUnidadFiscalizableUfIdSMA en generar archivo:" + (System.currentTimeMillis() - t) + " ms");
		}
	}	
	@SuppressWarnings("rawtypes")
	@GetMapping("/getUnidadFiscalizableUfIdProcesoIdFechaSMA/{ufId}/{procesoId}/{fecha}")
	public @ResponseBody ResponseEntity getUnidadFiscalizableUfIdProcesoIdFechaSMA(@PathVariable Integer ufId, @PathVariable Integer procesoId, @PathVariable String fecha) throws Exception
	{
		long t = System.currentTimeMillis();
		try
		{
			logger.info("getUnidadFiscalizableUfIdProcesoIdFechaSMA:" + ufId + "/" + procesoId + "/" + fecha);
			FileVO file = new ConsultasSMA().getUnidadFiscalizableUfIdProcesoIdFecha(ufId, procesoId, fecha);
			return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
				.body(file.getBytes());
			
		}
		catch(Exception e)
		{
			logger.error("Error al consultar getUnidadFiscalizableUfIdProcesoIdFechaSMA", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		finally
		{
			logger.info("getUnidadFiscalizableUfIdProcesoIdFechaSMA en generar archivo:" + (System.currentTimeMillis() - t) + " ms");
		}
	}	
	
	
}
