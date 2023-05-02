package cl.dlab.sma.service.vo;

public class EstacionesOutputVO extends VOBase {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public Integer getId() {
		return get("id");
	}

	public void setDescripcion(java.lang.String descripcion) {
		set("descripcion", descripcion);
	}

	public String getDescripcion() {
		return get("descripcion");
	}

	public void setIdRegulado(java.lang.Integer idRegulado) {
		set("idRegulado", idRegulado);
	}

	public Integer getIdRegulado() {
		return get("idRegulado");
	}
}