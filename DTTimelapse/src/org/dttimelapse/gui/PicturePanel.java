package org.dttimelapse.gui;

import javax.swing.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.*;
import java.io.*;

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

   
}