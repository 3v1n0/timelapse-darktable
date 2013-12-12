package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Spots extends DTOperation {
	
	/**
	 * spots
	 */
	private static final long serialVersionUID = 3132986542759292214L;

	public Spots() {
		super("spots",false);
	}

	@Override
	public void addParam() {
		this.put("num_spots",new DTParameter("float",1,null));
		this.put("spot",new DTParameter("float",32*5,null));
	}
}
//typedef struct spot_t
//{
//  // position of the spot
//  float x, y;
//  // position to clone from
//  float xc, yc;
//  float radius;
//}
//spot_t;
//
//typedef struct dt_iop_spots_params_t
//{
//  int num_spots;
//  spot_t spot[32];
//}
//dt_iop_spots_params_t;