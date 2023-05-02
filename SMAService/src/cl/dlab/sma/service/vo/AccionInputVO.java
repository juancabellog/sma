package cl.dlab.sma.service.vo;

public class AccionInputVO extends InputVO {

	@Override
	public Class<AccionOutputVO> getOutputClass() {
		return AccionOutputVO.class;
	}
}