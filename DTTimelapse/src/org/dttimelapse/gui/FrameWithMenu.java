package org.dttimelapse.gui;



import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;



// modified RM


public class FrameWithMenu extends JFrame implements ActionListener{
 
	private static final long serialVersionUID = 1L;
	
	private JMenuBar menuBar = new JMenuBar(); // Window menu bar
	
	private JMenuItem prevItem, prefItem, exitItem,
	infoItem, licenseItem, aboutItem,  logItem;
	
	private MainGui gui;
	private DTTPreferences dttPref;
	
	public FrameWithMenu(MainGui guii, DTTPreferences dttprefi) {   // contructor
		
			gui = guii;
			dttPref = dttprefi;
			
		    this.setTitle("DTTimelapse " + gui.version);
			
		    JMenu fileMenu = new JMenu("File"); // Create File menu
		    JMenu helpMenu = new JMenu("Help"); // Create Elements menu
		    fileMenu.setMnemonic('F'); // Create Mnemonic
		    helpMenu.setMnemonic('H'); // Create Mnemonic
		    
		    setJMenuBar(menuBar);
		     
	    
		    
		    // file menu
		    prevItem = fileMenu.add("Overwrite Previews");
		    prevItem.addActionListener(this);
		    prevItem.setEnabled(false);

		    prefItem = fileMenu.add("Preferences");
		    prefItem.addActionListener(this);
		    fileMenu.addSeparator();
		    exitItem = fileMenu.add("Quit");
		    exitItem.addActionListener(this);
		    
		    
		    
		    // help menu   
		    infoItem = helpMenu.add("Info");
		    infoItem.addActionListener(this);
		    licenseItem = helpMenu.add("License");
		    licenseItem.addActionListener(this);
		    aboutItem = helpMenu.add("About");
		    aboutItem.addActionListener(this);
		    helpMenu.addSeparator();
		    logItem = helpMenu.add("Log messages");
		    logItem.addActionListener(this);
		    		
		    logItem.setEnabled(false);
		    
		    menuBar.add(fileMenu);
		    menuBar.add(helpMenu);
		    
		    
		    exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		    
		    
		    menuBar.add(fileMenu);
		    menuBar.add(helpMenu);
		    
		    setDefaultCloseOperation(EXIT_ON_CLOSE);
		    
	  } // end constr
	
    public void actionPerformed (ActionEvent e){
        if(e.getSource() == this.prevItem){
        	//TODO overwrite preview
    	    

        }
        else if(e.getSource() == this.prefItem){
        	dttPref.setPreferences();
    	    

        }
        else if (e.getSource() == this.exitItem){
        	System.exit(0);
        } 
        else if (e.getSource() == this.infoItem){
        	
        	// Frame anlegen
        	JFrame frame = new JFrame ("Info");
        	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        	frame.setLocation(100, 100);
        	
        	JTextArea textArea;
        	JScrollPane scrollPane;
        	 
        	//URL url = getClass().getResource("/doc/INFO");        	
           	//File file = ResourceLoader.load("doc/INFO");
            
        	//File file = new File("doc/INFO");
        	
	        textArea = new JTextArea(40, 50);
	        
	        try {
					textArea.read(new InputStreamReader(
					        getClass().getResourceAsStream("/doc/INFO")),
					        null);
			} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
			}
       	 
            scrollPane = new JScrollPane(textArea);
            frame.add(scrollPane);
            frame.pack();
            frame.setVisible(true);
        }
       else if (e.getSource() == this.licenseItem){
        	
        	// Frame anlegen
        	JFrame frame = new JFrame ("License");
        	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        	frame.setLocation(100, 100);
        	
        	JTextArea textArea;
        	JScrollPane scrollPane;
        	 
        	//File file = new File("doc/LICENSE");
          	 
	        textArea = new JTextArea(40, 50);
            try {
				textArea.read(new InputStreamReader(
				        getClass().getResourceAsStream("/doc/LICENSE")),
				        null);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	 
            scrollPane = new JScrollPane(textArea, 
            		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            		JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            frame.add(scrollPane);
            frame.pack();
            frame.setVisible(true);
        }
        else if (e.getSource() == this.aboutItem){
        	
        	// Frame anlegen
        	JFrame frame = new JFrame ("About");
        	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        	frame.setLocation(100, 100);
        	
        	JTextArea textArea;
        	JScrollPane scrollPane;
        	 
        	//File file = new File("doc/ABOUT");
          	 
	        textArea = new JTextArea(25, 40);
	        try {
					textArea.read(new InputStreamReader(
					        getClass().getResourceAsStream("/doc/ABOUT")),
					        null);
			} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
			}
	         	 
            scrollPane = new JScrollPane(textArea, 
            		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            		JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            frame.add(scrollPane);
            frame.pack();
            frame.setVisible(true);
        }
    
   
        
        
        
        
    }

  
	
	
	
}