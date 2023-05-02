package cl.dlab.sma.service.vo;

public class HojaDeDatosOutputVO extends VOBase {

	public void setCodigoTipoArchivo(java.lang.String codigoTipoArchivo) {
		set("codigoTipoArchivo", codigoTipoArchivo);
	}

	public String getCodigoTipoArchivo() {
		return get("codigoTipoArchivo");
	}

	public void setNombre(java.lang.String nombre) {
		set("nombre", nombre);
	}

	public String getNombre() {
		return get("nombre");
	}

	public void setEncabezado(java.lang.String encabezado) {
		set("encabezado", encabezado);
	}

	public String getEncabezado() {
		return get("encabezado");
	}
}