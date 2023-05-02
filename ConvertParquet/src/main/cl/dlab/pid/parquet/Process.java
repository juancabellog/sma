package cl.dlab.pid.parquet;

import java.io.BufferedReader;
import java.io.IOException;

public interface Process
{
	String[] getFields(BufferedReader br, String line, String delimiter, int numFields) throws IOException;
}
