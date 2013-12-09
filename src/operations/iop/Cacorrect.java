package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Cacorrect extends DTOperation {

	/**
	 * cacorrect
	 */
	private static final long serialVersionUID = -3969080623012834663L;

	public Cacorrect() {
		super("cacorrect");
		this.put("keep",new DTParameter("int",1,null));
	}
	
}
//typedef struct dt_iop_cacorrect_params_t
//{
//  int keep;
//}
//dt_iop_cacorrect_params_t;
