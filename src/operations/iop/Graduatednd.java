package operations.iop;

import operations.DTOperation;
import operations.DTParameter;

public class Graduatednd extends DTOperation {
	
	/**
	 * graduatednd
	 */
	private static final long serialVersionUID = 1669798107487955526L;
	public Graduatednd() {
		super("graduatednd",true);
	}
	@Override
	public void addParam() {
		this.put("density",new DTParameter("float",1,null));
		this.put("compression",new DTParameter("float",1,null));
		this.put("rotation",new DTParameter("float",1,null));
		this.put("offset",new DTParameter("float",1,null));
		this.put("hue",new DTParameter("float",1,null));
		this.put("saturation",new DTParameter("float",1,null));
	}
}
//typedef struct dt_iop_graduatednd_params_t
//{
//  float density;
//  float compression;
//  float rotation;
//  float offset;
//  float hue;
//  float saturation;
//}