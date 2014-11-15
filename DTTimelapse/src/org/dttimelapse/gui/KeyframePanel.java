package org.dttimelapse.gui;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

// this class constructs a Jpanel with display of a Polyline

public class KeyframePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private int x[];
	private int n;

    Icon icon = new DynamicIcon();
    JLabel dynamicLabel = new JLabel(icon);


    
	public KeyframePanel() { // constructor

		x = new int[0];		

	} // end const

	
	
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
				
		for (int i = 0; i < n; i++) {
			int shift = 0;
			if (i == 0) shift = 5;
			if (i == n-1) shift = -5;

			icon.paintIcon(this, g, x[i] + shift, 395);

		}

	}


	public void setCoord(int xValues[], int number) {

		this.n = xValues.length; // number of keyframes

		if (n <= 1)
			return;

		x = new int[n];

		// change the scaling of the Values to fit the panel
		// we need global variables for dimensions

		final double factorX = (600.0 / (number - 1));

		// System.out.println("polygon n= " + n);
		// System.out.println("factor= " + factorX);

		for (int i = 0; i < n; i++) {

			x[i] = (int) (xValues[i] * factorX);
			
			//System.out.println("i= " + xValues[i] + " x= " + x[i]);

		}

		return;

	}
	
    // A little icon class to draw an icon.
    class DynamicIcon implements Icon {
        public int getIconWidth() { return 10; }
        public int getIconHeight() { return 10; }
        
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.fill3DRect(x-5, y-5, getIconWidth(), getIconHeight(), true);
        }
    };

	

}