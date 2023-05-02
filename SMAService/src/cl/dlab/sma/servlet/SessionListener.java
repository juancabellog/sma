package cl.dlab.sma.servlet;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import cl.dlab.sma.util.LogUtil;

public class SessionListener implements HttpSessionListener
{
	@Override
	public void sessionCreated(HttpSessionEvent se)
	{
		try
		{
			new SessionUtil().create(se.getSession().getId());
		}
		catch (Exception e)
		{
			LogUtil.error(getClass(), e, "Error al crear sesion");
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se)
	{
		try
		{
			new SessionUtil().delete(se.getSession().getId());
		}
		catch (Exception e)
		{
			LogUtil.error(getClass(), e, "Error al borrar sesion");
		}
		
	}

}
