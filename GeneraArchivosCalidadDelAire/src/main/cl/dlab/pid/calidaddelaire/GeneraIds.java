package cl.dlab.pid.calidaddelaire;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneraIds
{
	private static Logger logger = LoggerFactory.getLogger(GeneraIds.class);
	private static final String CALIDAD_DEL_AIRE_UF_ID_DISPOSITIVO_ID = "CalidadDelAire_UfId_DispositivoId_";
	private static final String CALIDAD_DEL_AIRE_UF_ID_DISPOSITIVO_ID_H = "CalidadDelAire_UfId_DispositivoId_H_";
	
	public static byte[] getIds() throws IOException
	{
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		process(bo);
		return bo.toByteArray();
	}
	private static void process(OutputStream os) throws IOException
	{
		logger.info("Entra a generar Ids");
		PropertyUtil.load("calidaddelaire.properties");
		ArrayList<String> list = new ArrayList<String>();
		for (DataBase db : DataBase.values())
		{
			logger.info("leyendo archivos desde:" + db + ", path:" + PropertyUtil.getProperty(db.getKeyPathArchivos()));
			writeIds(db, new File(PropertyUtil.getProperty(db.getKeyPathArchivos())), list); 
		}
		Collections.sort(list);
		try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os)))
		{
			bw.write("BaseDatos;Entidad;IdUnidadFiscalizable;IdDispositivo;TamañoArchivoZip;TamañoArchivoParquet");
			bw.newLine();
			for (String line : list)
			{
				bw.write(line);
				bw.newLine();
			}
		}
		logger.info("Termina de generar Ids");
	}
	private static void writeIds(DataBase db, File dir, ArrayList<String> list) throws IOException
	{
		for (File f : dir.listFiles())
		{
			if (f.isDirectory())
			{
				writeIds(db, f, list);
				continue;
			}
			String name = f.getName();
			if (!name.endsWith(".zip") || !name.startsWith(CALIDAD_DEL_AIRE_UF_ID_DISPOSITIVO_ID))
			{
				continue;
			}
			String ids = name.startsWith(CALIDAD_DEL_AIRE_UF_ID_DISPOSITIVO_ID_H) ? name.substring(CALIDAD_DEL_AIRE_UF_ID_DISPOSITIVO_ID_H.length(), name.length() -4) 
																				: name.substring(CALIDAD_DEL_AIRE_UF_ID_DISPOSITIVO_ID.length(), name.length() - 4);
			String[] t = ids.split("_");
			StringBuilder line = new StringBuilder();
			File parquet = new File(f.getAbsolutePath().replace(".zip", ".parquet"));
			line.append(db.getDbName()).append(";").append(db.getCollectionName()).append(";").append(t[0]).append(";").append(t[1])
						.append(";").append(f.length()).append(";").append(parquet.length());
			list.add(line.toString());
		}
	}
	public static void main(String[] args) throws Exception
	{
		logger.info("Entra a generar Ids");
		PropertyUtil.load("calidaddelaire.properties");
		process(new FileOutputStream("IdsUnidadFiscalizadoraDispositivo.csv"));
	}
	
}
