import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import operations.DTConfList;
import operations.DTConfiguration;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length<3) {
			System.err.println("Usage : args = xmpFolder rawFolder outFolder (width heigth)");
		}
	
		// inputs
		String xmpFolder = args[0];
		String rawFolder = args[1];
		String outFolder = args[2];
		String exportWidth ="";
		String exportHeight = "";
		
		// parameters
		String progName = "timelapse-darktable";
		String outMasterFile = "generateDarktableTimelapse.sh";
		String darktablecliBin = "/usr/bin/darktable-cli";
		String mencoderBin = "/usr/bin/mencoder";
		
		if  (args.length>3) {
			exportHeight = args[3];
			if  (args.length>4) {
				exportWidth = args[4];
			} else {
				exportWidth = exportHeight;
			}
		}
		
		System.out.println("===== START : "+progName+" ======");	
		// display inputs configuration
		System.out.println("xmpFolder = "+xmpFolder);
		System.out.println("rawFolder = "+rawFolder);
		System.out.println("outFolder = "+outFolder);
		System.out.println("");

		// create list of input XMP files
		DTConfList dtConfList = new DTConfList();
		dtConfList.addXmpFromFolder(xmpFolder);		
		
		// Linear interpolation of all XMP data and generation of associated XMP
		DTConfList dtl = dtConfList.interpLinearAllParam(outFolder);
		
		// display before/after
		System.out.println("\nParameter of interpolation (verbose)\n----------------------------------------------");
		System.out.println("\nsource");
		dtConfList.printAllParamTable();
		System.out.println("\ninterp");
		dtl.printAllParamTable();
		
		String cmdScript = null;
		String cmdRun = null;
		
		// write file with corresponding rendering commands for all pictures :
		// darktable-cli 'FIC.RAW' 'INTERP_FIC.RAW.XMP' 'FIC.RAW.JPG'"
		System.out.println("\nexport JPG for each frame...");
		System.out.println("This could be long, I can suggest you to take a coffee !");
		try {
			BufferedWriter outScript = new BufferedWriter(new FileWriter(outFolder+"/"+outMasterFile));
			Iterator<DTConfiguration> itDTL = dtl.iterator();
			while (itDTL.hasNext()) {
				DTConfiguration dtc = itDTL.next();
				String fic = dtc.srcFile;
				// for fic in `ls $xmpSrc`;do src=${fic%.*} ; echo $src; darktable-cli $rawSrc/$src $outFolder/$src.jpg; done
				cmdScript = darktablecliBin+" '"+rawFolder+"/"+fic+"' '"+outFolder+"/"+fic+".xmp' '"+outFolder+"/"+fic+".jpg' --width "+exportWidth+" --height "+exportHeight;
				cmdRun = darktablecliBin+" "+rawFolder+"/"+fic+" "+outFolder+"/"+fic+".xmp "+outFolder+"/"+fic+".jpg --width "+exportWidth+" --height "+exportHeight;
				
				// add line to the script
				outScript.write(cmdScript+"\n");
				
				// generate directly the output JPG
				runCmd(cmdRun);
			}
			outScript.close();
		} catch (IOException e) {}

		
		System.out.println("\nscript to re-generate JPG export :\n. '"+outFolder+"/"+outMasterFile+"'");
		
		
		// generation of the video using mencoder @ 25 fps
		System.out.println("\ngeneration of the video...");
		cmdRun=mencoderBin+" mf://"+outFolder+"/*.[jJ][pP][gG] -nosound -ovc lavc -lavcopts vcodec=mjpeg -mf fps=25 -o "+outFolder+"/video.avi";
		runCmd(cmdRun);
		
		// program ending
		System.out.println("\nYou can look at your timelapse right now !\n"+outFolder+"/video.avi");
		System.out.println("===== END : "+progName+" ======");
		
	}
	
	public static void runCmd(String cmdString){


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
	}
	

	
	


}
