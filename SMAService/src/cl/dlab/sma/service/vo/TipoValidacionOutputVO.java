package cl.dlab.sma.service.vo;

public class TipoValidacionOutputVO extends VOBase {

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

	public void setEsNormativa(java.lang.Boolean esNormativa) {
		set("esNormativa", esNormativa);
	}

	public Boolean getEsNormativa() {
		return get("esNormativa");
	}
}