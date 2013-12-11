package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Monochrome extends DTOperation {

	/**
	 * monochrome
	 */
	private static final long serialVersionUID = -396908062301212363L;

	public Monochrome() {
		super("monochrome",true);
		this.put("a",new DTParameter("float",1,null));
		this.put("b",new DTParameter("float",1,null));
		this.put("size",new DTParameter("float",1,null));
		this.put("highlights",new DTParameter("float",1,null));
	}
	
}
//typedef struct dt_iop_monochrome_params_t
//{
//  float a, b, size, highlights;
//}
//dt_iop_monochrome_params_t;