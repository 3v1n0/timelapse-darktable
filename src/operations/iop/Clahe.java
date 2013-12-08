package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Clahe extends DTOperation {

	/**
	 * clahe
	 */
	private static final long serialVersionUID = 9116309930302462999L;

	public Clahe() {
		super("clahe");
		if (this.version.equals("1")) {
			this.put("radius",new DTParameter("double",1,null));
			this.put("slope",new DTParameter("double",1,null));
		}
		else {
			this.printVersionError();
		}
	}	
}
//typedef struct dt_iop_rlce_params_t
//{
//  double radius;
//  double slope;
//}