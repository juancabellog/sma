package cl.dlab.sma.service.vo;

public class ComunaInputVO extends InputVO {

	@Override
	public Class<ComunaOutputVO> getOutputClass() {
		return ComunaOutputVO.class;
	}

	public void setIdRegion(java.lang.Integer idRegion) {
		set("idRegion", idRegion);
	}

	public Integer getIdRegion() {
		return get("idRegion");
	}
}