package org.dttimelapse.gui;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;


//  this panel contains a rectangle for luminance calculation
//  this panel measures 600x400
//  the preview images measures 750x500, the factor is 1.25x
//
//  every time the rectangle is changed, we have to calculate new
//



public class DrawingPanel extends JPanel {

	public int x1, y1, x2, y2;

	public DrawingPanel() {

		x1 = 0;
		y1 = 0;
		x2 = 600;
		y2 = 400;

		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				x1 = e.getX();
				y1 = e.getY();
				x2 = x1;
				y2 = y1;

				repaint();
			}

			public void mouseReleased(MouseEvent e) {
				x2 = e.getX();
				y2 = e.getY();
				
				// if the dimension is to small - set max area
				if ( (x2-x1) < 10 | (y2-y1) < 10) {
					x1 = 0;
					y1 = 0;
					x2 = 600;
					y2 = 400;
				}

				repaint();
				
			}
		});

		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				repaint();
			}
		});
	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		g2.setStroke(new BasicStroke(2));
		g2.setPaint(Color.orange);

		g2.drawRect(x1, y1, x2 - x1, y2 - y1);
		
		//System.out.println(x1 + "/"+ y1 + " " + x2 + "/" + y2);

	}

}
