package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Gamma extends DTOperation {

	/**
	 * gamma
	 */
	private static final long serialVersionUID = -4702179032766205794L;

	public Gamma() {
		super("gamma", true);
	}

	@Override
	public void addParam() {
		this.put("gamma", new DTParameter("float", 1, null));
		this.put("linear", new DTParameter("float", 1, null));
	}
}
// typedef struct dt_iop_gamma_params_t
// {
// float gamma, linear;
// }
// dt_iop_gamma_params_t;
