package cl.dlab.sma.service.vo;

public class TipoValidacionInputVO extends InputVO {

	@Override
	public Class<TipoValidacionOutputVO> getOutputClass() {
		return TipoValidacionOutputVO.class;
	}
}