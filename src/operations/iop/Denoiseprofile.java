package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Denoiseprofile extends DTOperation {

	/**
	 * denoiseprofile
	 */
	private static final long serialVersionUID = -7789719201477231540L;

	public Denoiseprofile() {
		super("denoiseprofile", true);
	}

	@Override
	public void addParam() {
		this.put("radius", new DTParameter("float", 1, null));
		this.put("strength", new DTParameter("float", 1, null));
		this.put("a", new DTParameter("float", 3, null));
		this.put("b", new DTParameter("float", 3, null));
		this.put("mode", new DTParameter("int", 1, null, false));
	}
}

// typedef struct dt_iop_denoiseprofile_params_t
// {
// float radius; // search radius
// float strength; // noise level after equalization
// float a[3], b[3]; // fit for poissonian-gaussian noise per color channel.
// uint32_t mode; // switch between nlmeans and wavelets
// }
// dt_iop_denoiseprofile_params_t;

