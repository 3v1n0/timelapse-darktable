package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Shadhi extends DTOperation {

	/**
	 * shadhi
	 */
	private static final long serialVersionUID = -6380779433056726371L;

	public Shadhi() {
		super("shadhi", true);
	}

	@Override
	public void addParam() {
		if (this.version.equals("1")) {
			this.put("order", new DTParameter("int", 1, null, false));
			this.put("radius", new DTParameter("float", 1, null));
			this.put("shadows", new DTParameter("float", 1, null));
			this.put("reserved1", new DTParameter("float", 1, null));
			this.put("highlights", new DTParameter("float", 1, null));
			this.put("reserved2", new DTParameter("float", 1, null));
			this.put("compress", new DTParameter("float", 1, null));
		} else if (this.version.equals("2")) {
			this.put("order", new DTParameter("int", 1, null, false));
			this.put("radius", new DTParameter("float", 1, null));
			this.put("shadows", new DTParameter("float", 1, null));
			this.put("reserved1", new DTParameter("float", 1, null));
			this.put("highlights", new DTParameter("float", 1, null));
			this.put("reserved2", new DTParameter("float", 1, null));
			this.put("compress", new DTParameter("float", 1, null));
			this.put("shadows_ccorrect", new DTParameter("float", 1, null));
			this.put("highlights_ccorrect", new DTParameter("float", 1, null));
		} else if (this.version.equals("3")) {
			this.put("order", new DTParameter("int", 1, null, false));
			this.put("radius", new DTParameter("float", 1, null));
			this.put("shadows", new DTParameter("float", 1, null));
			this.put("reserved1", new DTParameter("float", 1, null));
			this.put("highlights", new DTParameter("float", 1, null));
			this.put("reserved2", new DTParameter("float", 1, null));
			this.put("compress", new DTParameter("float", 1, null));
			this.put("shadows_ccorrect", new DTParameter("float", 1, null));
			this.put("highlights_ccorrect", new DTParameter("float", 1, null));
			this.put("flags", new DTParameter("int", 1, null, false));
		} else {
			this.put("order", new DTParameter("int", 1, null, false));
			this.put("radius", new DTParameter("float", 1, null));
			this.put("shadows", new DTParameter("float", 1, null));
			this.put("reserved1", new DTParameter("float", 1, null));
			this.put("highlights", new DTParameter("float", 1, null));
			this.put("reserved2", new DTParameter("float", 1, null));
			this.put("compress", new DTParameter("float", 1, null));
			this.put("shadows_ccorrect", new DTParameter("float", 1, null));
			this.put("highlights_ccorrect", new DTParameter("float", 1, null));
			this.put("flags", new DTParameter("int", 1, null, false));
			this.put("low_approximation", new DTParameter("float", 1, null, false));
		}
	}
}


//DT_MODULE(4)
//
///* legacy version 1 params */
//typedef struct dt_iop_shadhi_params1_t
//{
//  dt_gaussian_order_t order;
//  float radius;
//  float shadows;
//  float reserved1;
//  float highlights;
//  float reserved2;
//  float compress;
//}
//dt_iop_shadhi_params1_t;
//
///* legacy version 2 params */
//typedef struct dt_iop_shadhi_params2_t
//{
//  dt_gaussian_order_t order;
//  float radius;
//  float shadows;
//  float reserved1;
//  float highlights;
//  float reserved2;
//  float compress;
//  float shadows_ccorrect;
//  float highlights_ccorrect;
//}
//dt_iop_shadhi_params2_t;
//
//typedef struct dt_iop_shadhi_params3_t
//{
//  dt_gaussian_order_t order;
//  float radius;
//  float shadows;
//  float reserved1;
//  float highlights;
//  float reserved2;
//  float compress;
//  float shadows_ccorrect;
//  float highlights_ccorrect;
//  unsigned int flags;
//}
//dt_iop_shadhi_params3_t;
//
//typedef struct dt_iop_shadhi_params_t
//{
//  dt_gaussian_order_t order;
//  float radius;
//  float shadows;
//  float reserved1;
//  float highlights;
//  float reserved2;
//  float compress;
//  float shadows_ccorrect;
//  float highlights_ccorrect;
//  unsigned int flags;
//  float low_approximation;
//}
//dt_iop_shadhi_params_t;
//typedef enum dt_gaussian_order_t
//{
//  DT_IOP_GAUSSIAN_ZERO = 0,
//  DT_IOP_GAUSSIAN_ONE = 1,
//  DT_IOP_GAUSSIAN_TWO = 2
//}
//dt_gaussian_order_t;