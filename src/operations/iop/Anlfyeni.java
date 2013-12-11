package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Anlfyeni extends DTOperation {
	
	/**
	 * anlfyeni
	 */
	private static final long serialVersionUID = 3139986542759292214L;

	public Anlfyeni() {
		super("anlfyeni",true);
		this.put("alpha",new DTParameter("float",1,null));
		this.put("scale",new DTParameter("float",1,null));
		this.put("strength",new DTParameter("float",1,null));
	}
	
	void updateVersion(String version) {}
}
//typedef struct dt_iop_anlfyeni_params_t
//{
//  float alpha, scale, strength;
//}
//dt_iop_anlfyeni_params_t;
