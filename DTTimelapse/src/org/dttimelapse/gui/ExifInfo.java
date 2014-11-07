package org.dttimelapse.gui;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

// original code from Blog of Guy Bashan:
//   "Extract image Exif information using Java and ExifTool"

public class ExifInfo {
	// This class stores ALL Exif information given by exiftool in a hashmap
	// another approach is to get every needed info separately

	private Map<String, String> exifMap = new HashMap<String, String>();
	
	private static final String APERTURE = "Aperture Value";
	private static final String EXPOSURE_TIME = "Exposure Time";
	private static final String ISO = "ISO";
	private static final String WIDTH = "Image Width";
	private static final String HEIGHT = "Image Height";
	private static final String DATE_TAKEN = "Create Date";

	// path to exiftool
	//private static String exifToolApp = new String("D:\\Programme\\exiftool\\exiftool.exe");  // Win
	private static String exifToolApp = new String("exiftool");  // Linux

	// constructor
	public ExifInfo(String imagepath) throws IOException, InterruptedException {

		
		ProcessBuilder processBuilder = new ProcessBuilder(exifToolApp, imagepath);
		Process process = processBuilder.start();

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		String line;

		while ((line = stdInput.readLine()) != null) {
			int pos = line.indexOf(":");
			if (pos != -1) {
				exifMap.put(line.substring(0, pos).trim(), line.substring(pos + 1).trim());
			}
		}

		while ((line = stdError.readLine()) != null) {

		}

		process.waitFor();

	} // end construct


	// methods to get some specific exif information
 
	public String getAperture() {
	    return exifMap.get(APERTURE);
	}
	  	
	public String getExposureTime() {
		return exifMap.get(EXPOSURE_TIME);
	}

	public String getISO() {
		return exifMap.get(ISO);
	}

	public String getDateTaken() {
		return exifMap.get(DATE_TAKEN);
	}

	public String getWidth() {
		return exifMap.get(WIDTH);
	}

	public String getHeight() {
		return exifMap.get(HEIGHT);
	}

	public String toString() {
		return "Aperture: " + getAperture() + "\n" +
			"Shutter speed: " + getExposureTime() + "\n" +
			"ISO: " + getISO() + "\n" +
			"Width: " + getWidth() + "\n" +
			"Height: " + getHeight() + "\n" +
			"Date/Time: " + getDateTaken() + "\n";
	}
}