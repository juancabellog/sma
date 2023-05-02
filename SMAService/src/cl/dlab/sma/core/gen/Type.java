package cl.dlab.sma.core.gen;

public enum Type
{
	  Integer(Integer.class)
	, Long(Long.class)
	, Double(Double.class)
	, String(String.class)
	, Character(Character.class)
	, Date(java.util.Date.class)
	, Boolean(Boolean.class)
	, Timestamp(java.util.Date.class)
	, Timestamp4(java.util.Date.class)
	, Object(Object.class)
	, Join(null)
	, Function(null)
	, Bytes(byte[].class)
	;
	
	Class<?> type;
	Type(Class<?> type)
	{
		this.type = type;
	}
	public Class<?> getType()
	{
		return type;
	}
}
