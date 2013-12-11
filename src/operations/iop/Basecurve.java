package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Basecurve extends DTOperation {
	
	/**
	 * basecurve definition
	 */
	private static final long serialVersionUID = 5459335021716901200L;

	public Basecurve() {
		super("basecurve",false);
		this.put("basecurve",new DTParameter("float",3*2*20,null));
		this.put("basecurve_nodes",new DTParameter("int",3,null));
		this.put("basecurve_type",new DTParameter("int",3,null));
	}
}
/*

typedef struct dt_iop_basecurve_node_t
{
  float x;
  float y;
}
dt_iop_basecurve_node_t;

typedef struct dt_iop_basecurve_params_t
{
  // three curves (c, ., .) with max number of nodes
  // the other two are reserved, maybe we'll have cam rgb at some point.
  dt_iop_basecurve_node_t basecurve[3][MAXNODES]; // MAXNODES=20
  int basecurve_nodes[3];
  int basecurve_type[3];
}
dt_iop_basecurve_params_t;

*/