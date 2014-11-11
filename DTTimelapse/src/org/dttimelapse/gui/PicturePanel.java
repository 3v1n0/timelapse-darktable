package org.dttimelapse.gui;

import javax.swing.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;


//import	resources.*;


public class PicturePanel extends JPanel { 
	private static final long serialVersionUID = 1L;
	
	private Image image;
	Dimension correctSize;
		
    public PicturePanel() {   //constructor
 
    } // end const
 
//   		@Override
//   		protected void paintComponent(Graphics g) {
//   			super.paintComponent(g);
//   			if(image != null) { //Bild muss im Speicher liegen, sonst wird nichts gezeichnet
//   				g.drawImage(image, 0, 0, this);  // feste Bildgr��e
//   			}
//   		}
   		
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	
		// get width of jpanel
		int width = getWidth();
	    // calculate height with aspect ratio 3:2
		int height = (int) (width / 1.5);	
	
 
        g.drawImage(image, 0, 0, width, height, this); // scale image to panel
        
        this.correctSize = new Dimension(width,height);
		
        
        
	}  // end paint
   
	
	
	
	 public void loadImage(String filename){
		 // read imagefile from disc
	     try {
	      	 image = ImageIO.read(new File(filename));
	     }
	     catch(IllegalArgumentException iae) {
	          JOptionPane.showMessageDialog(this, "Image file not found!\n"+iae.getMessage());
	     }
	     catch(IOException ioe) {
	          JOptionPane.showMessageDialog(this, "Error at reading image file!\n"+ioe.getMessage());
	     }
	 }
	 
	 
	 public void loadLogo(){
		 // read logo image from resource
	     try {
	    	 //ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    	 //InputStream is = classLoader.getResourceAsStream("label_dttimelapse.jpg");
	    	 
	    	 InputStream is = getClass().getResourceAsStream("/icon/label_dttimelapse.png");
	    		
	    	//image = new ImageIcon(getClass().getClassLoader().getResource("/label_dttimelapse.png")).getImage();  
	    	 
	    	// JLabel label = new JLabel(new ImageIcon(getClass().getResource("icon/label_dttimelapse.png")));
	    	 
	    	 image = ImageIO.read(is);
    	 

	     }
	     catch(IllegalArgumentException iae) {
	          JOptionPane.showMessageDialog(this, "Grafiklogo nicht gefunden!\n"+iae.getMessage());
	     }
	     catch(IOException ioe) {
	          JOptionPane.showMessageDialog(this, "Fehler beim Einlesen des Grafiklogo!\n"+ioe.getMessage());
	     }
	 }
	 

//	 public void loadRes(String path){
//		 // read imagefile from url
//	     try {
//	      	 image = ResourceLoader.getImage("label_dttimelapse.png");
//	     }
//	     catch(IllegalArgumentException iae) {
//	          JOptionPane.showMessageDialog(this, "Image file not found!\n"+iae.getMessage());
//	     }
//
//	 }
}