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

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.QualifiedSwitch;
import com.martiansoftware.jsap.UnflaggedOption;


public class Main {

	/**
	 * @param args
	 * @throws JSAPException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws JSAPException, IOException {

		// hard-coded parameters
		String progName = "timelapse-darktable";
		String darktablecliBin = "/usr/bin/darktable-cli";
		String mencoderBin = "/usr/bin/mencoder";
		String convertBin = "/usr/bin/convert";
		String outMasterFile = "generateDarktableTimelapse.sh";
		String outLuminanceFile = "ficL.txt";
		String octaveDeflickPath = "/home/alexandre/eclipseWorkspace/timelapse-darktable/lib/octave";
		
		// ---- JAVA CLI inputs management ----
		JSAP jsap = new JSAP();
        
		// main arguments img/xmp/out
        FlaggedOption optImgSrc = new FlaggedOption("imgSrc")
                                .setStringParser(JSAP.STRING_PARSER)
                                .setDefault("imgSrc") 
                                .setRequired(true) 
                                .setShortFlag('i') 
                                .setLongFlag("imgSrc");
        optImgSrc.setHelp("Image source folder (raw or jpg)");
        jsap.registerParameter(optImgSrc);                        

        FlaggedOption optXmpSrc = new FlaggedOption("xmpSrc")
                                .setStringParser(JSAP.STRING_PARSER)
                                .setDefault("xmpSrc") 
                                .setRequired(true) 
                                .setShortFlag('x') 
                                .setLongFlag("xmpSrc");
        optXmpSrc.setHelp("XMP source folder (to interpolate)");
        jsap.registerParameter(optXmpSrc);
        
        FlaggedOption optOut = new FlaggedOption("out")
                                .setStringParser(JSAP.STRING_PARSER)
                                .setDefault("out") 
                                .setRequired(true) 
                                .setShortFlag('o') 
                                .setLongFlag("out");
        optOut.setHelp("output folder");
        jsap.registerParameter(optOut);
		
		// optional height / width
        FlaggedOption optHeight = new FlaggedOption("height")
                                .setStringParser(JSAP.INTEGER_PARSER)
                                .setDefault("0") 
                                .setRequired(false) 
                                .setShortFlag('h') 
                                .setLongFlag("height");
        optHeight.setHelp("Height of the exported JPG");
        jsap.registerParameter(optHeight);
        

        FlaggedOption optWidth = new FlaggedOption("width")
                                .setStringParser(JSAP.INTEGER_PARSER)
                                .setDefault("0") 
                                .setRequired(false) 
                                .setShortFlag('w') 
                                .setLongFlag("width");
        optWidth.setHelp("Width of the exported JPG");
        jsap.registerParameter(optWidth);
        
        // optional splie/linear interpolation
        FlaggedOption optInterp = new FlaggedOption("interpType")
        						.setStringParser(JSAP.STRING_PARSER)
        						.setDefault("linear")
        						.setRequired(false)
        						.setShortFlag('t')
        						.setLongFlag("interpolation-type");
        optInterp.setHelp("Interpolation type: linear|spline");
        jsap.registerParameter(optInterp);

        
        // optional export/movie/deflickering
		QualifiedSwitch optIsExportJpg = (QualifiedSwitch) 
								new QualifiedSwitch("isExportJpg")
                                .setShortFlag('j')
                                .setLongFlag("export-jpg");
		optIsExportJpg.setHelp("Final JPG export is required");
        jsap.registerParameter(optIsExportJpg);
        
        QualifiedSwitch optIsExportMovie = (QualifiedSwitch) 
        						new QualifiedSwitch("isExportMovie")
        						.setShortFlag('m')
        						.setLongFlag("export-movie");
        optIsExportMovie.setHelp("Timelapse movie making is required");
        jsap.registerParameter(optIsExportMovie);

        QualifiedSwitch optIsDeflick = (QualifiedSwitch) 
        		new QualifiedSwitch("isDeflick")
        		.setShortFlag('d')
        		.setLongFlag("deflick");
        optIsDeflick.setHelp("Deflikering will be applied");
        jsap.registerParameter(optIsDeflick);
        
        // extra arguments
        UnflaggedOption optRemain = new UnflaggedOption("extra")
                                .setStringParser(JSAP.STRING_PARSER)
                                .setDefault("")
                                .setRequired(false)
                                .setGreedy(true);
        optRemain.setHelp("Extra arguments parser");
        jsap.registerParameter(optRemain);
        
        // parse input args
        JSAPResult config = jsap.parse(args);    
        
        // failure/help management
        if (!config.success()) {
            
            System.err.println();

            // print out specific error messages describing the problems
            // with the command line, THEN print usage, THEN print full
            // help.  This is called "beating the user with a clue stick."
            for (java.util.Iterator errs = config.getErrorMessageIterator();
                    errs.hasNext();) {
                System.err.println("Error: " + errs.next());
            }
            
            System.err.println();
            System.err.println("Usage: java -jar *.jar");
            System.err.println("        "+ jsap.getUsage());
            System.err.println();
            System.err.println(jsap.getHelp());
            System.exit(1);
        }
        
        // JAVA CLI : inputs affectation
        String imgSrc = config.getString("imgSrc");
        String xmpSrc = config.getString("xmpSrc");
        String outFolder = config.getString("out");
        int exportWidth = config.getInt("width");
        int exportHeight = config.getInt("height");
        String interpType = config.getString("interpType");
        boolean isExportJpg = config.getBoolean("isExportJpg");
        boolean isExportMovie = config.getBoolean("isExportMovie");
        boolean isDeflick = config.getBoolean("isDeflick");
        
        // extra parameters
        String outFolderDeflick = outFolder+"/deflick";
        
        // ---- JAVA CLI inputs management : END ----
	
		// Let's go now !		
		System.out.println("===== START : "+progName+" ======");
		
		// display inputs configuration
		System.out.println("xmpSrc = "+xmpSrc);
		System.out.println("imgSrc = "+imgSrc);
		System.out.println("out = "+outFolder);
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

		// script strings declaration
		String cmdScript = null;
		String cmdRun = null;
		
		// deflickering
		if (isDeflick) {
			System.out.println("\ndeflickering (each frame in JPG with darktable-cli... could be long)");
			
			BufferedWriter outLum = new BufferedWriter(new FileWriter(outFolderDeflick+"/"+outLuminanceFile));
			String lum = null;
			Iterator<DTConfiguration> itDTL = dtl.iterator();
			while (itDTL.hasNext()) {
				DTConfiguration dtc = itDTL.next();
				String fic = dtc.srcFile;

				// w/h = 200 pix | hq = false for faster export
				// TODO: check if user w/h should be used for better result
				// small size (1-100pix) gives non consistent luminance
				cmdRun = darktablecliBin+" "+imgSrc+"/"+fic+" "+outFolderDeflick+"/"+fic+".xmp "+outFolderDeflick+"/"+fic+".jpg --width 200 --height 200 --hq 0";
				runCmd(cmdRun);
				
				// retrieve luminance
				// convert is used, could be replaced by jmagick
				cmdRun = convertBin+" "+outFolderDeflick+"/"+fic+".jpg"+" -scale 1x1! -format %[fx:luminance] info:";
				lum = runCmdOut(cmdRun);
				dtc.luminance = Double.valueOf(lum);
				
				// write into a file for octave post-processing
				outLum.write(lum+"\n");
			}
			outLum.close();
			
			// regression on luminance points with octave script
			BufferedWriter outOctaveMaster = new BufferedWriter(new FileWriter(outFolderDeflick+"/master.m"));
			outOctaveMaster.write("#!/usr/bin/octave -qf"+"\n");
			outOctaveMaster.write("addpath('"+octaveDeflickPath+"');"+"\n");
			outOctaveMaster.write("deflick('"+outFolderDeflick+"/"+outLuminanceFile+"');"+"\n");
			outOctaveMaster.close();
			
			// execute octave script
			runCmd("octave "+outFolderDeflick+"/master.m");
			
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
			
			// call deflickering
			dtl.deflick(outFolder);
			

			// TODO: while loop to set luminanceDeflick field in the DTConfiguration parameters
			// then take into account luminanceDeflick parameter in XMP exposition filter
			// to do so: calibrate the impact of Delta deltaE exposition filter on luminance 
			// if not possible evaluate on the current scene this impact by calibration
			// it will also suppose that exposition filter is used in XMP
		}
		
		
		// write file with corresponding rendering commands for all pictures :
		// darktable-cli 'FIC.RAW' 'INTERP_FIC.RAW.XMP' 'FIC.RAW.JPG'"
		if (isExportJpg) {
			System.out.println("\nexporting each frame in JPG with darktable-cli...");
			System.out.println("This could be long, I can suggest you to take a coffee !");
		}

		BufferedWriter outScript = new BufferedWriter(new FileWriter(outFolder+"/"+outMasterFile));
		Iterator<DTConfiguration> itDTL = dtl.iterator();
		while (itDTL.hasNext()) {
			DTConfiguration dtc = itDTL.next();
			String fic = dtc.srcFile;

			// for fic in `ls $xmpSrc`;do src=${fic%.*} ; echo $src; darktable-cli $rawSrc/$src $outFolder/$src.jpg; done
			cmdScript = darktablecliBin+" '"+imgSrc+"/"+fic+"' '"+outFolder+"/"+fic+".xmp' '"+outFolder+"/"+fic+".jpg' --width "+exportWidth+" --height "+exportHeight;
			cmdRun = darktablecliBin+" "+imgSrc+"/"+fic+" "+outFolder+"/"+fic+".xmp "+outFolder+"/"+fic+".jpg --width "+exportWidth+" --height "+exportHeight;

			// add line to the script file
			outScript.write(cmdScript+"\n");

			if (isExportJpg) {
				// generate directly the output JPG
				runCmd(cmdRun);	
			}
		}
		outScript.close();

		
		// movie timelapse generation
		if (isExportMovie) {
			if (isExportJpg) {
				// generation of the video using mencoder @ 25 fps
				System.out.println("\ngenerating timelapse video with mencoder...");
				cmdRun=mencoderBin+" mf://"+outFolder+"/*.[jJ][pP][gG] -nosound -ovc lavc -lavcopts vcodec=mjpeg -mf fps=25 -o "+outFolder+"/video.avi";
				runCmd(cmdRun);
				System.out.println("\nYou can look at your timelapse right now here!\n"+outFolder+"/video.avi");
			} else {
				System.out.println("\nVideo not generated, to do so --export-jpg1|-j option should be added to the comman line in addition to --export-movie1|-m...");
			}
		}
		
		
		
		// program ending
		System.out.println("===== END : "+progName+" ======");
		
	}
	
	public static void runCmd(String cmdString){
		runCmdOut(cmdString);
	}
	
	public static String runCmdOut(String cmdString){

		String sout = null;
		String s = null;
		try {
			// run the Unix "ps -ef" command
			// using the Runtime exec method:
			System.out.println(cmdString);
			Process p = Runtime.getRuntime().exec(cmdString);
			
			BufferedReader stdInput = new BufferedReader(new 
					InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new 
					InputStreamReader(p.getErrorStream()));

			// read the output from the command
			// System.out.println("Here is the standard output of the command:\n");
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
}


