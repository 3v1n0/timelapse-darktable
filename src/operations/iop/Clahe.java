package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Clahe extends DTOperation {

	/**
	 * clahe
	 */
	private static final long serialVersionUID = 9116309930302462999L;

	public Clahe() {
		super("clahe",true);
		this.put("radius",new DTParameter("double",1,null));
		this.put("slope",new DTParameter("double",1,null));
	}	
}
//typedef struct dt_iop_rlce_params_t
//{
//  double radius;
//  double slope;
//}