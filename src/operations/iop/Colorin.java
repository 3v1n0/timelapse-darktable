package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Colorin extends DTOperation {

	/**
	 * lens
	 */
	private static final long serialVersionUID = 3139886542759292214L;

	public Colorin() {
		super("colorin", false);
	}

	@Override
	public void addParam() {
		this.put("iccprofile", new DTParameter("int", 25, null));
		this.put("intent", new DTParameter("int", 1, null));
	}
}

// 4*char = 1*int

////max iccprofile file name length
//#define DT_IOP_COLOR_ICC_LEN 100
//
////constants fit to the ones from lcms.h:
//typedef enum dt_iop_color_intent_t
//{
//DT_INTENT_PERCEPTUAL             = INTENT_PERCEPTUAL,            // 0
//DT_INTENT_RELATIVE_COLORIMETRIC  = INTENT_RELATIVE_COLORIMETRIC, // 1
//DT_INTENT_SATURATION             = INTENT_SATURATION,            // 2
//DT_INTENT_ABSOLUTE_COLORIMETRIC  = INTENT_ABSOLUTE_COLORIMETRIC  // 3
//}
//dt_iop_color_intent_t;
//
//typedef struct dt_iop_colorin_params_t
//{
//  char iccprofile[DT_IOP_COLOR_ICC_LEN];
//  dt_iop_color_intent_t intent;
//}
//dt_iop_colorin_params_t;