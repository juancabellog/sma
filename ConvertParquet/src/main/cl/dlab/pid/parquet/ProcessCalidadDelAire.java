package cl.dlab.pid.parquet;

import java.io.BufferedReader;
import java.io.IOException;

public class ProcessCalidadDelAire implements Process
{

	@Override
	public String[] getFields(BufferedReader br, String line, String delimiter, int numFields) throws IOException
	{
		String[] fields = line.split(delimiter);
		if (fields.length < numFields)
		{
			String[] _fields = new String[numFields];
			for (int i = 0; i < fields.length; i++)
			{
				_fields[i] = fields[i];
			}
			for (int i = fields.length; i < _fields.length; i++)
			{
				_fields[i] = "";
			}
			return _fields;
		}
		return fields;
	}
	
}
