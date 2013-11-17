package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
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

	// ------ hard-coded parameters ----------
	// Program properties
	String progName = "timelapse-darktable";
	String progVersion = "0.3";
	// binaries location
	String darktablecliBin = "/usr/bin/darktable-cli";
	String mencoderBin = "/usr/bin/mencoder";
	String convertBin = "/usr/bin/convert";
	String octaveBin = "/usr/bin/octave";
	//String octaveDeflickPath = "/home/alexandre/eclipseWorkspace/timelapse-darktable/lib/octave";
	// default outputs
	String outMasterFile = "generateDarktableTimelapse.sh";
	String outLuminanceFile = "ficL.txt";		

	// ---- JAVA CLI inputs management : END ----

	public TLDTCore(String[] args) throws JSAPException, IOException{


		// ---- JAVA CLI inputs management ----
		Cli cliConf = new Cli(args);

		// JAVA CLI : inputs affectation
		String imgSrc = cliConf.config.getString("imgSrc");
		String xmpSrc = cliConf.config.getString("xmpSrc");
		String outFolder = cliConf.config.getString("out");
		int exportWidth = cliConf.config.getInt("width");
		int exportHeight = cliConf.config.getInt("height");
		String interpType = cliConf.config.getString("interpType");
		boolean isExportJpg = cliConf.config.getBoolean("isExportJpg");
		boolean isExportMovie = cliConf.config.getBoolean("isExportMovie");
		boolean isDeflick = cliConf.config.getBoolean("isDeflick");

		// extra parameters
		String outFolderDeflick = outFolder+"/deflick";


		// Let's go now !		
		System.out.println("===== START : "+progName+" v"+progVersion+" ======");

		// display inputs configuration
		System.out.println("\ncalling parameters:");
		System.out.println("xmpSrc = "+xmpSrc);
		System.out.println("imgSrc = "+imgSrc);
		System.out.println("outFolder = "+outFolder);
		System.out.println("exportWidth = "+exportWidth);
		System.out.println("exportHeight = "+exportHeight);
		System.out.println("interpType = "+interpType);
		System.out.println("isExportJpg = "+isExportJpg);
		System.out.println("isExportMovie = "+isExportMovie);
		System.out.println("isDeflick = "+isDeflick);
		System.out.println("");

		// create list of input XMP files from folder
		DTConfList dtConfList = new DTConfList();
		dtConfList.addXmpFromFolder(xmpSrc);		

		// linear|spline interpolation of all XMP data and generation of associated XMP
		String tmpFolder;
		if (isDeflick) {
			tmpFolder = outFolderDeflick;
		} else {
			tmpFolder = outFolder;
		}
		DTConfList dtl= dtConfList.interpAllParam(tmpFolder,interpType);

		// display before/after
		System.out.println("\nParameter of interpolation (verbose)\n----------------------------------------------");
		System.out.println("\nsource");
		dtConfList.printAllParamTable();
		System.out.println("\ninterp");
		dtl.printAllParamTable();

		// -------------------------------------------
		// DEFLICKERING
		// -------------------------------------------
		if (isDeflick) {
			System.out.println("\ndeflickering (each frame in JPG with darktable-cli... could be long)");

			BufferedWriter outLum = new BufferedWriter(new FileWriter(outFolderDeflick+"/"+outLuminanceFile));
			String lum = null;
			Iterator<DTConfiguration> itDTL = dtl.iterator();
			while (itDTL.hasNext()) {
				DTConfiguration dtc = itDTL.next();
				String fic = dtc.srcFile;

				// Generate thumbnail to evaluate luminance
				// w/h = 200 pix | hq = false for faster export
				runCmd(darktablecliBin,imgSrc+"/"+fic,outFolderDeflick+"/"+fic+".xmp",outFolderDeflick+"/"+fic+".jpg","--width 1920","--height 1920","--hq 0");

				// retrieve luminance
				// /usr/bin/convert is used, could be replaced by jmagick
				lum = runCmdOut(convertBin,outFolderDeflick+"/"+fic+".jpg","-scale","1x1!","-format","%[fx:luminance]","info:");
				dtc.luminance = Double.valueOf(lum);

				// write into a file for octave post-processing
				outLum.write(lum+"\n");
			}
			outLum.close();

			// regression on luminance points with octave script (write the "master" script)

			// write octave scripts in outFolder/deflick
			OctaveDeflickFcn odf = new OctaveDeflickFcn(outFolderDeflick,outLuminanceFile);
			odf.writeFiles(); // write octave scripts

			// execute octave script : filtering luminance values
			runCmd(octaveBin,odf.outFolderDeflick+"/"+odf.masterFileName);

			// add luminanceDeflick to DTConfiguration reading _deflick.txt line by line
			FileInputStream fstream = new FileInputStream(outFolderDeflick+"/"+outLuminanceFile.replaceAll(".txt", "_deflick.txt"));
			DataInputStream dis = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			String lumDeflick;
			Iterator<DTConfiguration> itDTL2 = dtl.iterator();
			while (itDTL2.hasNext()) {
				DTConfiguration dtc = itDTL2.next();

				if ((lumDeflick = br.readLine()) != null)   {
					System.out.println (lumDeflick);
					dtc.luminanceDeflick = Double.valueOf(lumDeflick);
				}

			}
			// Close the input stream (reading _deflick.txt file)
			dis.close();

			// calibration EV <=> luminance 
			double[] evCalib = {-4,-3,-2,-1,-0.5,0,0.5,1,2,3,4};
			int evCalibZeroIdx = 5; // index of EV = 0 in the calibration table evCalib
			double[] lumCalib = new double[evCalib.length];
			double[] deltaLumCalib = new double[evCalib.length];
			DTConfiguration dtc = dtl.first();
			String fic = dtc.srcFile;
			String outFolderCalib = outFolderDeflick+"/calib";
			//double evFirst = getOpParValue(dtc,"exposure ", "exposure", 0);
			double evFirst = dtc.getOpParValue("exposure ", "exposure", 0);
			for (int i = 0; i < evCalib.length; i++) {
				// change operations/iop/exposure parameter to calibrate luminance sensitivity

				// update XMP configuration with calibration value +/- current exposure value
				//setOpParValue(dtc,"exposure ", "exposure", 0, evFirst+evCalib[i]);
				dtc.setOpParValue("exposure ", "exposure", 0, evFirst+evCalib[i]);
				dtc.updateXmpConf(outFolderCalib);
				// generate JPG
				runCmd(darktablecliBin,imgSrc+"/"+fic,outFolderCalib+"/"+fic+".xmp",outFolderCalib+"/"+fic+"_"+i+".jpg","--width "+exportWidth,"--height "+exportHeight);
				// retrieve luminance
				lumCalib[i] = Double.valueOf(runCmdOut(convertBin,outFolderCalib+"/"+fic+"_"+i+".jpg","-scale","1x1!","-format","%[fx:luminance]","info:"));
			}
			//setOpParValue(dtc,"exposure ", "exposure", 0, evFirst); // reset value
			dtc.setOpParValue("exposure ", "exposure", 0, evFirst); // reset value
			dtc.updateXmpConf(outFolderCalib);

			// write calibration curve
			BufferedWriter fileCalibCurve = new BufferedWriter(new FileWriter(outFolderCalib+"/calib.txt"));
			fileCalibCurve.write("deltaEV"+" "+"deltaLum\n");
			for (int i = 0; i < evCalib.length; i++) {
				// compute deltaLum/lum0 evCalib[2]=0 => ref
				deltaLumCalib[i] = (lumCalib[i]/lumCalib[evCalibZeroIdx] - 1.0d);
				fileCalibCurve.write(evCalib[i]+" "+deltaLumCalib[i]+"\n");
			}
			fileCalibCurve.close();

			// calibration curve : LinearInterpolator (5 points: -2 -1 0 +1 +2 EV)
			LinearInterpolator li = new LinearInterpolator();
			PolynomialSplineFunction calibLumDeltaEV = li.interpolate(deltaLumCalib, evCalib);

			// call deflickering
			dtl.deflick(outFolder,calibLumDeltaEV);

		}

		// -------------------------------------------
		// EXPORT JPG
		// -------------------------------------------
		// write file with corresponding rendering commands for all pictures :
		// darktable-cli 'FIC.RAW' 'INTERP_FIC.RAW.XMP' 'FIC.RAW.JPG'"
		if (isExportJpg) {
			System.out.println("\nexporting each frame in JPG with darktable-cli...");
			System.out.println("This could be long, I can suggest you to take a coffee !");
		} else {
			System.out.println("\nScript to generate each JPG in batch could be found here:");
			System.out.println(outFolder+"/"+outMasterFile);
		}

		// script
		String cmdScript = null;
		BufferedWriter outScript = new BufferedWriter(new FileWriter(outFolder+"/"+outMasterFile));
		Iterator<DTConfiguration> itDTL = dtl.iterator();
		while (itDTL.hasNext()) {
			DTConfiguration dtc = itDTL.next();
			String fic = dtc.srcFile;

			// add line to the script file
			cmdScript = darktablecliBin+" '"+imgSrc+"/"+fic+"' '"+outFolder+"/"+fic+".xmp' '"+outFolder+"/"+fic+".jpg' --width "+exportWidth+" --height "+exportHeight;
			outScript.write(cmdScript+"\n");

			if (isExportJpg) {
				// generate directly the output JPG
				runCmd(darktablecliBin,imgSrc+"/"+fic,outFolder+"/"+fic+".xmp",outFolder+"/"+fic+".jpg","--width "+exportWidth,"--height "+exportHeight);	
			}
		}
		outScript.close();


		// ---------------------------------
		// MOVIE GENERATION
		// ---------------------------------
		if (isExportMovie) {
			if (isExportJpg) {
				// generation of the video using mencoder @ 25 fps
				System.out.println("\ngenerating timelapse video with mencoder...");
				runCmd(mencoderBin,"mf://"+outFolder+"/*.[jJ][pP][gG]","-nosound","-ovc","lavc","-lavcopts","vcodec=mjpeg","-mf","fps=25","-o",outFolder+"/video.avi");
				System.out.println("\nYou can look at your timelapse right now here!\n"+outFolder+"/video.avi");
			} else {
				System.out.println("\nVideo not generated, to do so --export-jpg1|-j option should be added to the comman line in addition to --export-movie1|-m...");
			}
		}


		// program ending
		System.out.println("===== END : "+progName+" ======");
	}	






	// --------------------------------------------
	// SUPPORT FUNCTIONS
	// --------------------------------------------
	public static void runCmd(String... cmdString){
		runCmdOut(cmdString);
	}

	public static String runCmdOut(String... cmdString){
		// execute command and output last command output

		String sout = null;
		String s = null;
		try {
			// run an Unix command using the Runtime exec method:
			for (int i = 0; i < cmdString.length; i++) {
				System.out.print(cmdString[i]+" ");
			}
			System.out.print("\n");

			Process p = Runtime.getRuntime().exec(cmdString);

			BufferedReader stdInput = new BufferedReader(new 
					InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new 
					InputStreamReader(p.getErrorStream()));

			// read the output from the command
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
				sout = s;
			}
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}	         

			p.waitFor();

		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		return sout;
	}

/*
	public static double getOpParValue(DTConfiguration dtc, String operation,String parameter,Integer index){
		// return operation / parameter / value[idx]
		try{
			DTValue v = (DTValue) dtc.get(operation).get(parameter).get("value");
			return (Double) v.get(index);
		} catch (Exception nulException) {
			//System.out.println("null exception: "+operation+" "+parameter);
			System.err.println("getOpParValue fail, : "+operation+" "+parameter);
			return (Double) null;
		}

	}

	public static void setOpParValue(DTConfiguration dtc, String operation,String parameter,Integer index,double value){
		// set operation / parameter / value[idx]
		DTValue v = (DTValue) dtc.get(operation).get(parameter).get("value");
		v.put(index,value);
		dtc.get(operation).get(parameter).put("value",v);
	}
*/
}
