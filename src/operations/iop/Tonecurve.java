package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Tonecurve extends DTOperation {

	/**
	 * Tonecurve
	 */
	private static final long serialVersionUID = 3132986542759292214L;

	public Tonecurve() {
		super("tonecurve",true);
	}

	@Override
	public void addParam() {
		if (this.version.equals("1")) {
			this.put("tonecurve_x",new DTParameter("float",6,null));
			this.put("tonecurve_y",new DTParameter("float",6,null));
			this.put("tonecurve_preset",new DTParameter("int",1,null,false));
		} else {
			int DT_IOP_TONECURVE_MAXNODES=20;
			this.put("tonecurve",new DTParameter("float",3*2*DT_IOP_TONECURVE_MAXNODES,null));
			this.put("tonecurve_nodes",new DTParameter("int",3,null));
			this.put("tonecurve_type",new DTParameter("int",3,null));	
			this.put("tonecurve_autoscale_ab",new DTParameter("int",1,null));
			this.put("tonecurve_preset",new DTParameter("int",1,null,false));
		}
	}
}

//typedef struct dt_iop_tonecurve_node_t
//{
//  float x;
//  float y;
//}
//dt_iop_tonecurve_node_t;
//
//typedef struct dt_iop_tonecurve_params_t
//{
//  dt_iop_tonecurve_node_t tonecurve[3][DT_IOP_TONECURVE_MAXNODES];  // three curves (L, a, b) with max number of nodes
//  int tonecurve_nodes[3];
//  int tonecurve_type[3];
//  int tonecurve_autoscale_ab;
//  int tonecurve_preset;
//}
//dt_iop_tonecurve_params_t;
//
//
//// parameter structure of tonecurve 1st version, needed for use in legacy_params()
//typedef struct dt_iop_tonecurve_1_params_t
//{
//  float tonecurve_x[6], tonecurve_y[6];
//  int tonecurve_preset;
//}
//dt_iop_tonecurve_1_params_t;
//
