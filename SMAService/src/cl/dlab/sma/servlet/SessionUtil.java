package cl.dlab.sma.servlet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import cl.dlab.sma.core.sql.BaseSQL;
import cl.dlab.sma.util.LogUtil;

public class SessionUtil
{
	public void create(String id) throws Exception
	{
		LogUtil.info(getClass(), "se crea session:" + id);
		BaseSQL sql = new BaseSQL(null, false);
		try
		{
			PreparedStatement stmt = sql.getConnection().prepareStatement("insert into ActiveSession (id) values (?)");
			stmt.setString(1, id);
			stmt.executeUpdate();
			sql.getConnection().commit();
		}
		catch(Exception e)
		{
			sql.getConnection().rollback();
			LogUtil.error(getClass(), e, "Error al crear session:" + id);
		} 
		finally
		{
			sql.getConnection().close();
		}
	}
	public void delete(String id) throws Exception
	{
		LogUtil.info(getClass(), "se borra session:" + id);
		BaseSQL sql = new BaseSQL(null, false);
		try
		{
			PreparedStatement stmt = sql.getConnection().prepareStatement("delete from ActiveSession where id = ?");
			stmt.setString(1, id);
			stmt.executeUpdate();
			sql.getConnection().commit();
		}
		catch(Exception e)
		{
			sql.getConnection().rollback();
			LogUtil.error(getClass(), e, "Error al crear session:" + id);
		} 
		finally
		{
			sql.getConnection().close();
		}
	}
	public void update(String id, String data) throws Exception
	{
		LogUtil.info(getClass(), "se actualiza session:" + id);
		BaseSQL sql = new BaseSQL(null, false);
		try
		{
			PreparedStatement stmt = sql.getConnection().prepareStatement("update ActiveSession set data = ? where id = ? ");
			stmt.setString(1, data);
			stmt.setString(2, id);
			stmt.executeUpdate();
			sql.getConnection().commit();
		}
		catch(Exception e)
		{
			sql.getConnection().rollback();
			LogUtil.error(getClass(), e, "Error al update session:" + id);
		} 
		finally
		{
			sql.getConnection().close();
		}
	}
	public String getData(String id) throws Exception
	{
		LogUtil.info(getClass(), "get session:" + id);
		BaseSQL sql = new BaseSQL(null, false);
		try
		{
			PreparedStatement stmt = sql.getConnection().prepareStatement("select data from  ActiveSession where id = ?");
			stmt.setString(1, id);
			ResultSet rset = stmt.executeQuery();
			if (rset.next()) {
				return rset.getString(1);
			}
			return null;
		}
		catch(Exception e)
		{
			sql.getConnection().rollback();
			LogUtil.error(getClass(), e, "Error al obtener session session:" + id);
			return null;
		} 
		finally
		{
			sql.getConnection().close();
		}
	}
}
