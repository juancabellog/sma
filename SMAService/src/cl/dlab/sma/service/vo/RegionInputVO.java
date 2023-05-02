package cl.dlab.sma.service.vo;

public class RegionInputVO extends InputVO {

	@Override
	public Class<RegionOutputVO> getOutputClass() {
		return RegionOutputVO.class;
	}
}