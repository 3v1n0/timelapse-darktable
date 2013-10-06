package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Colorzones extends DTOperation {
	/**
	 * colorzones
	 */
	private static final long serialVersionUID = -8173842643135545850L;

	// TODO : manage history version (last version : 3*8, first 3*6)
	public Colorzones() {
		super("colorzones");
		this.put("channel",new DTParameter("int",1,null));
		this.put("equalizer_x",new DTParameter("float",3*8,null));
		this.put("equalizer_y",new DTParameter("float",3*8,null));
//		this.put("strength",new DTParameter("float",1,null));
	}
}

//#define DT_IOP_COLORZONES_BANDS 8
//#define DT_IOP_COLORZONES1_BANDS 6
//typedef struct dt_iop_colorzones_params_t
//{
//  int32_t channel;
//  float equalizer_x[3][DT_IOP_COLORZONES_BANDS], equalizer_y[3][DT_IOP_COLORZONES_BANDS];
//  float strength;
//}
//dt_iop_colorzones_params_t;
//
//typedef struct dt_iop_colorzones_params2_t
//{
//  int32_t channel;
//  float equalizer_x[3][DT_IOP_COLORZONES_BANDS], equalizer_y[3][DT_IOP_COLORZONES_BANDS];
//}
//dt_iop_colorzones_params2_t;
//
//typedef struct dt_iop_colorzones_params1_t
//{
//  int32_t channel;
//  float equalizer_x[3][DT_IOP_COLORZONES1_BANDS], equalizer_y[3][DT_IOP_COLORZONES1_BANDS];
//}
//dt_iop_colorzones_params1_t;
