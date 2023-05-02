package cl.dlab.pid.calidaddelaire;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsultasSMA extends GeneraBase
{
	private static Logger logger = LoggerFactory.getLogger(ConsultasSMA.class);
	
	private static final String HTTP_RESPONSE_CODE_401 = "HTTP response code: 401";
	private static final String AUTHORIZATION = "Authorization";
	private static final String APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String ACCEPT = "accept";
	private static final String HTTP_APIRESTSMA_LOGIN = "http://apirestsma.eastus.cloudapp.azure.com/api/v1/login";
	private static final String POST = "POST";
	
	private static ConsultasSMA instance;
	
	public static ConsultasSMA getInstance() throws IOException
	{
		if (instance == null)
		{
			instance = new ConsultasSMA();
			instance.login();
		}
		return instance;
	}
	
	private String token;
	
	private byte[] getBytes(HttpURLConnection con) throws IOException
	{
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		try(InputStream is = con.getInputStream())
		{
		  int len;
		  byte[] b = new byte[1024];
		  while ((len = is.read(b)) != -1) {
		          bo.write(b, 0, len);
		      }
		  bo.flush();
		  bo.close();
		}
		return bo.toByteArray();
	}
	private void login() throws IOException
	{
		String usr = PropertyUtil.getProperty("dlab.apirestsma.user");
		String pwd = PropertyUtil.getProperty("dlab.apirestsma.password");
		logger.info("Login:" + usr + ", pwd:" + pwd);
		HttpURLConnection con = (HttpURLConnection)new URL(HTTP_APIRESTSMA_LOGIN).openConnection();
		con.setRequestMethod(POST);
		con.setDoOutput(true);
		con.addRequestProperty(ACCEPT, APPLICATION_JSON);
		con.addRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
		con.getOutputStream().write(("{\"user\": \"" + usr + "\", \"password\": \"" + pwd + "\"}").getBytes());

		token = new String(getBytes(con));
		token = "Bearer " + token.substring(1, token.length() - 1);
		logger.info("login exitoso");
	}
	
	protected String getUrlGetMethod(String url, int numTry) throws IOException
	{
		if (token == null)
		{
			login();
		}
		HttpURLConnection con = (HttpURLConnection)new URL(url).openConnection();
		con.addRequestProperty(ACCEPT, APPLICATION_JSON);
		con.addRequestProperty(AUTHORIZATION, token);
		con.setConnectTimeout(180000);
		try {
			return new String(getBytes(con));
		}
		catch(IOException e)
		{
			if (numTry == 0 && e.getMessage().contains(HTTP_RESPONSE_CODE_401))
			{
				System.out.println("Error generado:" + e.getMessage());
				token = null;
				return getUrlGetMethod(url, 1);
			}
			if (numTry == 1)
			{
				System.out.println("Error generado:" + e.getMessage());
				return null;
			}
			throw e;
		}
	}	
	public FileVO getUnidadFiscalizableList() throws IOException
	{
		String data = getUrlGetMethod("http://apirestsma.eastus.cloudapp.azure.com/api/v1/unidadfiscalizable/list", 0);
		return getFile(Document.parse("{data: " + data + "}"), "UnidadFiscalizableList", false);
	}
	public FileVO getUnidadFiscalizableUfId(Integer ufId) throws IOException
	{
		String data = getUrlGetMethod("http://apirestsma.eastus.cloudapp.azure.com/api/v1/unidadfiscalizable/" + ufId, 0);
		return getFile(Document.parse("{data: [" + data + "]}"), "UnidadFiscalizableUfId_" + ufId, false);
	}
	public FileVO getUnidadFiscalizableUfIdProcesoIdFecha(Integer ufId, Integer procesoId, String fecha) throws IOException
	{
		String data = getUrlGetMethod("http://apirestsma.eastus.cloudapp.azure.com/api/v1/unidadfiscalizable/" + ufId + "/proceso/" + procesoId + "/fecha/" + fecha, 0);
		return getFile(Document.parse("{data: " + data + "}"), "UnidadFiscalizableUfId_" + ufId + "_ProcesoId_" + procesoId + "_fecha_" + fecha.replaceAll("-", "_"), true);
	}
		
	@SuppressWarnings("unchecked")
	private FileVO getFile(Document doc, String name, boolean isDetalle) throws IOException
	{
		logger.info("***************************************************************************");
		logger.info("Generando csv de respuesta consulta SMA");
		logger.info("Usando ByteArrayWriter ");
		logger.info("***************************************************************************");
		String[] procesoFields = PropertyUtil.getProperty("dlab.sma.procesos.fields").split(",");

		ByteArrayWriter wr = new ByteArrayWriter(name);

		if (isDetalle)
		{
			String header = PropertyUtil.getProperty("dlab.sma.det.header");
			String[] unidadFiscalizableFields = PropertyUtil.getProperty("dlab.sma.unidadesfiscalizables.det.fields").split(",");
			String[] dispositivoFields = PropertyUtil.getProperty("dlab.pid.dispositivo.fields").split(",");
			String[] parametroFields = PropertyUtil.getProperty("dlab.pid.parametro.fields").split(",");
	
			wr.write(header).newLine();
	
			ArrayList<Document> unidadesFiscalizables = (ArrayList<Document>) doc.get("data");
			for (Document unidadFiscalizable : unidadesFiscalizables)
			{
				ArrayList<Document> dispositivos = (ArrayList<Document>)unidadFiscalizable.get("data");
				for (Document dispositivo : dispositivos)
				{
					ArrayList<Document> parametros = (ArrayList<Document>)dispositivo.get("Parametros");
					for (Document parametro : parametros)
					{
						String sep = "";
						for (String field : unidadFiscalizableFields)
						{
							String value = getValue(unidadFiscalizable.get(field));
							wr.write(sep).write(value);
							sep = "|";
						}
						for (String field : dispositivoFields)
						{
							String value = getValue(dispositivo.get(field));
							wr.write(sep).write(value);
						}
						for (String field : parametroFields)
						{
							String value = getValue(parametro.get(field));
							wr.write(sep).write(value);
						}
						wr.newLine();
					}	
				}
			}
		}
		else
		{
			String header = PropertyUtil.getProperty("dlab.sma.header");
			String[] unidadFiscalizableFields = PropertyUtil.getProperty("dlab.sma.unidadesfiscalizables.fields").split(",");
			String[] dispositivoFields = PropertyUtil.getProperty("dlab.sma.dispositivos.fields").split(",");
			String[] parametroFields = PropertyUtil.getProperty("dlab.sma.parametro.fields").split(",");

			wr.write(header).newLine();
	
			ArrayList<Document> unidadesFiscalizables = (ArrayList<Document>) doc.get("data");
			for (Document unidadFiscalizable : unidadesFiscalizables)
			{
				ArrayList<Document> procesos = (ArrayList<Document>)unidadFiscalizable.get("procesos");
				for (Document proceso : procesos)
				{
					ArrayList<Document> dispositivos = (ArrayList<Document>)proceso.get("dispositivos");
					for (Document dispositivo : dispositivos)
					{
						ArrayList<Document> parametros = (ArrayList<Document>)dispositivo.get("parametros");
						for (Document parametro : parametros)
						{
							String sep = "";
							for (String field : unidadFiscalizableFields)
							{
								String value = getValue(unidadFiscalizable.get(field));
								wr.write(sep).write(value);
								sep = "|";
							}
							for (String field : procesoFields)
							{
								String value = getValue(proceso.get(field));
								wr.write(sep).write(value);
							}
							for (String field : dispositivoFields)
							{
								String value = getValue(dispositivo.get(field));
								wr.write(sep).write(value);
							}
							for (String field : parametroFields)
							{
								String value = getValue(parametro.get(field));
								wr.write(sep).write(value);
							}
							wr.newLine();
						}	
					}
				}
			}			
		}
		
		wr.close();
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ZipOutputStream zo = new ZipOutputStream(bo);
		
		zo.putNextEntry(new ZipEntry(wr.getFileName() + ".csv"));
		wr.write(zo);	
		zo.closeEntry();
		zo.close();		
		wr.clear();
		logger.info("***************************************************************************");
		logger.info("Termina de generar archivo:" + name);
		logger.info("***************************************************************************");
		
		return new FileVO(name + ".zip", bo.toByteArray());
	}
}
