package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Basecurve extends DTOperation {

	/**
	 * basecurve definition
	 */
	private static final long serialVersionUID = 5459335021716901200L;

	public Basecurve() {
		super("basecurve", false);
	}

	@Override
	public void addParam() {
		
		if (this.version.equals("1")) {
			this.put("tonecurve_x", new DTParameter("float", 6, null));
			this.put("tonecurve_y", new DTParameter("float", 6, null));
			this.put("tonecurve_preset", new DTParameter("int", 1, null));
		} else {
			this.put("basecurve_t", new DTParameter("float", 2*3*20, null));
			this.put("basecurve_nodes", new DTParameter("int", 3, null));
			this.put("basecurve_type", new DTParameter("int", 3, null));
		}
		
	}
}

//#define MAXNODES 20
//
//DT_MODULE(2)
//
//typedef struct dt_iop_basecurve_node_t
//{
//  float x;
//  float y;
//}
//dt_iop_basecurve_node_t;
//
//typedef struct dt_iop_basecurve_params_t
//{
//  // three curves (c, ., .) with max number of nodes
//  // the other two are reserved, maybe we'll have cam rgb at some point.
//  dt_iop_basecurve_node_t basecurve[3][MAXNODES];
//  int basecurve_nodes[3];
//  int basecurve_type[3];
//}
//dt_iop_basecurve_params_t;
//
//typedef struct dt_iop_basecurve_params1_t
//{
//  float tonecurve_x[6], tonecurve_y[6];
//  int tonecurve_preset;
//}
//dt_iop_basecurve_params1_t;
