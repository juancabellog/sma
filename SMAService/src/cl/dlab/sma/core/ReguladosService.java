package cl.dlab.sma.core;

import java.sql.Connection;
import java.util.HashMap;

import cl.dlab.sma.service.vo.InputVO;
import cl.dlab.sma.service.vo.ReguladosOutputVO;
import cl.dlab.sma.service.vo.RespuestaVO;

public class ReguladosService extends BaseService {

	public ReguladosService() {
		super();
	}

	public ReguladosService(Connection con) {
		super(con);
	}

	public RespuestaVO<ReguladosOutputVO> consultar(InputVO input)
			throws Exception {
		return new cl.dlab.sma.core.sql.rad.Regulados(con, con == null)
				.consultar(input);
	}

	public HashMap<String, Object> consultar(
			java.util.HashMap<String, Object> input) throws Exception {
		return new cl.dlab.sma.core.sql.rad.Regulados(con, true)
				.consultar(input);
	}

	public void eliminar(java.util.HashMap<String, Object> input)
			throws Exception {
		new cl.dlab.sma.core.sql.rad.Regulados(con, true).eliminar(input);
	}

	public void guardar(java.util.HashMap<String, Object> input)
			throws Exception {
		new cl.dlab.sma.core.sql.rad.Regulados(con, true).guardar(input);
	}
}