package org.dttimelapse.gui;

/**
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.print.DocFlavor.URL;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.TreePath;


import org.dttimelapse.math.Polynomal;

 
public class MainGui extends JComponent {	  	
	
	public static String version = "0.1beta";
	
	// global variables
	public static String activePathname;
	public static Integer activeIndex;
	public static Integer activeNumber;		
	
//	DefaultListModel listModel;
//	JList list;
	
	private JLabel labelDirectory;
	private JPanel sliderPanel;
	
	JSlider picSlider;
	
	private JSlider deflicSlider;
	private JScrollPane scrollPane;
	
	private JLayeredPane layeredPane;
			
	private JPanel rightPanel, leftPanel, deflicRow2Panel,  progressPanel;
	
	
	
	JScrollPane tablePane;
	
	private JPanel basicWorkflowPanel, deflicWorkflowPanel;
	
	private PolygonPanel meanPanel, meanOptPanel;
	private PointerPanel pointerPanel;
	private DrawingPanel drawingPanel; 
	private KeyframePanel keyframePanel;
	
	private JProgressBar progressBar;

	private JSplitPane splitPane;
	
	private JButton loadButton, transButton, saveButton, exportButton, renderButton;
	
	private JButton dloadButton, dtransButton, dsaveButton, dexportButton, drenderButton ;

	private JToggleButton deflickerButton;
	
	private JButton dlumiButton;
	
	private JButton playButton, stopButton, resetButton;
    
	ListSelectionModel listSelectionModel;
    
    // specific classes of DTTimelapse GUI
    SlideShow slideShow;
	PicturePanel picturePanel; // jpanel with image display
	DTTPreferences dttPref;   // preferences
	JTable picTable, fixTable;
	PictureModel picModel;
	
	
    public MainGui() {                  //constructor    	  
  
	    ///  custom Nimbus Look+Feel ----------------------------------------
  		try {
 			  UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
 			} catch( Exception e ) { e.printStackTrace(); }
 	
 		UIManager.put("nimbusBase", new Color(0, 0, 0));      //tabs black
 		UIManager.put("nimbusBlueGrey", new Color( 80, 80, 80));  //menubar
 		UIManager.put("control", new Color(  80, 80, 80));       //background darkgray
 		
 		UIManager.put("List.background", new Color(150, 150, 150)); 
 		UIManager.put("Table.background", new Color(150,150,150)); 
 		//UIManager.put("List.foreground", new Color( 255,255,255)); 
 		UIManager.put("Tree.background", new Color(150, 150, 150)); 
 		//UIManager.put("Tree.foreground", new Color( 255,255,255));  		
 		
 		UIManager.put("nimbusLightBackground", new Color(150,150,150));  // works for jlist
 		
 		// -------------------- Global settings -------------------------------------- 		
 		activeNumber = 0;
 		activeIndex = 0;
 		
 	   	// create Preference Object and load
    	dttPref = new DTTPreferences();
		dttPref.loadPreferences();
		
        // create component for slideshow
		slideShow = new SlideShow(this);	
		
    	// create frame with menubar
		// herein we start preferences and choose dirs 
        FrameWithMenu f = new FrameWithMenu(this, dttPref);

        labelDirectory = new JLabel("");	

		//----------------------------------------------------------------------
		// initialize jtable
		// Our TableModel 
		picModel = new PictureModel();        
		picTable = new JTable(picModel);

		picTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		listSelectionModel = picTable.getSelectionModel();
		
	    listSelectionModel.addListSelectionListener(new SelectionListener());
	    
	    picTable.setSelectionModel(listSelectionModel);

    
	    fixTable = new JTable(picModel);
	    fixTable.setSelectionModel(listSelectionModel);
	    
	    
        // panels on left side *************************************** 
   
        JLabel labelLeft = new JLabel("Preview");

        picturePanel = new PicturePanel();
        picturePanel.setBounds(0, 0, 600, 400);  // mandatory for layeredpane?
        
        JPanel treePanel = new JPanel();
        
        try {
			final FileTree ft = new FileTree(dttPref.prefTreeDirectory);
			
			ft.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent evt) {					
										
					TreePath path = evt.getPath();					
					activePathname = ft.getPathName(path);
					
					newDirectory();			// check new directory					
				}
			});			
			
			ft.setPreferredSize(null);
			ft.setMinimumSize(new Dimension(450,0));			
	    	
			treePanel.setLayout(new GridLayout(0, 1)); // extends width of jtree
			treePanel.add(new JScrollPane(ft));			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
        
        //treePanel.setPreferredSize(new Dimension(400,400));
        //treePanel.setMaximumSize(new Dimension(400,400));        
        
        {
	        meanPanel = new PolygonPanel();
	        //meanPanel.setCoord(x, y);
	        meanPanel.setForeground(Color.blue);
	        //meanPanel.setPreferredSize(new Dimension(450, 300));
	        //meanPanel.setMinimumSize(new Dimension(450, 300));
	        meanPanel.setBounds(0, 0, 600, 400); // mandatory to display
	        meanPanel.setOpaque(false);
        }

        {
	        meanOptPanel = new PolygonPanel();
 	        //meanOptPanel.setCoord(x, y);
	        meanOptPanel.setForeground(Color.yellow);
	        //meanPanel.setPreferredSize(new Dimension(450, 300));
	        //meanPanel.setMinimumSize(new Dimension(450, 300));
	        meanOptPanel.setBounds(0, 0, 600, 400); // mandatory to display
	        meanOptPanel.setOpaque(false);
        }

        {
	        pointerPanel = new PointerPanel();
	        pointerPanel.setForeground(Color.gray);
	        //pointerPanel.setPreferredSize(new Dimension(450, 300));
	        //pointerPanel.setMinimumSize(new Dimension(450, 300));
	        pointerPanel.setBounds(0, 0, 600, 400); // mandatory to display
	        pointerPanel.setOpaque(false);
        }

        {
	        drawingPanel = new DrawingPanel();
	        drawingPanel.setForeground(Color.gray);
	        //pointerPanel.setPreferredSize(new Dimension(450, 300));
	        //pointerPanel.setMinimumSize(new Dimension(450, 300));
	        drawingPanel.setBounds(0, 0, 600, 400); // mandatory to display
	        drawingPanel.setOpaque(false);
	        drawingPanel.setVisible(false);
        }
        
        {
	        keyframePanel = new KeyframePanel();
	        keyframePanel.setForeground(Color.yellow);
	        //pointerPanel.setPreferredSize(new Dimension(450, 300));
	        //pointerPanel.setMinimumSize(new Dimension(450, 300));
	        keyframePanel.setBounds(0, 0, 600, 400); // mandatory to display
	        keyframePanel.setOpaque(false);
        }
 
        
        
        
        layeredPane = new JLayeredPane(); // shows picture and curves

        layeredPane.add(picturePanel, JLayeredPane.FRAME_CONTENT_LAYER);  //  (-3000)
      
        layeredPane.add(meanPanel,   0);  //    original mean of luminance
        layeredPane.add(meanOptPanel,   1);  // optimized mean of luminance
        layeredPane.add(pointerPanel,   2);  // pointer of activeindex
        layeredPane.add(drawingPanel,   3);  // area for luminance calculation        
        layeredPane.add(keyframePanel,  4);  // area for keyframe icons        
       
        
        layeredPane.setPreferredSize(new Dimension(600,400));
        
        // leftpanel 
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        
        leftPanel.add(labelLeft);
        leftPanel.add(layeredPane);
        leftPanel.add( sliderPanel() );
        leftPanel.add(treePanel);
        


        
        // panels on right side *********************************       
        
        // Erzeugung eines JTabbedPane-Objektes
        JTabbedPane tabbedPane = new JTabbedPane
            (JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );
 
        // Hier werden die JPanels als Registerkarten hinzugefgt
        tabbedPane.addTab("Basic workflow", basicWorkflowPanel() );
        tabbedPane.addTab("Deflicker workflow", deflicWorkflowPanel() );
        tabbedPane.addTab("Interpolation", interpolationPanel() );
        tabbedPane.setMaximumSize( tabbedPane.getPreferredSize() );
        tabbedPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // table with image information
        JPanel tablePanel = new JPanel();
        
        //tablePanel.setLayout(new GridLayout(1, 0)); // extends width of table       
        tablePanel.setLayout(new BorderLayout()); //
        
                
        
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        
        rightPanel.add( progressPanel() );
        rightPanel.add(tabbedPane);
        rightPanel.add(labelDirectory);
 //       rightPanel.add(tablePanel);    // no use of jlist anymore
        
        
        tablePane = new JScrollPane(picTable);
        rightPanel.add(tablePane);        
        //rightPanel.add(Box.createVerticalGlue());   // decrease hight of tablepanel  
        
        
        
    
       
        
        
        // splitpane
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(601);   // set initial size
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
         
        f.add(splitPane);
                 
        f.pack();
        
        f.setSize(1500,1000);
        
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);      
        
        // load logo
        //picturePanel.loadImage("icon/label_dttimelapse.png");
        picturePanel.loadLogo(); 
        picturePanel.repaint();
        
        
        // add some abstract listeners for sliders
        picSlider.addChangeListener( new ChangeListener() {  // ----------------  picSlider
        	@Override public void stateChanged( ChangeEvent e ) {
        		
        		activeIndex=picSlider.getValue();
      		
        		if (activeNumber <= 0) { return;}   // no action without pics
      		
       		
        		//jtable
        		//picTable.setRowSelectionInterval(activeIndex, activeIndex);
      		
        		picTable.getSelectionModel().setSelectionInterval(activeIndex, activeIndex);
        		picTable.scrollRectToVisible(new Rectangle(picTable.getCellRect(activeIndex, 0, true)));
      		
	      	
      			pointerPanel.setCoord(activeIndex, activeNumber);
      			pointerPanel.repaint();

      		  
	        	// changes in picSlider shows selected picture
	        	picRefresh();    	     
      	  }
      	} );
 
        
        deflicSlider.addChangeListener( new ChangeListener() {  // ----------------  deflicSlider
        	@Override public void stateChanged( ChangeEvent e ) {
        		//TODO update exposure curve
  			
        		//  int order = -1;  // order of polynom, max number of points
        		int order = deflicSlider.getValue();
        		
        		//calculate smoothing curve
        		// create array with new y-value of luminance
        		double[] x, y;
        		x = new double[activeNumber];
        		y = new double[activeNumber];
        		
	  	  		for (int i = 0; i < activeNumber; i++) {
	  	  			x[i] = i;
	  				y[i] = (double) picModel.getValueAt(i, 9);   // lumi
	  				
	  				//System.out.println (picTable.getValueAt(i, 3));
	  				//System.out.println("x= " + x[i] + " y= " + y[i]);
	  			}
	          
//	  	  		System.out.println ( Arrays.toString(x)   );   
//	  	  		System.out.println ( Arrays.toString(y)   );  
	  	  		
    		 	Polynomal poly = new Polynomal(x, y, order); 		    
         		
    		 	// display new polyline
    		 	// create array with new y-value
    		 	
    		 	for (int i = 0; i < activeNumber; i++) {
  	  				x[i] = i;
  	  				y[i] = poly.calculate(i);  	  				
 	  				double flicker =  (double) picModel.getValueAt(i, 9) - y[i];
 	  				picModel.setValueAt( y[i], i, 10); // store smooth in table
 	  				picModel.setValueAt( flicker, i, 11); // store smooth in table
    		 	}
    		 	
    		 	picTable.repaint(); // Repaint all the component (all Cells).
		        // better use 
		        // ((AbstractTableModel) jTable.getModel()).fireTableCellUpdated(x, 0); // Repaint one cell.
    		 	
 
//    		 	System.out.println("x= " + x[1] + " y= " + y[1]);		 	
//       		System.out.println("x= " + x[15] + " y= " + y[15]);
    		 	
    		 	meanOptPanel.setCoord(x, y);
    		 	meanOptPanel.repaint();
  	        
    		 	layeredPane.repaint();

        	}
        });

          
    } // end of constructor
    

    
 
    
	public void picRefresh(){
		// load and display active picture
 		//picturePanel.loadImage(activePathname + "/preview/" +picTable.getValueAt(activeIndex, 2));   // old
		//picturePanel.repaint();
		//meanPanel.repaint();
		
		if (activeNumber < 1) return;
		
		// set extension of filename to "jpg" 
		String fullname = (String) fixTable.getValueAt(activeIndex, 2);
		String name = fullname.substring(0, fullname.lastIndexOf(".")) + ".jpg";
			
		//System.out.println("name= " + name);
		
		picturePanel.loadImage(activePathname + "/preview/" + name);   
		
		layeredPane.repaint();
	}    
    
    
	// methods to create some parts of the GUI
	public JPanel sliderPanel(){
		// panel with slider, start and stop button
		picSlider = new JSlider();
 
		playButton = new JButton("Play preview");
		stopButton = new JButton("Stop");
		resetButton = new JButton("Reset");
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(playButton);
		buttonPanel.add(stopButton);
		buttonPanel.add(resetButton);
		
		playButton.addActionListener(new ButtonListener());
		stopButton.addActionListener(new ButtonListener());
		resetButton.addActionListener(new ButtonListener());
		
		sliderPanel = new JPanel();
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		sliderPanel.add(picSlider);
		sliderPanel.add(buttonPanel);
		
		return sliderPanel;
	}
	
	public JPanel progressPanel(){
		// Panel with programname and progressbar
		
		progressPanel = new JPanel();
		// progressPanel.setLayout(new BorderLayout());
        
		JLabel labelSoftware = new JLabel("DTTimelapse");
		
		progressPanel.add(labelSoftware);
        
		progressBar = new JProgressBar();
		progressBar.setMaximum(0);
		progressBar.setStringPainted(true);
		
		progressPanel.add(progressBar);
		progressPanel.setMaximumSize( progressPanel.getPreferredSize() );
		progressPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		return progressPanel;
	} 
    
      
	public JPanel basicWorkflowPanel(){
		// basic workflow
		loadButton = new JButton("Load XMP");
		transButton = new JButton("Interpolation");
		saveButton = new JButton("Save XMP");	
		exportButton = new JButton("Export Frames");
		renderButton = new JButton("Render Video");

		loadButton.addActionListener(new ButtonListener()); 
		transButton.addActionListener(new ButtonListener()); 
		saveButton.addActionListener(new ButtonListener()); 
		exportButton.addActionListener(new ButtonListener()); 
		renderButton.addActionListener(new ButtonListener()); 
   
		JPanel basicWorkflowPanel = new JPanel();
		basicWorkflowPanel.add(loadButton);
		basicWorkflowPanel.add(transButton);    
		basicWorkflowPanel.add(saveButton);
		basicWorkflowPanel.add(exportButton);
		basicWorkflowPanel.add(renderButton);
		basicWorkflowPanel.setMaximumSize( basicWorkflowPanel.getPreferredSize() );
		
		return basicWorkflowPanel;
	}

	public JPanel deflicWorkflowPanel(){
		// deflicker workflow
		dloadButton = new JButton("Load XMP");
		dtransButton = new JButton("Interpolation");
		dsaveButton = new JButton("Save XMP");	
		dexportButton = new JButton("Export Frames");
		drenderButton = new JButton("Render Video");
		dlumiButton = new JButton("Recalculate luminance");
		
		dlumiButton.setToolTipText("Define rectangle in preview area!");
  
		dloadButton.addActionListener(new ButtonListener()); 
		dtransButton.addActionListener(new ButtonListener()); 
		dsaveButton.addActionListener(new ButtonListener()); 
		dexportButton.addActionListener(new ButtonListener()); 
		drenderButton.addActionListener(new ButtonListener()); 
		dlumiButton.addActionListener(new ButtonListener()); 
		
		deflickerButton = new JToggleButton("Deflicker");
		deflickerButton.addActionListener(new ButtonListener2());
		
		//deflickerButton.addActionListener(actionListener2);
		
		deflicSlider = new JSlider();
		deflicSlider.setMaximum(8);
		deflicSlider.setMajorTickSpacing(1);
		deflicSlider.setPaintTicks(true);
		deflicSlider.setValue(4);          // initial value
		
		JPanel deflicRow1Panel = new JPanel();
		deflicRow1Panel.add(dloadButton);    
		deflicRow1Panel.add(dtransButton);    
		deflicRow1Panel.add(deflickerButton);    
		deflicRow1Panel.add(dsaveButton);
		deflicRow1Panel.add(dexportButton);
		deflicRow1Panel.add(drenderButton);
		
		//deflicRow1Panel.setMaximumSize( basicWorkflowPanel.getPreferredSize() );
		//deflicWorkflowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		deflicRow2Panel = new JPanel();
		deflicRow2Panel.add(new JLabel("Order of polynom"));    
		deflicRow2Panel.add(deflicSlider);   
		deflicRow2Panel.setVisible(false);
		deflicRow2Panel.add(dlumiButton); 
		 
		JPanel deflicWorkflowPanel = new JPanel();
		deflicWorkflowPanel.setLayout(new BorderLayout());
		deflicWorkflowPanel.add(deflicRow1Panel, BorderLayout.NORTH);    
		deflicWorkflowPanel.add(deflicRow2Panel, BorderLayout.SOUTH);    
		
		return deflicWorkflowPanel;
	}
	
	public JPanel interpolationPanel(){
		// Interpolation settings
		JRadioButton radio1=new JRadioButton("linear");  
		JRadioButton radio2=new JRadioButton("spline");  
		radio1.setSelected(true); 
		
		ButtonGroup bg=new ButtonGroup();  
		bg.add(radio1);
		bg.add(radio2);  
		
		JPanel interpolationPanel = new JPanel();
		interpolationPanel.add(new JLabel("Interpolation setting"));
		interpolationPanel.add(radio1);
		interpolationPanel.add(radio2);    
		
		return interpolationPanel;
	}
    
	
	public void newDirectory() {   // check new choosen directory 
	//		System.out.println("File " + pathname + " has been "
	//		+ (evt.isAddedPath() ? "selected" : "deselected"));
	//	System.out.println("File object is " + file);
		
		// change label
		labelDirectory.setText(activePathname);
		
		activeNumber = 0;
		
		// set area for luminance to max
		drawingPanel.x1 = 0;
		drawingPanel.y1 = 0;
		drawingPanel.x2 = 600;
		drawingPanel.y2 = 400;

		picModel = new PictureModel();  //empty modell		
		
		if (deflickerButton.isSelected()) {  
	    	deflickerButton.doClick();   // reverse the button
		}		
		
		// Scan the directory to find all images
		try {
			activeNumber = scanDirectory();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}							
		
		picTable.setModel(picModel);  // new data			
		fixTable.setModel(picModel);  // model with fixed columns
				
        // System.out.println("Total number is " + activeNumber);
		
        
		if (activeNumber > 0) {			
			// pictures found			
			
			// set column width
			picTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );	    
			fixTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			
			TableColumn col;
			
			TableColumnModel tcm = picTable.getColumnModel();
	        TableColumnModel tcmfix = new DefaultTableColumnModel();  //empty			
		       
	        // make some settings to fix the fist 3 columns of jtable ******************
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

	        // column index shifted with remove  !!
	        
			// Set the widths of the columns for the
			// second table before we get its preferred size
	        tcmfix.getColumn(0).setPreferredWidth(40);   // index	       
	        tcmfix.getColumn(1).setPreferredWidth(15);   // key
	        tcmfix.getColumn(2).setPreferredWidth(200);  // filename
	        

	        fixTable.setColumnModel(tcmfix); // install new col model
//			fixTable.setPreferredScrollableViewportSize(fixTable.getPreferredSize());

			// Keep row selection in sync   ListSelectionModel
			//fixTable.setSelectionModel(picTable.getSelectionModel());
			// or
			ListSelectionModel lmodel = picTable.getSelectionModel();
		    fixTable.setSelectionModel(lmodel);

		    Dimension fixedSize = fixTable.getPreferredSize();
		    JViewport viewport = new JViewport();
		    viewport.setView(fixTable);
		    viewport.setPreferredSize(fixedSize);
		    viewport.setMaximumSize(fixedSize);
		    
		    // put header of second table in top left corner
		    tablePane.setCorner(JScrollPane.UPPER_LEFT_CORNER, fixTable.getTableHeader());
		    
		    // put second table in row header
		    tablePane.setRowHeaderView(viewport);
	    
			// Set appropriate column widths of picTable
			for (int i = 0; i < widths.length; i++) {
				col = tcm.getColumn(i);
				col.setMinWidth(widths[i]);
				col.setPreferredWidth(widths[i]);
			}		

//			picTable.getColumn( "Index" ).setPreferredWidth( 40 );
//			picTable.getColumn( "Key" ).setPreferredWidth(  15 );
//			picTable.getColumn( "Filename" ).setPreferredWidth( 200 );
//			picTable.getColumn( "Aperture" ).setPreferredWidth(  60 );
//			picTable.getColumn( "ExposureTime" ).setPreferredWidth(  60 );
//			picTable.getColumn( "ISO" ).setPreferredWidth(  40 );
//			picTable.getColumn( "Width" ).setPreferredWidth(  50 );
//			picTable.getColumn( "Height" ).setPreferredWidth(  50 );
//			picTable.getColumn( "DateTaken" ).setPreferredWidth(  150 );
//			picTable.getColumn( "Mean" ).setPreferredWidth( 50  );		

			
			
		    try {
				scanPreview();            // extract previews
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		    try {
				calcLuminance();          // calculate luminance + smooth
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    // initial settings
		    meanPanel.setVisible(false);
		    meanOptPanel.setVisible(false);
		    pointerPanel.setVisible(false);
		    drawingPanel.setVisible(false);
		    
			activeIndex = 0;
			picSlider.setValue(activeIndex);  // picslider refresh						
			picSlider.setMaximum(activeNumber-1);   // picSlider refresh			
				
		} else {
			
			// no images found
			picSlider.setMaximum(0);
	
			picturePanel.loadLogo(); 
			picturePanel.repaint();
		}	
		
	}  //   end of newDirectory
	
   	// Column widths
	protected int[] widths = {
		60, 60 , 60 , 60, 60,  150, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60
	};


	
      
	public int scanDirectory() throws Exception{
		//extract exif infos
		// sort all pictures by name
		
		progressBar.setIndeterminate(true);
		progressBar.paint(progressBar.getGraphics());  // not very good		
        
		// extract info of all files in directory
		// exiftool -csv -ext JPG -ext NEF -ext CR2 -ext DNG 
		//          -aperture -shutterspeed -iso -ImageWidth -ImageHeight -createdate .
		//String[] cmdArrayEx = {"D:\\Programme\\exiftool\\exiftool.exe", "-csv", 
 
		String[] cmdArrayEx = {"exiftool", "-csv", 
				 "-ext", "JPG", "-ext", "NEF" , "-ext", "CR2", "-ext", "DNG",
				 "-aperture", "-shutterspeed", "-iso", "-ImageWidth", "-ImageHeight",
				 "-createdate", "directory" };

		cmdArrayEx[16] = activePathname;
       
		ProcessBuilder processBuilder = new ProcessBuilder(cmdArrayEx);
		Process process = processBuilder.start();

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		String line;
		
		List<String> listA = new ArrayList<String>();

		// store all output in list
		while ((line = stdInput.readLine()) != null) {
			
			//skip first line with tag infos
			if (line.startsWith("SourceFile")) { continue;   }
			
			//System.out.println(line);
			
			if (line != null) {					
				listA.add( line );		// add line to list				
			}	
			
		} // end of while
		
		while ((line = stdError.readLine()) != null) {
			// nothing to do
		}
		
		// now we have a list with all output of exiftool 
		// sort the list according to pathname (first element of line)		
		Collections.sort(listA);			
			
		// second turn to fetch the data and put it in picModel	
		//access list via new for-loop
		int i = 0;
		for(Object object : listA) {
		    String element = (String) object;
		    
			String[] splittedLine = element.split(",");
			String pathname = splittedLine[0];
			
			String name = pathname.substring( pathname.lastIndexOf("/")+1, pathname.length() );
			
			//System.out.println(i + name);
			
			Vector<Comparable> vec = new Vector<Comparable>(); // data to add to table
			
			if (splittedLine.length == 7) {
				// create vector and add to table
				// create vector with data					
				vec.add(i);
				vec.add(false);
				vec.add(name);					
				vec.add(splittedLine[1]);  // aperture
				vec.add(splittedLine[2]);  // shutterspeed
				vec.add(splittedLine[3]);  // iso
				//int iso = Integer.parseInt(splittedLine[3]);
				
				int width = Integer.parseInt(splittedLine[4]);
				int height = Integer.parseInt(splittedLine[5]);
				
				//vec.add(iso);
				vec.add(width);					
				vec.add(height);					
				
				vec.add(splittedLine[6]); // date taken
			} else {
				// some corrupt pics have no exifdata
				vec.add(i);
				vec.add(false);
				vec.add(name);					
			}			
							
			picModel.addData(vec);	
			
			i++;
				
		} // end for-loop of list		
		
		
		process.waitFor();
		
		progressBar.setIndeterminate(false);
		
		return i;   //  activeNumber
	}  //   end of exiftool
        
	
        
	public void scanPreview() throws Exception, IOException, InterruptedException{   // create previews
		
		File dir = new File(activePathname+"/preview");
		// attempt to create the directory here
		boolean successful = dir.mkdir();
		if (successful) {
			//System.out.println("directory was created successfully");
		}
	
		// imagemagick convert overwrites existing files without warning
		// command= convert path+fname -resize 750x500 path+/preview/+fname
        
    	final String[] cmdArrayJpg = {"convert", "input", "-resize", "750x500", "output"};       
        
     	final String[] cmdArrayExif = {"exiftool", "input", "-b", "-previewimage" };
 
    	final String[] cmdArrayConvert = {"convert" , "-", "-resize", "750x500", "output" };
	    
		// set progressbar
	    progressBar.setMaximum(activeNumber);
	    progressBar.setString("create previews");	    
	    
		// define new Thread as inline class
	    Thread threadConvert = new Thread()  {
			public void run() {				
	    
				for (int ii = 0; ii < activeNumber; ii++) {
					// create preview of every picture
					// do only one call at time, to avoid system overload
					
					
					//String fullname = (String) picTable.getValueAt(ii, 2);  // uses jtable, index can change
					
					
					String fullname = (String) fixTable.getValueAt(ii, 2);  // uses tablemodell
				
					
					int dot = fullname.lastIndexOf(".");
					String name = fullname.substring(0, dot);
					String extension = fullname.substring(dot + 1);	
					
					String output = activePathname + "/preview/" + name + ".jpg";					
					if (new File(output).isFile())  {
						// if target file exists, then jump to next loop
						continue;
					}
					
					if(extension.equalsIgnoreCase("jpg")){
						// set command for JPG						
						// set input
						cmdArrayJpg[1] = activePathname + "/" + fullname;
						// set output						
						cmdArrayJpg[4] = output;					         			         

						//System.out.println(Arrays.toString(cmdArrayJpg));
						
						// A Runtime object has methods for dealing with the OS
						Runtime r = Runtime.getRuntime();
						Process p = null;     // Process tracks one external native process
						BufferedReader is;  // reader for output of process
						String line;
						
						// Our argv[0] contains the program to run; remaining elements
						// of argv contain args for the target program. This is just
						// what is needed for the String[] form of exec.
						try {
							p = r.exec(cmdArrayJpg);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// getInputStream gives an Input stream connected to
						// the process p's standard output. Just use it to make
						// a BufferedReader to readLine() what the program writes out.
						//is = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
						//try {
						//	while ((line = is.readLine()) != null) System.out.println(line);
						//} catch (IOException e1) {
						//	// TODO Auto-generated catch block
						//	e1.printStackTrace();
						//}
						
						try {
							p.waitFor();  // wait for process to complete
						} catch (InterruptedException e) {
							System.err.println(e);  // "Can'tHappen"
							return;
						}
						//System.err.println("Process done, exit status was " + p.exitValue()); 					
						
					} else {
						// rawfile needs two exiftool and convert
						// set command for raw						
						// set input
						cmdArrayExif[1] = activePathname + "/" + fullname;
						// set output						
						cmdArrayConvert[4] = output;				         

						//System.out.println(Arrays.toString(cmdArrayExif));
						//System.out.println(Arrays.toString(cmdArrayConvert));
						
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
						Piper pipe = new Piper(p1.getInputStream(), p2.getOutputStream());
						new Thread(pipe).start();
						// Wait for second process to finish
						try {
							p2.waitFor();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// Show output of second process
						//java.io.BufferedReader r = new java.io.BufferedReader(new java.io.InputStreamReader(p2.getInputStream()));
						//String s = null;
						//while ((s = r.readLine()) != null) {
						//    System.out.println(s);
						//}						
					}
					
					progressBar.setValue(ii);
					progressBar.paint(progressBar.getGraphics());  // not very good		
					
				} // end for-loop
				
				// all previews created - reset progressbar
				progressBar.setMaximum(0);
				progressBar.setString("");
			
			}  // end of run()
		};
		threadConvert.start();   // convert files in one separate thread
		
		// wait some time to extract first preview, otherwise the GUI is faster
		//    than the previews and shows "picture not found"
		Thread wait = new Thread();
		wait.sleep(500);  	
 	
 	}  // end scanPreview
	
	
	
	public void calcLuminance() throws Exception{     //calculate luminance of preview images		
		//TODO define picture area for calculation
        // /usr/bin/convert is used, could be replaced by imagej in
        //             newer version to prevent dependancies
		
		// -region 600x400+10+20 =  width x height + offsetx + offsety
		//
		// panelarea is 600x400, imagepreview has 750x500
		//
		int width = (int) ((drawingPanel.x2 - drawingPanel.x1) * 1.25) ;
		int height = (int) ((drawingPanel.y2 - drawingPanel.y1) * 1.25) ;
		int offX = (int) (drawingPanel.x1 * 1.25);
		int offY = (int) (drawingPanel.y1 * 1.25);
				
		final String parameter = String.valueOf(width) + "x" + String.valueOf(height) + "+" +
				String.valueOf(offX) + "+" + String.valueOf(offY);
		
		//System.out.println(parameter);	

    	final String[] cmdArrayCrop = {"convert", "inputfile", "-crop", "parameter", "-" };
    	cmdArrayCrop[3] = parameter;
		
    	final String[] cmdArrayLum = {"convert", "-", 
    			"-scale", "1x1!", "-format", "%[fx:luminance]", "info:"};	
     	//cmdArrayLum[3] = parameter;     	
     	
	    Thread threadCalc = new Thread() { // define new Thread as inline class
			public void run() {
				
				Double luminance = 0.0;
	    
				for (int i = 0; i < activeNumber; i++) {
			         
					String fullname = (String) fixTable.getValueAt(i, 2);
					String name = fullname.substring(0, fullname.lastIndexOf(".")) + ".jpg";
					
					 // set preview image as input
			         cmdArrayCrop[1] = activePathname + "/preview/" + name;
			         
			        //System.out.println(Arrays.toString(cmdArrayLum));					  
			         
			        // wait if preview is not yet created
			        //TODO beware of endless loops			         
					while (	new File(activePathname + "/preview/" + name).isFile()	== false )  {
						// preview file don't exist                  
						// do nothing and wait						
						Thread wait = new Thread();
						try {
							wait.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					};
					
					//System.out.println(Arrays.toString(cmdArrayCrop));
					//System.out.println(Arrays.toString(cmdArrayLum));

					BufferedReader is;  // reader for output of process
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
					Piper pipe = new Piper(p1.getInputStream(), p2.getOutputStream());
					new Thread(pipe).start();
					// Wait for second process to finish
					try {
						p2.waitFor();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// get output of second process
				    is = new BufferedReader(new InputStreamReader(p2.getInputStream()));
					
				    try {
						while ((line = is.readLine()) != null) {							
							//System.out.println(line);
							
							luminance = Double.valueOf(line);
							
							// set value in table
							// luminance mean is in column 9
							picModel.setValueAt(luminance, i, 9);
							
							picTable.repaint(); // Repaint all the component (all Cells).
							// better use 
							// ((AbstractTableModel) jTable.getModel()).fireTableCellUpdated(x, 0); // Repaint one cell.
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}					
				} // end for-loop				
				
				// save luminance curve ----------------------------------------
				// create array with new y-value
				double[] x, y;
				x = new double[activeNumber];
				y = new double[activeNumber];
				
				for (int i = 0; i < activeNumber; i++) {
					x[i] = i;
					y[i] = (double) picModel.getValueAt(i, 9);
					
  				//System.out.println (picTable.getValueAt(i, 3));
  				//System.out.println("x= " + x[i] + " y= " + y[i]);
				}	          
	  	        meanPanel.setCoord(x, y);  	        
	  	        
	  	        
	  	        
	  	        // calculate smoothing curve, but don't display this until
	  	        //         deflicker is active
	  	        // we should the caclculation at this place, otherwise the
	  	        //    calc. of luminance is slower than this process
	  	        
	  	        int order = deflicSlider.getValue();
    		 	Polynomal poly = new Polynomal(x, y, order);  // with initial order=4    		 	
      			
    		 	// store new smoothed line
    		 	// create array with new y-value
    		 	for (int i = 0; i < activeNumber; i++) {
  	  				//x[i] = i;
  	  				y[i] = poly.calculate(i);
  	  				double lumi = (double) picModel.getValueAt(i, 9);
  	  				double flicker = lumi - y[i];  	  				
  	  				picModel.setValueAt( y[i], i, 10); // store smooth in table
  	  				picModel.setValueAt( flicker, i, 11); // store flicker in table
  	  				
  	  				//System.out.println( "i= " + i + " lum= " + lumi + " smooth= " + y[i] + " flick= " + flicker);
  	  				
    		 	}
    		 	
    		 	meanOptPanel.setCoord(x, y);    		 	
    		 	    		 	   		 	  	        
    		 	layeredPane.repaint();	  	// after recalculating        
	  	        
			}  // end of run()		
			
		};
		threadCalc.start();   		
		
 	}  // end calcLuminance
	
		
	
	
	class SelectionListener implements ListSelectionListener {  // -------   used for jtable
		
		public void valueChanged(ListSelectionEvent ae) {
			if(ae.getValueIsAdjusting()) // mouse button not released yet
				return;
			
			int row = 0;
			int col = 0;			
	
			System.out.println("event: " + ae.getSource() );
			

				
			row =  fixTable.getSelectedRow();
			
			//if (row < 0) row =  picTable.getSelectedRow();
				
			
			if(row < 0)  return;            // true when clearSelection					

			col =  fixTable.getSelectedColumn();	
			
			// we must check two jtable !
			if(col < 0) col = picTable.getSelectedColumn();
			
			if(col < 0)  return;            // true when clearSelection					
			
			
			activeIndex = row;			

			
			//	System.out.println("Selected fix row: " + row);
			
			
			picSlider.setValue(activeIndex);

			//System.out.println("Selected row: "+row);
			//System.out.println("Selected col: "+col);
			//System.out.println( picTable.getValueAt(row, col) );   //	        
			
			//	        System.out.println(picTable.getValueAt(row, 2) );   // spalte 2 = filename

			// test: change mean
			// table.setValueAt(0.999, row, 3);	        
			
			//table.clearSelection();
		}
	}	



	
	
	
	
	
	class ButtonListener2 implements ActionListener { // ----------------  deflicker jogglebutton
		public void actionPerformed(ActionEvent ae) {
			// this is used for deflicker jogglebutton          
			AbstractButton abstractButton = (AbstractButton) ae.getSource();
			boolean selected = abstractButton.getModel().isSelected();
			//System.out.println("Action - selected=" + selected + "\n");
			if (selected) {  
				
				if (activeNumber < 10) {
					JOptionPane.showMessageDialog (null, "Not enough pictures for deflicker!",
							"Title", JOptionPane.WARNING_MESSAGE);
					deflickerButton.doClick();   // reverse the button
					
				} else {
					// activate deflicker options
					
					
					// display keyframes
					List<Integer> indexlist = new ArrayList<Integer>();
					int[] x;
					
					// get indexnumbers of keyframes
					int numberkey = 0;					
					for (int i = 0; i < activeNumber; i++) {
						if ((Boolean) picModel.getValueAt( i, 1)) {
							indexlist.add(i);
						}											
					}					
					x = new int[indexlist.size()];
					// set indexnumbers in array
					for (int i = 0; i < indexlist.size(); i++) {						
						x[i] = indexlist.get(i);																		
					}	   										
		  	        keyframePanel.setCoord( x, activeNumber);  	        
		  	        layeredPane.repaint();
					
					
					
					
					
					// set gradient background for luminance with differnt gray		
					TableColumn colLum = picTable.getColumnModel().getColumn(6);
					colLum.setCellRenderer(new MeanColorColumnRenderer());

					// set gradient background for flicker with differnt red		
					TableColumn colFlicker = picTable.getColumnModel().getColumn(8);
					colFlicker.setCellRenderer(new FlickerColorColumnRenderer());

					
					deflicRow2Panel.setVisible(true);
					meanPanel.setVisible(true);
					meanOptPanel.setVisible(true);
					pointerPanel.setVisible(true);
					drawingPanel.setVisible(true);
					
				}
				
			} else {
				deflicRow2Panel.setVisible(false);
				meanPanel.setVisible(false);
				meanOptPanel.setVisible(false);
				pointerPanel.setVisible(false);
				drawingPanel.setVisible(false);
			}
		}
	}
	
	
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			if(ae.getSource() == loadButton | ae.getSource() == dloadButton){
				System.out.println("Button load XMP");
				
			} else if (ae.getSource() == transButton | ae.getSource() == dtransButton){
				System.out.println("Button Interpolation");
				
			} else if (ae.getSource() == saveButton | ae.getSource() == dsaveButton){
				System.out.println("Button save XMP");
				
			} else if (ae.getSource() == exportButton | ae.getSource() == dexportButton){
				System.out.println("Button export frames");
				
				
				// some testing ****************************
				picModel.setValueAt( 99.9, activeIndex, 12);
				picModel.setValueAt( 88.8, activeIndex, 14);
				picModel.setValueAt( 77.7, activeIndex, 16);
				picModel.setValueAt( 66.6, activeIndex, 18);
				
				// toggle keyframe
				if ((Boolean) picModel.getValueAt( activeIndex, 1)) {
					picModel.setValueAt( false, activeIndex, 1);
				} else {
					picModel.setValueAt( true, activeIndex, 1);
				}
				
				picTable.repaint();					
				
				// end of testing ****************************
				
				
				
			} else if (ae.getSource() == renderButton | ae.getSource() == drenderButton){
				System.out.println("Button render video");
				
			} else if (ae.getSource() == dlumiButton ){
				// recalculate luminance with rectangle
				try {
					calcLuminance();   // calc and repaint
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// invoke smoothing curve
				deflicSlider.setValue(3);   // hack to force calculation !?
				deflicSlider.setValue(4);   // initial value
				
				
			}else if (ae.getSource() == playButton ){
				slideShow.start();
			}else if (ae.getSource() == stopButton ){
				slideShow.stopRequest();
			}else if (ae.getSource() == resetButton ){
				activeIndex=0;
				picSlider.setValue(activeIndex);
			} else {
				
				System.out.println("unknown Button '" + ((JButton)ae.getSource()).getText() + 
						"' geklickt.");
				
			}
		}
	}
}

