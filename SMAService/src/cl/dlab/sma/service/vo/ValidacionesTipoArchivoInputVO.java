package cl.dlab.sma.service.vo;

public class ValidacionesTipoArchivoInputVO extends InputVO {

	@Override
	public Class<ValidacionesTipoArchivoOutputVO> getOutputClass() {
		return ValidacionesTipoArchivoOutputVO.class;
	}

	public void setCodigoTipoArchivo(java.lang.String codigoTipoArchivo) {
		set("codigoTipoArchivo", codigoTipoArchivo);
	}

	public String getCodigoTipoArchivo() {
		return get("codigoTipoArchivo");
	}

}