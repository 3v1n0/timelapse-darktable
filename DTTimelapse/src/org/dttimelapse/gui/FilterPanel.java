package org.dttimelapse.gui;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

// this class constructs a Jpanel with display of a Polyline
//     for filter data
//
// the range is from -5 to 5, zero in middle

public class FilterPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public int xScreen[], yScreen[];
	private int n;

	private double min, max;

	public FilterPanel() { // constructor

		xScreen = new int[0];
		yScreen = new int[0];

	} // end const

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawPolyline(xScreen, yScreen, n);

		// Swing graphics with more functions
		//
		Graphics2D g2 = (Graphics2D) g;
		//
		g2.setStroke(new BasicStroke(2)); // 3 pix

		// g2.drawLine( 30, 150, 200, 150 );
		//
		// g2.scale( 0.2500, 0.2500 );
		//
		g2.drawPolyline(xScreen, yScreen, n);

		g2.drawString(Double.toString(max), 10, 10);
		g2.drawString(Double.toString((min + max) / 2), 10, 200);
		g2.drawString(Double.toString(min), 10, 390);

	}

	public void setMinMax(double imin, double imax) {
		min = imin;
		max = imax;
		return;
	}

	public void setCoord(double xValues[], double yValues[]) {

		this.n = xValues.length; // number of coordinate pairs

		if (n != yValues.length) {
			System.out.println("Problem with uneven koordinates!");
			return;
		}

		if (n <= 1)
			return;

		xScreen = new int[n];
		yScreen = new int[n];

		double range = max - min;

		// change the scaling of the Values to fit the panel
		// we need global variables for dimensions

		final double factorX = (600.0 / (n - 1));

		final double factorY = (380.0 / range);

		// System.out.println("polygon n= " + n);
		// System.out.println("factor= " + factorX);

		for (int i = 0; i < n; i++) { // all pictures

			xScreen[i] = (int) (xValues[i] * factorX);
			yScreen[i] = (int) (390 - (yValues[i] - min) * factorY);

			// y[i] = (int) (200-200 * yValues[i]);

			// System.out.println("i= " + i + " x= " + xScreen[i]+ " y= " +
			// yScreen[i]);

		}

		return;

	}

	public void clear() {

		this.n = 0;

		xScreen = new int[1];
		yScreen = new int[1];

		xScreen[0] = 0;
		yScreen[0] = 0;

		return;

	}

}
