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



import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class PictureModel extends AbstractTableModel {


	private Vector data = new Vector();   // rows
	private Vector listeners = new Vector();
	
	String[] columnNames = {
			"Index",		// 0
			"Key",
			"Filename",
			"Aperture",
			"ExposureTime",
			"ISO",
			"Width",
			"Height",
			"DateTaken",
			"Mean",			//  9
			"Smooth",		// 10  
			"Exp cor",
			"Crop T",		
			"Crop B",
			"Crop L",
			"Crop R"
	};

	 //public final Object[] longValues = {"", new Integer(20), new Float(20), new Float(20), Boolean.TRUE};

	
	
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
		 return ((Vector) data.get(row)).get(col);
	}

	public Class getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }

	public boolean isCellEditable(int row, int col) {
        if (col == 1) {  // isKeyframe
            return true; 
        } else {
            return false;    // no editing
        }
	}
	
	public void setValueAt(Object value, int row, int col){
		 ((Vector) data.get(row)).setElementAt(value, col);
		 
		 fireTableCellUpdated(row,col);  // don't works ??		 
		 
	}
	
	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	
	
	public void addData(Vector vec){   // add data vector
		
		// if the vector is to short fill zero
		//        default table modell uses 'null' to fill		
		while (vec.size() <  columnNames.length) {
			vec.add(0.0);
		}
		
		int index = data.size();  // index of new row
		
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
		 
	public void removeRow(int row){
		 data.removeElementAt(row);
		 fireTableDataChanged();
	}
}



