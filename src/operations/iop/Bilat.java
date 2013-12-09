package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Bilat extends DTOperation {
	
	/**
	 * bilat
	 */
	private static final long serialVersionUID = 3139986542759292214L;

	public Bilat() {
		super("bilat");
		this.put("sigma_r",new DTParameter("float",1,null));
		this.put("sigma_s",new DTParameter("float",1,null));
		this.put("detail",new DTParameter("float",1,null));
	}
}
//typedef struct dt_iop_bilat_params_t
//{
//  float sigma_r;
//  float sigma_s;
//  float detail;
//}
//dt_iop_bilat_params_t;
