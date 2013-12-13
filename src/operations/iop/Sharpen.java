package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Sharpen extends DTOperation {

	/**
	 * sharpen
	 */
	private static final long serialVersionUID = -3052531236077147121L;

	public Sharpen() {
		super("sharpen", true);
	}

	@Override
	public void addParam() {
		this.put("radius", new DTParameter("float", 1, null));
		this.put("amount", new DTParameter("float", 1, null));
		this.put("threshold", new DTParameter("float", 1, null));
	}
}
/*
 * typedef struct dt_iop_sharpen_params_t { float radius, amount, threshold; }
 * dt_iop_sharpen_params_t;
 */