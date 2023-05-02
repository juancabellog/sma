package cl.dlab.pid.parquet;

import java.io.BufferedReader;
import java.io.IOException;

public class ProcessCaudal implements Process
{

	@Override
	public String[] getFields(BufferedReader br, String line, String delimiter, int numFields) throws IOException
	{
		String[] fields;
		while(true)
		{
			fields = line.split(delimiter);
			if (fields.length == numFields)
			{
				break;
			}
			System.out.println(line);
			line = line + br.readLine();
		}
		return fields;
	}

}
