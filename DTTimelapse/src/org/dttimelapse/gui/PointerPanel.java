package org.dttimelapse.gui;

import java.awt.Graphics;

import javax.swing.JPanel;

// this class constructs a Jpanel with display of a vertikal line

public class PointerPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private int x1, y1, x2, y2;
	private int n;

	public PointerPanel() { // constructor

		x1 = 0;
		y1 = 0;
		x2 = 0;
		y2 = 400;

	} // end const

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// int xValues2[] = { 0, 750, 00, 750 };
		// int yValues2[] = { 0, 500, 500, 0 };

		g.drawLine(x1, y1, x2, y2);

		// Swing graphics with more functions
		//
		// Graphics2D g2 = (Graphics2D) g;
		//
		// g2.setStroke( new BasicStroke( 5) ); // 10 pix
		// //g2.drawLine( 30, 150, 200, 150 );
		//
		// g2.scale( 0.2500, 0.2500 );
		//
		// g2.drawPolyline( xValues2, yValues2, 4 );

	}

	public void setCoord(int xValue, int n) {
		// x is activeIndex
		// n is avtiveNumber

		if (n < 2)
			return;

		// y2 = 400;

		// TODO change the scaling of the Values to fit the panel
		// we need global variables for dimensions

		final double factorX = (600.0 / (n - 1));

		//System.out.println("n= " + n);
		//System.out.println("factor= " + factorX);

		x1 = (int) (xValue * factorX);
		x2 = x1;

		return;

	}

}