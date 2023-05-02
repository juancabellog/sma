package cl.dlab.pid.util;

public interface RowListener<T>
{
	void addRow(T item);
	void generaZipFiles() throws Exception;
}
