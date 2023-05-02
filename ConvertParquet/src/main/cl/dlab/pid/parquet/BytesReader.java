package cl.dlab.pid.parquet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipInputStream;

public class BytesReader
{
	private static final int MAX_GB = 1024 * 1024 * 1024;
	
	private ArrayList<byte[]> bytesArray;
	private File fileTmp;
	public BytesReader(ZipInputStream zis) throws IOException
	{
		bytesArray = new ArrayList<byte[]>();
		read(zis);
	}
	private void read(ZipInputStream zis) throws IOException
	{
		int len;
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
        while ((len = zis.read(buffer)) > 0) 
        {
        	bo.write(buffer, 0, len);
        	if (bo.size() > MAX_GB)
        	{
        		bytesArray.add(bo.toByteArray());
        		bo = new ByteArrayOutputStream();
        		System.out.println("agrega bytes to bytearray");
        	}
        }
        if (bo.size() > 0)
        {
        	bytesArray.add(bo.toByteArray());
        }
        bo = null;
	}
	public InputStream getInputStream() throws IOException
	{
		if (bytesArray.size() > 1)
		{
			fileTmp = File.createTempFile("convert_parquet_", ".csv");
			System.out.println();
			System.out.println("Archivo entrada muy grande, se crea archivo temporal:" + bytesArray.size() + "**" + fileTmp.getAbsolutePath());
			FileOutputStream fo = new FileOutputStream(fileTmp);
			for (byte[] bs : bytesArray)
			{
				fo.write(bs);
			}
			fo.close();
			return new FileInputStream(fileTmp);
			
		}
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		for (byte[] bs : bytesArray)
		{
			bo.write(bs);
		}
		bo.close();
		return new ByteArrayInputStream(bo.toByteArray());
	}
	public void deleteTmpFile()
	{
		if (fileTmp != null)
		{
			fileTmp.delete();
		}
	}
}
