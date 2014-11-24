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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileNotFoundException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;

import org.dttimelapse.math.MovingAverage;



// some long methods are shifted to "DirectoryMethods.java"
//       for better maintaining
//
// See values and index of picModel in "PictureModel.java"


public class MainGui extends JComponent {

	private static final long serialVersionUID = 1L;

	public static String version = "0.1beta";

	// global variables
	public static String activePathname;
	public static Integer activeIndex;
	public static Integer activeNumber;

	JLabel labelDirectory;
	
	JSlider picSlider, deflicSlider;
	
	JScrollPane scrollPane;

	JLayeredPane layeredPane;

	JPanel sliderPanel, rightPanel, leftPanel, deflicRow2Panel, progressPanel;

	JScrollPane tablePane;

	JPanel basicWorkflowPanel, deflicWorkflowPanel;

	JProgressBar progressBar;

	JSplitPane splitPane;

	JButton loadButton, transButton, saveButton, exportButton, renderButton;

	JButton dloadButton, dtransButton, dsaveButton, dexportButton,
			drenderButton, dlumiButton;

	JToggleButton deflickerButton, playButton;

	JButton resetButton;	

	JCheckBox cbImage, cbClipping, cbLumi;
	JComboBox comboFilter;

	ListSelectionModel listSelectionModel;

	// specific classes of DTTimelapse GUI
	SlideShow slideShow;
	PicturePanel picturePanel; // image display
	DTTPreferences dttPref; // preferences
	JTable picTable, fixTable;
	PictureModel picModel;

	PolygonPanel meanPanel, meanOptPanel;
	PointerPanel pointerPanel;
	DrawingPanel drawingPanel;
	KeyframePanel keyframePanel;
	ClippingPanel clippingPanel;
	FilterPanel filterPanel;

	DirectoryMethods dm;  // some large methods of mainGui
	
	

	public MainGui() { // constructor

		// / custom Nimbus Look+Feel ----------------------------------------
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}

		UIManager.put("nimbusBase", new Color(0, 0, 0)); // tabs black
		UIManager.put("nimbusBlueGrey", new Color(80, 80, 80)); // menubar
		UIManager.put("control", new Color(80, 80, 80)); // background darkgray

		UIManager.put("List.background", new Color(150, 150, 150));
		UIManager.put("Table.background", new Color(150, 150, 150));
		// UIManager.put("List.foreground", new Color( 255,255,255));
		UIManager.put("Tree.background", new Color(150, 150, 150));
		// UIManager.put("Tree.foreground", new Color( 255,255,255));

		UIManager.put("nimbusLightBackground", new Color(150, 150, 150)); // jlist

		// -------------------- Global settings ---------------------------------
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

		// ----------------------------------------------------------------------
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
		picturePanel.setBounds(0, 0, 600, 400); // mandatory for layeredpane?

		JPanel treePanel = new JPanel();

		try {
			final FileTree ft = new FileTree(dttPref.prefTreeDirectory);

			ft.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent evt) {

					TreePath path = evt.getPath();
					activePathname = ft.getPathName(path);

					dm.newDirectory(); // check new directory
				}
			});

			ft.setPreferredSize(null);
			ft.setMinimumSize(new Dimension(450, 0));

			treePanel.setLayout(new GridLayout(0, 1)); // extends width of jtree
			treePanel.add(new JScrollPane(ft));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// definition of layered panels
		meanPanel = new PolygonPanel();
		// meanPanel.setCoord(x, y);
		meanPanel.setForeground(Color.blue);
		// meanPanel.setPreferredSize(new Dimension(450, 300));
		// meanPanel.setMinimumSize(new Dimension(450, 300));
		meanPanel.setBounds(0, 0, 600, 400); // mandatory to display
		meanPanel.setOpaque(false);

		meanOptPanel = new PolygonPanel();
		meanOptPanel.setForeground(Color.yellow);
		meanOptPanel.setBounds(0, 0, 600, 400); // mandatory to display
		meanOptPanel.setOpaque(false);

		pointerPanel = new PointerPanel();
		pointerPanel.setForeground(Color.gray);
		pointerPanel.setBounds(0, 0, 600, 400); // mandatory to display
		pointerPanel.setOpaque(false);

		drawingPanel = new DrawingPanel();
		drawingPanel.setForeground(Color.orange);
		drawingPanel.setBounds(0, 0, 600, 400); // mandatory to display
		drawingPanel.setOpaque(false);
		drawingPanel.setVisible(false);

		keyframePanel = new KeyframePanel();
		keyframePanel.setBounds(0, 0, 600, 400); // mandatory to display
		keyframePanel.setOpaque(false);

		clippingPanel = new ClippingPanel();
		clippingPanel.setForeground(Color.MAGENTA);
		clippingPanel.setBounds(0, 0, 600, 400); // mandatory to display
		clippingPanel.setOpaque(false);

		filterPanel = new FilterPanel();
		filterPanel.setForeground(Color.yellow);
		filterPanel.setBounds(0, 0, 600, 400); // mandatory to display
		filterPanel.setOpaque(false);

		layeredPane = new JLayeredPane(); // shows picture and curves
		layeredPane.add(picturePanel, JLayeredPane.FRAME_CONTENT_LAYER); // (-3000)
		layeredPane.add(meanOptPanel, 0); // optimized mean of luminance
		layeredPane.add(meanPanel, 1); // original mean of luminance
		layeredPane.add(pointerPanel, 2); // pointer of activeindex
		layeredPane.add(drawingPanel, 3); // area for luminance calculation
		layeredPane.add(keyframePanel, 4); // area for keyframe icons
		layeredPane.add(clippingPanel, 5); // rectangle with clipping
		layeredPane.add(filterPanel, 6); // filter curve

		layeredPane.setPreferredSize(new Dimension(600, 400));

		// leftpanel
		leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

		leftPanel.add(labelLeft);
		leftPanel.add(layeredPane);
		leftPanel.add(sliderPanel());
		leftPanel.add(treePanel);

		// panels on right side *********************************

		// JTabbedPane-Object
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP,
				JTabbedPane.SCROLL_TAB_LAYOUT);

		tabbedPane.addTab("Basic workflow", basicWorkflowPanel());
		tabbedPane.addTab("Deflicker workflow", deflicWorkflowPanel());
		tabbedPane.addTab("Interpolation", interpolationPanel());
		tabbedPane.setMaximumSize(tabbedPane.getPreferredSize());
		tabbedPane.setAlignmentX(Component.LEFT_ALIGNMENT);

		// table with image information
		JPanel tablePanel = new JPanel();

		// tablePanel.setLayout(new GridLayout(1, 0)); // extends width of table
		tablePanel.setLayout(new BorderLayout()); //

		rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

		rightPanel.add(progressPanel());
		rightPanel.add(tabbedPane);
		rightPanel.add(labelDirectory);
		// rightPanel.add(tablePanel); // no use of jlist anymore

		tablePane = new JScrollPane(picTable);
		rightPanel.add(tablePane);
		// rightPanel.add(Box.createVerticalGlue()); // decrease hight of
		// tablepanel

		// splitpane
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(601); // set initial size
		splitPane.setLeftComponent(leftPanel);
		splitPane.setRightComponent(rightPanel);

		f.add(splitPane);

		f.pack();

		f.setSize(1550, 1000); // initial size of frame

		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

		// load logo
		picturePanel.loadLogo();
		picturePanel.repaint();

		// add some abstract listeners for sliders
		picSlider.addChangeListener(new ChangeListener() { //  picSlider
			@Override
			public void stateChanged(ChangeEvent e) {

				activeIndex = picSlider.getValue();

				if (activeNumber <= 0) {
					return;
				} // no action without pics

				if (!slideShow.isAlive()) {
					// scroll table when no slideshow
					picTable.getSelectionModel().setSelectionInterval(
							activeIndex, activeIndex);
					picTable.scrollRectToVisible(new Rectangle(picTable
							.getCellRect(activeIndex, 0, true)));
				}

				pointerPanel.setCoord(activeIndex, activeNumber);
				pointerPanel.repaint();

				// changes in picSlider shows selected picture
				picRefresh();
			}
		});

		
		deflicSlider.addChangeListener(new ChangeListener() { // deflicSlider			
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO update exposure curve

				// calculate smoothing curve
				// create array with new y-value of luminance
				double[] x, y;
				x = new double[activeNumber];
				y = new double[activeNumber];

				for (int i = 0; i < activeNumber; i++) {
					x[i] = i;
					y[i] = (double) picModel.getValueAt(i, 9); // lumi

					// System.out.println (picTable.getValueAt(i, 3));
					// System.out.println("x= " + x[i] + " y= " + y[i]);
				}

				// System.out.println ( Arrays.toString(x) );
				// System.out.println ( Arrays.toString(y) );

				// calculate new smoothing line with moving average
				int range = deflicSlider.getValue();
				MovingAverage ma = new MovingAverage(y); // set y-values
				for (int i = 0; i < activeNumber; i++) {
					y[i] = ma.calculate(i, range);
					// y[i] = ma.calculateWeighted( i, range );
					double lumi = (double) picModel.getValueAt(i, 9);
					double flicker = lumi - y[i];
					picModel.setValueAt(y[i], i, 10); // store smooth in
														// table
					picModel.setValueAt(flicker, i, 11); // store
															// flicker
															// in table

					// System.out.println( "x= " + i + " y= " + y[i] +
					// " aver= " + y[i] );
				}

				picTable.repaint(); // Repaint all the component (all
									// Cells).
				// better use
				// ((AbstractTableModel)
				// jTable.getModel()).fireTableCellUpdated(x, 0); //
				// Repaint one cell.

				// System.out.println("x= " + x[1] + " y= " + y[1]);
				// System.out.println("x= " + x[15] + " y= " + y[15]);

				meanOptPanel.setCoord(x, y);
				meanOptPanel.repaint();

				layeredPane.repaint();

			}
		});
		
		
		// extern methods in "DirectoryMethods.java"
		dm = new DirectoryMethods(this);  // implement extern methods

	} // end of constructor

	
//	public void newDirectory() { // 
//		
//		dm.newDirectory();    // check new choosen directory
//	}

	public void picRefresh() {
		// load and display active picture
		// picturePanel.loadImage(activePathname + "/preview/"
		// +picTable.getValueAt(activeIndex, 2)); // old
		// picturePanel.repaint();
		// meanPanel.repaint();

		if (activeNumber < 1)
			return;

		// set extension of filename to "jpg"
		String fullname = (String) fixTable.getValueAt(activeIndex, 2);
		String name = fullname.substring(0, fullname.lastIndexOf(".")) + ".jpg";

		// System.out.println("name= " + name);

		picturePanel.loadImage(activePathname + "/preview/" + name);

		if (cbClipping.isSelected()) {
			// set clipping area
			clippingPanel.setRectangleCoord(
					(int) picModel.getValueAt(activeIndex, 15),
					(int) picModel.getValueAt(activeIndex, 16),
					(int) picModel.getValueAt(activeIndex, 17),
					(int) picModel.getValueAt(activeIndex, 18),
					(double) picModel.getValueAt(activeIndex, 19));
			clippingPanel.repaint();
		}

		layeredPane.repaint();
	}

	// methods to create some parts of the GUI
	public JPanel sliderPanel() {
		// panel with slider, start and stop button
		picSlider = new JSlider();
		picSlider.setMaximum(0);

		playButton = new JToggleButton("Play/Stop");
		resetButton = new JButton("Reset");

		playButton.addActionListener(new ButtonListener3());
		resetButton.addActionListener(new ButtonListener3());

		cbImage = new JCheckBox("Image", true);
		cbClipping = new JCheckBox("Clipping", false);
		cbLumi = new JCheckBox("Luminance", false);

		ItemListener cbListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {

				// System.out.print( ((JCheckBox) e.getItem()).getText() );
				// System.out.println( e.getStateChange() == ItemEvent.SELECTED
				// ?
				// " selected" : " unselected" );

				Boolean isSelected;
				if (e.getStateChange() == ItemEvent.SELECTED) {
					isSelected = true;
				} else {
					isSelected = false;
				}

				if (e.getSource() == cbImage) {
					// image checkbox
					picturePanel.setVisible(isSelected);
				}

				if (e.getSource() == cbClipping) {
					// clipping checkbox
					clippingPanel.setVisible(isSelected);
					pointerPanel.setVisible(isSelected);

					picRefresh(); // show rect of activeindex
				}

				if (e.getSource() == cbLumi) {
					// deflicker checkbox
					meanPanel.setVisible(isSelected);
					meanOptPanel.setVisible(isSelected);
					pointerPanel.setVisible(isSelected);

					keyframePanel.setVisible(isSelected);

				}

			}
		};

		cbImage.addItemListener(cbListener);
		cbClipping.addItemListener(cbListener);
		cbLumi.addItemListener(cbListener);

		String[] filter = { "none", "Exp-black", "Exp-exposure", "WB temp",
				"WB tint", "Vibrance" };

		// Non-editable JComboBox
		comboFilter = new JComboBox();

		for (String s : filter)	comboFilter.addItem(s);

		comboFilter.addActionListener(new ComboListener());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(playButton);
		buttonPanel.add(resetButton);
		buttonPanel.add(cbImage);
		buttonPanel.add(cbClipping);
		buttonPanel.add(cbLumi);
		buttonPanel.add(comboFilter);
		buttonPanel.add(new JLabel("Filter"));

		sliderPanel = new JPanel();
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		sliderPanel.add(picSlider);
		sliderPanel.add(buttonPanel);

		return sliderPanel;
	}

	public JPanel progressPanel() {
		// Panel with programname and progressbar

		progressPanel = new JPanel();
		// progressPanel.setLayout(new BorderLayout());

		JLabel labelSoftware = new JLabel("DTTimelapse");

		progressPanel.add(labelSoftware);

		progressBar = new JProgressBar();
		progressBar.setMaximum(0);
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(250, 20));

		progressPanel.add(progressBar);
		progressPanel.setMaximumSize(progressPanel.getPreferredSize());
		progressPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		return progressPanel;
	}

	public JPanel basicWorkflowPanel() {
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
		basicWorkflowPanel
				.setMaximumSize(basicWorkflowPanel.getPreferredSize());

		return basicWorkflowPanel;
	}

	public JPanel deflicWorkflowPanel() {
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

		// deflickerButton.addActionListener(actionListener2);

		deflicSlider = new JSlider();
		deflicSlider.setMajorTickSpacing(10);
		deflicSlider.setMinorTickSpacing(1);
		deflicSlider.setPaintTicks(true);
		deflicSlider.setPaintLabels(true);
		deflicSlider.setMaximum(30);
		deflicSlider.setValue(10); // initial value

		deflicSlider
				.setToolTipText("Radius for calculating the moving average");

		JPanel deflicRow1Panel = new JPanel();
		deflicRow1Panel.add(dloadButton);
		deflicRow1Panel.add(dtransButton);
		deflicRow1Panel.add(deflickerButton);
		deflicRow1Panel.add(dsaveButton);
		deflicRow1Panel.add(dexportButton);
		deflicRow1Panel.add(drenderButton);

		// deflicRow1Panel.setMaximumSize( basicWorkflowPanel.getPreferredSize()
		// );
		// deflicWorkflowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		deflicRow2Panel = new JPanel();
		deflicRow2Panel.add(new JLabel("Radius for smoothing"));
		deflicRow2Panel.add(deflicSlider);
		deflicRow2Panel.setVisible(false);
		deflicRow2Panel.add(dlumiButton);

		JPanel deflicWorkflowPanel = new JPanel();
		deflicWorkflowPanel.setLayout(new BorderLayout());
		deflicWorkflowPanel.add(deflicRow1Panel, BorderLayout.NORTH);
		deflicWorkflowPanel.add(deflicRow2Panel, BorderLayout.SOUTH);

		return deflicWorkflowPanel;
	}

	public JPanel interpolationPanel() {
		// Interpolation settings
		JRadioButton radio1 = new JRadioButton("linear");
		JRadioButton radio2 = new JRadioButton("spline");
		radio1.setSelected(true);

		ButtonGroup bg = new ButtonGroup();
		bg.add(radio1);
		bg.add(radio2);

		JPanel interpolationPanel = new JPanel();
		interpolationPanel.add(new JLabel("Interpolation setting"));
		interpolationPanel.add(radio1);
		interpolationPanel.add(radio2);

		return interpolationPanel;
	}

	
	class SelectionListener implements ListSelectionListener { //  jtable
		public void valueChanged(ListSelectionEvent ae) {
			if (ae.getValueIsAdjusting()) // mouse button not released yet
				return;

			int row = 0;
			int col = 0;

			// System.out.println("event: " + ae.getSource() );

			row = fixTable.getSelectedRow();

			if (row < 0)
				return; // true when clearSelection

			col = fixTable.getSelectedColumn();

			// we must check two jtable !
			if (col < 0)
				col = picTable.getSelectedColumn();
			if (col < 0)
				return; // true when clearSelection

			activeIndex = row;

			// System.out.println("Selected fix row: " + row);

			picSlider.setValue(activeIndex);

			// table.clearSelection();
		}
	}
	

	class ComboListener implements ActionListener { // filter combobox														
		
		public void actionPerformed(ActionEvent e) {
			// System.out.println( e );
			JComboBox selectedChoice = (JComboBox) e.getSource();
			// System.out.println( selectedChoice.getSelectedItem() );
			// TODO show filter curve

			double[] x = new double[activeNumber];
			double[] y = new double[activeNumber];				
			int col = -1;
			double min = 0.0;
			double max = 0.0;

			if ("none".equals(selectedChoice.getSelectedItem())) {
				col = -1;
			} else if ("Exp-black".equals(selectedChoice.getSelectedItem())) {
				col = 13;
				min = -0.1;
				max = 0.1;
			} else if ("Exp-exposure".equals(selectedChoice.getSelectedItem())) {				
				col = 14;
				min = -3.0;
				max = 3.0;				
			} else if ("WB temp".equals(selectedChoice.getSelectedItem())) {
				col = 20;
				min = 2000.0;
				max = 23000.0;				
			} else if ("WB tint".equals(selectedChoice.getSelectedItem())) {
				col = 21;
				min = 0.1;
				max = 8.0;				
			} else if ("Vibrance".equals(selectedChoice.getSelectedItem())) {
				col = 22;
				min = 0.0;
				max = 100;				
			}			
			
			
			if (col == -1) {
				// set coord zero
				filterPanel.clear();
				filterPanel.repaint();
				
				keyframePanel.resetY();
				keyframePanel.repaint();
				
				filterPanel.setVisible(false);
				keyframePanel.setVisible(false);
				pointerPanel.setVisible(false);
				
			} else {
				// set coordinates for filterpanel
				for (int i = 0; i < activeNumber; i++) {
					x[i] = i;
					y[i] = (double) picModel.getValueAt(i, col);
		
					// System.out.println (picTable.getValueAt(i, 3));
					// System.out.println("x= " + x[i] + " y= " + y[i]);
				}
				filterPanel.setCoord(x, y, min, max);
				filterPanel.repaint();
				
				keyframePanel.setY(filterPanel.yScreen); // set y-values of keyframes
				keyframePanel.repaint();

				filterPanel.setVisible(true);
				keyframePanel.setVisible(true);
				pointerPanel.setVisible(true);
				
			}			
		}
	}
	
	
	class ButtonListener3 implements ActionListener { // play jogglebutton														
		public void actionPerformed(ActionEvent ae) {

			AbstractButton abstractButton = (AbstractButton) ae.getSource();
			boolean selected = abstractButton.getModel().isSelected();
			// System.out.println("Action - selected=" + selected + "\n");
			if (selected) {
				slideShow.start();

			} else {
				slideShow.stopRequest();
				// scroll table
				picTable.getSelectionModel().setSelectionInterval(activeIndex,
						activeIndex);
				picTable.scrollRectToVisible(new Rectangle(picTable
						.getCellRect(activeIndex, 0, true)));
			}

			if (ae.getSource() == resetButton) {
				activeIndex = 0;
				picSlider.setValue(activeIndex);
			}

		}
	}

	
	class ButtonListener2 implements ActionListener { //deflicker jogglebutton
		public void actionPerformed(ActionEvent ae) {
			// this is used for deflicker jogglebutton
			AbstractButton abstractButton = (AbstractButton) ae.getSource();
			boolean selected = abstractButton.getModel().isSelected();
			// System.out.println("Action - selected=" + selected + "\n");
			if (selected) {

				if (activeNumber < 10) {
					JOptionPane.showMessageDialog(null,
							"Not enough pictures for deflicker!", "Title",
							JOptionPane.WARNING_MESSAGE);
					deflickerButton.doClick(); // reverse the button

				} else {
					// activate deflicker options

					// set gradient background for luminance with differnt gray
					TableColumn colLum = picTable.getColumnModel().getColumn(6);
					colLum.setCellRenderer(new MeanColorColumnRenderer());

					// set gradient background for flicker with differnt red
					TableColumn colFlicker = picTable.getColumnModel()
							.getColumn(8);
					colFlicker
							.setCellRenderer(new FlickerColorColumnRenderer());

					if (!cbLumi.isSelected())
						cbLumi.doClick();
					cbLumi.setEnabled(false);

					deflicRow2Panel.setVisible(true);
					drawingPanel.setVisible(true);

					// meanPanel.setVisible(true);
					// meanOptPanel.setVisible(true);
					// pointerPanel.setVisible(true);
				}

			} else {
				// deactivate deflicker options

				cbLumi.setEnabled(true);
				if (cbLumi.isSelected())
					cbLumi.doClick();

				deflicRow2Panel.setVisible(false);
				drawingPanel.setVisible(false);

				// meanPanel.setVisible(false);
				// meanOptPanel.setVisible(false);
				// pointerPanel.setVisible(false);
			}
		}
	}

	
	// Action of main buttons
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			
			if (ae.getSource() == dlumiButton) {
				// recalculate luminance with rectangle
				try {
					dm.calcLuminance(); // calc and repaint
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// invoke smoothing curve
				// deflicSlider.setValue(9); // hack to force calculation !?
				// deflicSlider.setValue(10); // initial value

			} else if (ae.getSource() == loadButton |
					ae.getSource() == dloadButton) {
				System.out.println("Button load XMP");
				
				
				// find and store keyframes index in keyframePanel
				keyframePanel.setCoord(picModel, activeNumber);
			} else if (ae.getSource() == transButton
					| ae.getSource() == dtransButton) {
				System.out.println("Button Interpolation"); 
				

			} else if (ae.getSource() == saveButton
					| ae.getSource() == dsaveButton) {
				System.out.println("Button save XMP");
				

			} else if (ae.getSource() == exportButton
					| ae.getSource() == dexportButton) {
				System.out.println("Button export frames");
				

				// some testing ****************************
				// clipping test settings
				picModel.setValueAt(0.10, activeIndex, 13);
				picModel.setValueAt(-3.00, activeIndex, 14);				
				picModel.setValueAt(100, activeIndex, 15);
				picModel.setValueAt(100, activeIndex, 16);
				picModel.setValueAt(1400, activeIndex, 17);
				picModel.setValueAt(1000, activeIndex, 18);
				picModel.setValueAt(0.00, activeIndex, 19);				
				picModel.setValueAt(20000.0, activeIndex, 20);
				picModel.setValueAt(4.00, activeIndex, 21);
				picModel.setValueAt(40.0, activeIndex, 22);

				// toggle keyframe
				if ((Boolean) picModel.getValueAt(activeIndex, 1)) {
					picModel.setValueAt(false, activeIndex, 1);
				} else {
					picModel.setValueAt(true, activeIndex, 1);
				}
				picTable.repaint();
				// end of testing ****************************

			} else if (ae.getSource() == renderButton
					| ae.getSource() == drenderButton) {
				System.out.println("Button render video");
				

			} 
			
		}
	}
}
