package cl.dlab.pid.parquet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.hadoop.fs.Path;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConvertCsvToParquet
{
	private static Logger logger = LoggerFactory.getLogger(ConvertCsvToParquet.class);
	
	private static final String CSV_DELIMITER = "|";

	private static byte[] getBytes(String fileName) throws IOException
	{
		return getBytes(new File(fileName));
	}
	private static byte[] getBytes(File file) throws IOException
	{
		try(FileInputStream fi = new FileInputStream(file))
		{
			byte[] bytes = new byte[fi.available()];
			fi.read(bytes);
			return bytes;
		}
		
	}
	public void convertToParquet(String csvFileName, String schemaFileName, String parquetFileName, Process processLine) throws IOException
	{
		convertToParquet(getBytes(csvFileName), schemaFileName, parquetFileName, processLine);
	}
	public void convertToParquet(byte[] csvBytesFile, String schemaFileName, String parquetFileName, Process processLine) throws IOException
	{
		convertToParquet(new ByteArrayInputStream(csvBytesFile), schemaFileName, parquetFileName, processLine);
	}
	public void convertToParquet(InputStream inputStream, String schemaFileName, String parquetFileName, Process processLine) throws IOException
	{
		logger.info("Escribiendo archivo:" + parquetFileName);
		String delimiter = Pattern.quote(CSV_DELIMITER);
		String schemaString = new String(getBytes(schemaFileName));
		File parquetFile = new File(parquetFileName);
		if (parquetFile.exists())
		{
			parquetFile.delete();
		}
		try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream)))
		{
			int numFields = br.readLine().split(delimiter).length;
			String line = null;
	
			Path path = new Path(parquetFile.toURI());
			MessageType schema = MessageTypeParser.parseMessageType(schemaString);
			CsvParquetWriter writer = new CsvParquetWriter(path, schema, true);
	
			int numLine = 1;
			try
			{
				while ((line = br.readLine()) != null)
				{
					numLine++;
					String[] fields = processLine.getFields(br, line, delimiter, numFields);
					writer.write(Arrays.asList(fields));
				}
			}
			catch(Exception e)
			{
				logger.info("Error procesando linea:" + numLine + "**" + line);
				e.printStackTrace();
			}
			writer.close();
		}
	}
	public void write(OutputStream os, File file) throws IOException
	{
		try(FileInputStream fi = new FileInputStream(file))
		{
			byte[] buffer = new byte[1024];
			int len;
	        while ((len = fi.read(buffer)) > 0) 
	        {
	        	os.write(buffer, 0, len);
	        }
		}		
	}
	
	public void convertCsvZipFile(File zipFile, String schemaFileName, String dirName, Process processLine) throws IOException
	{
		logger.info("Leyendo zip:" + zipFile.getAbsolutePath());
        String id = zipFile.getName().split("[.]")[0].split("_UfId_")[1] + "/";
        if (id.startsWith("H_"))
        {
        	id = id.substring(2);
        }
        File d = new File(dirName + id);
        if (!d.exists())
        {
        	d.mkdirs();
        }

        try(ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile)))
		{
			ZipEntry ze;
			while((ze = zis.getNextEntry()) != null)
			{
				if (!ze.getName().endsWith(".csv") || ze.getName().startsWith("__MACOSX"))
				{
					continue;
				}
				BytesReader reader = new BytesReader(zis);
	            convertToParquet(reader.getInputStream(), schemaFileName, dirName + id + ze.getName().replace(".csv", ".parquet"), processLine);
	            reader.deleteTmpFile();
			}
		}
        logger.info("Generando ZIP");
		try(ZipOutputStream zo = new ZipOutputStream(new FileOutputStream(zipFile.getAbsolutePath().replace(".zip", ".parquet.zip"))))
		{
	        
	        File[] fileList = d.listFiles();
	        for (File file : fileList)
			{
				if (file.getName().endsWith(".parquet"))
				{
					zo.putNextEntry(new ZipEntry(file.getName()));
					write(zo, file);
					zo.closeEntry();
				}
			}
		}
	}
	public static void main(String[] args) throws Exception
	{
		String dir = "/Users/manolocabello/Documents/dlab/db/pid/";
		new ConvertCsvToParquet().convertToParquet(dir + "CaudalAguaSuperficial_all_10.csv"
				, dir + "CaudalAguaSuperficial_all_1.schema"
				, dir + "CaudalAguaSuperficial_all_10.parquet"
				, new ProcessCaudal());
		
	}
	public static void convert(String keyPathArchivos) throws Exception
	{
		long t = System.currentTimeMillis();
		PropertyUtil.load("calidaddelaire.properties");		
		logger.info("**************************");
		logger.info("Comienza a generar parquet");
		logger.info("**************************");
		String pathArchivos = PropertyUtil.getProperty(keyPathArchivos);
		File dir = new File(pathArchivos);
        File[] fileList = dir.listFiles();
        ProcessCalidadDelAire processLine = new ProcessCalidadDelAire();
        for (File file : fileList)
		{
			if (file.getName().endsWith(".zip") && !file.getName().endsWith(".parquet.zip"))
			{
				new ConvertCsvToParquet().convertCsvZipFile(file
						, PropertyUtil.getProperty("dlab.pid.path.schema")
						, pathArchivos
						, processLine);
			}
		}
		
		logger.info("Tiempo en escribir parquet:" + (System.currentTimeMillis() - t) + " ms");
		logger.info("**************************");
		logger.info("Termina de generar parquet");
		logger.info("**************************");
	}
	public static void main2(String[] args) throws Exception
	{
		convert("dlab.pid.path.archivos");
	}
}
