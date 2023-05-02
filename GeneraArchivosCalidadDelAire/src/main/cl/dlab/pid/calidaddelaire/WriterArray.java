package cl.dlab.pid.calidaddelaire;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class WriterArray implements BytesWriter
{
	BufferedWriter bw;
	Integer ufId;
	String name;
	File tmpFile;
	public WriterArray(DataBase db, Integer ufId, Integer dispositivoId) throws IOException
	{
		this.ufId = ufId;
		name = db.getNameUfIdDispositivoId() + ufId + "_" + dispositivoId;
		tmpFile = new File("tmp/" + name + ".tmp");
		bw = new BufferedWriter(new FileWriter(tmpFile));
	}
	public BytesWriter write(String s) throws IOException
	{
		bw.write(s);
		bw.flush();
		return this;
	}
	public void newLine() throws IOException
	{
		bw.newLine();
	}
	public void close() throws IOException
	{
		bw.flush();
		bw.close();
	}
	public void clear()
	{
		tmpFile.delete();
	}
	public void write(OutputStream os) throws IOException
	{
		int len;
		byte[] buffer = new byte[1024];
		try(FileInputStream fi = new FileInputStream(tmpFile))
		{
	        while ((len = fi.read(buffer)) > 0) 
	        {
	        	os.write(buffer, 0, len);
	        }
		}
		
	}
	
	public void writeZipFile(OutputStream os) throws IOException
	{
		ZipOutputStream zo = new ZipOutputStream(os);
		zo.putNextEntry(new ZipEntry(getFileName() + ".csv"));
		write(zo);
		zo.closeEntry();
		zo.close();		
	}
	public String getFileName()
	{
		return name;
	}
	public Integer getUfId()
	{
		return ufId;
	}
}
