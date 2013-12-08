package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Vignette extends DTOperation {
	
	/**
	 * vignette
	 */
	private static final long serialVersionUID = -4614199112042197214L;

	public Vignette() {
		super("vignette");
		if (this.version.equals("3")) {
			this.put("scale",new DTParameter("float",1,null));
			this.put("falloff_scale",new DTParameter("float",1,null));
			this.put("brightness",new DTParameter("float",1,null));
			this.put("saturation",new DTParameter("float",1,null));
			this.put("center",new DTParameter("float",2,null));
			this.put("autoratio",new DTParameter("int",1,null));
			this.put("whratio",new DTParameter("float",1,null));
			this.put("shape",new DTParameter("float",1,null));
			this.put("dithering",new DTParameter("int",1,null));
		}
		else {
			this.printVersionError();
		}
		// version 1 & 2 to be implemented...
	}
	
}

// DT_MODULE(3)

// version 3:
//typedef struct dt_iop_vignette_params_t
//{
//float scale;			// 0 - 100 Inner radius, percent of largest image dimension
//float falloff_scale;		// 0 - 100 Radius for falloff -- outer radius = inner radius + falloff_scale
//float brightness;		// -1 - 1 Strength of brightness reduction
//float saturation;		// -1 - 1 Strength of saturation reduction
//dt_iop_vector_2d_t center;	// Center of vignette
//gboolean autoratio;		//
//float whratio;		// 0-1 = width/height ratio, 1-2 = height/width ratio + 1
//float shape;
//int dithering;                // if and how to perform dithering
//}
//dt_iop_vignette_params_t;

//
//typedef struct dt_iop_dvector_2d_t
//{
//  double x;
//  double y;
//} dt_iop_dvector_2d_t;
//
//typedef struct dt_iop_fvector_2d_t
//{
//  float x;
//  float y;
//} dt_iop_vector_2d_t;
//
//typedef struct dt_iop_vignette_params1_t
//{
//  double scale;              // 0 - 100 Radie
//  double falloff_scale;   // 0 - 100 Radie for falloff inner radie of falloff=scale and outer=scale+falloff_scale
//  double strength;         // 0 - 1 strength of effect
//  double uniformity;       // 0 - 1 uniformity of center
//  double bsratio;            // -1 - +1 ratio of brightness/saturation effect
//  gboolean invert_falloff;
//  gboolean invert_saturation;
//  dt_iop_dvector_2d_t center;            // Center of vignette
//}
//dt_iop_vignette_params1_t;
//
//typedef struct dt_iop_vignette_params2_t
//{
//  float scale;			// 0 - 100 Inner radius, percent of largest image dimension
//  float falloff_scale;		// 0 - 100 Radius for falloff -- outer radius = inner radius + falloff_scale
//  float brightness;		// -1 - 1 Strength of brightness reduction
//  float saturation;		// -1 - 1 Strength of saturation reduction
//  dt_iop_vector_2d_t center;	// Center of vignette
//  gboolean autoratio;		//
//  float whratio;		// 0-1 = width/height ratio, 1-2 = height/width ratio + 1
//  float shape;
//}
//dt_iop_vignette_params2_t;

