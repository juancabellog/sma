package cl.dlab.sma.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import cl.dlab.sma.util.PropertyUtil;

public class ValidaArchivo {
	private static final SimpleDateFormat FMT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	private static StringBuilder readURL(URLConnection con) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line = null;
		StringBuilder buff = new StringBuilder();
		while((line = br.readLine()) != null)
		{
			buff.append(line);
		}
		return buff;
	}
	private Object getCellValue(Cell cell)
	{
		if (cell == null)
		{
			return "";
		}
		CellType type = cell.getCellType();
		switch (type) {
		case BLANK:
		case _NONE:
			return "";
		case BOOLEAN:
			return cell.getBooleanCellValue();
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell))
			{
				return FMT.format(cell.getDateCellValue());
			}
			return cell.getNumericCellValue();
		default:
			return "";
		}
	}
	private HashMap<String, ArrayList<HashMap<String, Object>>> getExcelFile(byte[] excelBytes) throws IOException
	{
		HashMap<String, ArrayList<HashMap<String, Object>>> result = new HashMap<String, ArrayList<HashMap<String,Object>>>();
		XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes));
		for (Iterator<Sheet> sheetIterator = workbook.sheetIterator(); sheetIterator.hasNext();) 
		{
			Sheet sheet = sheetIterator.next();
			ArrayList<HashMap<String, Object>> rows = new ArrayList<HashMap<String,Object>>();
			boolean isFirst = true;
			ArrayList<String> header = new ArrayList<String>();
			for (Iterator<Row> rowIterator = sheet.rowIterator(); rowIterator.hasNext();) 
			{
				Row row = rowIterator.next();
				if (isFirst)
				{
					isFirst = false;
					for (Iterator<Cell> cellIterator = row.cellIterator(); cellIterator.hasNext();) 
					{
						Cell cell = cellIterator.next();
						header.add(cell.getStringCellValue());
					}
					continue;
				}
				HashMap<String, Object> values = new HashMap<String, Object>(); 
				boolean allRowValuesNull = true;
				Object val;
				for (int c = 0; c < row.getLastCellNum(); c++) 
				{
					if (c == header.size())
					{
						break;
					}
					Cell cell = row.getCell(c);
					values.put(header.get(c), val = getCellValue(cell));
					if (val != null && val.toString().length() > 0)
					{
						allRowValuesNull = false;
					}
				}
				if (!allRowValuesNull)
				{
					rows.add(values);
				}
			}
			result.put(sheet.getSheetName(), rows);			
		}
		workbook.close();
		return result;
	}
	public String validar(HashMap<String, Object> input) throws Exception
	{
		HttpURLConnection con = (HttpURLConnection)new URL(PropertyUtil.getProperty("URL_VALIDADOR_ARCHIVO")).openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		try(OutputStream os = con.getOutputStream()) {
			JSONObject obj = new JSONObject();
			obj.put("codigoTipoArchivo", input.get("codigoBaseDatos"));
			obj.put("excelFile", getExcelFile((byte[])input.get("excelFile")));
			
		    byte[] output = obj.toString().getBytes("utf-8");
		    os.write(output, 0, output.length);			
		}
		return readURL(con).toString();
		
	}

}
