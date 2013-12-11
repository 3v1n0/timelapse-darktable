package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Shadhi extends DTOperation {
	
	/**
	 * shadhi
	 */
	private static final long serialVersionUID = -6380779433056726371L;

	public Shadhi() {
		super("shadhi",true);
		this.put("order",new DTParameter("int",1,null,false));
		this.put("radius",new DTParameter("float",1,null));
		this.put("shadows",new DTParameter("float",1,null));
		this.put("reserved1",new DTParameter("float",1,null));
		this.put("highlights",new DTParameter("float",1,null));
		this.put("reserved2",new DTParameter("float",1,null));
		this.put("compress",new DTParameter("float",1,null));
		this.put("shadows_ccorrect",new DTParameter("float",1,null));
		this.put("highlights_ccorrect",new DTParameter("float",1,null));
	}
}

/* from darktable
typedef struct dt_iop_shadhi_params_t
{
  dt_gaussian_order_t order;
  float radius;
  float shadows;
  float reserved1;
  float highlights;
  float reserved2;
  float compress;
  float shadows_ccorrect;
  float highlights_ccorrect;
}
dt_iop_shadhi_params_t;

typedef enum dt_gaussian_order_t
{
  DT_IOP_GAUSSIAN_ZERO = 0,
  DT_IOP_GAUSSIAN_ONE = 1,
  DT_IOP_GAUSSIAN_TWO = 2
}
dt_gaussian_order_t;


typedef struct dt_iop_shadhi_data_t
{
  dt_gaussian_order_t order;
  float radius;
  float shadows;
  float highlights;
  float compress;
  float shadows_ccorrect;
  float highlights_ccorrect;
}
dt_iop_shadhi_data_t;
 * */
