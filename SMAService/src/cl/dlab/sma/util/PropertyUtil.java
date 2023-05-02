package cl.dlab.sma.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtil
{
	public static String BASE = "";
	public static String CONFIG_NAME = "sma.config";

	private static Properties CONFIG_PPROPERTIES = new Properties();
	private static long CONFIG_PPROPERTIES_LENGTH;
	private static Properties SQL_COMPATIBILITY;

	public static Integer getId(String key) throws IOException
	{
		return Integer.parseInt(getProperty("ID-" + key));
	}

	public static Integer getIdSinErrores(String key)
	{
		try
		{
			return Integer.parseInt(getProperty("ID-" + key));
		}
		catch (Exception e)
		{
			LogUtil.error(PropertyUtil.class, e, "Error al cargar constante:", key);
			return -1;
		}
	}
	public static String getPropertySinErrores(String key)
	{
		try
		{
			return getProperty(key);
		}
		catch (Exception e)
		{
			LogUtil.error(PropertyUtil.class, e, "Error al cargar constante:", key);
			return null;
		}
	}

	public static String getProperty(String key) throws IOException
	{
		synchronized (CONFIG_PPROPERTIES)
		{
			File f = new File(BASE + CONFIG_NAME);
			if (f.length() != CONFIG_PPROPERTIES_LENGTH)
			{
				CONFIG_PPROPERTIES.load(new FileInputStream(f));
			}
		}
		return CONFIG_PPROPERTIES.getProperty(key);
	}
	public static void loadSqlCompatibility(String databaseType) throws Exception
	{
		loadSqlCompatibility("", databaseType);
	}
	public static void loadSqlCompatibility(String path, String databaseType) throws Exception
	{
		SQL_COMPATIBILITY = new Properties();
		SQL_COMPATIBILITY.load(new FileInputStream(path + "sql-compatibilities-" + databaseType + ".config"));
		
	}
	public static String getSqlProperty(String key)
	{
		return SQL_COMPATIBILITY.getProperty(key);
	}
}
