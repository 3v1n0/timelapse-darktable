package operations;

import java.util.HashMap;

import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

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
	public Double luminance; // initial luminance after first interpolation
	public Double luminanceDeflick; // luminance after deflickering
	
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
			System.err.println("getOpParValue fail, : "+operation+" "+parameter);
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

	public void deflick(String outFolder, PolynomialSplineFunction calibLumDeltaEV) {
		// change operations/iop/exposure parameter to deflick (first filter => "exposure ")
		double EV = getOpParValue("exposure ", "exposure", 0);
		
		// compute target EV for deflick
		//double evDeflick = computeEVtarget(EV, this.luminance, this.luminanceDeflick);
		double evDeflick = calibLumDeltaEV.value((this.luminanceDeflick/this.luminance-1.0d));
				
		// update XMP configuration
		setOpParValue("exposure ", "exposure", 0, EV+evDeflick);
		this.updateXmpConf(outFolder);
	}
	
	@SuppressWarnings("unused")
	private static double computeEVtarget(double EV,double lum,double lumTarget){
		double EVtarget;
		// relation between exposure and luminance
		// (get manually with a calibration script)
		// Let define the calibration function
		// f: EV -> (1+EV) if EV>0 | 1/(1-EV) if EV<0
		// lum = lum0*f(EV)  
		//
		// Target for deflick:
		// lumTarget = lum0*f(EVtarget)
		//
		// elimination of lum0:
		// lumTarget = lum*f(EVtarget)/f(EV)
		// solving the problem EVtarget = f(EV,lum,lumTarget)
		// invf : r -> (1-r) if r>1 | 1-1/r if r<1 
		// EVtarget = invf(f(EV)*lumTarget/lum)
		
		EVtarget = evFactor(lumFactor(EV)*lumTarget/lum);
		
		return EVtarget;
	}
	
	private static double lumFactor(double EV){
		double lumFactor;
		// Let define the calibration function
		// f: EV -> (1+EV) if EV>0 | 1/(1-EV) if EV<0
		if (EV>=0) {
			lumFactor = 1+EV;
		} else {
			lumFactor = 1/(1-EV);
		}
		return lumFactor;
	}
	
	private static double evFactor(double lumRatio){
		double evFactor;
		// Let define the inverse of calibration function
		// invf : r -> (1-r) if r>1 | 1-1/r if r<1 
		if (lumRatio>=1) {
			evFactor = 1-lumRatio;
		} else {
			evFactor = 1-1/lumRatio;
		}
		return evFactor;
	}
	
}
