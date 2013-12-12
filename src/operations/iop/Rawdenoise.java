package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Rawdenoise extends DTOperation {
	
	/**
	 * rawdenoise
	 */
	private static final long serialVersionUID = 3132986542759292214L;

	public Rawdenoise() {
		super("rawdenoise",true);
	}

	@Override
	public void addParam() {
		this.put("threshold",new DTParameter("float",1,null));
	}
}

//typedef struct dt_iop_rawdenoise_params_t
//{
//  float threshold;
//}
//dt_iop_rawdenoise_params_t;