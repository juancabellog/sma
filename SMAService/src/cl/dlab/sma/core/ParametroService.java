package cl.dlab.sma.core;

import java.sql.Connection;
import java.util.HashMap;

import cl.dlab.sma.service.vo.InputVO;
import cl.dlab.sma.service.vo.ParametroOutputVO;
import cl.dlab.sma.service.vo.RespuestaVO;

public class ParametroService extends BaseService {

	public ParametroService() {
		super();
	}

	public ParametroService(Connection con) {
		super(con);
	}

	public RespuestaVO<ParametroOutputVO> consultar(InputVO input)
			throws Exception {
		return new cl.dlab.sma.core.sql.rad.Parametro(con, con == null)
				.consultar(input);
	}

	public HashMap<String, Object> consultar(
			java.util.HashMap<String, Object> input) throws Exception {
		return new cl.dlab.sma.core.sql.rad.Parametro(con, true)
				.consultar(input);
	}

	public void eliminar(java.util.HashMap<String, Object> input)
			throws Exception {
		new cl.dlab.sma.core.sql.rad.Parametro(con, true).eliminar(input);
	}

	public void guardar(java.util.HashMap<String, Object> input)
			throws Exception {
		new cl.dlab.sma.core.sql.rad.Parametro(con, true).guardar(input);
	}
}