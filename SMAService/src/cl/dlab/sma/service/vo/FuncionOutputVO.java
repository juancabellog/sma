package cl.dlab.sma.service.vo;

public class FuncionOutputVO extends VOBase {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public Integer getId() {
		return get("id");
	}

	public void setOrden(java.lang.Integer orden) {
		set("orden", orden);
	}

	public Integer getOrden() {
		return get("orden");
	}

	public void setDescripcion(java.lang.String descripcion) {
		set("descripcion", descripcion);
	}

	public String getDescripcion() {
		return get("descripcion");
	}
}