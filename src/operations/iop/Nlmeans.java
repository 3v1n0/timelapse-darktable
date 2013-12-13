package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Nlmeans extends DTOperation {

	/**
	 * nlmeans
	 */
	private static final long serialVersionUID = -3969080623012834663L;

	public Nlmeans() {
		super("nlmeans", true);
	}

	@Override
	public void addParam() {
		if (this.version.equals("1")) {
			this.put("luma", new DTParameter("float", 1, null));
			this.put("chroma", new DTParameter("float", 1, null));
		} else {
			this.put("radius", new DTParameter("float", 1, null));
			this.put("strength", new DTParameter("float", 1, null));
			this.put("luma", new DTParameter("float", 1, null));
			this.put("chroma", new DTParameter("float", 1, null));
		}
	}
}

// typedef struct dt_iop_nlmeans_params_v1_t
// {
// float luma;
// float chroma;
// }
// dt_iop_nlmeans_params_v1_t;
//
// typedef struct dt_iop_nlmeans_params_t
// {
// // these are stored in db.
// float radius;
// float strength;
// float luma;
// float chroma;
// }