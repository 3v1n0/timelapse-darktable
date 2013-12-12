package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Atrous extends DTOperation {
	/**
	 * atrous
	 */
	private static final long serialVersionUID = -8173842643135545850L;

	// TODO : manage history version (last version : 3*8, first 3*6)
	public Atrous() {
		super("atrous",false);
	}

	@Override
	public void addParam() {
		this.put("octaves",new DTParameter("int",1,null,false)); // not interpolatable
		this.put("x",new DTParameter("float",5*6,null));
		this.put("y",new DTParameter("float",5*6,null));		
	}
}
//
//#define BANDS 6
//#define MAX_NUM_SCALES 8 // 2*2^(i+1) + 1 = 1025px support for i = 8
//#define RES 64
//typedef enum atrous_channel_t
//{
//  atrous_L    = 0,  // luminance boost
//  atrous_c    = 1,  // chrominance boost
//  atrous_s    = 2,  // edge sharpness
//  atrous_Lt   = 3,  // luminance noise threshold
//  atrous_ct   = 4,  // chrominance noise threshold
//  atrous_none = 5
//}
//atrous_channel_t;
//
//typedef struct dt_iop_atrous_params_t
//{
//  int32_t octaves;
//  float x[atrous_none][BANDS], y[atrous_none][BANDS];
//}