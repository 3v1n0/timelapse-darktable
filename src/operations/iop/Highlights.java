package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Highlights extends DTOperation {
	/**
	 * colorzones
	 */
	private static final long serialVersionUID = -8173842643135545850L;

	public Highlights() {
		super("colorzones", true);
	}

	@Override
	public void addParam() {

		if (this.version.equals("1")) {
			this.put("mode", new DTParameter("int", 1, null, false));
			this.put("blendL", new DTParameter("float",1, null));
			this.put("blendC", new DTParameter("float",1, null));
			this.put("blendh", new DTParameter("float",1, null));
			this.put("clip", new DTParameter("float",1, null));
		} else {
			this.put("mode", new DTParameter("int", 1, null, false));
			this.put("blendL", new DTParameter("float",1, null));
			this.put("blendC", new DTParameter("float",1, null));
			this.put("blendh", new DTParameter("float",1, null));
			this.put("clip", new DTParameter("float",1, null));
		}
	}

}

//DT_MODULE(2)
//
//typedef enum dt_iop_highlights_mode_t
//{
//  DT_IOP_HIGHLIGHTS_CLIP = 0,
//  DT_IOP_HIGHLIGHTS_LCH = 1
//}
//dt_iop_highlights_mode_t;
//
//typedef struct dt_iop_highlights_params_t
//{
//  dt_iop_highlights_mode_t mode;
//  float blendL, blendC, blendh; // unused
//  float clip;
//}
//dt_iop_highlights_params_t;

