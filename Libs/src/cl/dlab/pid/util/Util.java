package cl.dlab.pid.util;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Util
{
	public static ArrayList<VOField> getHeaders(Class<?> c) throws Exception
	{
		Field[] fields = c.getDeclaredFields();
		ArrayList<VOField> headers = new ArrayList<VOField>();
		for (Field field : fields)
		{
			if (!field.getName().equals("serialVersionUID")) {
				headers.add(new VOField(c, field));
			}
		}
		return headers;
	}

}
