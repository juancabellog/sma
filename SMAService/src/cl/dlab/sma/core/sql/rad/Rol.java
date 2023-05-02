package cl.dlab.sma.core.sql.rad;

import java.sql.Connection;

import cl.dlab.sma.core.sql.BaseSQL;

public class Rol extends BaseSQL {

	public Rol() throws Exception {
		super();
	}

	public Rol(Connection con, java.lang.Boolean commitAndClose)
			throws Exception {
		super(con, commitAndClose);
	}
}