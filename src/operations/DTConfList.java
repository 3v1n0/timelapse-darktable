package operations;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class DTConfList extends TreeSet<DTConfiguration> {
	/**
	 * DTConfList : TreeSet of DTConfiguration
	 */
	private static final long serialVersionUID = -3341022703073091969L;

	public DTConfList() {
		super();
	}

	public void addXmp(String xmpFile) {
		this.add(new DTConfiguration(xmpFile));
	}

	public ArrayList<String> getSrcFileList() {
		// return the srcFile (DTConfiguration.srcFile) list
		ArrayList<String> srcList = new ArrayList<String>();
		Iterator<DTConfiguration> it = this.iterator();
		while (it.hasNext()) {
			srcList.add(it.next().srcFile);
		}
		return srcList;
	}

	public ArrayList<Number> getIndexList() {
		// return the list of indices in an array list
		ArrayList<Number> indexList = new ArrayList<Number>();
		// this.first();
		Iterator<DTConfiguration> it = this.iterator();
		while (it.hasNext()) {
			indexList.add(it.next().index);
		}
		return indexList;
	}

	public double[] getIndexDoubleArray() {
		// return the list of indices (DTConfiguration.index) in double[]
		return this.todoubleArray(this.getIndexList());
	}

	public ArrayList<Number> getOneParamList(String operation,
			String parameter, Integer index) {
		// return one operation/parameter values in a list
		ArrayList<Number> parList = new ArrayList<Number>();
		Iterator<DTConfiguration> it = this.iterator();
		while (it.hasNext()) {
			parList.add(it.next().getOpParValue(operation, parameter, index));
		}
		return parList;
	}

	public double[] getOneParamDoubleArray(String operation, String parameter,
			Integer index) {
		// return one operation/parameter all values in a matrix of double[]
		return this.todoubleArray(this.getOneParamList(operation, parameter,
				index));
	}

	public double[][] todoubleMat(ArrayList<Number[]> numList) {
		// variable with more than one dimension (vector) are put in one matrix
		// (jVec,iList)
		// doubleArray[jVec] is one vector composed of all iList parameters
		double[][] doubleArray = new double[numList.get(1).length][numList
				.size()];
		for (int iList = 0; iList < numList.size(); iList++) {
			for (int jVec = 0; jVec < numList.get(0).length; jVec++) {
				doubleArray[jVec][iList] = numList.get(iList)[jVec]
						.doubleValue();
			}
		}
		return doubleArray;
	}

	public double[] todoubleArray(ArrayList<Number> numList) {
		// return a double[] from a list composed of scalars
		double[] doubleArray = new double[numList.size()];
		for (int i = 0; i < numList.size(); i++) {
			doubleArray[i] = numList.get(i).doubleValue();
		}
		return doubleArray;
	}

	public int[] tointArray(ArrayList<Number> numList) {
		// return an int[] from a list composed of scalars
		int[] intArray = new int[numList.size()];
		for (int i = 0; i < intArray.length; i++) {
			intArray[i] = numList.get(i).intValue();
		}
		return intArray;
	}

	public double[] genInterpIndexList() {
		// return the list of indices from (min file index) to (max file index)
		// with a step of 1
		// increasing list of indices :
		double[] indSrc = this.getIndexDoubleArray(); // already sorted
		// generate increasing list between first and last indices
		double[] xi = new double[(1 + (int) Math.abs(indSrc[indSrc.length - 1]
				- indSrc[0]))];
		for (int i = (int) indSrc[0]; i <= indSrc[indSrc.length - 1]; i += 1) {
			xi[(int) (i - indSrc[0])] = i;
		}
		// if indSrc=[5 9 11] then xi = [5 6 7 8 9 10 11]
		return xi;
	}

	public double[] linearInterp(double[] x, double[] y, double[] xi) {
		// return linear interpolation of (x,y) on xi
		double[] yi = new double[xi.length];
		for (int i = 0; i < xi.length; i++) {
			yi[i] = this.linearInterp(x, y, xi[i]);
		}
		return yi;
	}

	public double zohInterp(double[] indexDoubleArray,
			double[] oneParamDoubleArray, int fileIndex) {
		// zero order hold interpolation
		int i = 0;
		int iMax = (indexDoubleArray.length - 1);
		while (indexDoubleArray[i] <= fileIndex && i < iMax) {
			i += 1;
		}
		if (fileIndex >= indexDoubleArray[i]) {
			i = i + 1;
		}
		return oneParamDoubleArray[i - 1];
	}

	public double linearInterp(double[] x, double[] y, double xi) {
		// return linear interpolation of (x,y) on xi
		LinearInterpolator li = new LinearInterpolator();
		PolynomialSplineFunction psf = li.interpolate(x, y);
		double yi = psf.value(xi);
		return yi;
	}

	public double[] splineInterp(double[] x, double[] y, double[] xi) {
		// return "free" cubic spline interpolation of (x,y) on xi
		double[] yi = new double[xi.length];
		for (int i = 0; i < xi.length; i++) {
			yi[i] = this.splineInterp(x, y, xi[i]);
		}
		return yi;
	}

	public double splineInterp(double[] x, double[] y, double xi) {
		// return "free" cubic spline interpolation of (x,y) on xi
		SplineInterpolator si = new SplineInterpolator();
		PolynomialSplineFunction psf = si.interpolate(x, y);
		double yi = psf.value(xi);
		return yi;
	}

	public double[] interpLinearOneOpParamAllIndices(String operation,
			String parameter, int paramIndex) {
		// just for test
		return this.linearInterp(this.getIndexDoubleArray(),
				this.getOneParamDoubleArray(operation, parameter, paramIndex),
				this.genInterpIndexList());
	}

	public double interpZohOneOpParam(String operation, String parameter,
			int paramIndex, int fileIndex) {
		return this.zohInterp(this.getIndexDoubleArray(),
				this.getOneParamDoubleArray(operation, parameter, paramIndex),
				fileIndex);
	}

	public double interpLinearOneOpParam(String operation, String parameter,
			int paramIndex, int fileIndex) {
		return this.linearInterp(this.getIndexDoubleArray(),
				this.getOneParamDoubleArray(operation, parameter, paramIndex),
				fileIndex);
	}

	public double[] interpSplineOneOpParamAllIndices(String operation,
			String parameter, int paramIndex) {
		// just for test
		return this.splineInterp(this.getIndexDoubleArray(),
				this.getOneParamDoubleArray(operation, parameter, paramIndex),
				this.genInterpIndexList());
	}

	public double interpSplineOneOpParam(String operation, String parameter,
			int paramIndex, int fileIndex) {
		return this.splineInterp(this.getIndexDoubleArray(),
				this.getOneParamDoubleArray(operation, parameter, paramIndex),
				fileIndex);
	}

	public DTConfList interpAllParam(String outFolder, String method) {
		// interpolation method available: linear | spline

		// copy 1st configuration before interpolation
		DTConfList dtclInterp = new DTConfList();
		dtclInterp.add(new DTConfiguration(this.first()));

		double[] interpFileIdx = this.genInterpIndexList();
		for (int i = 0; i < interpFileIdx.length; i++) {
			DTConfiguration dtc = new DTConfiguration(this.first());
			Integer confIdx = (int) interpFileIdx[i];
			dtc.index = confIdx;
			// format: prefix_0000_suffix.xmp
			String s = confIdx.toString();
			while (s.length() < 4) {
				// complete with 0 to have 4 digits
				s = "0" + s;
			}
			dtc.srcFile = dtc.srcFile.replaceAll("(.*\\D)(\\d+)(\\D.*)", "$1"
					+ s + "$3");
			dtclInterp.add(dtc);
		}

		// for each operation : interpolate parameters
		// scan all operations of first element TreeSet (DTConfiguration)
		// suppose that all files in TreeSet have the same DTConfiguration
		// (operation/parameters)
		Iterator<String> itOp = this.first().keySet().iterator();
		while (itOp.hasNext()) {
			// loop on all DTOperation in LinkedHashMap
			String operation = itOp.next();
			Iterator<String> itPar = this.first().get(operation).keySet()
					.iterator();
			while (itPar.hasNext()) {
				// loop on all DTParameter in LinkedHashMap
				String parameter = itPar.next();
				DTValue dtv = (DTValue) this.first().get(operation)
						.get(parameter).get("value");
				Iterator<Integer> itVal = dtv.keySet().iterator();
				// TODO: correct interpolation... not working now ! :-(
				while (itVal.hasNext()) {
					// loop on all DTValue in LinkedHashMap
					Integer paramIndex = itVal.next();
					for (int i = 0; i < interpFileIdx.length; i++) {
						// lowest level reached: interpolate values
						Integer confIdx = (int) interpFileIdx[i];
						double vi = 0;
						boolean isInterpolatable = (this.first().get(operation).isInterpolatable && this
								.first().get(operation).get(parameter).isInterpolatable);
						if (isInterpolatable) {
							if (method.equalsIgnoreCase("linear")) {
								vi = this.interpLinearOneOpParam(operation,
										parameter, paramIndex, confIdx);
							} else if (method.equalsIgnoreCase("spline")) {
								if (this.size() > 2) {
									vi = this.interpSplineOneOpParam(operation,
											parameter, paramIndex, confIdx);
								} else {
									System.err
											.println("Only 2 XMP keyframes:\nlinear interpolation is used instead of spline");
									vi = this.interpLinearOneOpParam(operation,
											parameter, paramIndex, confIdx);
								}
							} else {
								System.err
										.println("Method "
												+ method
												+ " not supported. Available: linear | spline");
							}
						} else {
							// zero order hold
							vi = this.interpZohOneOpParam(operation, parameter,
									paramIndex, confIdx);
						}
						dtclInterp.get(confIdx).setOpParValue(operation,
								parameter, paramIndex, vi);
					}
				}
			}
		}
		dtclInterp.updateXmpConf(outFolder); // update xmpConf in coherency &
												// write XMP files
		return dtclInterp;
	}

	public DTConfList deflick(String outFolder,
			PolynomialSplineFunction calibLumDeltaEV) {
		DTConfList dtclDeflick = new DTConfList();
		Iterator<DTConfiguration> itConf = this.iterator();
		while (itConf.hasNext()) {
			// first: copy current DTConfList
			DTConfiguration dtc = itConf.next();
			dtclDeflick.add(dtc);

			// apply deflickering
			dtc.deflick(outFolder, calibLumDeltaEV);
		}
		return dtclDeflick;
	}

	public void updateXmpConf(String outFolder) {
		Iterator<DTConfiguration> it = this.iterator();
		while (it.hasNext()) {
			// loop on each DTConfiguration of TreeSet
			it.next().updateXmpConf(outFolder); // update xmpConf from
												// DTOperation parameters
		}
	}

	/**
	 * print on screen all operations/parameters/values of the current
	 * DTConfList
	 */
	public ArrayList<String> getAllParamList() {
		ArrayList<String> allParamList = new ArrayList<String>();

		boolean firstLine = true;
		Iterator<DTConfiguration> itDt = this.iterator();
		while (itDt.hasNext()) {
			// loop on all DTConfiguration in TreeSet
			String line = "";
			String header = "";
			DTConfiguration dtc = itDt.next();
			Integer confIdx = dtc.index;
			if (firstLine == true) {
				header = header + "Idx" + " \t";
			}
			line = line + confIdx;
			Iterator<String> itOp = this.first().keySet().iterator();
			while (itOp.hasNext()) {
				// loop on all DTOperation in LinkedHashMap
				String operation = itOp.next();
				if (firstLine == true) {
					header = header + "|| \t" + operation + " \t";
				}
				line = line + "|| \t" + operation + " \t";
				Iterator<String> itPar = this.first().get(operation).keySet()
						.iterator();
				while (itPar.hasNext()) {
					// loop on all DTParameter in LinkedHashMap
					String parameter = itPar.next();
					if (firstLine == true) {
						header = header + "| \t" + parameter;
					}
					line = line + "| \t";
					DTValue dtv = (DTValue) this.first().get(operation)
							.get(parameter).get("value");
					Iterator<Integer> itVal = dtv.keySet().iterator();
					while (itVal.hasNext()) {
						// loop on all DTValue in LinkedHashMap
						Integer paramIndex = itVal.next();
						if (firstLine == true) {
							header = header + " \t" + paramIndex;
						}
						double vi = dtc.getOpParValue(operation, parameter,
								paramIndex);
						line = line + vi + " \t";
					}
				}
			}
			if (firstLine == true) {
				allParamList.add(header);
			}
			firstLine = false;
			allParamList.add(line);
		}
		return allParamList;
	}

	public void printAllParamTable() {
		ArrayList<String> allParamList = this.getAllParamList();
		for (int i = 0; i < allParamList.size(); i++) {
			System.out.println(allParamList.get(i));
		}
	}

	public DTConfiguration get(Integer index) {
		// return configuration corresponding to index
		Iterator<DTConfiguration> itIndex = this.iterator();
		while (itIndex.hasNext()) {
			DTConfiguration dtc = itIndex.next();
			Integer idx = dtc.index;
			if (idx.equals(index)) {
				return dtc;
			}
		}
		return null;

	}

	public void addXmpFromFolder(String folderPath) {
		ArrayList<String> xmpFiles = new ArrayList<String>();
		xmpFiles = this.getXmpFileInFolder(folderPath);
		Iterator<String> it = xmpFiles.iterator();
		while (it.hasNext()) {
			this.addXmp(folderPath + "/" + it.next());
		}
	}

	public ArrayList<String> getXmpFileInFolder(String folderPath) {
		ArrayList<String> files = new ArrayList<String>();

		String file;
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				file = listOfFiles[i].getName();
				if (file.endsWith(".xmp") || file.endsWith(".XMP")) {
					// System.out.println(file);
					files.add(file);
				}
			}
		}
		return files;
	}
}
