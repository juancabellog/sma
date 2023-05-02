package cl.dlab.sma.service.vo;

public class AnaliticasInputVO extends InputVO {

	@Override
	public Class<AnaliticasOutputVO> getOutputClass() {
		return AnaliticasOutputVO.class;
	}
}