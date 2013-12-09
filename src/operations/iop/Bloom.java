package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Bloom extends DTOperation {
	
	/**
	 * bloom
	 */
	private static final long serialVersionUID = 3139986542759292214L;

	public Bloom() {
		super("bloom");
		this.put("size",new DTParameter("float",1,null));
		this.put("threshold",new DTParameter("float",1,null));
		this.put("strength",new DTParameter("float",1,null));
	}
}
//typedef struct dt_iop_bloom_data_t
//{
//  float size;
//  float threshold;
//  float strength;
//}
//dt_iop_bloom_data_t;
