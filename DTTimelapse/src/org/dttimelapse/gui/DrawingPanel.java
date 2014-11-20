package org.dttimelapse.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

//  this panel contains a rectangle for luminance calculation
//  this panel measures 600x400
//  the preview images measures 750x500, the factor is 1.25x
//
//  every time the rectangle is changed, we have to calculate new
//

public class DrawingPanel extends JPanel {

	public int x1, y1, x2, y2;
	Point startDrag, endDrag;

	public DrawingPanel() {

		x1 = 0;
		y1 = 0;
		x2 = 600;
		y2 = 400;

		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				startDrag = new Point(e.getX(), e.getY());
				endDrag = startDrag;
				x1 = e.getX();
				y1 = e.getY();
				x2 = x1;
				y2 = y1;

				repaint();
			}

			public void mouseReleased(MouseEvent e) {
				x2 = e.getX();
				y2 = e.getY();

				startDrag = null;
				endDrag = null;

				// if the dimension is to small - set max area
				if ((x2 - x1) < 10 | (y2 - y1) < 10) {
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
				endDrag = new Point(e.getX(), e.getY());
				repaint();
			}
		});
	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		g2.setStroke(new BasicStroke(2));
		//g2.setPaint(Color.orange);

		g2.drawRect(x1, y1, x2 - x1, y2 - y1);

		// System.out.println(x1 + "/"+ y1 + " " + x2 + "/" + y2);
		
		if (startDrag != null && endDrag != null) {
			//g2.setPaint(Color.orange);
			Shape r = makeRectangle(startDrag.x, startDrag.y, endDrag.x,
					endDrag.y);
			g2.draw(r);
		}
	}
	
	private Rectangle2D.Float makeRectangle(int x1, int y1, int x2, int y2) {
		return new Rectangle2D.Float(Math.min(x1, x2), Math.min(y1, y2),
				Math.abs(x1 - x2), Math.abs(y1 - y2));
	}


}
