package cl.dlab.sma.service.vo;

public class UsuarioInputVO extends InputVO {

	@Override
	public Class<UsuarioOutputVO> getOutputClass() {
		return UsuarioOutputVO.class;
	}
}