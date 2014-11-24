package org.dttimelapse.gui;

/*
Copyright 2014 Rudolf Martin

This file is part of DTTimelapse.

DTTimelapse is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

DTTimelapse is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with DTTimelapse.  If not, see <http://www.gnu.org/licenses/>.
*/

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.dttimelapse.math.MovingAverage;




public class DirectoryMethods {
	// some larger methods, shifted from mainGui
	//  for better maintaining

	private static final long serialVersionUID = 1L;

	
	private MainGui mg;
	
	public DirectoryMethods(MainGui gui) {
		
		mg = gui;
		
	}
	
	
	
	//*******************************************************************************

	public void newDirectory() { // check new choosen directory
		// System.out.println("File " + pathname + " has been "
		// + (evt.isAddedPath() ? "selected" : "deselected"));
		// System.out.println("File object is " + file);

		// change label
		mg.labelDirectory.setText(mg.activePathname);

		mg.activeNumber = 0;

		// set area for luminance to max
		mg.drawingPanel.x1 = 0;
		mg.drawingPanel.y1 = 0;
		mg.drawingPanel.x2 = 600;
		mg.drawingPanel.y2 = 400;
		
		mg.cbClipping.setSelected(false);
		mg.cbLumi.setSelected(false);

		mg.picModel = new PictureModel(); // empty modell

		if (mg.deflickerButton.isSelected()) {
			mg.deflickerButton.doClick(); // reverse the button
		}

		// Scan the directory to find all images
		try {
			mg.activeNumber = scanDirectory();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mg.picTable.setModel(mg.picModel); // new data
		mg.fixTable.setModel(mg.picModel); // model with fixed columns

		// System.out.println("Total number is " + activeNumber);

		if (mg.activeNumber > 0) {
			// pictures found

			// set column width
			mg.picTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			mg.fixTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

			TableColumn col;

			TableColumnModel tcm = mg.picTable.getColumnModel();
			TableColumnModel tcmfix = new DefaultTableColumnModel(); // empty

			// make some settings to fix the fist 3 columns of jtable
			// ******************
			// build a second table from the original
			// put in fixed columns
			// remove fixed columns from first table
			col = tcm.getColumn(0);
			tcm.removeColumn(col);
			tcmfix.addColumn(col);
			col = tcm.getColumn(0);
			tcm.removeColumn(col);
			tcmfix.addColumn(col);
			col = tcm.getColumn(0);
			tcm.removeColumn(col);
			tcmfix.addColumn(col);

			// column index shifted with remove !!

			// Set the widths of the columns for the
			// second table before we get its preferred size
			tcmfix.getColumn(0).setPreferredWidth(40); // index
			tcmfix.getColumn(1).setPreferredWidth(15); // key
			tcmfix.getColumn(2).setPreferredWidth(200); // filename

			mg.fixTable.setColumnModel(tcmfix); // install new col model
			// fixTable.setPreferredScrollableViewportSize(fixTable.getPreferredSize());

			// Keep row selection in sync ListSelectionModel
			// fixTable.setSelectionModel(picTable.getSelectionModel());
			// or
			ListSelectionModel lmodel = mg.picTable.getSelectionModel();
			mg.fixTable.setSelectionModel(lmodel);

			Dimension fixedSize = mg.fixTable.getPreferredSize();
			JViewport viewport = new JViewport();
			viewport.setView(mg.fixTable);
			viewport.setPreferredSize(fixedSize);
			viewport.setMaximumSize(fixedSize);

			// put header of second table in top left corner
			mg.tablePane.setCorner(JScrollPane.UPPER_LEFT_CORNER,
					mg.fixTable.getTableHeader());

			// put second table in row header
			mg.tablePane.setRowHeaderView(viewport);

			// Set appropriate column widths of picTable
			for (int i = 0; i < widths.length; i++) {
				col = tcm.getColumn(i);
				col.setMinWidth(widths[i]);
				col.setPreferredWidth(widths[i]);
			}

			// picTable.getColumn( "Index" ).setPreferredWidth( 40 );
			// picTable.getColumn( "Key" ).setPreferredWidth( 15 );
			// picTable.getColumn( "Filename" ).setPreferredWidth( 200 );
			// picTable.getColumn( "Aperture" ).setPreferredWidth( 60 );
			// picTable.getColumn( "ExposureTime" ).setPreferredWidth( 60 );
			// picTable.getColumn( "ISO" ).setPreferredWidth( 40 );
			// picTable.getColumn( "Width" ).setPreferredWidth( 50 );
			// picTable.getColumn( "Height" ).setPreferredWidth( 50 );
			// picTable.getColumn( "DateTaken" ).setPreferredWidth( 150 );
			// picTable.getColumn( "Mean" ).setPreferredWidth( 50 );

			try {
				scanPreview(); // extract previews
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				calcLuminance(); // calculate luminance + smooth
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// initial settings
			mg.meanPanel.setVisible(false);
			mg.meanOptPanel.setVisible(false);
			mg.pointerPanel.setVisible(false);
			mg.drawingPanel.setVisible(false);
			mg.keyframePanel.setVisible(false);

			mg.clippingPanel.setDimension((int) mg.picModel.getValueAt(0, 6),
					(int) mg.picModel.getValueAt(0, 7));

			mg.activeIndex = 0;
			mg.picSlider.setValue(mg.activeIndex); // picslider refresh
			mg.picSlider.setMaximum(mg.activeNumber - 1); // picSlider refresh

		} else {

			// no images found
			mg.picSlider.setMaximum(0);

			mg.picturePanel.loadLogo();
			mg.picturePanel.repaint();
		}

	} // end of newDirectory

	// Column widths
	protected int[] widths = { 60, 60, 60, 60, 60, 150, 60, 60, 60, 60, 60, 60,
			60, 60, 60, 60, 60, 60, 60, 60 };

	//*******************************************************************************
	
	public int scanDirectory() throws Exception {
		// extract exif infos
		// sort all pictures by name

		mg.progressBar.setIndeterminate(true);
		mg.progressBar.paint(mg.progressBar.getGraphics()); // not very good

		// extract info of all files in directory
		// exiftool -csv -ext JPG -ext NEF -ext CR2 -ext DNG
		// -aperture -shutterspeed -iso -ImageWidth -ImageHeight -createdate .
		// String[] cmdArrayEx = {"D:\\Programme\\exiftool\\exiftool.exe",
		// "-csv",

		String[] cmdArrayEx = { "exiftool", "-csv", "-ext", "JPG", "-ext",
				"NEF", "-ext", "CR2", "-ext", "DNG", "-aperture",
				"-shutterspeed", "-iso", "-ExifImageWidth", "-ExifImageHeight",
				"-createdate", "directory" };

		cmdArrayEx[16] = mg.activePathname;

		ProcessBuilder processBuilder = new ProcessBuilder(cmdArrayEx);
		Process process = processBuilder.start();

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));

		String line;

		List<String> listA = new ArrayList<String>();

		// store all output in list
		while ((line = stdInput.readLine()) != null) {

			// skip first line with tag infos
			if (line.startsWith("SourceFile")) {
				continue;
			}

			// System.out.println(line);

			if (line != null) {
				listA.add(line); // add line to list
			}

		} // end of while

		while ((line = stdError.readLine()) != null) {
			// nothing to do
		}

		// now we have a list with all output of exiftool
		// sort the list according to pathname (first element of line)
		Collections.sort(listA);

		// second turn to fetch the data and put it in picModel
		// access list via new for-loop
		int i = 0;
		for (Object object : listA) {
			String element = (String) object;

			//System.out.println(i + element);
			
			String[] splittedLine = element.split(",");
			String pathname = splittedLine[0];

			String name = pathname.substring(pathname.lastIndexOf("/") + 1,
					pathname.length());

			//System.out.println(i + name);

			Vector<Comparable> vec = new Vector<Comparable>(); // data to add to
																// table

			//TODO check datatype of exifdata 
			
			if (splittedLine.length == 7) {
				// create vector and add to table
				// create vector with data
				vec.add(i);
				vec.add(false);
				vec.add(name);
				vec.add(splittedLine[1]); // aperture
				vec.add(splittedLine[2]); // shutterspeed
				vec.add(splittedLine[3]); // iso
				// int iso = Integer.parseInt(splittedLine[3]);

				int width, height;
				if (isInteger(splittedLine[4])) {
					width = Integer.parseInt(splittedLine[4]);
				} else {
					width = 0;
				}
				if (isInteger(splittedLine[5])) {
					height = Integer.parseInt(splittedLine[4]);
				} else {
					height = 0;
				}
				
				//System.out.println("w= " + width + " h= " + height);

				// vec.add(iso);
				vec.add(width);
				vec.add(height);

				vec.add(splittedLine[6]); // date taken
			} else {
				// some corrupt pics have no exifdata
				vec.add(i);
				vec.add(false);
				vec.add(name);
				vec.add("");
				vec.add("");
				vec.add("");
				vec.add(0);
				vec.add(0);
				vec.add("");
			}

			mg.picModel.addData(vec);
			i++;

		} // end for-loop of list

		process.waitFor();

		mg.progressBar.setIndeterminate(false);

		return i; // activeNumber
	} // end of exiftool

	// check for integer in string
	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}
	
	//*******************************************************************************
	
	public void scanPreview() throws Exception, IOException,
			InterruptedException { // create previews

		File dir = new File(mg.activePathname + "/preview");
		// attempt to create the directory here
		boolean successful = dir.mkdir();
		if (successful) {
			// System.out.println("directory was created successfully");
		}

		// imagemagick convert overwrites existing files without warning
		// command= convert path+fname -resize 750x500 path+/preview/+fname

		final String[] cmdArrayJpg = { "convert", "input", "-resize",
				"750x500", "output" };

		final String[] cmdArrayExif = { "exiftool", "input", "-b",
				"-previewimage" };

		final String[] cmdArrayConvert = { "convert", "-", "-resize",
				"750x500", "output" };

		// set progressbar
		mg.progressBar.setMaximum(mg.activeNumber);
		mg.progressBar.setString("Extract previews");

		// define new Thread as inline class
		Thread threadConvert = new Thread() {
			public void run() {

				for (int ii = 0; ii < mg.activeNumber; ii++) {
					// create preview of every picture
					// do only one call at time, to avoid system overload

					// String fullname = (String) picTable.getValueAt(ii, 2); //
					// uses jtable, index can change

					String fullname = (String) mg.fixTable.getValueAt(ii, 2); // uses
																			// tablemodell

					int dot = fullname.lastIndexOf(".");
					String name = fullname.substring(0, dot);
					String extension = fullname.substring(dot + 1);

					String output = mg.activePathname + "/preview/" + name
							+ ".jpg";
					if (new File(output).isFile()) {
						// if target file exists, then jump to next loop
						continue;
					}

					if (extension.equalsIgnoreCase("jpg")) {
						// set command for JPG
						// set input
						cmdArrayJpg[1] = mg.activePathname + "/" + fullname;
						// set output
						cmdArrayJpg[4] = output;

						// System.out.println(Arrays.toString(cmdArrayJpg));

						// A Runtime object has methods for dealing with the OS
						Runtime r = Runtime.getRuntime();
						Process p = null; // Process tracks one external native
											// process
						BufferedReader is; // reader for output of process
						String line;

						// Our argv[0] contains the program to run; remaining
						// elements
						// of argv contain args for the target program. This is
						// just
						// what is needed for the String[] form of exec.
						try {
							p = r.exec(cmdArrayJpg);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// getInputStream gives an Input stream connected to
						// the process p's standard output. Just use it to make
						// a BufferedReader to readLine() what the program
						// writes out.
						// is = new BufferedReader(new
						// InputStreamReader(p.getInputStream()));

						// try {
						// while ((line = is.readLine()) != null)
						// System.out.println(line);
						// } catch (IOException e1) {
						// // TODO Auto-generated catch block
						// e1.printStackTrace();
						// }

						try {
							p.waitFor(); // wait for process to complete
						} catch (InterruptedException e) {
							System.err.println(e); // "Can'tHappen"
							return;
						}
						// System.err.println("Process done, exit status was " +
						// p.exitValue());

					} else {
						// rawfile needs two exiftool and convert
						// set command for raw
						// set input
						cmdArrayExif[1] = mg.activePathname + "/" + fullname;
						// set output
						cmdArrayConvert[4] = output;

						// System.out.println(Arrays.toString(cmdArrayExif));
						// System.out.println(Arrays.toString(cmdArrayConvert));

						Runtime r = Runtime.getRuntime();
						// Start two processes: exiftool .. | convert ...
						Process p1 = null;
						try {
							p1 = r.exec(cmdArrayExif);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// convert will wait for input on stdin
						Process p2 = null;
						try {
							p2 = r.exec(cmdArrayConvert);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// Create and start Piper
						Piper pipe = new Piper(p1.getInputStream(),
								p2.getOutputStream());
						new Thread(pipe).start();
						// Wait for second process to finish
						try {
							p2.waitFor();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// Show output of second process
						// java.io.BufferedReader r = new
						// java.io.BufferedReader(new
						// java.io.InputStreamReader(p2.getInputStream()));
						// String s = null;
						// while ((s = r.readLine()) != null) {
						// System.out.println(s);
						// }
					}

					mg.progressBar.setValue(ii);
					mg.progressBar.paint(mg.progressBar.getGraphics()); // not very
																	// good

				} // end for-loop

				// all previews created - reset progressbar
				mg.progressBar.setMaximum(0);
				mg.progressBar.setString("");

			} // end of run()
		};
		threadConvert.start(); // convert files in one separate thread

		// wait some time to extract first preview, otherwise the GUI is faster
		// than the previews and shows "picture not found"
		Thread wait = new Thread();
		wait.sleep(500);

	} // end scanPreview

	
	//*******************************************************************************

	
	public void calcLuminance() throws Exception { // calculate luminance of
													// preview images
		// TODO define picture area for calculation
		// /usr/bin/convert is used, could be replaced by imagej in
		// newer version to prevent dependancies

		// -region 600x400+10+20 = width x height + offsetx + offsety
		//
		// panelarea is 600x400, imagepreview has 750x500
		//
		int width = (int) ((mg.drawingPanel.x2 - mg.drawingPanel.x1) * 1.25);
		int height = (int) ((mg.drawingPanel.y2 - mg.drawingPanel.y1) * 1.25);
		int offX = (int) (mg.drawingPanel.x1 * 1.25);
		int offY = (int) (mg.drawingPanel.y1 * 1.25);

		final String parameter = String.valueOf(width) + "x"
				+ String.valueOf(height) + "+" + String.valueOf(offX) + "+"
				+ String.valueOf(offY);

		//System.out.println(parameter);

		final String[] cmdArrayCrop = { "convert", "inputfile", "-crop",
				"parameter", "-" };
		cmdArrayCrop[3] = parameter;

		final String[] cmdArrayLum = { "convert", "-", "-scale", "1x1!",
				"-format", "%[fx:luminance]", "info:" };
		// cmdArrayLum[3] = parameter;

		Thread threadCalc = new Thread() { // define new Thread as inline class
			public void run() {

				Double luminance = 0.0;

				for (int i = 0; i < mg.activeNumber; i++) {

					String fullname = (String) mg.fixTable.getValueAt(i, 2);
					String name = fullname.substring(0,
							fullname.lastIndexOf("."))
							+ ".jpg";

					// set preview image as input
					cmdArrayCrop[1] = mg.activePathname + "/preview/" + name;

					// System.out.println(Arrays.toString(cmdArrayLum));

					// wait if preview is not yet created
					// TODO beware of endless loops
					while (new File(mg.activePathname + "/preview/" + name)
							.isFile() == false) {
						// preview file don't exist
						// do nothing and wait
						Thread wait = new Thread();
						try {
							wait.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					;

					// System.out.println(Arrays.toString(cmdArrayCrop));
					// System.out.println(Arrays.toString(cmdArrayLum));

					BufferedReader is; // reader for output of process
					String line;

					Runtime r = Runtime.getRuntime();
					// Start two processes: convert crop .. | convert fx ...
					Process p1 = null;
					try {
						p1 = r.exec(cmdArrayCrop);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// convert will wait for input on stdin
					Process p2 = null;
					try {
						p2 = r.exec(cmdArrayLum);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// Create and start Piper
					Piper pipe = new Piper(p1.getInputStream(),
							p2.getOutputStream());
					new Thread(pipe).start();
					// Wait for second process to finish
					try {
						p2.waitFor();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// get output of second process
					is = new BufferedReader(new InputStreamReader(
							p2.getInputStream()));

					try {
						while ((line = is.readLine()) != null) {
							// System.out.println(line);

							luminance = Double.valueOf(line);

							// set value in table
							// luminance mean is in column 9
							mg.picModel.setValueAt(luminance, i, 9);

							mg.picTable.repaint(); // Repaint all the component
												// (all Cells).
							// better use
							// ((AbstractTableModel)
							// jTable.getModel()).fireTableCellUpdated(x, 0); //
							// Repaint one cell.
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} // end for-loop

				// save luminance curve ----------------------------------------
				// create array with new y-value
				double[] x, y;
				x = new double[mg.activeNumber];
				y = new double[mg.activeNumber];

				for (int i = 0; i < mg.activeNumber; i++) {
					x[i] = i;
					y[i] = (double) mg.picModel.getValueAt(i, 9);

					// System.out.println (picTable.getValueAt(i, 3));
					// System.out.println("x= " + x[i] + " y= " + y[i]);
				}
				mg.meanPanel.setCoord(x, y);

				// double[] yy = { 2, 2, 3, 3, 5, 3, 3, 1 }; // testing

				// calculate smoothing curve, but don't display this until
				// deflicker is active
				// we must do the calculation at this place, otherwise the
				// calc. of luminance is slower than this process
				int range = mg.deflicSlider.getValue();
				MovingAverage ma = new MovingAverage(y); // set y-values
				// store new smoothing line as moving average
				for (int i = 0; i < mg.activeNumber; i++) {
					y[i] = ma.calculate(i, range);
					// y[i] = ma.calculateWeighted( i, 2 );
					double lumi = (double) mg.picModel.getValueAt(i, 9);
					double flicker = lumi - y[i];
					mg.picModel.setValueAt(y[i], i, 10); // store smooth in table
					mg.picModel.setValueAt(flicker, i, 11); // store flicker in
															// table

					// System.out.println( "x= " + i + " y= " + yy[i] +
					// " aver= " + y[i] );
				}

				mg.meanOptPanel.setCoord(x, y);

				mg.layeredPane.repaint(); // after recalculating

			} // end of run()

		};
		threadCalc.start();

	} // end calcLuminance

}
