package cl.dlab.sma.service.vo;

public class TipoArchivoInputVO extends InputVO {

	@Override
	public Class<TipoArchivoOutputVO> getOutputClass() {
		return TipoArchivoOutputVO.class;
	}
}