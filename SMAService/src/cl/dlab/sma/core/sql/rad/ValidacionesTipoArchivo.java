package cl.dlab.sma.core.sql.rad;

import java.sql.Connection;

import cl.dlab.sma.core.sql.BaseSQL;

public class ValidacionesTipoArchivo extends BaseSQL {

	public ValidacionesTipoArchivo() throws Exception {
		super();
	}

	public ValidacionesTipoArchivo(Connection con,
			java.lang.Boolean commitAndClose) throws Exception {
		super(con, commitAndClose);
	}
}