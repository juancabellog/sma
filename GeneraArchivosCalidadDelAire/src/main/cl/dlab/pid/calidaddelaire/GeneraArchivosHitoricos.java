package cl.dlab.pid.calidaddelaire;

public class GeneraArchivosHitoricos
{
	public static void main(String[] args) throws Exception
	{
		PropertyUtil.load("calidaddelaire.properties");
		new GeneraArchivos().process(PropertyUtil.getProperty("dlab.pid.listaufids.historicos").split(",")
				, PropertyUtil.getProperty("dlab.pid.path.archivos.historicos")
				, DataBase.Historica
				, true);
	}
}
