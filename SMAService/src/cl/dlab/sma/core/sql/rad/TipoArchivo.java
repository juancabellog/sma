package cl.dlab.sma.core.sql.rad;

import java.sql.Connection;

import cl.dlab.sma.core.sql.BaseSQL;

public class TipoArchivo extends BaseSQL {

	public TipoArchivo() throws Exception {
		super();
	}

	public TipoArchivo(Connection con, java.lang.Boolean commitAndClose)
			throws Exception {
		super(con, commitAndClose);
	}
}