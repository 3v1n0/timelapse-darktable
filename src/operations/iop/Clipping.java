package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Clipping extends DTOperation {
	
	/**
	 * clipping
	 */
	private static final long serialVersionUID = 2201454626114491541L;

	public Clipping() {
		super("clipping");
		this.put("angle",new DTParameter("float",1,null));
		this.put("cx",new DTParameter("float",1,null));
		this.put("cy",new DTParameter("float",1,null));
		this.put("cw",new DTParameter("float",1,null));
		this.put("ch",new DTParameter("float",1,null));
		this.put("k_h",new DTParameter("float",1,null));
		this.put("k_v",new DTParameter("float",1,null));
		this.put("kxa",new DTParameter("float",1,null));
		this.put("kya",new DTParameter("float",1,null));
		this.put("kxb",new DTParameter("float",1,null));
		this.put("kyb",new DTParameter("float",1,null));
		this.put("kxc",new DTParameter("float",1,null));
		this.put("kyc",new DTParameter("float",1,null));
		this.put("kxd",new DTParameter("float",1,null));
		this.put("kyd",new DTParameter("float",1,null));
		this.put("k_type",new DTParameter("int",1,null));
		this.put("k_sym",new DTParameter("int",1,null));
		this.put("k_apply",new DTParameter("int",1,null));
		this.put("crop_auto",new DTParameter("int",1,null));
		this.put("ratio_n",new DTParameter("int",1,null));
		this.put("ratio_d",new DTParameter("int",1,null));
	}
}

/*
size in .xmp 21 octet

typedef struct dt_iop_clipping_params_t
{
  float angle, cx, cy, cw, ch, k_h, k_v;
  float kxa, kya, kxb, kyb, kxc, kyc, kxd, kyd;
  int k_type, k_sym;
  int k_apply, crop_auto;
  int ratio_n, ratio_d;
}
dt_iop_clipping_params_t;

typedef struct dt_iop_clipping_data_t
{
  float angle;              // rotation angle
  float aspect;             // forced aspect ratio
  float m[4];               // rot matrix
  float ki_h, k_h;          // keystone correction, ki and corrected k
  float ki_v, k_v;          // keystone correction, ki and corrected k
  float tx, ty;             // rotation center
  float cx, cy, cw, ch;     // crop window
  float cix, ciy, ciw, cih; // crop window on roi_out 1.0 scale
  uint32_t all_off;         // 1: v and h off, else one of them is used
  uint32_t flags;           // flipping flags
  uint32_t flip;            // flipped output buffer so more area would fit.

  float k_space[4];         //space for the "destination" rectangle of the keystone quadrilatere
  float kxa, kya, kxb, kyb, kxc, kyc, kxd, kyd; //point of the "source" quadrilatere (modified if keystone is not "full")
  float a,b,d,e,g,h; //value of the transformation matrix (c=f=0 && i=1)
  int k_apply;
  int crop_auto;
  float enlarge_x, enlarge_y;
}
dt_iop_clipping_data_t;
 * */
