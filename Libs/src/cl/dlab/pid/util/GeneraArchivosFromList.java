package cl.dlab.pid.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneraArchivosFromList
{
	private static Logger logger = LoggerFactory.getLogger(GeneraArchivosFromList.class);
	
	private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static final DecimalFormat DEC = new DecimalFormat("###.########");
	private static final String SPECIAL_CHARS = String.valueOf((char)13) + (char)10;
	
	public byte[] generaCSV(String allfields, ResultSet rset) throws Exception
	{
		logger.info("Entra a escribir archivo");
		String[] fields = allfields.split(",");
		
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(bo));
		bw.write(allfields.replaceAll(",", "|"));
		bw.newLine();
		
		int n = fields.length + 1;
		while(rset.next())
		{
			bw.write(generaRow(n, rset, new StringBuilder(), "").toString());
			bw.flush();
		}
		bw.flush();
		bw.close();
		byte[] bytes = bo.toByteArray();
		logger.info("Archivo generado, bytes:" + bytes.length);
		return bytes;
	}
	private StringBuilder generaRow(int n, ResultSet rset, StringBuilder row, String sep) throws Exception
	{
		for (int i = 1; i < n; i++)
		{
			Object value = rset.getObject(i);
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
				String s = (String)value;
				if (s.indexOf("|") != -1)
				{
					System.out.print("***********\n" + s + "\n*** no valido:" + i);
					s = s.replaceAll("[|]", " ");
					System.out.println(" cambiado por:" + s);
				}
				row.append(sep).append(s.replaceAll(SPECIAL_CHARS, " "));
			}
			sep = "|";
		}
		row.append("\n");
		return row;
	}	
}
