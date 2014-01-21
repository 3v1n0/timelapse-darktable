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
			} else {
				System.err
				.println(object
						+ " not yet implemented in DTParameter: contact developer");
				// "double" to implement (clahe)
			}
		}
		this.put("value", value);

	}

	public String write() {
		String param = "";
		String s = "";
		for (int i = 0; i < this.length; i++) {
			// get value(i)
			DTValue dtVal = (DTValue) this.get("value");
			Double vald = (Double) dtVal.get(i);
			if (this.get("type").equals("float")) {
				// Write float in hexa
				Float valf = (float) ((double) vald);
				Integer valb = Float.floatToIntBits(valf);
				s = Integer.toHexString(valb);

			} else if (this.get("type").equals("int")
					|| this.get("type").equals("boolean")) {
				// 4*char = 1*int
				// Write integer in hexa
				Integer vali = (int) ((double) vald);
				s = Integer.toHexString(vali);
			}
			while (s.length() < 8) {
				// complete with 0 to have 8 characters
				s = "0" + s;

			}
			// inverts order of the 8 char word, 2 by 2 char : DDCCBBAA =>
			// AABBCCDD (little/big endian convention?)
			param = param + s.substring(6, 8) + s.substring(4, 6)
					+ s.substring(2, 4) + s.substring(0, 2);
		}
		return param;
	}

}
