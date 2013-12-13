package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Splittoning extends DTOperation {

	/**
	 * splittoning
	 */
	private static final long serialVersionUID = 3132986542759292214L;

	public Splittoning() {
		super("splittoning", true);
	}

	@Override
	public void addParam() {
		this.put("shadow_hue", new DTParameter("float", 1, null));
		this.put("shadow_saturation", new DTParameter("float", 1, null));
		this.put("highlight_hue", new DTParameter("float", 1, null));
		this.put("highlight_saturation", new DTParameter("float", 1, null));
		this.put("balance", new DTParameter("float", 1, null));
		this.put("compress", new DTParameter("float", 1, null));
	}
}

// typedef struct dt_iop_splittoning_params_t
// {
// float shadow_hue;
// float shadow_saturation;
// float highlight_hue;
// float highlight_saturation;
// float balance; // center luminance of gradient
// float compress; // Compress range
// }
// dt_iop_splittoning_params_t;
