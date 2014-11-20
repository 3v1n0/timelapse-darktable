package org.dttimelapse.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;

// this class constructs a JPanel with display of the clipping rectangle

public class ClippingPanel extends JPanel {
	private static final long serialVersionUID = 1L;	

	private int width, height; // dimension of original picture
	private int dx, dy, dw, dh;
	private double angle;     // radiant
	

	public ClippingPanel() { // constructor
		
	} 

	public ClippingPanel(int w, int h) { 
		this.width = w;
		this.height = h;		
	} 
	// end const

	
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    
	    Graphics2D g2d = (Graphics2D)g;
	    //g2d.setColor(Color.WHITE);
	    g2d.setStroke( new BasicStroke( 2) ); // 2 pix
	    
	    // create rectangle at origin 
	    Rectangle rect = new Rectangle(0, 0, dw, dh);

	    // rotate rectangle 
	    //g2d.rotate(Math.toRadians(45));
	    g2d.rotate(angle);
	    
	    // translate rectangle to position
	    g2d.translate(dx,dy);
	    
	    g2d.draw(rect);
	}

	
	public void setRectangleCoord(int x, int y, int w, int h, double ang) {
		
		if ((w == 0) | (h == 0)) {
			// no input -> no display
			dx = 0;
			dy = 0;
			dw = 0;
			dh = 0;
			angle = 0.0;						
			return;
		}

		// TODO change the scaling of the Values to fit the panel
		// we need global variables for dimensions

		final double factor = 600.0 / width;

		// System.out.println("n= " + n);
		// System.out.println("factor= " + factorX);

		this.angle = ang;
		
		dx = (int) (x * factor);
		dy = (int) (y * factor);
		dw = (int) (w * factor);
		dh = (int) (h * factor);		

		return;
	}

	public void setDimension(int w, int h) {
		this.width = w;
		this.height = h;	
		
		//System.out.println("w= " + w  + " h= " + h);
	}

}