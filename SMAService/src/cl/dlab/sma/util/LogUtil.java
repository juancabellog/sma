package cl.dlab.sma.util;

import java.util.HashMap;

import org.apache.log4j.Logger;

public final class LogUtil
{
	private static HashMap<Class<?>, Logger> loggers = new HashMap<Class<?>, Logger>();

	private static final  Logger getLogger(Class<?> c)
	{
		Logger log;
		synchronized (loggers)
		{
			log = loggers.get(c);
			if (log == null)
			{
				loggers.put(c, log = Logger.getLogger(c));
			}
		}
		return log;
	}
	public static final void debug(Class<?> c, Object...msg)
	{
		Logger log = getLogger(c);
		if (log.isDebugEnabled())
		{
			StringBuilder buff = new StringBuilder();
			StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
			buff.append("(").append(stack.getClassName()).append(":").append(stack.getLineNumber()).append(") - ");
			
			for (Object s : msg)
			{
				buff.append(s);
			}
			log.debug(buff.toString());
		}
	}
	public static final void info(Class<?> c, Object...msg)
	{
		Logger log = getLogger(c);
		if (log.isInfoEnabled())
		{
			StringBuilder buff = new StringBuilder();
			StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
			buff.append("(").append(stack.getClassName()).append(":").append(stack.getLineNumber()).append(") - ");
			
			for (Object s : msg)
			{
				buff.append(s);
			}
			log.info(buff.toString());
		}
	}
	public static final void warn(Class<?> c, Object...msg)
	{
		Logger log = getLogger(c);
		StringBuilder buff = new StringBuilder();
		StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
		buff.append("(").append(stack.getClassName()).append(":").append(stack.getLineNumber()).append(") - ");
		
		for (Object s : msg)
		{
			buff.append(s);
		}
		log.warn(buff.toString());
	}
	public static final void warn(Class<?> c, Exception e, Object...msg)
	{
		Logger log = getLogger(c);
		StringBuilder buff = new StringBuilder();
		for (Object s : msg)
		{
			buff.append(s);
		}
		log.warn(buff.toString(), e);
	}
	public static final void error(Class<?> c, Exception e, Object...msg)
	{
		error(c, (Throwable)e, msg);
	}
	public static final void error(Class<?> c, Throwable e, Object...msg)
	{
		Logger log = getLogger(c);
		StringBuilder buff = new StringBuilder();
		for (Object s : msg)
		{
			buff.append(s);
		}
		log.error(buff.toString(), e);
	}
	public static final void error(Class<?> c, Object...msg)
	{
		Logger log = getLogger(c);
		StringBuilder buff = new StringBuilder();
		for (Object s : msg)
		{
			buff.append(s);
		}
		log.error(buff.toString());
	}
	
}
