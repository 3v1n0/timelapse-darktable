package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Temperature extends DTOperation {
	
	/**
	 * Exposure definition
	 */
	private static final long serialVersionUID = 337000576097680999L;

	public Temperature() {
		super("temperature",true);
		this.put("temp_out",new DTParameter("float",1,null));
		this.put("coeffs",new DTParameter("float",3,null));
	}
	
	
}


/*
typedef struct dt_iop_temperature_params_t
{
  float temp_out;
  float coeffs[3];
}
dt_iop_temperature_params_t;
*/