package cl.dlab.pid.calidaddelaire;

public class FileVO
{
	
	private String name;
	private byte[] bytes;
	
	public FileVO(String name, byte[] bytes)
	{
		this.name = name;
		this.bytes = bytes;
	}

	public String getName()
	{
		return name;
	}

	public byte[] getBytes()
	{
		return bytes;
	}
}
