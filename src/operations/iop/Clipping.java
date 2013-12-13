package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Clipping extends DTOperation {

	/**
	 * clipping
	 */
	private static final long serialVersionUID = 2201454626114491541L;

	public Clipping() {
		super("clipping", true);
	}

	@Override
	public void addParam() {
		if (this.version.equals("2") || this.version.equals("3")) {
			this.put("angle", new DTParameter("float", 1, null));
			this.put("cx", new DTParameter("float", 1, null));
			this.put("cy", new DTParameter("float", 1, null));
			this.put("cw", new DTParameter("float", 1, null));
			this.put("ch", new DTParameter("float", 1, null));
			this.put("k_h", new DTParameter("float", 1, null));
			this.put("k_v", new DTParameter("float", 1, null));
		} else if (this.version.equals("3")) {
			this.put("angle", new DTParameter("float", 1, null));
			this.put("cx", new DTParameter("float", 1, null));
			this.put("cy", new DTParameter("float", 1, null));
			this.put("cw", new DTParameter("float", 1, null));
			this.put("ch", new DTParameter("float", 1, null));
			this.put("k_h", new DTParameter("float", 1, null));
			this.put("k_v", new DTParameter("float", 1, null));
			this.put("kxa", new DTParameter("float", 1, null));
			this.put("kya", new DTParameter("float", 1, null));
			this.put("kxb", new DTParameter("float", 1, null));
			this.put("kyb", new DTParameter("float", 1, null));
			this.put("kxc", new DTParameter("float", 1, null));
			this.put("kyc", new DTParameter("float", 1, null));
			this.put("kxd", new DTParameter("float", 1, null));
			this.put("kyd", new DTParameter("float", 1, null));
			this.put("k_type", new DTParameter("int", 1, null));
			this.put("k_sym", new DTParameter("int", 1, null));
			this.put("k_apply", new DTParameter("int", 1, null));
			this.put("crop_auto", new DTParameter("int", 1, null));
		} else {
			this.put("angle", new DTParameter("float", 1, null));
			this.put("cx", new DTParameter("float", 1, null));
			this.put("cy", new DTParameter("float", 1, null));
			this.put("cw", new DTParameter("float", 1, null));
			this.put("ch", new DTParameter("float", 1, null));
			this.put("k_h", new DTParameter("float", 1, null));
			this.put("k_v", new DTParameter("float", 1, null));
			this.put("kxa", new DTParameter("float", 1, null));
			this.put("kya", new DTParameter("float", 1, null));
			this.put("kxb", new DTParameter("float", 1, null));
			this.put("kyb", new DTParameter("float", 1, null));
			this.put("kxc", new DTParameter("float", 1, null));
			this.put("kyc", new DTParameter("float", 1, null));
			this.put("kxd", new DTParameter("float", 1, null));
			this.put("kyd", new DTParameter("float", 1, null));
			this.put("k_type", new DTParameter("int", 1, null));
			this.put("k_sym", new DTParameter("int", 1, null));
			this.put("k_apply", new DTParameter("int", 1, null));
			this.put("crop_auto", new DTParameter("int", 1, null));
			this.put("ratio_n", new DTParameter("int", 1, null));
			this.put("ratio_d", new DTParameter("int", 1, null));
		}
	}
}

/*
 * size in .xmp 21 octet
 * 
 * typedef struct dt_iop_clipping_params_t { float angle, cx, cy, cw, ch, k_h,
 * k_v; float kxa, kya, kxb, kyb, kxc, kyc, kxd, kyd; int k_type, k_sym; int
 * k_apply, crop_auto; int ratio_n, ratio_d; } dt_iop_clipping_params_t;
 */

// int legacy_params (dt_iop_module_t *self, const void *const old_params, const
// int old_version, void *new_params, const int new_version)
// {
// if (new_version <= old_version) return 1;
// if (new_version != 5) return 1;
//
// dt_iop_clipping_params_t *n = (dt_iop_clipping_params_t *)new_params;
// if(old_version==2 && new_version == 5)
// {
// //old structure def
// typedef struct old_params_t
// {
// float angle, cx, cy, cw, ch, k_h, k_v;
// }
// old_params_t;
//
// old_params_t *o = (old_params_t *)old_params;
//
// uint32_t intk = *(uint32_t *)&o->k_h;
// int is_horizontal;
// if(intk & 0x40000000u) is_horizontal = 1;
// else is_horizontal = 0;
// intk &= ~0x40000000;
// float floatk = *(float *)&intk;
// if(is_horizontal)
// {
// n->k_h = floatk;
// n->k_v = 0.0;
// }
// else
// {
// n->k_h = 0.0;
// n->k_v = floatk;
// }
//
// n->angle=o->angle, n->cx=o->cx, n->cy=o->cy, n->cw=o->cw, n->ch=o->ch;
// n->kxa = n->kxd = 0.2f;
// n->kxc = n->kxb = 0.8f;
// n->kya = n->kyb = 0.2f;
// n->kyc = n->kyd = 0.8f;
// if (n->k_h ==0 && n->k_v==0) n->k_type = 0;
// else n->k_type = 4;
// n->k_sym = 0;
// n->k_apply = 0;
// n->crop_auto = 1;
//
// // will be computed later, -2 here is used to detect uninitialized value, -1
// is already used for no clipping.
// n->ratio_d = n->ratio_n = -2;
// }
// if(old_version==3 && new_version == 5)
// {
// //old structure def
// typedef struct old_params_t
// {
// float angle, cx, cy, cw, ch, k_h, k_v;
// }
// old_params_t;
//
// old_params_t *o = (old_params_t *)old_params;
//
// n->angle=o->angle, n->cx=o->cx, n->cy=o->cy, n->cw=o->cw, n->ch=o->ch;
// n->k_h=o->k_h, n->k_v=o->k_v;
// n->kxa = n->kxd = 0.2f;
// n->kxc = n->kxb = 0.8f;
// n->kya = n->kyb = 0.2f;
// n->kyc = n->kyd = 0.8f;
// if (n->k_h ==0 && n->k_v==0) n->k_type = 0;
// else n->k_type = 4;
// n->k_sym = 0;
// n->k_apply = 0;
// n->crop_auto = 1;
//
// // will be computed later, -2 here is used to detect uninitialized value, -1
// is already used for no clipping.
// n->ratio_d = n->ratio_n = -2;
// }
// if(old_version==4 && new_version == 5)
// {
// typedef struct old_params_t
// {
// float angle, cx, cy, cw, ch, k_h, k_v;
// float kxa, kya, kxb, kyb, kxc, kyc, kxd, kyd;
// int k_type, k_sym;
// int k_apply, crop_auto;
// }
// old_params_t;
//
// old_params_t *o = (old_params_t *)old_params;
//
// n->angle=o->angle, n->cx=o->cx, n->cy=o->cy, n->cw=o->cw, n->ch=o->ch;
// n->k_h=o->k_h, n->k_v=o->k_v;
// n->kxa=o->kxa, n->kxb=o->kxb, n->kxc=o->kxc, n->kxd=o->kxd;
// n->kya=o->kya, n->kyb=o->kyb, n->kyc=o->kyc, n->kyd=o->kyd;
// n->k_type = o->k_type;
// n->k_sym = o->k_sym;
// n->k_apply = o->k_apply;
// n->crop_auto = o->crop_auto;
//
// // will be computed later, -2 here is used to detect uninitialized value, -1
// is already used for no clipping.
// n->ratio_d = n->ratio_n = -2;
// }
//
// return 0;
// }

