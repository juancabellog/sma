package cl.dlab.sma.service.vo;

public class AccionesxFuncionInputVO extends InputVO {

	@Override
	public Class<AccionesxFuncionOutputVO> getOutputClass() {
		return AccionesxFuncionOutputVO.class;
	}

	public void setIdFuncion(java.lang.Integer idFuncion) {
		set("idFuncion", idFuncion);
	}

	public Integer getIdFuncion() {
		return get("idFuncion");
	}
}