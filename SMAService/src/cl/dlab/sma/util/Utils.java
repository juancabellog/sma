package cl.dlab.sma.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils
{
	public static final String FMT_MS = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String FMT_MST = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String FMT = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String SHORT_FMT = "yyyy-MM-dd";
	public static final String FMT_UID = "yyyyMMddHHmmssSSS";
	
	public static final SimpleDateFormat DATE_FMT_MS = new SimpleDateFormat(FMT_MS);
	public static final SimpleDateFormat DATE_FMT_MST = new SimpleDateFormat(FMT_MST);
	public static final SimpleDateFormat DATE_FMT = new SimpleDateFormat(FMT);
	public static final SimpleDateFormat SHORT_DATE_FMT = new SimpleDateFormat(SHORT_FMT);
	public static final SimpleDateFormat DATE_FMT_UID = new SimpleDateFormat(FMT_UID);
	
	public static void main(String[] args) throws ParseException
	{
		Date d = DATE_FMT_MS.parse("2019-05-24 11:43:21.748");
		System.out.println(d);
		System.out.println(DATE_FMT_MS.format(d));
		
		for (int i = 0; i < 100; i++)
		{
			System.out.println(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
		}
	}
	public static Date parseDate(String s) throws ParseException
	{
		SimpleDateFormat fmt =  s == null || s.length() == 0 ? (SimpleDateFormat) null : (s.length() == 10 
				? getDateShortFmt() : (s.length() == 19 ? getDateFmt() : (s.charAt(10) == 'T' ? getDateFmtMst() : getDateFmtMs())));
		if (fmt == null)
		{
			return null;
		}
		synchronized (fmt)
		{
			return fmt.parse(s);
		}
	}
	public static SimpleDateFormat getDateShortFmt()
	{
		if (Thread.currentThread() instanceof MassiveLoadThread)
		{
			return ((MassiveLoadThread)Thread.currentThread()).getDateShortFmt();
		}
		else
		{
			synchronized (Utils.SHORT_DATE_FMT)
			{
				return Utils.SHORT_DATE_FMT;
			}
		}
		
	}
	public static SimpleDateFormat getDateFmt()
	{
		if (Thread.currentThread() instanceof MassiveLoadThread)
		{
			return ((MassiveLoadThread)Thread.currentThread()).getDateFmt();
		}
		else
		{
			synchronized (Utils.DATE_FMT)
			{
				return Utils.DATE_FMT;
			}
		}
	}
	
	public static SimpleDateFormat getDateFmtMs()
	{
		if (Thread.currentThread() instanceof MassiveLoadThread)
		{
			return ((MassiveLoadThread)Thread.currentThread()).getDateFmtMs();
		}
		else
		{
			synchronized (Utils.DATE_FMT_MS)
			{
				return Utils.DATE_FMT_MS;
			}
		}
		
	}
	public static SimpleDateFormat getDateFmtMst()
	{
		if (Thread.currentThread() instanceof MassiveLoadThread)
		{
			return ((MassiveLoadThread)Thread.currentThread()).getDateFmtMst();
		}
		else
		{
			synchronized (Utils.DATE_FMT_MST)
			{
				return Utils.DATE_FMT_MST;
			}
		}
		
	}
	public static SimpleDateFormat getDateFmtUid()
	{
		if (Thread.currentThread() instanceof MassiveLoadThread)
		{
			return ((MassiveLoadThread)Thread.currentThread()).getDateFmtUID();
		}
		else
		{
			synchronized (Utils.DATE_FMT_UID)
			{
				return Utils.DATE_FMT_UID;
			}
		}
		
	}
	
}
