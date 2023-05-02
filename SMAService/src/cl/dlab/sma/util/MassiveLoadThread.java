package cl.dlab.sma.util;

import java.sql.Connection;
import java.text.SimpleDateFormat;

public class MassiveLoadThread extends Thread
{
	private SimpleDateFormat dateFmt;
	private SimpleDateFormat dateFmtMs;
	private SimpleDateFormat dateFmtUID;
	private SimpleDateFormat dateFmtMst;
	private SimpleDateFormat dateShortFmt;
	private Connection connection;
	
	public MassiveLoadThread(SimpleDateFormat dateFmt, SimpleDateFormat dateFmtMs, SimpleDateFormat dateFmtUID, SimpleDateFormat dateFmtMst, SimpleDateFormat dateShortFmt, Connection con)
	{
		super();
		setPriority(Thread.MAX_PRIORITY);
		this.dateFmt = dateFmt;
		this.dateFmtMs = dateFmtMs;
		this.dateFmtUID = dateFmtUID;
		this.dateFmtMst = dateFmtMst;
		this.dateShortFmt = dateShortFmt;
		this.connection = con;
	}
	/**
	 * @return the dateFmt
	 */
	public SimpleDateFormat getDateFmt()
	{
		return dateFmt;
	}
	/**
	 * @return the dateFtmlMs
	 */
	public SimpleDateFormat getDateFmtMs()
	{
		return dateFmtMs;
	}
	/**
	 * @return the dateFmtUID
	 */
	public SimpleDateFormat getDateFmtUID()
	{
		return dateFmtUID;
	}
	/**
	 * @return the dateFmtMst
	 */
	public SimpleDateFormat getDateFmtMst()
	{
		return dateFmtMst;
	}
	/**
	 * @return the connection
	 */
	public Connection getConnection()
	{
		return connection;
	}
	/**
	 * @return the dateShortFmt
	 */
	public SimpleDateFormat getDateShortFmt()
	{
		return dateShortFmt;
	}
	
}

