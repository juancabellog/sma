package cl.dlab.pid.parquet;

import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.schema.MessageType;

import java.io.IOException;
import java.util.List;
import org.apache.hadoop.fs.Path;

public class CsvParquetWriter extends ParquetWriter<List<String>>
{

	public CsvParquetWriter(Path file, MessageType schema) throws IOException
	{
		this(file, schema, false);
	}

	public CsvParquetWriter(Path file, MessageType schema, boolean enableDictionary) throws IOException
	{
		this(file, schema, CompressionCodecName.GZIP, enableDictionary);
	}

	@SuppressWarnings("deprecation")
	public CsvParquetWriter(Path file, MessageType schema, CompressionCodecName codecName, boolean enableDictionary)
			throws IOException
	{
		super(file
				, (WriteSupport<List<String>>) new CsvWriteSupport(schema)
				, codecName
				, DEFAULT_BLOCK_SIZE
				, DEFAULT_PAGE_SIZE
				, enableDictionary
				, false);

	}
}