package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import operations.DTConfList;
import operations.DTConfiguration;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import cli.Cli;

import com.martiansoftware.jsap.JSAPException;

import deflick.OctaveDeflickFcn;

public class TLDTCore {

	// Program properties
	// ------ hard-coded parameters ----------
	public String progName = "timelapse-darktable";
	public String progVersion = "0.5c";
	// binaries location
	public String darktablecliBin = runCmdOut("which", "darktable-cli");
	public String mencoderBin = runCmdOut("which", "mencoder");
	public String convertBin = runCmdOut("which", "convert");
	public String octaveBin = runCmdOut("which", "octave");
	// default outputs
	public String outMasterFile = "generateDarktableTimelapse.sh";
	public String outLuminanceFile = "ficL.txt";
	public boolean deflickRecompLum = false;
	// ------ parameters from CLI -------------
	public String imgSrc;
	public String xmpSrc;
	public String outFolder;
	public int exportWidth;
	public int exportHeight;
	public String interpType;
	public boolean isExportJpg;
	public boolean isExportMovie;
	public boolean isDeflick;
	public int deflickLpFiltMinNum;

	// local variables
	public DTConfList dtConfListKeys;
	public DTConfList dtConfListInterp;

	// extra parameters
	public String outFolderDeflick;

	// intrinsic timelapse property
	public PolynomialSplineFunction calibLumDeltaEV;

	// ---- JAVA CLI inputs management : END ----

	public TLDTCore(String[] args) throws JSAPException, IOException {

		// INITIALISATION OF PROGRAM

		// ---- JAVA CLI inputs management ----
		Cli cliConf = new Cli(args);

		// JAVA CLI : inputs affectation
		this.imgSrc = cliConf.imgSrc;
		this.xmpSrc = cliConf.xmpSrc;
		this.outFolder = cliConf.outFolder;
		this.outFolderDeflick = this.outFolder + "/predeflick";
		this.exportWidth = cliConf.exportWidth;
		this.exportHeight = cliConf.exportHeight;
		this.interpType = cliConf.interpType;
		this.isExportJpg = cliConf.isExportJpg;
		this.isExportMovie = cliConf.isExportMovie;
		this.isDeflick = cliConf.isDeflick;
		this.deflickLpFiltMinNum = cliConf.deflickLpFiltMinNum;

		// Let's go now !
		System.out.println("===== START : " + progName + " v" + progVersion
				+ " ======");
		// display inputs configuration
		System.out.println("\ncalling parameters:");
		System.out.println("xmpSrc = " + xmpSrc);
		System.out.println("imgSrc = " + imgSrc);
		System.out.println("outFolder = " + outFolder);
		System.out.println("exportWidth = " + exportWidth);
		System.out.println("exportHeight = " + exportHeight);
		System.out.println("interpType = " + interpType);
		System.out.println("isExportJpg = " + isExportJpg);
		System.out.println("isExportMovie = " + isExportMovie);
		System.out.println("isDeflick = " + isDeflick);
		System.out.println("deflickLpFiltMinNum = " + deflickLpFiltMinNum);
		System.out.println("");

		// create list of input XMP files from folder
		this.dtConfListKeys = new DTConfList();
		dtConfListKeys.addXmpFromFolder(this.xmpSrc);

		// create list of interpolated XMP (empty)
		this.dtConfListInterp = new DTConfList();

	}

	public void generateTimelapse() throws IOException {
		this.interpolateXmp();
		this.printBothConfList();
		this.deflick();
		this.exportJpg();
		this.exportMovie();
	}

	public void interpolateXmp() {

		// linear|spline interpolation of all XMP data and generation of
		// associated XMP
		String tmpFolder;
		if (this.isDeflick) {
			tmpFolder = this.outFolderDeflick;
		} else {
			tmpFolder = this.outFolder;
		}
		this.dtConfListInterp = this.dtConfListKeys.interpAllParam(tmpFolder,
				this.interpType);
	}

	public void printBothConfList() {
		// display before/after
		System.out
				.println("\nParameter of interpolation (verbose)\n----------------------------------------------");
		System.out.println("\nsource");
		this.dtConfListKeys.printAllParamTable();
		System.out.println("\ninterp");
		this.dtConfListInterp.printAllParamTable();
	}

	public void deflick() throws IOException {
		// -------------------------------------------
		// DEFLICKERING
		// -------------------------------------------
		if (this.isDeflick) {
			System.out
					.println("\ndeflickering (each frame in JPG with darktable-cli... could be long)");

			deflickWriteLuminance();
			deflickWriteFilter();
			this.calibLumDeltaEV = deflickCalib();
			// call deflickering (write XMP files)
			this.dtConfListInterp.deflick(this.outFolder, this.calibLumDeltaEV);
		}
	}

	public void deflickWriteLuminance() throws IOException {

		String lumFileName = this.outFolderDeflick + "/"
				+ this.outLuminanceFile;
		boolean isLumFileExist = (new File(lumFileName)).exists();

		if (this.deflickRecompLum || !isLumFileExist) {

			// Compute raw luminance from interpolation XMP
			// and write luminance file
			BufferedWriter outLum = new BufferedWriter(new FileWriter(
					lumFileName));
			String lum = null;
			Iterator<DTConfiguration> itDTL = this.dtConfListInterp.iterator();
			while (itDTL.hasNext()) {
				DTConfiguration dtc = itDTL.next();
				String fic = dtc.srcFile;

				// Generate thumbnail to evaluate luminance
				// hq = false for faster export
				runCmd(this.darktablecliBin, this.imgSrc + "/" + fic,
						this.outFolderDeflick + "/" + fic + ".xmp",
						this.outFolderDeflick + "/" + fic + ".jpg",
						"--width 1920", "--height 1920", "--hq 0");

				// retrieve luminance
				// /usr/bin/convert is used, could be replaced by imagej in
				// newer version to prevent dependancies
				lum = runCmdOut(this.convertBin, this.outFolderDeflick + "/"
						+ fic + ".jpg", "-scale", "1x1!", "-format",
						"%[fx:luminance]", "info:");
				dtc.luminance = Double.valueOf(lum);

				// write into a file for octave post-processing
				outLum.write(lum + "\n");
			}
			outLum.close();
		} else {
			// load existing luminance file
			FileInputStream fstream = new FileInputStream(lumFileName);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String lum = null;
			boolean isErrDisplayed=false;

			Iterator<DTConfiguration> itDTL = this.dtConfListInterp.iterator();
			while (itDTL.hasNext()) {
				DTConfiguration dtc = itDTL.next();
				// retrieve luminance
				try {
					lum = br.readLine(); // read line by line
					dtc.luminance = Double.valueOf(lum);
				} catch (Exception e) {
					if (!isErrDisplayed) {
						System.err
						.println("\nluminance file for deflickering do not have the same size as current XMP list:\n"
								+ "consider deleting " + this.outLuminanceFile + " and run again timelapse-darktable\n");
						isErrDisplayed=true;
					}
				}
			}
			in.close();

		}

	}

	public void deflickWriteFilter() throws IOException {
		// regression on luminance points with octave script (write the "master"
		// script)

		// write octave scripts in outFolder/deflick
		OctaveDeflickFcn odf = new OctaveDeflickFcn(this.outFolderDeflick,
				this.outLuminanceFile, this.octaveBin);
		odf.setLpFiltMinNum(this.deflickLpFiltMinNum);
		odf.writeFiles(); // write octave scripts

		// execute octave script : filtering luminance values
		runCmd(this.octaveBin, odf.outFolderDeflick + "/" + odf.masterFileName);

		// add luminanceDeflick to DTConfiguration reading _deflick.txt line by
		// line
		FileInputStream fstream = new FileInputStream(this.outFolderDeflick
				+ "/"
				+ this.outLuminanceFile.replaceAll(".txt", "_deflick.txt"));
		DataInputStream dis = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));
		String lumDeflick;
		Iterator<DTConfiguration> itDTL2 = this.dtConfListInterp.iterator();
		while (itDTL2.hasNext()) {
			DTConfiguration dtc = itDTL2.next();

			if ((lumDeflick = br.readLine()) != null) {
				System.out.println(lumDeflick);
				dtc.luminanceDeflick = Double.valueOf(lumDeflick);
			}

		}
		// Close the input stream (reading _deflick.txt file)
		dis.close();
	}

	private PolynomialSplineFunction deflickCalib() throws IOException {
		// calibration EV <=> luminance
		double[] evCalib = { -4, -3, -2, -1, -0.5, 0, 0.5, 1, 2, 3, 4 };
		int evCalibZeroIdx = 5; // index of EV = 0 in the calibration table
								// evCalib
		double[] lumCalib = new double[evCalib.length];
		double[] deltaLumCalib = new double[evCalib.length];
		DTConfiguration dtc = this.dtConfListInterp.first();
		String fic = dtc.srcFile;
		String outFolderCalib = this.outFolderDeflick + "/calib";
		int iFolder = 1;
		while ((new File(outFolderCalib)).exists()) {
			outFolderCalib = this.outFolderDeflick + "/calib" + iFolder;
			iFolder += 1;
		}

		
		// double evFirst = getOpParValue(dtc,"exposure ", "exposure", 0);
		double evFirst = dtc.getOpParValue("exposure ", "exposure", 0);
		for (int i = 0; i < evCalib.length; i++) {
			// change operations/iop/exposure parameter to calibrate luminance
			// sensitivity

			// update XMP configuration with calibration value +/- current
			// exposure value
			// setOpParValue(dtc,"exposure ", "exposure", 0,
			// evFirst+evCalib[i]);
			dtc.setOpParValue("exposure ", "exposure", 0, evFirst + evCalib[i]);
			dtc.setOpEnable("exposure ", true);
			dtc.updateXmpConf(outFolderCalib);
			// generate JPG
			runCmd(this.darktablecliBin, this.imgSrc + "/" + fic,
					outFolderCalib + "/" + fic + ".xmp", outFolderCalib + "/"
							+ fic + "_" + i + ".jpg", "--width " + exportWidth,
					"--height " + exportHeight);
			// retrieve luminance
			lumCalib[i] = Double.valueOf(runCmdOut(convertBin, outFolderCalib
					+ "/" + fic + "_" + i + ".jpg", "-scale", "1x1!",
					"-format", "%[fx:luminance]", "info:"));
		}
		// setOpParValue(dtc,"exposure ", "exposure", 0, evFirst); // reset
		// value
		dtc.setOpParValue("exposure ", "exposure", 0, evFirst); // reset value
		dtc.updateXmpConf(outFolderCalib);

		// write calibration curve
		BufferedWriter fileCalibCurve = new BufferedWriter(new FileWriter(
				outFolderCalib + "/calib.txt"));
		fileCalibCurve.write("deltaEV" + " " + "deltaLum\n");
		for (int i = 0; i < evCalib.length; i++) {
			// compute deltaLum/lum0 evCalib[2]=0 => ref
			deltaLumCalib[i] = (lumCalib[i] / lumCalib[evCalibZeroIdx] - 1.0d);
			fileCalibCurve.write(evCalib[i] + " " + deltaLumCalib[i] + "\n");
		}
		fileCalibCurve.close();

		// calibration curve : LinearInterpolator (5 points: -2 -1 0 +1 +2 EV)
		LinearInterpolator li = new LinearInterpolator();
		PolynomialSplineFunction calibLumDeltaEV = li.interpolate(
				deltaLumCalib, evCalib);

		return calibLumDeltaEV;

	}

	public void exportJpg() throws IOException {

		// -------------------------------------------
		// EXPORT JPG
		// -------------------------------------------
		// write file with corresponding rendering commands for all pictures :
		// darktable-cli 'FIC.RAW' 'INTERP_FIC.RAW.XMP' 'FIC.RAW.JPG'"
		if (this.isExportJpg) {
			System.out
					.println("\nexporting each frame in JPG with darktable-cli...");
			System.out
					.println("This could be long, I can suggest you to take a coffee !");
		} else {
			System.out
					.println("\nScript to generate each JPG in batch could be found here:");
			System.out.println(this.outFolder + "/" + this.outMasterFile);
		}

		// script
		String cmdScript = null;
		BufferedWriter outScript = new BufferedWriter(new FileWriter(
				this.outFolder + "/" + this.outMasterFile));
		Iterator<DTConfiguration> itDTL = this.dtConfListInterp.iterator();
		while (itDTL.hasNext()) {
			DTConfiguration dtc = itDTL.next();
			String fic = dtc.srcFile;

			// add line to the script file
			cmdScript = this.darktablecliBin + " '" + this.imgSrc + "/" + fic
					+ "' '" + this.outFolder + "/" + fic + ".xmp' '"
					+ this.outFolder + "/" + fic + ".jpg' --width "
					+ this.exportWidth + " --height " + this.exportHeight;
			outScript.write(cmdScript + "\n");

			if (this.isExportJpg) {
				// generate directly the output JPG
				runCmd(this.darktablecliBin, this.imgSrc + "/" + fic,
						this.outFolder + "/" + fic + ".xmp", this.outFolder
								+ "/" + fic + ".jpg", "--width "
								+ this.exportWidth, "--height "
								+ this.exportHeight);
			}
		}
		outScript.close();
	}

	void exportMovie() {

		// ---------------------------------
		// MOVIE GENERATION
		// ---------------------------------
		if (isExportMovie) {
			if (isExportJpg) {
				// generation of the video using mencoder @ 25 fps
				System.out
						.println("\ngenerating timelapse video with mencoder...");
				runCmd(mencoderBin, "mf://" + outFolder + "/*.[jJ][pP][gG]",
						"-nosound", "-ovc", "lavc", "-lavcopts",
						"vcodec=mjpeg", "-mf", "fps=25", "-o", outFolder
								+ "/video.avi");
				System.out
						.println("\nYou can look at your timelapse right now here!\n"
								+ outFolder + "/video.avi");
			} else {
				System.out
						.println("\nVideo not generated, to do so --export-jpg1|-j option should be added to the comman line in addition to --export-movie1|-m...");
			}
		}
	}

	// --------------------------------------------
	// SUPPORT FUNCTIONS
	// --------------------------------------------
	public static void runCmd(String... cmdString) {
		runCmdOut(cmdString);
	}

	public static String runCmdOut(String... cmdString) {
		// execute command and output last command output

		String sout = "";
		String s = null;
		try {
			// run an Unix command using the Runtime exec method:
			for (int i = 0; i < cmdString.length; i++) {
				System.out.print(cmdString[i] + " ");
			}
			System.out.print("\n");

			Process p = Runtime.getRuntime().exec(cmdString);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			// read the output from the command
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
				sout = s;
			}
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}

			p.waitFor();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return sout;
	}
}
