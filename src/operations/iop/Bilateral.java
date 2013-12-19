package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Bilateral extends DTOperation {

	/**
	 * bilateral
	 */
	private static final long serialVersionUID = 3139986512349292214L;

	public Bilateral() {
		super("bilateral", true);
	}

	@Override
	public void addParam() {
		this.put("sigma", new DTParameter("float", 5, null));
	}
}

//DT_MODULE(1)
//
//typedef struct dt_iop_bilateral_params_t
//{
//  // standard deviations of the gauss to use for blurring in the dimensions x,y,r,g,b (or L*,a*,b*)
//  float sigma[5];
//}
//dt_iop_bilateral_params_t;

