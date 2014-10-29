package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Colorcorrection extends DTOperation {

	/**
	 * colorcorrection
	 */
	private static final long serialVersionUID = 3139986545659292214L;

	public Colorcorrection() {
		super("colorcorrection", true);
	}

	@Override
	public void addParam() {
		this.put("hia", new DTParameter("float", 1, null));
		this.put("hib", new DTParameter("float", 1, null));
		this.put("loa", new DTParameter("float", 1, null));
		this.put("lob", new DTParameter("float", 1, null));
		this.put("saturation", new DTParameter("float", 1, null));
	}
}
//DT_MODULE(1)
//
//#define DT_COLORCORRECTION_INSET 5
//#define DT_COLORCORRECTION_MAX 40.
//
//typedef struct dt_iop_colorcorrection_params_t
//{
//  float hia, hib, loa, lob, saturation;
//}
//dt_iop_colorcorrection_params_t;