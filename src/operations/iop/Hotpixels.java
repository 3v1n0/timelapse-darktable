package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Hotpixels extends DTOperation {
	
	/**
	 * hotpixels
	 */
	private static final long serialVersionUID = -1852564851363812265L;

	public Hotpixels() {
		super("hotpixels",false);
		this.put("strength",new DTParameter("float",1,null));
		this.put("threshold",new DTParameter("float",1,null));
		this.put("markfixed",new DTParameter("boolean",1,null));
		this.put("permissive",new DTParameter("boolean",1,null));
	}
}
//typedef struct dt_iop_hotpixels_params_t
//{
//  float strength;
//  float threshold;
//  gboolean markfixed;
//  gboolean permissive;
//}