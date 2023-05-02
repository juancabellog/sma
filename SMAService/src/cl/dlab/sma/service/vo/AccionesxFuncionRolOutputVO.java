package cl.dlab.sma.service.vo;

import java.util.Date;

public class AccionesxFuncionRolOutputVO extends VOBase {

	public void setIdRol(java.lang.Integer idRol) {
		set("idRol", idRol);
	}

	public Integer getIdRol() {
		return get("idRol");
	}

	public void setIdFuncion(java.lang.Integer idFuncion) {
		set("idFuncion", idFuncion);
	}

	public Integer getIdFuncion() {
		return get("idFuncion");
	}

	public void setIdAccion(java.lang.Integer idAccion) {
		set("idAccion", idAccion);
	}

	public Integer getIdAccion() {
		return get("idAccion");
	}

	public void setTimestamp(Date timestamp) {
		set("timestamp", timestamp);
	}

	public Date getTimestamp() {
		return get("timestamp");
	}
}