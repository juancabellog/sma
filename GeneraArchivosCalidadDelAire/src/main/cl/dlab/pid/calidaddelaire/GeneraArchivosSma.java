package cl.dlab.pid.calidaddelaire;

public class GeneraArchivosSma
{
	public static void main(String[] args) throws Exception
	{
		PropertyUtil.load("calidaddelaire.properties");
		new GeneraArchivos().process(PropertyUtil.getProperty("dlab.pid.listaufids.sma").split(",")
				, PropertyUtil.getProperty("dlab.pid.path.archivos.sma")
				, DataBase.ApiRest_SMA
				, true);
	}
}
