package org.dttimelapse.gui;

/**
 Copyright 2014 Rudolf Martin

 This file is part of DTTimelapse.

 DTTimelapse is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 DTTimelapse is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with DTTimelapse.  If not, see <http://www.gnu.org/licenses/>.
 */

// credits to "Neal's Stuff - 3D related stuff and other things" http://nealbuerger.com

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

public class PictureModel extends AbstractTableModel {

	private Vector data = new Vector(); // rows
	private Vector listeners = new Vector();

	String[] columnNames = {
			"Index", // 0
			"Key", // 1
			"Filename", // 2
			"Aperture", // 3
			"ExposureTime", // 4
			"ISO",  // 5
			"Width", // 6
			"Height", //7
			"DateTaken", // 8
			"Mean", // 9
			"Smooth", // 10
			"Flicker", // 11
			"D-Exposure", // 12
			"Black", // 13
			"Exposure", // 14
			"Clip x", // 15
			"Clip y", // 16
			"Clip w", // 17
			"Clip h", // 18
			"Angle", // 19
			"WB temp", // 20
			"WB tint", // 21
			"Vibrance", // 22
	};

	// public final Object[] longValues = {"", new Integer(20), new Float(20),
	// new Float(20), Boolean.TRUE};

	// Column widths of picTable, starting at "Aperture"
	protected int[] widths = { 60, 60, 60, 60, 60, 150, 60, 60, 60, 60, 60, 60,
			60, 60, 60, 60, 60, 60, 60, 60 };

	
	
	// methods

	// Number Columns
	public int getColumnCount() {
		return columnNames.length;
	}

	// Number of data
	public int getRowCount() {
		return data.size();
	}

	// title of columns
	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {

		if (data.size() == 0) {
			return null;
		}
		return ((Vector) data.get(row)).get(col);
	}

	public Class getColumnClass(int col) {

		if (data.size() == 0) {
			return String.class;
		}
		return getValueAt(0, col).getClass();
	}

	public boolean isCellEditable(int row, int col) {
		if (col == 1) { // isKeyframe
			return false;
		} else {
			return true; // no editing
		}
	}

	public void setValueAt(Object value, int row, int col) {
		((Vector) data.get(row)).setElementAt(value, col);

		fireTableCellUpdated(row, col); // don't works ??
	}

	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	public void addData(Vector vec) { // add data vector

		// if the vector is to short fill zero
		// default table modell uses zero to fill
		while (vec.size() < columnNames.length) {
			vec.add(0.0);
		}

		// remove clipping elements (format double)
		vec.remove(15);
		vec.remove(15);
		vec.remove(15);
		vec.remove(15);
		// add integer elements instead
		vec.add(15, 0);
		vec.add(15, 0);
		vec.add(15, 0);
		vec.add(15, 0);

		int index = data.size(); // index of new row

		data.add(vec);

		fireTableDataChanged();

		// Jetzt werden alle Listeners benachrichtigt ???
		// Zuerst ein Event, "neue Row an der Stelle index" herstellen
		TableModelEvent e = new TableModelEvent(this, index, index,
				TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);

		// Nun das Event verschicken ???
		for (int i = 0, n = listeners.size(); i < n; i++) {
			((TableModelListener) listeners.get(i)).tableChanged(e);
		}
	}

	public void removeRow(int row) {
		data.removeElementAt(row);
		fireTableDataChanged();
	}
}

/**
 * EXAMPLE (not used) Applied background and foreground color to single column
 * of a JTable in order to distinguish it apart from other columns.
 */
class ColorColumnRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;

	Color bkgndColor, fgndColor;

	public ColorColumnRenderer(Color bkgnd, Color foregnd) {
		super();
		bkgndColor = bkgnd;
		fgndColor = foregnd;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component cell = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);

		cell.setBackground(bkgndColor);
		cell.setForeground(fgndColor);

		return cell;
	}
}

class SmoothColumnRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private static final DecimalFormat formatter = new DecimalFormat("0.000");

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component cellComponent = super.getTableCellRendererComponent(table,
				value, isSelected, hasFocus, row, column);

		cellComponent.setBackground(new Color(150, 150, 150));
		cellComponent.setForeground(Color.BLACK);

		if (value instanceof Number) {
			JLabel label = (JLabel) cellComponent;
			label.setHorizontalAlignment(JLabel.RIGHT);
			Number num = (Number) value;
			String text = formatter.format(num);
			label.setText(text);
		}

		// value = formatter.format( (Number) value );

		return cellComponent;
	}
}

class MeanColorColumnRenderer extends DefaultTableCellRenderer {

	// show different gray background according to luminance

	private static final long serialVersionUID = 1L;

	private static final DecimalFormat formatter = new DecimalFormat("0.000");

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component cellComponent = super.getTableCellRendererComponent(table,
				value, isSelected, hasFocus, row, column);

		if ((double) table.getValueAt(row, column) < 0.40) {
			cellComponent.setBackground(Color.DARK_GRAY);
		} else if ((double) table.getValueAt(row, column) < 0.50) {
			cellComponent.setBackground(Color.GRAY);
		} else if ((double) table.getValueAt(row, column) < 0.60) {
			cellComponent.setBackground(Color.LIGHT_GRAY);
		} else {
			cellComponent.setBackground(Color.WHITE);
		}

		if (value instanceof Number) {
			JLabel label = (JLabel) cellComponent;
			label.setHorizontalAlignment(JLabel.RIGHT);
			Number num = (Number) value;
			String text = formatter.format(num);
			label.setText(text);
		}

		// value = formatter.format( (Number) value );

		return cellComponent;
	}
}

class FlickerColorColumnRenderer extends DefaultTableCellRenderer {
	// show different red background according to flicker value

	private static final long serialVersionUID = 1L;

	private static final DecimalFormat formatter = new DecimalFormat("0.000");

	Color mistyrose = new Color(255, 228, 225);
	Color pink = new Color(255, 192, 203);
	Color lightpink = new Color(255, 182, 193);
	Color tomato = new Color(255, 99, 71);
	Color orangered = new Color(255, 69, 0);
	Color red = new Color(255, 0, 0);

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component cellComponent = super.getTableCellRendererComponent(table,
				value, isSelected, hasFocus, row, column);

		if ((double) table.getValueAt(row, column) < 0.00) {
			cellComponent.setBackground(Color.LIGHT_GRAY);
		} else if ((double) table.getValueAt(row, column) < 0.01) {
			cellComponent.setBackground(mistyrose);
		} else if ((double) table.getValueAt(row, column) < 0.02) {
			cellComponent.setBackground(pink);
		} else if ((double) table.getValueAt(row, column) < 0.03) {
			cellComponent.setBackground(lightpink);
		} else if ((double) table.getValueAt(row, column) < 0.04) {
			cellComponent.setBackground(tomato);
		} else if ((double) table.getValueAt(row, column) < 0.05) {
			cellComponent.setBackground(orangered);
		} else {
			cellComponent.setBackground(red);
		}

		if (value instanceof Number) {
			JLabel label = (JLabel) cellComponent;
			label.setHorizontalAlignment(JLabel.RIGHT);
			Number num = (Number) value;
			String text = formatter.format(num);
			label.setText(text);
		}

		return cellComponent;
	}

}
