package cl.dlab.sma.core;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cl.dlab.sma.core.sql.rad.AccionesxFuncionRol;
import cl.dlab.sma.core.sql.rad.Rol;
import cl.dlab.sma.service.vo.InputVO;
import cl.dlab.sma.service.vo.RespuestaVO;
import cl.dlab.sma.service.vo.RolOutputVO;

public class RolService extends BaseService
{

	public RolService()
	{
		super();
	}

	public RolService(Connection con)
	{
		super(con);
	}

	public RespuestaVO<RolOutputVO> consultar(InputVO input) throws Exception
	{
		return new Rol(con, con == null).consultar(input);
	}

	public HashMap<String, Object> consultar(java.util.HashMap<String, Object> input) throws Exception
	{
		return new Rol(con, true).consultar(input);
	}

	public void eliminar(java.util.HashMap<String, Object> input) throws Exception
	{
		new Rol(con, true).eliminar(input);
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> guardar(java.util.HashMap<String, Object> input) throws Exception
	{
		Rol rol = new Rol(con, false);
		try
		{
			AccionesxFuncionRol accionesService = new AccionesxFuncionRol(rol.getConnection(), false);
			ArrayList<HashMap<String, Object>> funciones = (ArrayList<HashMap<String, Object>>)input.get("funciones");

			rol.guardar(input);
			if ((Boolean)input.get("isNew"))
			{
				input.put("id", rol.getLastIdInsert(input));
			}
			
			input.put("idRol", input.get("id"));
			accionesService.eliminarAll(input);
			Date fecha = new Date();
			for (HashMap<String, Object> funcion : funciones)
			{
				if ((Boolean)funcion.get("selected"))
				{
					ArrayList<HashMap<String, Object>> acciones = (ArrayList<HashMap<String, Object>>)funcion.get("acciones");
					for (HashMap<String, Object> accion : acciones)
					{
						if ((Boolean)accion.get("selected"))
						{
							accion.put("idFuncion", funcion.get("id"));
							accion.put("idRol", input.get("id"));
							accion.put("timestamp", fecha);
							accion.put("isNew", true);
							accionesService.guardar(accion);
						}
						
					}
				}
			}

			rol.getConnection().commit();
			return input;
		}
		finally
		{
			rol.getConnection().close();;
		}
	}
}