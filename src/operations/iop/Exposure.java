package operations.iop;

import operations.DTOperation;
import operations.DTParameter;
import operations.Versionable;

public class Exposure extends DTOperation implements Versionable {

	/**
	 * exposure definition
	 */
	private static final long serialVersionUID = -4705176132766205794L;

	public Exposure() {
		super("exposure",true);
		this.put("black",new DTParameter("float",1,null));
		this.put("exposure",new DTParameter("float",1,null));
		this.put("gain",new DTParameter("float",1,null));
	}

	@Override
	public void updateVersion(String version) {
		System.out.println("success");
	}
	
}

/*
typedef struct dt_iop_exposure_params_t
{
  float black, exposure, gain;
}
dt_iop_exposure_params_t;
*/
