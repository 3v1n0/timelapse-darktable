package operations;

import java.util.LinkedHashMap;

public class DTParameter extends LinkedHashMap<String, Object> {

	/**
	 * Generic class to define darktable parameter
	 */
	private static final long serialVersionUID = -8964653384534158760L;

	int length;
	int wordSize;
	boolean isInterpolatable;

	public DTParameter(String type, int length, DTValue value,
			boolean isInterpolatable) {
		super();
		this.put("type", type);
		this.put("value", value);
		this.length = length;
		this.wordSize = 8;
		this.isInterpolatable = isInterpolatable;
	}

	public DTParameter(String type, int length, DTValue value) {
		this(type, length, value, true);
	}

	/**
	 * @param : String in XMP file describing parameterisation
	 */
	public String read(String param) {
		/*
		 * Retrieve specifed number of Bytes code in hexa : 2 char = 1 Byte
		 */
		String paramString = param.substring(0, this.wordSize * this.length);
		// get value, specified in OPXxx.java types
		this.getValue(this.get("type"), paramString);
		// update param String, removing part already read
		return param.substring(this.wordSize * this.length, param.length());
	}

	public void getValue(Object object, String s) {

		DTValue value = new DTValue();
		String remain = s;
		for (int i = 0; i < this.length; i++) {
			// inverts order of the 8 char word, 2 by 2 char : DDCCBBAA =>
			// AABBCCDD (little/big endian ?)
			s = remain.substring(6, 8) + remain.substring(4, 6)
					+ remain.substring(2, 4) + remain.substring(0, 2);
			remain = remain.substring(8, remain.length());
			if (object.equals("float") || object.equals("double")) {
				// Interpret hexa value as Float
				Integer intValue = Long.valueOf(s, 16).intValue();
				value.put(i, (double) Float.intBitsToFloat(intValue));
			} else if (object.equals("int") || object.equals("boolean")) {
				// Interpret hexa value as Integer
				Integer intValue = Integer.valueOf(s, 16).intValue();
				value.put(i, (double) intValue);
			} else if (object.equals("string")) {
				// interpret as string
				Integer intValue=0;
				for (int j = 0; j < 4; j++) {
					intValue = 16*16*intValue + (Integer.valueOf(s.substring(2*j,2*j+2), 16).intValue());
				}
				value.put(i, (double) intValue);
			} else {
				System.err.println(object + " not yet implemented in DTParameter: contact developer");
				// "double" to implement (clahe)
			}
		}
		this.put("value", value);

	}

	public String write() {
		String param = "";
		for (int i = 0; i < this.length; i++) {
			// get value(i)
			DTValue dtVal = (DTValue) this.get("value");
			Double vald = (Double) dtVal.get(i);
			Integer vali = null;
			if (this.get("type").equals("float")) {
				// Write float in hexa
				Float valf = (float) ((double) vald);
				vali = Float.floatToIntBits(valf);
			} else if (this.get("type").equals("int")
					|| this.get("type").equals("boolean")) {
				// 4*char = 1*int
				// Write integer in hexa
				vali = (int) ((double) vald);
			} else if (this.get("type").equals("string")) {
				// TODO write int/string in hexa 
				vali  = (int) ((double) vald);
			}
			String s = String.format("%08x", vali);
			// inverts order of the 8 char word, 2 by 2 char : DDCCBBAA =>
			// AABBCCDD (little/big endian convention?)
			param = param + s.substring(6, 8) + s.substring(4, 6)
					+ s.substring(2, 4) + s.substring(0, 2);
		}		
		return param;
	}

}
