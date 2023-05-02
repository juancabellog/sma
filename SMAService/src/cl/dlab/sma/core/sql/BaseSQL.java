package cl.dlab.sma.core.sql;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import cl.dlab.sma.core.gen.EntityType;
import cl.dlab.sma.core.gen.FieldType;
import cl.dlab.sma.core.gen.QueryType;
import cl.dlab.sma.core.gen.Type;
import cl.dlab.sma.service.vo.InputVO;
import cl.dlab.sma.service.vo.QueryVO;
import cl.dlab.sma.service.vo.RespuestaVO;
import cl.dlab.sma.service.vo.VOBase;
import cl.dlab.sma.servlet.InitializeServlet;
import cl.dlab.sma.util.LogUtil;
import cl.dlab.sma.util.MassiveLoadThread;
import cl.dlab.sma.util.PropertyUtil;
import cl.dlab.sma.util.Utils;


public class BaseSQL
{
	
	private static HashMap<Class<? extends BaseSQL>, EntityType> entities = new HashMap<Class<? extends BaseSQL>, EntityType>();
	private static long MAX_TIME = 1000000; //10 minutos
	
	protected Integer getProperty(String key) throws IOException
	{
		return PropertyUtil.getId(key);
	}
	
	public static EntityType getEntity(Class<? extends BaseSQL> c) throws Exception
	{
		try
		{
			EntityType entity = entities.get(c);
			if (entity != null)
			{
				entity.setLastTime(System.currentTimeMillis());
				return entity;
			}
			synchronized (entities)
			{
				entity = entities.get(c);
				if (entity != null)
				{
					return entity;
				}
				
				ObjectInputStream oi = new ObjectInputStream(new FileInputStream(PropertyUtil.BASE + "resources/cl/dlab/sma/core/sql/sma/" + c.getSimpleName()));
				EntityType ent = (EntityType)oi.readObject();
				oi.close();
				ent.setLastTime(System.currentTimeMillis());
				entities.put(c, ent);
				return ent;
			}
			
		}
		finally
		{
			new Thread(new Runnable()
			{
				
				@Override
				public void run()
				{
					synchronized (entities)
					{
						ArrayList<Class<? extends BaseSQL>> list = new ArrayList<Class<? extends BaseSQL>>(entities.keySet());  
						for (Class<? extends BaseSQL> c : list)
						{
							EntityType ent = entities.get(c);
							if (ent.getLastTime() >= MAX_TIME)
							{
								entities.remove(c);
							}
						}
						
					}
				}
			}).start();
		}
	}
	
	protected boolean commitAndClose = true;
	protected Connection con;
	protected PreparedStatement stmtInsert;
	
	public BaseSQL(Connection con, boolean commitAndClose) throws Exception
	{
		this.con = con;
		this.commitAndClose = commitAndClose;
		newConnection();
	}
	public BaseSQL() throws Exception
	{
		newConnection();
	}
	protected Connection newConnection() throws Exception 
	{
		if (con == null)
		{
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup(PropertyUtil.getProperty("jdbc.resource.name"));
			con = ds.getConnection();		
			con.setAutoCommit(false);
			return con;
		}
		return con;
	}
	protected void setValue(PreparedStatement stmt, int index, String value) throws SQLException
	{
		LogUtil.debug(this.getClass(), value);
		if (value != null && value.length() > 0)
		{
			stmt.setString(index, value);
		}
		else
		{
			stmt.setNull(index, Types.VARCHAR);
		}
	}
	
	private void setValue(int index, FieldType f, PreparedStatement stmt, Object value) throws Exception
	{
		if (value == null)
		{
			switch (f.getType())
			{
			case Boolean:
				stmt.setNull(index, Types.BOOLEAN);
				break;
			case Integer:
				stmt.setNull(index, Types.INTEGER);
				break;
			case Long:
			case Double:
				stmt.setNull(index, Types.NUMERIC);
				break;
			case Date:
				stmt.setNull(index, Types.DATE);
				break;
			case Timestamp:
			case Timestamp4:
				stmt.setNull(index, Types.VARCHAR);
				break;
			case String:
				stmt.setNull(index, Types.VARCHAR);
				break;
			case Character:
				stmt.setNull(index, Types.CHAR);
				break;
			default:
				stmt.setNull(index, Types.BINARY);
				break;
			}
			return;	
		}
		switch (f.getType())
		{
		case Boolean:
			stmt.setBoolean(index, (Boolean)value);
			break;
		case Integer:
			stmt.setInt(index, value instanceof Integer ? (Integer)value : value instanceof Number ? ((Number)value).intValue() : Integer.valueOf(value.toString()));
			break;
		case Long:
			stmt.setLong(index, value instanceof Long ? (Long)value : value instanceof Number ? ((Number)value).longValue() : Long.parseLong(value.toString()));
			break;
		case Double:
			stmt.setDouble(index, value instanceof Double ? (Double)value : value instanceof Number ? ((Number)value).doubleValue() : Double.valueOf(value.toString()));
			break;
		case Date:
			stmt.setDate(index, new java.sql.Date(value instanceof Date ? ((Date)value).getTime() : parseDate(value.toString()).getTime()));
			break;
		case Timestamp:
		case Timestamp4:
			LogUtil.debug(this.getClass(), "settime4:", (value instanceof Date ? formatDate((Date)value) : (String)value));
			stmt.setString(index, value instanceof Date ? formatDate((Date)value) : formatDate(parseDate((String)value)));
			break;
		case String:
			if (value instanceof Boolean)
			{
				stmt.setString(index, (Boolean)value ? "S" : "N");
			}
			else
			{
				stmt.setString(index, (String)value);
			}
			break;
		case Character:
			stmt.setString(index, value instanceof String ? (String)value : ((Character)value).toString());
			break;
		case Bytes:
			stmt.setBytes(index, (byte[])value);
			break;
		case Object:
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(value);
			oo.close();
			stmt.setBytes(index, bo.toByteArray());
			break;
		default:
			break;
		}
		
	}
	private String formatDate(Date d)
	{
		return Utils.getDateFmtMs().format(d);
	}
	public static Date parseDate(String s) throws ParseException
	{
		if (s.length() == 16)
		{
			s += ":00";
		}
		if (s.charAt(10) == ' ')
		{
			s = new StringBuilder(s).replace(10, 10, "T").toString();
		}
		return Utils.getDateFmt().parse(s);
	}
	public static void main(String[] args)
	{
		Date d = new Date();
		System.out.println(d);
		System.out.println(new Timestamp(d.getTime()));
	}
	public EntityType getEntity() throws Exception
	{
		return getEntity(this.getClass());
	}

	public void eliminarAll(HashMap<String, Object> input) throws Exception 
	{
		EntityType entity = getEntity(this.getClass());
		try
		{
			LogUtil.debug(this.getClass(), "delete:", entity.getDeleteAll());
			PreparedStatement stmt = con.prepareStatement(entity.getDeleteAll());
			try
			{
				int index = 0;
				for (FieldType f : entity.getDkeys())
				{
					LogUtil.debug(this.getClass(), index, "*", f.getProperty(), "**", input.get(f.getProperty()));
					setValue(++index, f, stmt, input.get(f.getProperty()));
				}
				stmt.executeUpdate();
				commit();
			}
			finally
			{
				stmt.close();
			}
		}
		finally
		{
			close();
		}	
		
	}
	public Integer getMaxIdInt(HashMap<String, Object> input) throws Exception 
	{
		return (Integer)getMaxId(input);
	}
	public Date getMaxIdDate(HashMap<String, Object> input) throws Exception 
	{
		String s = (String)getMaxId(input);
		return s == null ? null : (Date)Utils.DATE_FMT_MS.parse(s);
	}
	public Object getMaxId(HashMap<String, Object> input) throws Exception 
	{
		EntityType entity = getEntity(this.getClass());
		try
		{
			//LogUtil.debug(this.getClass(), "maxId:", entity.getSelectMaxId());
			PreparedStatement stmt = con.prepareStatement(entity.getSelectMaxId());
			try
			{
				int index = 0;
				for (FieldType f : entity.getAkeys())
				{
					LogUtil.debug(this.getClass(), index, "*", f.getProperty(), "**", input.get(f.getProperty()));
					setValue(++index, f, stmt, input.get(f.getProperty()));
				}
				ResultSet rset = stmt.executeQuery();
				try
				{
					rset.next();
					return rset.getObject(1);
				}
				finally
				{
					rset.close();
				}
			}
			finally
			{
				stmt.close();
			}
		}
		finally
		{
			close();
		}	
		
	}	
	public void lock() throws Exception 
	{
		lock(-1);
	}
	public void lock(int timeout) throws Exception 
	{
		EntityType entity = getEntity(this.getClass());
		try
		{
			//LogUtil.debug(this.getClass(), "lock:", entity.getLock());
			PreparedStatement stmt = con.prepareStatement(entity.getLock());
			try
			{
				stmt.setInt(1, timeout);
				if (InitializeServlet.DATABASE_TYPE.equals("mysql"))
				{
					ResultSet rset = stmt.executeQuery();
					while(rset.next())
					{
						//System.out.println("lock:" + rset.getObject(1));
					}
				}
				else
				{
					stmt.executeUpdate();
				}
			}
			finally
			{
				stmt.close();
			}
		}
		finally
		{
			close();
		}	
	}
	public void unlock() throws Exception 
	{
		EntityType entity = getEntity(this.getClass());
		try
		{
			//LogUtil.debug(this.getClass(), "unlock:", entity.getUnlock());
			PreparedStatement stmt = con.prepareStatement(entity.getUnlock());
			try
			{
				if (InitializeServlet.DATABASE_TYPE.equals("mysql"))
				{
					ResultSet rset = stmt.executeQuery();
					while(rset.next())
					{
						//System.out.println("unlock:" + rset.getObject(1));
					}
				}
				else
				{
					stmt.executeUpdate();
				}
			}
			finally
			{
				stmt.close();
			}
		}
		finally
		{
			close();
		}	
	}
	public void eliminar(HashMap<String, Object> input) throws Exception 
	{
		EntityType entity = getEntity(this.getClass());
		try
		{
			LogUtil.debug(this.getClass(), "delete:", entity.getDeleteSql());
			PreparedStatement stmt = con.prepareStatement(entity.getDeleteSql());
			try
			{
				int index = 0;
				for (FieldType f : entity.getKeys())
				{
					LogUtil.debug(this.getClass(), index, "*", f.getProperty(), "**", input.get(f.getProperty()));
					setValue(++index, f, stmt, input.get(f.getProperty()));
				}
				stmt.executeUpdate();
				commit();
			}
			finally
			{
				stmt.close();
			}
		}
		finally
		{
			close();
		}	
	}
	public void guardar(HashMap<String, Object> input) throws Exception 
	{
		Boolean isNew = (Boolean)input.get("isNew");
		Boolean isBatch = (Boolean)input.get("isBatch");
		Boolean replaceTimestamp = (Boolean)input.get("replaceTimestamp");
		if (replaceTimestamp == null || replaceTimestamp)
		{
			input.put("timestamp", new Date());
		}
		if (input.get("tmp-usuario") != null) {
			input.put("usuario", input.get("tmp-usuario"));
		}
		
		if (isBatch != null && isBatch)
		{
			addBatch(input);
		}
		else if (isNew)
		{
			crear(input);
		}
		else
		{
			modificar(input);
		}
	}
	
	private void setUser(HashMap<String, Object> input)
	{
		if (input != null)
		{
			if (!input.containsKey("tmp-usuario"))
			{
				return;
			}
			input.put("usuario", input.get("tmp-usuario"));
		}
		
	}
	private void modificar(HashMap<String, Object> input) throws Exception 
	{
		setUser(input);
		EntityType entity = getEntity(this.getClass());
		try
		{
			//System.out.println(this.getClass()+ " update:"+ entity.getUpdateSql());
			LogUtil.debug(this.getClass(), "update:", entity.getUpdateSql());
			PreparedStatement stmt = con.prepareStatement(entity.getUpdateSql());
			try
			{
				int index = 0;
				for (FieldType f : entity.getField())
				{
					if (!f.getKey() && f.getType() != Type.Join && f.getType() != Type.Function)
					{
						//System.out.println(this.getClass()+ " values:"+f.getProperty()+ "**"+ input.get(f.getProperty()));
						LogUtil.debug(this.getClass(), "values:", f.getProperty(), "**", input.get(f.getProperty()));
						setValue(++index, f, stmt, input.get(f.getProperty()));
					}
				}
				for (FieldType f : entity.getKeys())
				{
					setValue(++index, f, stmt, input.get(f.getProperty()));
				}
				
				stmt.executeUpdate();
				commit();
			}
			finally
			{
				stmt.close();
			}
		}
		finally
		{
			close();
		}	
	}	
	private void crear(HashMap<String, Object> input) throws Exception 
	{
		setUser(input);
		EntityType entity = getEntity(this.getClass());
		try
		{
			LogUtil.debug(this.getClass(), "crear::", entity.getInsertSql());
			//System.out.println("insert:" + entity.getInsertSql());
			PreparedStatement stmt = con.prepareStatement(entity.getInsertSql());
			try
			{
				int index = 0;
				for (FieldType f : entity.getField())
				{
					if (f.getType() != Type.Join && !f.getAutoincrement() && f.getType() != Type.Function)
					{
						//System.out.println(this.getClass()+ "values:"+ f.getProperty()+ "**"+ input.get(f.getProperty()));
						LogUtil.debug(this.getClass(), "values:", f.getProperty(), "**", input.get(f.getProperty()));
						setValue(++index, f, stmt, input.get(f.getProperty()));
					}
				}
				LogUtil.debug(this.getClass(), "antes de update...");
				stmt.executeUpdate();
				commit();
			}
			finally
			{
				stmt.close();
			}
		}
		finally
		{
			close();
		}	
	}
	public void addBatch(HashMap<String, Object> input) throws Exception 
	{
		setUser(input);
		EntityType entity = getEntity(this.getClass());
		LogUtil.debug(this.getClass(), "crear::", entity.getInsertSql());
		//System.out.println("insert:" + entity.getInsertSql());
		if (stmtInsert == null)
		{
			stmtInsert = con.prepareStatement(entity.getInsertSql());
		}
		int index = 0;
		for (FieldType f : entity.getField())
		{
			if (f.getType() != Type.Join && !f.getAutoincrement() && f.getType() != Type.Function)
			{
				//System.out.println(this.getClass()+ "values:"+ f.getProperty()+ "**"+ input.get(f.getProperty()));
				LogUtil.debug(this.getClass(), "values:", f.getProperty(), "**", input.get(f.getProperty()));
				setValue(++index, f, stmtInsert, input.get(f.getProperty()));
			}
		}
		LogUtil.debug(this.getClass(), "antes de addBatch...");
		stmtInsert.addBatch();
	}	
	public HashMap<String, Object> consultar(HashMap<String, Object> input) throws Exception 
	{
		return consultar(getEntity(this.getClass()), input);
	}
	protected String getSql(HashMap<String, Object> input, String sql)
	{
		LogUtil.debug(this.getClass(), sql);
		return sql;
	}
	protected List<FieldType> getConditionFields(HashMap<String, Object> input, List<FieldType> conditionFields)
	{
		return conditionFields;
	}
	public HashMap<String, Object> consultar(EntityType entity, HashMap<String, Object> input) throws Exception 
	{
		try
		{
			String queryId = (String)input.remove("query_id");
			PreparedStatement stmt;
			List<FieldType> conditionFields;
			if (queryId == null)
			{
				String sql = getSql(input, entity.getSelectAll());
				stmt = entity.getProcedureName() != null ? con.prepareCall(sql) : con.prepareStatement(sql);
				if (entity.getTableName() != null)
				{
					conditionFields = getConditionFields(input, entity.getAkeys());
				}
				else
				{
					conditionFields = entity.getSqlFields();
				}
				//System.out.println("sql:" + sql + "**" + conditionFields);
			}
			else if (queryId.equals("selectOne"))
			{
				String sql = getSql(input, entity.getSelect());
				//System.out.println("sql-select one::" + sql);
				stmt = con.prepareStatement(sql);
				conditionFields = getConditionFields(input, entity.getKeys());
				//System.out.println("conditionFields::" + conditionFields);
			}
			else
			{
				
				//System.out.println("sql:" + getSql(input, entity.getSelectAll()));
				QueryType queryType = entity.getQueryById(queryId);
				stmt = entity.getProcedureName() != null ? con.prepareCall(getSql(input, entity.getSelectAll())) : con.prepareStatement(getSql(input, queryType.getSql()));
				conditionFields = getConditionFields(input, queryType.getField());
			}
			try
			{
				HashMap<String, Object> result = new HashMap<String, Object>();
				result.put("listData", _consultar(entity, stmt, input, conditionFields));
				return result;
			}
			finally
			{
				stmt.close();
			}
		}
		finally
		{
			close();
		}
	}	
	protected ArrayList<HashMap<String, Object>> _consultar(EntityType entity, PreparedStatement stmt, HashMap<String, Object> input) throws Exception 
	{
		return _consultar(entity, stmt, input, entity.getAkeys());
	}
	protected ArrayList<HashMap<String, Object>> _consultar(EntityType entity, PreparedStatement stmt, HashMap<String, Object> input, List<FieldType> conditionFields) throws Exception 
	{
		int index = 0;
		if (conditionFields != null)
		{
			for (FieldType f : conditionFields)
			{
				if (f != null && input.containsKey(f.getProperty()))
				{
					LogUtil.debug(this.getClass(), "values:", f.getProperty(), "**", input.get(f.getProperty()));
					//System.out.println("values:" + f.getProperty() + "**" + input.get(f.getProperty()));
					setValue(++index, f, stmt, input.get(f.getProperty()));
				}
			}
		}
		return _consultar(entity, stmt.executeQuery(), input);
	}
	protected ArrayList<HashMap<String, Object>> _consultar(EntityType entity, ResultSet rset, HashMap<String, Object> input) throws Exception
	{
		try
		{
			ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
			while(rset.next())
			{
				HashMap<String, Object> row = new HashMap<String, Object>();
				int index = 0;
				for (FieldType f : entity.getField())
				{
					//System.out.println("que raro:" + f.getProperty() + "**" + f.getSqlName());
					Object value = rset.getObject(++index);
					Type type = f.getType().equals(Type.Join) ? f.getTypeRef() : f.getType();
					
					if (value != null)
					{
						if (type == Type.Timestamp || type == Type.Timestamp4)
						{
							value = parseDate((String)value);
						}
						else if (type == Type.Character && value instanceof String)
						{
							value = ((String)value).charAt(0);
						}
						else if (type == Type.Boolean && value instanceof String)
						{
							value = value != null && ((String)value).charAt(0) == '1';
						}
						else if (type == Type.Object)
						{
							ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream((byte[])value));
							value = oi.readObject();
							oi.close();
						}
						else if (type == Type.Double)
						{
							if (!(value instanceof Double) && (value instanceof Number))
							{
								value = ((Number)value).doubleValue();
							}
						}
						else if (type == Type.Long)
						{
							if (!(value instanceof Long) && (value instanceof Number))
							{
								value = ((Number)value).longValue();
							}
						}
						else if (type == Type.Integer)
						{
							if (!(value instanceof Integer) && (value instanceof Number))
							{
								value = ((Number)value).intValue();
							}
						}
						if (value instanceof Date)
						{
							if (Thread.currentThread() instanceof MassiveLoadThread)
							{
								value = ((MassiveLoadThread)Thread.currentThread()).getDateFmt().format((Date)value);
							}
							else
							{
								synchronized (Utils.DATE_FMT)
								{
									value = Utils.DATE_FMT.format((Date)value);
								}
							}
							
						}
					}
					row.put(f.getProperty(), value);
					
				}
				addRow(input, row);
				list.add(row);
			}
			return list;
		}
		finally
		{
			close();
		}
	}	
	public String getId(String prefix) throws Exception
	{
		EntityType entity = getEntity(this.getClass());
		LogUtil.debug(this.getClass(), "selecID:", entity.getSelectId(), "**", entity.getTableName());
		return getId(prefix, entity.getSelectId());
	}
	protected String getId(String prefix, String sql) throws Exception
	{
		try
		{
			PreparedStatement stmt = con.prepareStatement(sql);
			try
			{
				int i = prefix.length() + 1;
				stmt.setInt(1, i);
				stmt.setInt(2, i);
				stmt.setString(3, prefix + "%");
				ResultSet rset = stmt.executeQuery();
				try
				{
					long max = 0;
					if (rset.next())
					{
						Object n = rset.getObject(1);
						if (n != null)
						{
							max = ((Number)n).longValue();
						}
					}
					return String.format(prefix + "%0" + (16 - prefix.length()) + "d", max + 1);
				}
				finally
				{
					rset.close();
				}
			}
			finally
			{
				stmt.close();
			}
		}
		finally
		{
			close();
		}
	}	
	
	
	public <Input extends InputVO, Output extends VOBase> RespuestaVO<Output> consultar(EntityType entity, Input input) throws Exception
	{
		return _consultar(consultar(entity, input.getProperties()), input);
	}
	public <Input extends InputVO, Output extends VOBase> RespuestaVO<Output> consultar(Input input) throws Exception
	{
		if (input instanceof QueryVO)
		{
			input.getProperties().put("query_id", ((QueryVO) input).getQueryName());
		}
		return _consultar(consultar(input.getProperties()), input);
		
	}
	@SuppressWarnings("unchecked")
	private <Input extends InputVO, Output extends VOBase> RespuestaVO<Output> _consultar(HashMap<String, Object> hash, Input input) throws Exception
	{
		ArrayList<HashMap<String, Object>> arr = (ArrayList<HashMap<String, Object>>)hash.get("listData");
		RespuestaVO<Output> result = new RespuestaVO<Output>();
		for (HashMap<String, Object> hs : arr)
		{
			Output out = (Output)input.getOutputClass().newInstance();
			out.setProperties(hs);
			result.getRegistros().add(out);
		}
		return result;
	}
	protected void addRow(HashMap<String, Object> input, HashMap<String, Object> row)
	{
	}

	public Integer getLastIdInsert(HashMap<String, Object> input) throws Exception
	{
		PreparedStatement stmt = con.prepareStatement(PropertyUtil.getSqlProperty("LAST_ID_INSERT"));
		try
		{
			ResultSet rset = stmt.executeQuery();
			try
			{
				if (rset.next())
				{
					return rset.getInt(1);
				}
				return null;
			}
			finally
			{
				rset.close();
			}
		}
		finally
		{
			stmt.close();
		}
	}

	/**
	 * @param con the con to set
	 */
	public void setConnection(Connection con)
	{
		this.con = con;
	}
	public Connection getConnection()
	{
		return con;
	}
	public void commit() throws SQLException
	{
		if (commitAndClose)
		{
			con.commit();
		}
	}
	public void close() throws SQLException
	{
		if (commitAndClose)
		{
			con.close();
		}
	}

	/**
	 * @return the commitAndClose
	 */
	public boolean isCommitAndClose()
	{
		return commitAndClose;
	}

	/**
	 * @param commitAndClose the commitAndClose to set
	 */
	public void setCommitAndClose(boolean commitAndClose)
	{
		this.commitAndClose = commitAndClose;
	}

	
}
