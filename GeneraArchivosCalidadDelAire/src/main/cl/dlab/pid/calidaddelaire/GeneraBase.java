package cl.dlab.pid.calidaddelaire;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneraBase
{
	private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	private static final DecimalFormat DEC = new DecimalFormat("###.########");
	
	
	protected String getValue(Object value)
	{
		if (value == null)
		{
			return "";
		}
		else if (value instanceof BigDecimal)
		{
			return DEC.format(((BigDecimal)value).doubleValue()).replace(',', '.');
		}
		else if (value instanceof Date)
		{
			return FMT.format((Date)value);
		}
		else if (value instanceof Number)
		{
			return DEC.format(((Number)value).doubleValue()).replace(',', '.');
		}
		else
		{
			return (String)value;
		}		
	}	

}
