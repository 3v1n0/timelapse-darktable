package operations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import operations.iop.Exposure;

import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import xmp.XmpDTConf;

public class DTConfiguration extends HashMap<String, DTOperation> implements
		Comparable<DTConfiguration> {

	/**
	 * DTConfiguration is the object to describe entire picture settings it
	 * should contains all information included in the XMP file
	 */
	private static final long serialVersionUID = -2007602055999190529L;

	public Integer index; // numbering of file (get in file name)
	public Integer rating; // star rating
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

		// index of the File : Get first number between two characters which are
		// not digit
		index = xmpConf.index;
		
		// Star rating
		rating = xmpConf.rating;

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
		for (int i = 0; i < xmpConf.histOp.size(); i++) {
			DTOperation op = DTOperation.readOperation(xmpConf.histOp.get(i),
					xmpConf.histVer.get(i), xmpConf.histEna.get(i),
					xmpConf.histPar.get(i), xmpConf.blopVer.get(i),
					xmpConf.blopPar.get(i), xmpConf.multPrio.get(i),
					xmpConf.multName.get(i));
			if (op != null) {
				try {
					// add operation to HashMap Key=name+multiName
					this.put(op.name + op.multiName, op);
				} catch (Exception e) {
					System.out.println(e);
				}

			}
		}
	}

	public void updateXmpConf(String outFolder) {
		/** Replace operations of history list **/
		for (int i = 0; i < this.xmpConf.histOp.size(); i++) {
			// for each operation, write parameter value from DTOperation
			String opkey = xmpConf.histOp.get(i) + xmpConf.multName.get(i);
			DTOperation dtOp = this.get(opkey);
			String xmpConfUpd = DTOperation.writeParams(dtOp);
			this.xmpConf.histPar.set(i, xmpConfUpd);
			this.xmpConf.histEna.set(i, DTOperation.writeEnable(dtOp));
		}
		this.xmpConf.srcFile = this.srcFile; // update xmpConf srcFile
		this.xmpConf.rating = this.rating; // update xmpConf rating
		this.xmpConf.ratingStr = ""+this.rating; // update xmpConf ratingStr
		this.xmpConf.write(outFolder); // update xmpConf in coherency & write
										// XMP file
	}

	public double getOpParValue(String operation, String parameter,
			Integer index) {
		// return operation / parameter / value[idx]
		try {
			DTValue v = (DTValue) this.get(operation).get(parameter)
					.get("value");
			return (Double) v.get(index);
		} catch (Exception nulException) {
			// System.out.println("null exception: "+operation+" "+parameter);
			System.err.println("getOpParValue failed on " + this.srcFile
					+ " for " + operation + " / " + parameter);
			return (Double) null;
		}

	}

	public void setOpParValue(String operation, String parameter,
			Integer index, double value) {
		// set operation / parameter / value[idx]
		DTValue v = (DTValue) this.get(operation).get(parameter).get("value");
		v.put(index, value);
		this.get(operation).get(parameter).put("value", v);
	}
	
	public void setOpEnable(String operation, String multiName, boolean isEnabled) {
		// set operation / multiname / is enabled flag
		DTOperation op = this.get(operation+multiName);
		op.enabled=isEnabled;
	}
	
	public void setOpEnable(String operation, boolean isEnabled) {
		// set operation / multiname / is enabled flag
		try {
			setOpEnable(operation, "", isEnabled);
		} catch (Exception e) {
			try {
				setOpEnable(operation, " ", isEnabled);
			} catch (Exception e2) {
				System.err.println(operation + " " + "not existing operation");
			}
		}
	}

	@Override
	public int compareTo(DTConfiguration arg0) {
		return this.index.compareTo(arg0.index);
	}

	public void deflick(String outFolder,
			PolynomialSplineFunction calibLumDeltaEV) {
		// change operations/iop/exposure parameter to deflick (first filter =>
		// "exposure ")
		String expoName = this.findExposure();
		double EV = getOpParValue(expoName, "exposure", 0);

		// compute target EV for deflick
		// double evDeflick = computeEVtarget(EV, this.luminance,
		// this.luminanceDeflick);
		double evDeflick = calibLumDeltaEV.value((this.luminanceDeflick
				/ this.luminance - 1.0d));

		// update XMP configuration
		setOpParValue(expoName, "exposure", 0, EV + evDeflick);
		setOpEnable(expoName, true);
		this.updateXmpConf(outFolder);
	}

	@SuppressWarnings("unused")
	private static double computeEVtarget(double EV, double lum,
			double lumTarget) {
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

		EVtarget = evFactor(lumFactor(EV) * lumTarget / lum);

		return EVtarget;
	}

	private static double lumFactor(double EV) {
		double lumFactor;
		// Let define the calibration function
		// f: EV -> (1+EV) if EV>0 | 1/(1-EV) if EV<0
		if (EV >= 0) {
			lumFactor = 1 + EV;
		} else {
			lumFactor = 1 / (1 - EV);
		}
		return lumFactor;
	}

	private static double evFactor(double lumRatio) {
		double evFactor;
		// Let define the inverse of calibration function
		// invf : r -> (1-r) if r>1 | 1-1/r if r<1
		if (lumRatio >= 1) {
			evFactor = 1 - lumRatio;
		} else {
			evFactor = 1 - 1 / lumRatio;
		}
		return evFactor;
	}

	public void addDefaultExposure() {
		// Add default Exposure iop
		Exposure opExpo = new Exposure();
		opExpo.addDefaultValue();
		this.put("exposure ", (DTOperation) opExpo);
		// Update associated xmp params
		this.xmpConf.addNode(opExpo);
	}
	
	public String findExposure() {
		String  iop = null;
		Set<String> keys = this.keySet();
		boolean expoFound = false;
		Iterator<String> ik = keys.iterator();
		while (ik.hasNext() && !expoFound) {
			iop = ik.next();
			if (iop.startsWith("exposure")) {
				expoFound=true;
			}
		}
		if (!expoFound) {
			System.err.println("Exposure filter not found: Add active exposure to your XMP settings for all keyframes");
		} 
		return iop;	
	}

}
