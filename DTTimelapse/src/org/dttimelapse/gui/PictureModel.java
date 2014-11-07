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


import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class PictureModel implements TableModel {

	// Unsere Implementation des TableModels

	private Vector pictures = new Vector();
	private Vector listeners = new Vector();

	
	
	// methods
	public void addPicture(Picture picture) {
		// Das wird der Index des Vehikels werden
		int index = pictures.size();
		pictures.add(picture);

		// Jetzt werden alle Listeners benachrichtigt

		// Zuerst ein Event, "neue Row an der Stelle index" herstellen
		TableModelEvent e = new TableModelEvent(this, index, index,
				TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);

		// Nun das Event verschicken
		for (int i = 0, n = listeners.size(); i < n; i++) {
			((TableModelListener) listeners.get(i)).tableChanged(e);
		}
	}

	
	
	// Die Anzahl Columns
	public int getColumnCount() {
		return 10;
	}

	// Die Anzahl Vehikel
	public int getRowCount() {
		return pictures.size();
	}

	// Die Titel der einzelnen Columns
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Index";
		case 1:
			return "Key";
		case 2:
			return "Filename";
		case 3:
			return "Mean";
		case 4:
			return "Aperture";
		case 5:
			return "ExposureTime";
		case 6:
			return "ISO";
		case 7:
			return "Width";
		case 8:
			return "Height";
		case 9:
			return "DateTaken";
		default:
			return null;
		}
	}

	// Der Wert der Zelle (rowIndex, columnIndex)
	public Object getValueAt(int rowIndex, int columnIndex) {
		Picture picture = (Picture) pictures.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return new Integer(picture.getIndex());
		case 1:
			return picture.isKeyframe() ? Boolean.TRUE : Boolean.FALSE;
		case 2:
			return picture.getFilename();
		case 3:
			return new Double(picture.getMean());
		case 4:
			return picture.getAperture();
		case 5:
			return picture.getExposureTime();
		case 6:
			return picture.getISO();
		case 7:
			return picture.getWidth();
		case 8:
			return picture.getHeight();
		case 9:
			return picture.getDateTaken();
		default:
			return null;
		}
	}

	// Eine Angabe, welchen Typ von Objekten in den Columns angezeigt werden
	// soll
	public Class getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return Integer.class;
		case 1:
			return Boolean.class;
		case 2:
			return String.class;
		case 3:
			return Double.class;
		case 4:
			return String.class;
		case 5:
			return String.class;
		case 6:
			return String.class;
		case 7:
			return String.class;
		case 8:
			return String.class;
		case 9:
			return String.class;
		default:
			return null;
		}
	}

	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false; // keine Bearbeitung
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		Picture picture = (Picture) pictures.get(rowIndex);

		switch (columnIndex) {
		case 0:
			picture.setIndex(((Integer) aValue).intValue());
			break;
		case 1:
			picture.setIsKeyframe(((Boolean) aValue).booleanValue());
			break;
		case 2:
			picture.setFileName((String) aValue);
			break;
		case 3:
			picture.setMean(((Double) aValue).doubleValue());
			break;
		case 4:
			picture.setAperture((String) aValue);
			break;
		case 5:
			picture.setExposureTime((String) aValue);
			break;
		case 6:
			picture.setISO((String) aValue);
			break;
		case 7:
			picture.setWidth((String) aValue);
			break;
		case 8:
			picture.setHeight((String) aValue);
			break;
		case 9:
			picture.setDateTaken((String) aValue);
			break;
		}

	}
}

class Picture {
	// holds all need information about pictures in directory
	private int index;
	private boolean keyframe;
	private String filename;
	private double mean;
	private String aperture;
	private String exposureTime;
	private String iso;
	private String width;
	private String height;
	private String dateTaken;

	// constructor
	public Picture(int index, boolean keyframe, String filename, double mean,
			String aperture, String exposureTime, String iso, String width,
			String height, String dateTaken) {
		this.index = index;
		this.keyframe = keyframe;
		this.filename = filename;
		this.mean = mean;
		this.aperture = aperture;
		this.exposureTime = exposureTime;
		this.iso = iso;
		this.width = width;
		this.height = height;
		this.dateTaken = dateTaken;
	}

	// getter
	public int getIndex() {
		return index;
	}

	public boolean isKeyframe() {
		return keyframe;
	}

	public String getFilename() {
		return filename;
	}

	public double getMean() {
		return mean;
	}

	public String getAperture() {
		return aperture;
	}

	public String getExposureTime() {
		return exposureTime;
	}

	public String getISO() {
		return iso;
	}

	public String getWidth() {
		return width;
	}

	public String getHeight() {
		return height;
	}

	public String getDateTaken() {
		return dateTaken;
	}

	
	// setter
	public void setIndex(int index) {
		this.index = index;
	}

	public void setIsKeyframe(boolean keyframe) {
		this.keyframe = keyframe;
	}

	public void setFileName(String name) {
		this.filename = name;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}

	public void setAperture(String name) {
		this.aperture = name;
	}

	public void setExposureTime(String name) {
		this.exposureTime = name;
	}

	public void setISO(String name) {
		this.iso = name;
	}

	public void setWidth(String name) {
		this.width = name;
	}

	public void setHeight(String name) {
		this.height = name;
	}

	public void setDateTaken(String name) {
		this.dateTaken = name;
	}

}
