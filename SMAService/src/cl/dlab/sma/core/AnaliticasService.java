package cl.dlab.sma.core;

import java.sql.Connection;
import java.util.HashMap;

import cl.dlab.sma.service.vo.AnaliticasOutputVO;
import cl.dlab.sma.service.vo.InputVO;
import cl.dlab.sma.service.vo.RespuestaVO;

public class AnaliticasService extends BaseService {

	public AnaliticasService() {
		super();
	}

	public AnaliticasService(Connection con) {
		super(con);
	}

	public RespuestaVO<AnaliticasOutputVO> consultar(InputVO input)
			throws Exception {
		return new cl.dlab.sma.core.sql.rad.Analiticas(con, con == null)
				.consultar(input);
	}

	public HashMap<String, Object> consultar(
			java.util.HashMap<String, Object> input) throws Exception {
		return new cl.dlab.sma.core.sql.rad.Analiticas(con, true)
				.consultar(input);
	}

	public void eliminar(java.util.HashMap<String, Object> input)
			throws Exception {
		new cl.dlab.sma.core.sql.rad.Analiticas(con, true).eliminar(input);
	}

	public void guardar(java.util.HashMap<String, Object> input)
			throws Exception {
		new cl.dlab.sma.core.sql.rad.Analiticas(con, true).guardar(input);
	}
}