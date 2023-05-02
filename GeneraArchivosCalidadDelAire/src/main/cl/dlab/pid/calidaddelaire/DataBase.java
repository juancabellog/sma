package cl.dlab.pid.calidaddelaire;

public enum DataBase
{
	  Historica("sma", "CA_historicos", "dlab.pid.path.archivos.historicos", "CalidadDelAire_UfId_H_", "CalidadDelAire_UfId_DispositivoId_H_")
	, ApiRest("ExportData", "CA_ApiRest", "dlab.pid.path.archivos", "CalidadDelAire_UfId_", "CalidadDelAire_UfId_DispositivoId_")
	, ApiRest_SMA("ExportData", "CA_ApiRest_SMA", "dlab.pid.path.archivos.sma", "CalidadDelAire_UfId_SMA_", "CalidadDelAire_UfId_DispositivoId_SMA_")
	;
	
	private String dbName;
	private String collectionName;
	private String keyPathArchivos;
	private String nameUfId;
	private String nameUfIdDispositivoId;
	
	DataBase(String dbName, String collectionName, String keyPathArchivos, String nameUfId, String nameUfIdDispositivoId)
	{
		this.dbName = dbName;
		this.collectionName = collectionName;
		this.keyPathArchivos = keyPathArchivos;
		this.nameUfId = nameUfId;
		this.nameUfIdDispositivoId = nameUfIdDispositivoId;
	}
	
	@Override
	public String toString()
	{
		return "bdName:" + dbName + ", collectionName:" + collectionName + ", key-path-salida:" + keyPathArchivos;
	}
	public String getKeyPathArchivos()
	{
		return keyPathArchivos;
	}

	public String getDbName()
	{
		return dbName;
	}

	public String getCollectionName()
	{
		return collectionName;
	}

	public String getNameUfId()
	{
		return nameUfId;
	}

	public String getNameUfIdDispositivoId()
	{
		return nameUfIdDispositivoId;
	}

}
