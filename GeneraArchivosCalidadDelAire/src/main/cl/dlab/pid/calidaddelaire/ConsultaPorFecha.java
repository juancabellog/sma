package cl.dlab.pid.calidaddelaire;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

public class ConsultaPorFecha extends GeneraArchivos
{
	private static SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd-MM-yyyy");
	
	private Date fechaInicio;
	private Date fechaTermino;
		
	@Override
	protected Bson getFilter(Integer ufId)
	{
		return Filters.and(Filters.eq("UfId", ufId), 
				   Filters.gte("timestamp", fechaInicio),
				   Filters.lt("timestamp", fechaTermino));
	}
	

	public FileVO getFile(DataBase db, Integer ufId, String fechaInicio, String fechaTermino, String formato) throws Exception
	{
		this.fechaInicio = DATE_FMT.parse(fechaInicio);
		this.fechaTermino = DATE_FMT.parse(fechaTermino);
		super.process(new String[] {ufId.toString()}, "tmp/", db, false);
		String fileNameBase =  db.getNameUfId() + ufId  + ".zip";
		File file = new File("tmp/" + fileNameBase);
		if (!file.exists())
		{
			return null;
		}
		try(FileInputStream fi = new FileInputStream(file))
		{
			byte[] bytes = new byte[fi.available()];
			fi.read(bytes);
			return new FileVO(fileNameBase, bytes);
		}
		finally
		{
			file.delete();
		}
		
	}
	public static void main(String[] args) throws Exception
	{
		PropertyUtil.load("calidaddelaire.properties");
		FileVO file = new ConsultaPorFecha().getFile(DataBase.ApiRest, 299, "02-02-2022", "03-02-2022", "csv");
		System.out.println(file.getName() + ".." + file.getBytes().length);
	}
}
