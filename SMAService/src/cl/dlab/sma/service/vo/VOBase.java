package cl.dlab.sma.service.vo;

import java.util.HashMap;

public class VOBase
{
	private HashMap<String, Object> properties;
	
	public VOBase()
	{
		this.properties = new HashMap<String, Object>();
	}
	public VOBase(HashMap<String, Object> properties)
	{
		this.properties = properties;
	}
	@SuppressWarnings("unchecked")
	public <D> D get(String key)
	{
		return (D)properties.get(key);
	}
	public <D> void set(String key, D value)
	{
		properties.put(key, value);
	}
	/**
	 * @return the properties
	 */
	public HashMap<String, Object> getProperties()
	{
		return properties;
	}
	/**
	 * @param properties the properties to set
	 */
	public void setProperties(HashMap<String, Object> properties)
	{
		this.properties = properties;
	}
	
	@Override
	public String toString()
	{
		return this.properties.toString();
	}
}
