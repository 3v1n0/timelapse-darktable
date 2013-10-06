package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Levels extends DTOperation {
	
	/**
	 * levels
	 */
	private static final long serialVersionUID = 3139986542759292214L;

	public Levels() {
		super("levels");
		this.put("levels",new DTParameter("float",3,null));
		this.put("levels_preset",new DTParameter("int",1,null));
	}
}
//typedef struct dt_iop_levels_params_t
//{
//  float levels[3];
//  int levels_preset;
//}