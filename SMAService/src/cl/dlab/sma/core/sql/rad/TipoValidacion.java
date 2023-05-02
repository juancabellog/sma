package cl.dlab.sma.core.sql.rad;

import java.sql.Connection;

import cl.dlab.sma.core.sql.BaseSQL;

public class TipoValidacion extends BaseSQL {

	public TipoValidacion() throws Exception {
		super();
	}

	public TipoValidacion(Connection con, java.lang.Boolean commitAndClose)
			throws Exception {
		super(con, commitAndClose);
	}
}