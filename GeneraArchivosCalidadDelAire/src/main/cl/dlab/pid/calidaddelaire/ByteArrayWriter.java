package cl.dlab.pid.calidaddelaire;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ByteArrayWriter implements BytesWriter
{
	private static final int UN_MB = 1024 * 1024;
	private ArrayList<byte[]> bytesArrays;
	BufferedWriter bw;
	ByteArrayOutputStream bo;
	Integer ufId;
	String name;
	
	public ByteArrayWriter(DataBase db, Integer ufId, Integer dispositivoId) throws IOException
	{
		this.ufId = ufId;
		name = db.getNameUfIdDispositivoId() + ufId + "_" + dispositivoId;
		bytesArrays = new ArrayList<byte[]>();
		initBytesArrays();
	}
	public ByteArrayWriter(String name) throws IOException
	{
		this.name = name;
		bytesArrays = new ArrayList<byte[]>();
		initBytesArrays();
	}
	private void initBytesArrays()
	{
		bo = new ByteArrayOutputStream();
		bw = new BufferedWriter(new OutputStreamWriter(bo));
	}
	public BytesWriter write(String s) throws IOException
	{
		bw.write(s);
		if (bo.size() >= UN_MB)
		{
			bw.flush();
			bw.close();
			bytesArrays.add(bo.toByteArray());
			initBytesArrays();
			if (bytesArrays.size() % 1024 == 0)
			{
				System.out.println("Tamaño del array:" + bytesArrays.size());
			}
			
		}
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
		if (bo.size() > 0)
		{
			bytesArrays.add(bo.toByteArray());
		}
		bo = null;
		bw = null;
	}
	public void clear()
	{
		bytesArrays.clear();
		bytesArrays = null;
	}
	public void write(OutputStream os) throws IOException
	{
		for (byte[] bs : bytesArrays)
		{
			os.write(bs, 0, bs.length);
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
