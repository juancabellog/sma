package cl.dlab.pid.controller;

import java.io.IOException;
import java.sql.PreparedStatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cl.dlab.pid.util.GeneraArchivosFromList;
import cl.dlab.pid.util.PropertyUtil;



@CrossOrigin(origins="*")
//@CrossOrigin(origins="http://localhost:4200")
@RestController
public class AppController implements ApplicationContextAware {
	
	private static Logger logger = LoggerFactory.getLogger(AppController.class);
	
	@Autowired
    private JdbcTemplate jdbcTemplate;	

	@Value("${dlab.pid.path.file.properties}")
	private String pathFileProperties;
	
	
	private ApplicationContext context;

	@Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;
        try
        {
        	PropertyUtil.load(pathFileProperties);
        	//System.out.println(jdbcTemplate.queryForList("select * from Regiones"));
        }
        catch(IOException e)
        {
        	logger.error("Error al leer archivo de propiedades:" + pathFileProperties, e);
        }
    }
	
	@GetMapping("/shutdownContext")
	public void shutdownContext() {
        ((ConfigurableApplicationContext) context).close();
    }
	
	@SuppressWarnings("rawtypes")
	@GetMapping("/getCsvCalidadAguaSuperficialYSubterranea")
	public @ResponseBody ResponseEntity getCsvCalidadAguaSuperficialYSubterranea() throws Exception
	{
		return getCsv("CalidadAguaSuperficialYSubterranea");
	}
	@SuppressWarnings("rawtypes")
	@GetMapping("/getCsvCalidadAguaYSedimentoMarino")
	public @ResponseBody ResponseEntity getCsvCalidadAguaYSedimentoMarino() throws Exception
	{
		return getCsv("CalidadAguaYSedimentoMarino");
	}
	@SuppressWarnings("rawtypes")
	@GetMapping("/getCsvCaudalAguaSuperficial")
	public @ResponseBody ResponseEntity getCsvCaudalAguaSuperficial() throws Exception
	{
		return getCsv("CaudalAguaSuperficial");
	}
	@SuppressWarnings("rawtypes")
	@GetMapping("/getCsvCaudalAguaSubterranea")
	public @ResponseBody ResponseEntity getCsvCaudalAguaSubterranea() throws Exception
	{
		return getCsv("CaudalAguaSubterranea");
	}	
	@SuppressWarnings("rawtypes")
	@GetMapping("/getCsvNivelAguaSubterranea")
	public @ResponseBody ResponseEntity getCsvNivelAguaSubterranea() throws Exception
	{
		return getCsv("NivelAguaSubterranea");
	}
	
	
	@SuppressWarnings("rawtypes")
	@GetMapping("/getCsvResumenCalidadAguaSuperficialYSubterranea")
	public @ResponseBody ResponseEntity getResumenCalidadAguaSuperficialYSubterranea() throws Exception
	{
		return getCsv("ResumenCalidadAguaSuperficialYSubterranea");
	}
	@SuppressWarnings("rawtypes")
	@GetMapping("/getCsvResumenCalidadAguaYSedimentoMarino")
	public @ResponseBody ResponseEntity getResumenCalidadAguaYSedimentoMarino() throws Exception
	{
		return getCsv("ResumenCalidadAguaYSedimentoMarino");
	}
	@SuppressWarnings("rawtypes")
	@GetMapping("/getCsvResumenCaudalAguaSuperficial")
	public @ResponseBody ResponseEntity getCsvResumenCaudalAguaSuperficial() throws Exception
	{
		return getCsv("ResumenCaudalAguaSuperficial");
	}
	@SuppressWarnings("rawtypes")
	@GetMapping("/getCsvResumenCaudalAguaSubterranea")
	public @ResponseBody ResponseEntity getCsvResumenCaudalAguaSubterranea() throws Exception
	{
		return getCsv("ResumenCaudalAguaSubterranea");
	}
	@SuppressWarnings("rawtypes")
	@GetMapping("/getCsvResumenNivelAguaSubterranea")
	public @ResponseBody ResponseEntity getCsvResumenNivelAguaSubterranea() throws Exception
	{
		return getCsv("ResumenNivelAguaSubterranea");
	}
	@SuppressWarnings("rawtypes")
	private @ResponseBody ResponseEntity getCsv(String type) throws Exception
	{	
		long t = System.currentTimeMillis();
		try
		{
			StringBuilder sql = new StringBuilder();
			String fields = PropertyUtil.getProperty("dlab.db.sql." + type);
			sql.append("select ").append(fields).append(" from ").append(type);
			logger.info("entra a ejecutar:" + sql);
			try(PreparedStatement stmt = jdbcTemplate.getDataSource().getConnection().prepareStatement(sql.toString()))
			{
			
				return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "getCsv" + type + "_all.csv" + "\"")
					.body(new GeneraArchivosFromList().generaCSV(fields, stmt.executeQuery()));
			}
		}
		catch(Exception e)
		{
			logger.error("Error al consultar getCsv:" + type, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		finally
		{
			logger.info("Tiempo total al consultar getCsv:" + type + " en generar archivo:" + (System.currentTimeMillis() - t) + " ms");
		}
	}
	

	
	
}
