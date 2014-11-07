package org.dttimelapse.gui;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.text.NumberFormat;
import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DTTPreferences implements PropertyChangeListener {


	// preference values of DTT - use these as global variables
	public static int prefVideoHeight, prefVideoWidth, prefVideoFramerate;
	public static String prefLastDirectory, prefTreeDirectory;

	
	
	
	// Fields for data entry
	private JTextField textTreeDirectory;
	private JFormattedTextField textVideoWidth;
	private JFormattedTextField textVideoHeight;
	private JFormattedTextField textVideoFramerate;

	private NumberFormat integerFormat;

	private JFrame prevFrame;

	public DTTPreferences() { // constructor

	} // end constructor

	public void setPreferences() {

		integerFormat = NumberFormat.getIntegerInstance();

		// aktuelle Einstellungen laden
		loadPreferences();

		// GUI Aufbauen
		prevFrame = new JFrame();
		prevFrame.setTitle("Set Preferences");
		prevFrame.setSize(600, 200);
		// prevFrame.setLocationByPlatform(true);
		prevFrame.setLocationRelativeTo(null);

		// Create the labels.

		JButton buttonDir = new JButton("Load tree directory");

		// ActionListener wird als anonyme Klasse eingebunden
		buttonDir.addActionListener(new java.awt.event.ActionListener() {
			// Beim Dr�cken des Men�punktes wird actionPerformed aufgerufen
			public void actionPerformed(java.awt.event.ActionEvent e) {
				// Dateiauswahldialog wird erzeugt...
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				// ... und angezeigt
				// FileNameExtensionFilter filter = new
				// FileNameExtensionFilter("JPG Images", "jpg");
				// fc.setFileFilter(filter);
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {

					prefTreeDirectory = fc.getSelectedFile().getAbsolutePath();
					;
					textTreeDirectory.setText(prefTreeDirectory);
				}
			}
		});

		JLabel labelTreeDirectory = new JLabel("Tree Root Directory");
		JLabel labelVideoWidth = new JLabel("Video Width");
		JLabel labelVideoHeight = new JLabel("Video Height");
		JLabel labelVideoFramerate = new JLabel("Video Framerate");

		// Create the text fields and set them up.
		textTreeDirectory = new JTextField(prefTreeDirectory, 15);

		textVideoWidth = new JFormattedTextField(integerFormat);
		;
		textVideoWidth.setValue(new Integer(prefVideoWidth));
		textVideoWidth.setColumns(10);
		textVideoWidth.addPropertyChangeListener("value", this);

		textVideoHeight = new JFormattedTextField(integerFormat);
		;
		textVideoHeight.setValue(new Integer(prefVideoHeight));
		textVideoHeight.setColumns(10);
		textVideoHeight.addPropertyChangeListener("value", this);

		textVideoFramerate = new JFormattedTextField(integerFormat);
		;
		textVideoFramerate.setValue(new Integer(prefVideoFramerate));
		textVideoFramerate.setColumns(10);
		textVideoFramerate.addPropertyChangeListener("value", this);

		// Lay out the labels in a panel.
		JPanel labelPane = new JPanel(new GridLayout(0, 1));
		labelPane.add(labelTreeDirectory);
		labelPane.add(labelVideoWidth);
		labelPane.add(labelVideoHeight);
		labelPane.add(labelVideoFramerate);

		// Layout the text fields in a panel.
		JPanel fieldPane = new JPanel(new GridLayout(0, 1));
		fieldPane.add(textTreeDirectory);
		fieldPane.add(textVideoWidth);
		fieldPane.add(textVideoHeight);
		fieldPane.add(textVideoFramerate);

		// Put the panels in this panel, labels on left,
		// text fields on right.
		// add(labelPane, BorderLayout.CENTER);
		// add(fieldPane, BorderLayout.LINE_END);

		JButton buttonOK = new JButton("OK");

		prevFrame.add(buttonDir, BorderLayout.NORTH);
		prevFrame.add(labelPane, BorderLayout.WEST);
		prevFrame.add(fieldPane, BorderLayout.CENTER);
		prevFrame.add(buttonOK, BorderLayout.SOUTH);

		prevFrame.setVisible(true);

		// ok button: save and quit
		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				prefTreeDirectory = textTreeDirectory.getText();
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

	// ** Called when a field's "value" property changes.
	public void propertyChange(PropertyChangeEvent e) {
		Object source = e.getSource();
		if (source == textVideoWidth) {
			prefVideoWidth = ((Number) textVideoWidth.getValue()).intValue();
		} else if (source == textVideoHeight) {
			prefVideoHeight = ((Number) textVideoHeight.getValue()).intValue();
		} else if (source == textVideoFramerate) {
			prefVideoFramerate = ((Number) textVideoFramerate.getValue())
					.intValue();
		}

	}

	void storePreferences() throws BackingStoreException {
		Preferences userPrefs = Preferences.userNodeForPackage(getClass());

		userPrefs.put("LastDirectory", prefLastDirectory);
		userPrefs.put("TreeDirectory", prefTreeDirectory);

		userPrefs.putInt("VideoWidth", prefVideoWidth);
		userPrefs.putInt("VideoHeight", prefVideoHeight);
		userPrefs.putInt("VideoFramerate", prefVideoFramerate);

		// System.out.println("stored preferences=");
		// System.out.println(prefLastDirectory);
		// System.out.println(prefTreeDirectory);
		// System.out.println(prefVideoWidth);
		// System.out.println(prefVideoHeight);
		// System.out.println(prefVideoFramerate);

		// Zur Sicherheit alles zur�ckschreiben
		userPrefs.flush();
	}

	public void loadPreferences() {
		Preferences userPrefs;
		userPrefs = Preferences.userNodeForPackage(getClass());

		// if no preferences - set initial values
		prefLastDirectory = userPrefs.get("LastDirectory", "");
		prefTreeDirectory = userPrefs.get("TreeDirectory", "/home/");

		prefVideoWidth = userPrefs.getInt("VideoWidth", 1280);
		prefVideoHeight = userPrefs.getInt("VideoHeight", 720);
		prefVideoFramerate = userPrefs.getInt("VideoFramerate", 25);

		// System.out.println("loaded preferences=");
		// System.out.println(prefLastDirectory);
		// System.out.println(prefTreeDirectory);
		// System.out.println(prefVideoWidth);
		// System.out.println(prefVideoHeight);
		// System.out.println(prefVideoFramerate);
	}

}