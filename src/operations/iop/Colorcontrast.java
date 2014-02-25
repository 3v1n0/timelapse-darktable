package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Colorcontrast extends DTOperation {

	/**
	 * lens
	 */
	private static final long serialVersionUID = 3139886542746292214L;

	public Colorcontrast() {
		super("colorcontrast");
	}

	@Override
	public void addParam() {
		this.put("a_steepness", new DTParameter("float", 1, null));
		this.put("a_offset", new DTParameter("float", 1, null));
		this.put("b_steepness", new DTParameter("float", 1, null));
		this.put("b_offset", new DTParameter("float", 1, null));
	}
}

//typedef struct dt_iop_colorcontrast_data_t
//{
//  // this is stored in the pixelpipeline after a commit (not the db),
//  // you can do some precomputation and get this data in process().
//  // stored in piece->data
//  float a_steepness;
//  float a_offset;
//  float b_steepness;
//  float b_offset;
//}
//dt_iop_colorcontrast_data_t;