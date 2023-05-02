package cl.dlab.pid.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtil
{
	private static PropertyUtil instance;
	
	public static void load(String pathProperty) throws IOException
	{
		instance = new PropertyUtil();
		instance.prop.load(new FileInputStream(pathProperty));
	}
	
	public static String getProperty(String key)
	{
		return instance.prop.getProperty(key);
	}
	private Properties prop;
	private PropertyUtil()
	{
		prop = new Properties();
	}
}
