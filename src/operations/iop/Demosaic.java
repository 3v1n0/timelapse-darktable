package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Demosaic extends DTOperation {

	/**
	 * lens
	 */
	private static final long serialVersionUID = 3139886542759292214L;

	public Demosaic() {
		super("demosaic", false);
	}

	@Override
	public void addParam() {
		this.put("green_eq", new DTParameter("int", 1, null));
		this.put("median_thrs", new DTParameter("float", 1, null));
		this.put("color_smoothing", new DTParameter("int", 1, null));
		this.put("demosaicing_method", new DTParameter("int", 1, null));
		this.put("yet_unused_data_specific_to_demosaicing_method", new DTParameter("int", 1, null));		

	}
}


//typedef struct dt_iop_demosaic_params_t
//{
//  uint32_t green_eq;
//  float median_thrs;
//  uint32_t color_smoothing;
//  uint32_t demosaicing_method;
//  uint32_t yet_unused_data_specific_to_demosaicing_method;
//}
//dt_iop_demosaic_params_t;

