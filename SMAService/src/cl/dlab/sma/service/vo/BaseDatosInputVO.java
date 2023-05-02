package cl.dlab.sma.service.vo;

public class BaseDatosInputVO extends InputVO {

	@Override
	public Class<BaseDatosOutputVO> getOutputClass() {
		return BaseDatosOutputVO.class;
	}
}