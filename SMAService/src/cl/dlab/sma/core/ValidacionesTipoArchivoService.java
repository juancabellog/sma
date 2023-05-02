package cl.dlab.sma.core;

import java.sql.Connection;
import java.util.HashMap;

import cl.dlab.sma.service.vo.InputVO;
import cl.dlab.sma.service.vo.RespuestaVO;
import cl.dlab.sma.service.vo.ValidacionesTipoArchivoOutputVO;

public class ValidacionesTipoArchivoService extends BaseService {

	public ValidacionesTipoArchivoService() {
		super();
	}

	public ValidacionesTipoArchivoService(Connection con) {
		super(con);
	}

	public RespuestaVO<ValidacionesTipoArchivoOutputVO> consultar(InputVO input)
			throws Exception {
		return new cl.dlab.sma.core.sql.rad.ValidacionesTipoArchivo(con,
				con == null).consultar(input);
	}

	public HashMap<String, Object> consultar(
			java.util.HashMap<String, Object> input) throws Exception {
		return new cl.dlab.sma.core.sql.rad.ValidacionesTipoArchivo(con, true)
				.consultar(input);
	}

	public void eliminar(java.util.HashMap<String, Object> input)
			throws Exception {
		new cl.dlab.sma.core.sql.rad.ValidacionesTipoArchivo(con, true)
				.eliminar(input);
	}

	public void guardar(java.util.HashMap<String, Object> input)
			throws Exception {
		new cl.dlab.sma.core.sql.rad.ValidacionesTipoArchivo(con, true)
				.guardar(input);
	}
}