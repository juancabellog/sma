package cl.dlab.sma.service.vo;

public class ReguladosOutputVO extends VOBase {

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

	public void setIdComuna(java.lang.Integer idComuna) {
		set("idComuna", idComuna);
	}

	public Integer getIdComuna() {
		return get("idComuna");
	}
}