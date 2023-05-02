package cl.dlab.sma.service.vo;

public class ParametroInputVO extends InputVO {

	@Override
	public Class<ParametroOutputVO> getOutputClass() {
		return ParametroOutputVO.class;
	}
}