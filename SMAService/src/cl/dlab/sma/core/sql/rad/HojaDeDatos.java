package cl.dlab.sma.core.sql.rad;

import java.sql.Connection;

import cl.dlab.sma.core.sql.BaseSQL;

public class HojaDeDatos extends BaseSQL {

	public HojaDeDatos() throws Exception {
		super();
	}

	public HojaDeDatos(Connection con, java.lang.Boolean commitAndClose)
			throws Exception {
		super(con, commitAndClose);
	}
}