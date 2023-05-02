package cl.dlab.sma.service.vo;

public class TipoErrorInputVO extends InputVO {

	@Override
	public Class<TipoErrorOutputVO> getOutputClass() {
		return TipoErrorOutputVO.class;
	}
}