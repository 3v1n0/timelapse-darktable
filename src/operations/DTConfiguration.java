package operations;

import java.util.HashMap;

import xmp.XmpDTConf;

public class DTConfiguration extends HashMap<String,DTOperation> implements Comparable<DTConfiguration> {
	
	/**
	 * DTConfiguration is the object to describe entire picture settings
	 * it should contains all information included in the XMP file
	 */
	private static final long serialVersionUID = -2007602055999190529L;
	
	public Integer index;  // numbering of file (get in file name)
	public String srcFile; // source RAW file
	public XmpDTConf xmpConf;
	
	public DTConfiguration(String xmpPath) {
		super();

		// read xmp file 
		xmpConf = new XmpDTConf(xmpPath);
		
		// source File
		srcFile = xmpConf.srcFile;

		// index of the File : Get first number between two characters which are not digit
		index = xmpConf.index;
		
		// read operations and their parameterization (add operations field)
		this.readAllOperations(this.xmpConf);
		
	}
	
	public DTConfiguration(DTConfiguration dtc) {
		super();
		srcFile = dtc.srcFile;
		index = dtc.index;
		xmpConf = dtc.xmpConf;
		this.readAllOperations(this.xmpConf);
	}
	
	public void readAllOperations(XmpDTConf xmpConf) {
		/** Read all operations of history list **/
		for(int i = 0; i < xmpConf.histOp.size(); i++) {
			DTOperation op = DTOperation.readOperation(xmpConf.histOp.get(i), xmpConf.histVer.get(i), xmpConf.histEna.get(i), xmpConf.histPar.get(i), xmpConf.blopVer.get(i), xmpConf.blopPar.get(i), xmpConf.multPrio.get(i), xmpConf.multName.get(i));
			if(op != null) {
				try {
					// add operation to HashMap Key=name+multiName
					this.put(op.name+op.multiName,op);					
				} catch (Exception e) {
					System.out.println(e);
				}
					
			}
		}
	}
	
	public void updateXmpConf(String outFolder) {
		/** Replace operations of history list **/
		for(int i = 0; i < this.xmpConf.histOp.size(); i++) {
			// for each operation, write parameter value from DTOperation
			String opkey = xmpConf.histOp.get(i)+xmpConf.multName.get(i);
			String xmpConfUpd = DTOperation.writeParams(this.get(opkey));
			this.xmpConf.histPar.set(i, xmpConfUpd);
		}
		this.xmpConf.srcFile = this.srcFile; // update xmpConf srcFile
		this.xmpConf.write(outFolder); // update xmpConf in coherency & write XMP file
	}

	public double getOpParValue(String operation,String parameter,Integer index){
		// return operation / parameter / value[idx]
		try{
			DTValue v = (DTValue) this.get(operation).get(parameter).get("value");
			return (Double) v.get(index);
		} catch (Exception nulException) {
			//System.out.println("null exception: "+operation+" "+parameter);
			System.err.println("Interpolation fail, check source XMP files for : "+operation);
			return (Double) null;
		}
		
	}
	

	public void setOpParValue(String operation,String parameter,Integer index,double value){
		// set operation / parameter / value[idx]
		DTValue v = (DTValue) this.get(operation).get(parameter).get("value");
		v.put(index,value);
		this.get(operation).get(parameter).put("value",v);
	}

	@Override
	public int compareTo(DTConfiguration arg0) {
		return this.index.compareTo(arg0.index);
	}
	
}
