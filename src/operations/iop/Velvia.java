package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Velvia extends DTOperation {
	
	/**
	 * velvia
	 */
	private static final long serialVersionUID = 4772494720136859836L;

	public Velvia() {
		super("velvia");
		if (this.version.equals("1")) {
			this.put("saturation",new DTParameter("float",1,null));
			this.put("vibrance",new DTParameter("float",1,null));
			this.put("luminance",new DTParameter("float",1,null));
			this.put("clarity",new DTParameter("float",1,null));
		} else if (this.version.equals("2")) {
			this.put("strength",new DTParameter("float",1,null));
			this.put("bias",new DTParameter("float",1,null));			
		} else {
			this.printVersionError();
		}
	}
}
//typedef struct dt_iop_velvia_params_t
//{
//  float strength;
//  float bias;
//}
//dt_iop_velvia_params_t;
//
///* legacy version 1 params */
//typedef struct dt_iop_velvia_params1_t
//{
//  float saturation;
//  float vibrance;
//  float luminance;
//  float clarity;
//}