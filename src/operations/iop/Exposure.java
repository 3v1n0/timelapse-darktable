package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Exposure extends DTOperation {

	/**
	 * exposure definition
	 */
	private static final long serialVersionUID = -4705176132766205794L;

	public Exposure() {
		super("exposure");
		this.put("black",new DTParameter("float",1,null));
		this.put("exposure",new DTParameter("float",1,null));
		this.put("gain",new DTParameter("float",1,null));
	}
}

/*
typedef struct dt_iop_exposure_params_t
{
  float black, exposure, gain;
}
dt_iop_exposure_params_t;
*/
