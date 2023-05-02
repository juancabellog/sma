package cl.dlab.sma.core.sql.rad;

import java.sql.Connection;

import cl.dlab.sma.core.sql.BaseSQL;

public class ConsultaAccionesxFuncion extends BaseSQL {

	public ConsultaAccionesxFuncion() throws Exception {
		super();
	}

	public ConsultaAccionesxFuncion(Connection con,
			java.lang.Boolean commitAndClose) throws Exception {
		super(con, commitAndClose);
	}
}