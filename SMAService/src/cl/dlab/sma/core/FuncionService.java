package cl.dlab.sma.core;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import cl.dlab.sma.core.sql.rad.ConsultaAccionesxFuncion;
import cl.dlab.sma.core.sql.rad.Funcion;
import cl.dlab.sma.service.vo.FuncionOutputVO;
import cl.dlab.sma.service.vo.InputVO;
import cl.dlab.sma.service.vo.RespuestaVO;

public class FuncionService extends BaseService
{

	public FuncionService()
	{
		super();
	}

	public FuncionService(Connection con)
	{
		super(con);
	}

	public RespuestaVO<FuncionOutputVO> consultar(InputVO input) throws Exception
	{
		return new Funcion(con, con == null).consultar(input);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<String, Object>> consultar(HashMap<String, Object> input) throws Exception
	{
		Funcion funcion = new Funcion(con, false);
		try
		{
			ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>)funcion.consultar(input).get("listData");
			ConsultaAccionesxFuncion acciones = new ConsultaAccionesxFuncion(funcion.getConnection(), false);
			HashMap<String, Object> in = new HashMap<>();
			for (HashMap<String, Object> hs : list)
			{
				in.put("idFuncion", hs.get("id"));
				hs.put("acciones", acciones.consultar(in).get("listData"));
			}
			return list;
		}
		finally
		{
			funcion.getConnection().close();
		}
		
	}

	public void eliminar(java.util.HashMap<String, Object> input) throws Exception
	{
		new Funcion(con, true).eliminar(input);
	}

	public void guardar(java.util.HashMap<String, Object> input) throws Exception
	{
		new Funcion(con, true).guardar(input);
	}
}