package cl.dlab.covid19.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.bson.Document;

public class TestApiRestSma
{
	private String token = "xx";
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
	private void login() throws Exception
	{
		long t = System.currentTimeMillis();
		HttpURLConnection con = (HttpURLConnection)new URL("http://apirestsma.eastus.cloudapp.azure.com/api/v1/login").openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.addRequestProperty("accept", "application/json");
		con.addRequestProperty("Content-Type", "application/json");
		con.getOutputStream().write(("{\n"
				+ "  \"user\": \"pid\",\n"
				+ "  \"password\": \"8gVv&JFr5SeT\"\n"
				+ "}").getBytes());

		token = new String(getBytes(con));
		token = "Bearer " + token.substring(1, token.length() - 1);
		System.out.println(System.currentTimeMillis() - t);
	}
	
	@SuppressWarnings("unused")
	private String getUrlGetMethod(String url, int numTry) throws Exception
	{
		if (token == null)
		{
			login();
		}
		HttpURLConnection con = (HttpURLConnection)new URL(url).openConnection();
		con.addRequestProperty("accept", "application/json");
		con.addRequestProperty("Authorization", token);
		System.out.println(token);
		try {
			return new String(getBytes(con));
		}
		catch(IOException e)
		{
			if (numTry == 0 && e.getMessage().contains("HTTP response code: 401"))
			{
				token = null;
				return getUrlGetMethod(url, 1);
			}
			throw e;
		}
	}
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception
	{
		String data = new TestApiRestSma().getUrlGetMethod("http://apirestsma.eastus.cloudapp.azure.com/api/v1/unidadfiscalizable/1674/proceso/34/fecha/2022-07-28", 0);
		Document doc = Document.parse("{data: " + data + "}");
		ArrayList<Document> list = (ArrayList<Document>)doc.get("data");
		System.out.println(list);
		//new TestApiRestSma().login();
	}
}
