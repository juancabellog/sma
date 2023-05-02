package cl.dlab.sma.service.vo;

public class FuncionInputVO extends InputVO {

	@Override
	public Class<FuncionOutputVO> getOutputClass() {
		return FuncionOutputVO.class;
	}
}