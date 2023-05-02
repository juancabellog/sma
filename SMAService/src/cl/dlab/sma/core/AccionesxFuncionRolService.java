package cl.dlab.sma.core;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import cl.dlab.sma.core.sql.rad.AccionesxFuncionRol;
import cl.dlab.sma.service.vo.AccionesxFuncionRolOutputVO;
import cl.dlab.sma.service.vo.InputVO;
import cl.dlab.sma.service.vo.RespuestaVO;

public class AccionesxFuncionRolService extends BaseService
{

	/*
	 * Un comentario
	 */
	public AccionesxFuncionRolService()
	{
		super();
	}
	
	public AccionesxFuncionRolService(Connection con)
	{
		super(con);
	}

	
	public RespuestaVO<AccionesxFuncionRolOutputVO> consultar(InputVO input) throws Exception
	{
		return new AccionesxFuncionRol(con, con == null).consultar(input);
	}

	@SuppressWarnings("unchecked")
	public HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> consultar(java.util.HashMap<String, Object> input) throws Exception
	{
		ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String,Object>>)new AccionesxFuncionRol(con, true).consultar(input).get("listData");
		HashMap<Integer, HashMap<Integer, HashMap<String, Object>>> result = new HashMap<>();
		for (HashMap<String, Object> hs : list)
		{
			Integer idFuncion = (Integer)hs.get("idFuncion");
			HashMap<Integer, HashMap<String, Object>> acciones = result.get(idFuncion);
			if (acciones == null)
			{
				result.put(idFuncion, acciones = new HashMap<>());
			}
			acciones.put((Integer)hs.get("idAccion"), hs);
		}
		return result;
	}

	public void eliminar(java.util.HashMap<String, Object> input) throws Exception
	{
		new AccionesxFuncionRol(con, true).eliminar(input);
	}

	public void guardar(java.util.HashMap<String, Object> input) throws Exception
	{
		new AccionesxFuncionRol(con, true).guardar(input);
	}
}