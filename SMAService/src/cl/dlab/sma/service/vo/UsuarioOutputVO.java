package cl.dlab.sma.service.vo;

public class UsuarioOutputVO extends VOBase {

	public void setId(java.lang.String id) {
		set("id", id);
	}

	public String getId() {
		return get("id");
	}

	public void setPassword(byte[] password) {
		set("password", password);
	}

	public byte[] getPassword() {
		return get("password");
	}

	public void setNombre(java.lang.String nombre) {
		set("nombre", nombre);
	}

	public String getNombre() {
		return get("nombre");
	}

	public void setIdRol(java.lang.Integer idRol) {
		set("idRol", idRol);
	}

	public Integer getIdRol() {
		return get("idRol");
	}

	public String getRol() {
		return get("rol");
	}
}