package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Vibrance extends DTOperation {
	
	/**
	 * vibrance
	 */
	private static final long serialVersionUID = -4614199116892197214L;

	public Vibrance() {
		super("vibrance",true);
	}

	@Override
	public void addParam() {
		this.put("amount",new DTParameter("float",1,null));
	}
	
}
// DT_MODULE(2)
//typedef struct dt_iop_vibrance_params_t
//{
//  float amount;
//}