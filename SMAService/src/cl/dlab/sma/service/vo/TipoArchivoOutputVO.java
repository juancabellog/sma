package cl.dlab.sma.service.vo;

import java.util.Date;

public class TipoArchivoOutputVO extends VOBase {

	public void setCodigo(java.lang.String codigo) {
		set("codigo", codigo);
	}

	public String getCodigo() {
		return get("codigo");
	}

	public void setDescripcion(java.lang.String descripcion) {
		set("descripcion", descripcion);
	}

	public String getDescripcion() {
		return get("descripcion");
	}

	public void setObservaciones(java.lang.String observaciones) {
		set("observaciones", observaciones);
	}

	public String getObservaciones() {
		return get("observaciones");
	}

	public void setFechaCreacion(Date fechaCreacion) {
		set("fechaCreacion", fechaCreacion);
	}

	public Date getFechaCreacion() {
		return get("fechaCreacion");
	}

	public void setUltModificacion(java.util.Date ultModificacion) {
		set("ultModificacion", ultModificacion);
	}

	public Date getUltModificacion() {
		return get("ultModificacion");
	}

	public void setUsuario(java.lang.String usuario) {
		set("usuario", usuario);
	}

	public String getUsuario() {
		return get("usuario");
	}

	public void setCodigoBaseDatos(java.lang.String codigoBaseDatos) {
		set("codigoBaseDatos", codigoBaseDatos);
	}

	public String getCodigoBaseDatos() {
		return get("codigoBaseDatos");
	}
}