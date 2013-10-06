import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import operations.DTConfList;
import operations.DTConfiguration;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length<3) {
			System.out.println("Usage : args = xmpFolder rawFolder outFolder (width heigth)");
		}
		// "/media/dsData/photo/photoShared/photoTimelapse/2013-08-29 Timelapse Pech David/test_timelapse/xmpRef"
		// "/media/dsData/photo/photoShared/photoTimelapse/2013-08-29 Timelapse Pech David"
		// "/media/dsData/photo/photoShared/photoTimelapse/2013-08-29 Timelapse Pech David/test_timelapse"
		System.out.println("===== TLDT ======");		
		// inputs
//		String xmpFolder = "/media/5841-8C01/dttl/TLDT/XMP_SOURCE";
//		String rawFolder = "/media/dsData/photo/photoShared/photoTimelapse/2013-08-29 Timelapse Pech David";
//		String outFolder = "/media/dsData/photo/photoShared/photoTimelapse/2013-08-29 Timelapse Pech David/test_timelapse";
		String xmpFolder = args[0];
		String rawFolder = args[1];
		String outFolder = args[2];
		String outMasterFile = "generateDarktableTimelapse.sh";
		String exportWidth ="";
		String exportHeight = "";
		
		if  (args.length>3) {
			exportHeight = args[3];
			if  (args.length>4) {
				exportWidth = args[4];
			} else {
				exportWidth = exportHeight;
			}
		}
		
		System.out.println("xmpFolder "+xmpFolder);
		System.out.println("rawFolder "+rawFolder);
		System.out.println("outFolder "+outFolder);

		// create list of input XMP files
		DTConfList dtConfList = new DTConfList();
		addXmpFromFolder(dtConfList, xmpFolder);		
		
		// interpolation of all XMP data and generation of associated XMP
		DTConfList dtl = dtConfList.interpLinearAllParam(outFolder);
		
		// display before/after
		System.out.println("\nsource");
		dtConfList.printAllParamTable();
		System.out.println("\ninterp");
		dtl.printAllParamTable();
		
		// write file with corresponding commands : "darktable-cli FIC.RAW INTERP_FIC.RAW.XMP FIC.RAW.JPG"
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(outFolder+"/"+outMasterFile));
			Iterator<DTConfiguration> itDTL = dtl.iterator();
			while (itDTL.hasNext()) {
				DTConfiguration dtc = itDTL.next();
				String fic = dtc.srcFile;
				// for fic in `ls $xmpSrc`;do src=${fic%.*} ; echo $src; darktable-cli $rawSrc/$src $outFolder/$src.jpg; done
				out.write("darktable-cli '"+rawFolder+"/"+fic+"' '"+outFolder+"/"+fic+".xmp' '"+outFolder+"/"+fic+".jpg' --width "+exportWidth+" --height "+exportHeight+"\n");
			}
			out.close();
		} catch (IOException e) {}


		System.out.println("\nNow launch in a terminal :\n. '"+outFolder+"/"+outMasterFile+"'\n\nEnd of TLDT");
		
	}
	

	public static void addXmpFromFolder(DTConfList dtConfList, String folderPath){
		ArrayList<String> xmpFiles = new ArrayList<String>();
		xmpFiles = getXmpFileInFolder(folderPath);
		Iterator<String> it = xmpFiles.iterator();
		while(it.hasNext()) {
			dtConfList.addXmp(folderPath+"/"+it.next());
		}
	}
	
	public static ArrayList<String> getXmpFileInFolder(String folderPath) {
		ArrayList<String> files = new ArrayList<String>();
		
		String file;
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles(); 

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				file = listOfFiles[i].getName();
				if (file.endsWith(".xmp") || file.endsWith(".XMP")){
					//System.out.println(file);
					files.add(file);
				}
			}
		}
		return files;
	}
	
	


}
