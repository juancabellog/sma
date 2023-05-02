package cl.dlab.pid.calidaddelaire;

import java.io.IOException;
import java.io.OutputStream;

public interface BytesWriter
{
	public void newLine() throws IOException;
	public void close() throws IOException;
	public void clear();
	public BytesWriter write(String s) throws IOException;	
	public void write(OutputStream os) throws IOException;
	public void writeZipFile(OutputStream os) throws IOException;
	public String getFileName();
	public Integer getUfId();
}
