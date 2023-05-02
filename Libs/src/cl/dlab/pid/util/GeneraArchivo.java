package cl.dlab.pid.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneraArchivo<T>
{
	private static Logger logger = LoggerFactory.getLogger(GeneraArchivo.class);
	
	private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static final DecimalFormat DEC = new DecimalFormat("###.########");
	private static final String SPECIAL_CHARS = String.valueOf((char)13) + (char)10;

	private RowListener<T> rowListener;
	public GeneraArchivo()
	{
	}
	public GeneraArchivo(RowListener<T> rowListener)
	{
		this.rowListener = rowListener;
	}
	private byte[] getZipBytes(String entryName, byte[] bytes) throws Exception
	{
		ByteArrayOutputStream zbo = new ByteArrayOutputStream();
		ZipOutputStream zo = new ZipOutputStream(zbo);
		zo.putNextEntry(new ZipEntry(entryName));
		zo.write(bytes);	
		zo.closeEntry();
		zo.close();

		return zbo.toByteArray();
		
	}
	public byte[] generaZip(Class<T> classType, List<T> list, String zipEntry, String zipName) throws Exception
	{
		byte[] bytes = generaZip(classType, list, zipEntry);
		try(FileOutputStream fo = new FileOutputStream(zipName))
		{
			fo.write(bytes);
		}
		if (rowListener != null)
		{
			System.gc();
			rowListener.generaZipFiles();
		}
		return bytes;
	}
	public byte[] generaZip(Class<T> classType, List<T> list, String zipEntry) throws Exception
	{
		logger.info("Se comprime archivo");
		byte[]  bytes = getZipBytes(zipEntry, generaCSV(classType, list));
		logger.info("bytes comprimidos a enviar:" + bytes.length);
		return bytes;
	}
	public byte[] generaCSV(Class<T> classType, List<T> list, String pathArchivoDestino) throws Exception
	{
		byte[] bytes = generaCSV(classType, list);
		try(FileOutputStream fo = new FileOutputStream(pathArchivoDestino))
		{
			fo.write(bytes);
		}
		return bytes;
	}
	public byte[] generaCSV(Class<T> classType, List<T> list) throws Exception
	{
		logger.info("Entra a escribir archivo");
		ArrayList<VOField> fields = Util.getHeaders(classType);
		
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(bo));
		bw.write(generaHeader(fields, new StringBuilder(), "").toString());
		bw.newLine();
		
		for (int r = 0; r < list.size(); r++)
		{
			T obj = list.get(r);
			if (obj == null)
			{
				continue;
			}
			if (rowListener != null)
			{
				rowListener.addRow(obj);
			}
			bw.write(generaRow(fields, obj, new StringBuilder(), "").toString());
			bw.flush();
		}
		bw.flush();
		bw.close();
		byte[] bytes = bo.toByteArray();
		logger.info("Archivo generado, bytes:" + bytes.length);
		return bytes;
	}
	private StringBuilder generaHeader(ArrayList<VOField> fields, StringBuilder br, String sep)
	{
		for (int c = 0; c < fields.size(); c++)
		{
			VOField field = fields.get(c);
			if (field.isList())
			{
				generaHeader(field.getFields(), br, sep);
			}
			else
			{
				br.append(sep).append(field.getName());
			}
			sep = "|";
		}
		return br;
		
	}
	@SuppressWarnings("unchecked")
	private StringBuilder generaRow(ArrayList<VOField> fields, Object obj, StringBuilder row, String sep) throws Exception
	{
		for (int c = 0; c < fields.size(); c++)
		{
			Object value = fields.get(c).getValue(obj);
			if (value == null)
			{
				row.append(sep).append("");
			}
			else if (value instanceof BigDecimal)
			{
				row.append(sep).append(DEC.format(((BigDecimal)value).doubleValue()).replace(',', '.'));
			}
			else if (value instanceof Date)
			{
				
				try
				{
					row.append(sep).append(FMT.format((Date)value));
				}
				catch(Exception e)
				{
					e.printStackTrace();
					System.out.println("que raro::" + value);
					throw e;
				}
			}
			else if (value instanceof Number)
			{
				row.append(sep).append(DEC.format(((Number)value).doubleValue()).replace(',', '.'));
			}
			else
			{
				if (value instanceof List)
				{
					ArrayList<VOField> fieldItems = fields.get(c).getFields();
					List<Object> list = (List<Object>)value;
					StringBuilder buff = new StringBuilder();
					for (Object item : list)
					{
						buff.append(generaRow(fieldItems, item, new StringBuilder(row.toString()), sep));
					}
					return buff;
				}
				else
				{
					row.append(sep).append(((String)value).replaceAll(SPECIAL_CHARS, " "));
				}
			}
			sep = "|";
		}
		row.append("\n");
		return row;
	}
}
