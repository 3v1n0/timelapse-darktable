package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Lens extends DTOperation {
	
	/**
	 * lens
	 */
	private static final long serialVersionUID = 3139886542759292214L;

	public Lens() {
		super("lens",false);
		this.put("modify_flags",new DTParameter("int",1,null));
		this.put("inverse",new DTParameter("int",1,null));
		this.put("scale",new DTParameter("float",1,null));
		this.put("crop",new DTParameter("float",1,null));
		this.put("focal",new DTParameter("float",1,null));
		this.put("aperture",new DTParameter("float",1,null));
		this.put("distance",new DTParameter("float",1,null));
		this.put("target_geom",new DTParameter("int",1,null));
		this.put("camera",new DTParameter("int",13,null));
		this.put("lens",new DTParameter("int",13,null));
		this.put("tca_override",new DTParameter("int",1,null));
		this.put("tca_r",new DTParameter("float",1,null));
		this.put("tca_b",new DTParameter("float",1,null));
	}
}

// 4*char = 1*word 
// char[52] => supposed int[13] 

//typedef struct dt_iop_lensfun_params_t
//{
//  int modify_flags;
//  int inverse;
//  float scale;
//  float crop;
//  float focal;
//  float aperture;
//  float distance;
//  lfLensType target_geom;
//  char camera[52];
//  char lens[52];
//  int tca_override;
//  float tca_r, tca_b;
//}
//dt_iop_lensfun_params_t;