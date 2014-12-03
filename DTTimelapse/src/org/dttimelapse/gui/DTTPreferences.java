package org.dttimelapse.gui;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.dttimelapse.gui.MainGui.ComboListener;
import org.dttimelapse.gui.MainGui.RadioListener;

import java.text.NumberFormat;
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


// define preferences in MainGui



public class DTTPreferences implements PropertyChangeListener {
	
	private MainGui mg;	

	// preference values of DTT - use these as global variables
//	public static int prefVideoHeight, prefVideoWidth, prefVideoFramerate;
//	public static String prefLastDirectory, prefTreeDirectory;
	
	
	// Fields for data entry
	private JTextField textTreeDirectory;
	private JFormattedTextField textVideoWidth;
	private JFormattedTextField textVideoHeight;
	private JFormattedTextField textVideoFramerate;

	private NumberFormat integerFormat;

	private JFrame prevFrame;

	JRadioButton radioFast;
	JRadioButton radioSlow;

	
	
	public DTTPreferences(MainGui gui) { // constructor

		mg = gui;

	} // end constructor
	
	

	public void setPreferences() {

		integerFormat = NumberFormat.getIntegerInstance();

		// aktuelle Einstellungen laden
		loadPreferences();

		// GUI Aufbauen
		prevFrame = new JFrame();
		prevFrame.setTitle("Set Preferences");
		prevFrame.setSize(400, 400);
		// prevFrame.setLocationByPlatform(true);
		prevFrame.setLocationRelativeTo(null);

		// Create the labels.

		JButton buttonDir = new JButton("Load tree directory");

		// ActionListener wird als anonyme Klasse eingebunden
		buttonDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Dateiauswahldialog wird erzeugt...
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				// ... und angezeigt
				// FileNameExtensionFilter filter = new
				// FileNameExtensionFilter("JPG Images", "jpg");
				// fc.setFileFilter(filter);
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {

					mg.prefTreeDirectory = fc.getSelectedFile().getAbsolutePath();
					;
					textTreeDirectory.setText(mg.prefTreeDirectory);
				}
			}
		});

		JLabel labelTreeDirectory = new JLabel("Tree Root Directory");
		JLabel labelVideoWidth = new JLabel("Video Width");
		JLabel labelVideoHeight = new JLabel("Video Height");
		JLabel labelVideoFramerate = new JLabel("Video Framerate");

		// Create the text fields and set them up.
		textTreeDirectory = new JTextField(mg.prefTreeDirectory, 30);

		textVideoWidth = new JFormattedTextField(integerFormat);
		textVideoWidth.setValue(mg.prefVideoWidth);
		textVideoWidth.setColumns(10);
		textVideoWidth.addPropertyChangeListener("value", this);

		textVideoHeight = new JFormattedTextField(integerFormat);
		textVideoHeight.setValue(mg.prefVideoHeight);
		textVideoHeight.setColumns(10);
		textVideoHeight.addPropertyChangeListener("value", this);

		textVideoFramerate = new JFormattedTextField(integerFormat);
		textVideoFramerate.setValue(mg.prefVideoFramerate);
		textVideoFramerate.setColumns(10);
		textVideoFramerate.addPropertyChangeListener("value", this);

		
		
        

		// Non-editable JComboBox
        JComboBox comboFormat = new JComboBox();
        String[] format = { "VHS", "LD", "DVD", "HDTV", "FullHD", "free" };
		for (String s : format)	comboFormat.addItem(s);
		
		comboFormat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // System.out.println( e );
                JComboBox selectedChoice = (JComboBox) e.getSource();
                // System.out.println( selectedChoice.getSelectedItem() );

                if ("free".equals(selectedChoice.getSelectedItem())) {
                	
                } else if ("VHS".equals(selectedChoice.getSelectedItem())) {
                	textVideoWidth.setValue(320); 
                	textVideoHeight.setValue(240);
                } else if ("LD".equals(selectedChoice.getSelectedItem())) {                          
                	textVideoWidth.setValue(640); 
                	textVideoHeight.setValue(480);
                } else if ("DVD".equals(selectedChoice.getSelectedItem())) {
                	textVideoWidth.setValue(720); 
                	textVideoHeight.setValue(576);
                } else if ("HDTV".equals(selectedChoice.getSelectedItem())) {
                	textVideoWidth.setValue(1280); 
                	textVideoHeight.setValue(720);
                } else if ("FullHD".equals(selectedChoice.getSelectedItem())) {
                	textVideoWidth.setValue(1920); 
                	textVideoHeight.setValue(1080);
                }                      
            }			
		});	
		
		comboFormat.setSelectedItem("free");
		
		
		// Lay out the labels in a panel.
		JPanel labelPane = new JPanel(new GridLayout(0, 1));		
		labelPane.add(labelVideoWidth);
		labelPane.add(labelVideoHeight);
		labelPane.add(labelVideoFramerate);

		// Layout the text fields in a panel.
		JPanel fieldPane = new JPanel(new GridLayout(0, 1));		
		fieldPane.add(textVideoWidth);
		fieldPane.add(textVideoHeight);
		fieldPane.add(textVideoFramerate);

		
		// Quality settings
		JRadioButton radioLow = new JRadioButton("low");
		JRadioButton radioMiddle = new JRadioButton("middle");
		JRadioButton radioHigh = new JRadioButton("high");
        radioMiddle.setSelected(true);
        
        ButtonGroup bgQual = new ButtonGroup();
        bgQual.add(radioLow);
        bgQual.add(radioMiddle);
        bgQual.add(radioHigh);
                
        JPanel qualityPanel = new JPanel();
        qualityPanel.add(new JLabel("Quality setting: "));                
        qualityPanel.add(radioLow);
        qualityPanel.add(radioMiddle);
        qualityPanel.add(radioHigh);

		
		
		
		
		
		JButton buttonOK = new JButton("OK");

		
		
		
		// Preview settings
		radioFast = new JRadioButton("fast and lousy");
		radioSlow = new JRadioButton("slow and fine");		
		if ("slow".equals(mg.prefPreview))  {
			radioSlow.setSelected(true);
		} else {
			radioFast.setSelected(true);
		}
		
		
		

        
        ButtonGroup bgPrev = new ButtonGroup();
        bgPrev.add(radioFast);
        bgPrev.add(radioSlow);
                
        JPanel previewPanel = new JPanel();
        previewPanel.add(new JLabel("Preview extracting: "));                
        previewPanel.add(radioFast);
        previewPanel.add(radioSlow);
        
        radioFast.addActionListener(new PrevListener());
        radioSlow.addActionListener(new PrevListener());

 		
 

        
        
        
		JPanel directoryPanel = new JPanel();		
		directoryPanel.add(labelTreeDirectory);
		directoryPanel.add(textTreeDirectory);
		directoryPanel.add(buttonDir);
		
		JPanel firstPanel = new JPanel();
		firstPanel.setLayout(new BorderLayout());
		firstPanel.add(directoryPanel, BorderLayout.CENTER);		
		firstPanel.add(previewPanel, BorderLayout.SOUTH);



		
		JPanel videoPanel = new JPanel();
		videoPanel.setLayout(new BorderLayout());
		videoPanel.add(labelPane, BorderLayout.WEST);
		videoPanel.add(fieldPane, BorderLayout.CENTER);
		videoPanel.add(qualityPanel, BorderLayout.SOUTH);
		videoPanel.add(comboFormat, BorderLayout.SOUTH);
		
		
		
        // JTabbedPane-Object
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Directory", firstPanel);
        tabbedPane.addTab("Video", videoPanel);

        
        
        prevFrame.add(tabbedPane, BorderLayout.NORTH);
		prevFrame.add(buttonOK, BorderLayout.SOUTH);

		
		prevFrame.setVisible(true);

		// ok button: save and quit
		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				mg.prefTreeDirectory = textTreeDirectory.getText();
				//prefLastDirectory = MainGui.imagepath;

				try {
					storePreferences();
				} catch (BackingStoreException ex) {
					ex.printStackTrace();
				}

				prevFrame.dispose();

			}
		});

	}

	
   	class PrevListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == radioFast) {
				mg.prefPreview = "fast";    				
			} else if (ae.getSource() == radioSlow) {     				
				mg.prefPreview = "slow";
			} 
		}
	}

	
	
	// ** Called when a field's "value" property changes.
	public void propertyChange(PropertyChangeEvent e) {
		Object source = e.getSource();
		if (source == textVideoWidth) {
			mg.prefVideoWidth = ((Number) textVideoWidth.getValue()).intValue();
		} else if (source == textVideoHeight) {
			mg.prefVideoHeight = ((Number) textVideoHeight.getValue()).intValue();
		} else if (source == textVideoFramerate) {
			mg.prefVideoFramerate = ((Number) textVideoFramerate.getValue())
					.intValue();
		}

	}

	void storePreferences() throws BackingStoreException {
		Preferences userPrefs = Preferences.userNodeForPackage(getClass());

		userPrefs.put("LastDirectory", mg.prefLastDirectory);
		userPrefs.put("TreeDirectory", mg.prefTreeDirectory);
		userPrefs.put("Preview", mg.prefPreview);
		

		userPrefs.putInt("VideoWidth", mg.prefVideoWidth);
		userPrefs.putInt("VideoHeight", mg.prefVideoHeight);
		userPrefs.putInt("VideoFramerate", mg.prefVideoFramerate);

		// System.out.println("stored preferences=");
		// System.out.println(prefLastDirectory);
		// System.out.println(prefTreeDirectory);
		// System.out.println(mg.prefVideoWidth);
		// System.out.println(mg.prefVideoHeight);
		// System.out.println(mg.prefVideoWidth);
		//System.out.println("store " + mg.prefPreview);

		userPrefs.flush();
	}

	public void loadPreferences() {
		Preferences userPrefs;
		userPrefs = Preferences.userNodeForPackage(getClass());

		// if no preferences - set initial values
		mg.prefLastDirectory = userPrefs.get("LastDirectory", "");
		mg.prefTreeDirectory = userPrefs.get("TreeDirectory", "/home/");
		mg.prefPreview = userPrefs.get("Preview", "slow");
		

		mg.prefVideoWidth = userPrefs.getInt("VideoWidth", 1280);
		mg.prefVideoHeight = userPrefs.getInt("VideoHeight", 720);
		mg.prefVideoFramerate = userPrefs.getInt("VideoFramerate", 25);
		
		

		// System.out.println("loaded preferences=");
		// System.out.println(prefLastDirectory);
		// System.out.println(prefTreeDirectory);
		// System.out.println(mg.prefVideoWidth);
		// System.out.println(mg.prefVideoHeight);
		// System.out.println(mg.prefVideoWidth);
		// System.out.println("load " + mg.prefPreview);
	}

}