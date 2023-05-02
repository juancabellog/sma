package cl.dlab.pid.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class VOField
{
	private String name;
	private Method method;
	private boolean isList;
	private ArrayList<VOField> fields;
	public <T> VOField(Class<T> classType, Field field) throws Exception
	{
		name = getName(field);
		try
		{
			method = classType.getMethod("get" + name);
			if ((isList = field.getType().equals(List.class)))
			{
				String type = field.getGenericType().getTypeName();
				int index = type.indexOf("<");
				int index2 = type.indexOf(">", index);
				fields = Util.getHeaders(Class.forName(type.substring(index + 1, index2)));
			}
		}
		catch(NoSuchMethodException e)
		{
			//no existe methodo
		}
	}
	private String getName(Field field)
	{
		String name = field.getName();
		return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}
	public Object getValue(Object obj) throws Exception
	{
		return method.invoke(obj);
	}
	public String getName()
	{
		return name;
	}
	public boolean isList()
	{
		return isList;
	}
	public ArrayList<VOField> getFields()
	{
		return fields;
	}
}
