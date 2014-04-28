package operations.iop;

import operations.DTOperation;
import operations.DTParameter;
import operations.DTValue;

public class Exposure extends DTOperation {

	/**
	 * exposure definition
	 */
	private static final long serialVersionUID = -4705176132766205794L;

	public Exposure() {
		super("exposure", true);
	}

	@Override
	public void addParam() {
		if (this.version.equals("1") || this.version.equals("2")) {
			this.put("black", new DTParameter("float", 1, null));
			this.put("exposure", new DTParameter("float", 1, null));
			this.put("gain", new DTParameter("float", 1, null));
		} else {
			this.put("black", new DTParameter("float", 1, null));
			this.put("exposure", new DTParameter("float", 1, null));
			this.put("deflicker", new DTParameter("boolean", 1, null));
			this.put("deflicker_percentile", new DTParameter("float", 1, null));
			this.put("deflicker_level", new DTParameter("float", 1, null));
		}
	}
	
	public void addDefaultValue() {
		this.addParam();
		// default value
		DTValue valueBlack = new DTValue();
		valueBlack.put(0, (double) 0);
		this.get("black").put("value",valueBlack);

		DTValue valueExpo = new DTValue();
		valueExpo.put(0, (double) 0);
		this.get("exposure").put("value",valueExpo);

		DTValue valueGain = new DTValue();
		valueGain.put(0, (double) 1);
		this.get("gain").put("value",valueGain);
		
		this.enabled = true;
		this.blendParams = "";
		this.blendVersion = "";
		this.multiPriority = "1";
		this.multiName = " ";
	}
	
	

}

/*
 * typedef struct dt_iop_exposure_params_t { float black, exposure, gain; }
 * dt_iop_exposure_params_t;
 */

// float black, exposure, gain; v2

//typedef struct dt_iop_exposure_params_t
//{
//  float black, exposure;
//  gboolean deflicker;
//  float deflicker_percentile, deflicker_level;
//}
//dt_iop_exposure_params_t;

