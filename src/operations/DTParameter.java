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
	
	// TODO: Big reghression: encode/decode now compress large inputs (>100 char)
	// FIXME: for darktable >=1.4
// OLD CODE darktable<1.4
//
//	// encode binary blob into text:
//	void dt_exif_xmp_encode (const unsigned char *input, char *output, const int len)
//	{
//	  const char hex[16] =
//	  {
//	    '0', '1', '2', '3', '4', '5', '6', '7', '8',
//	    '9', 'a', 'b', 'c', 'd', 'e', 'f'
//	  };
//	  for(int i=0; i<len; i++)
//	  {
//	    const int hi = input[i] >> 4;
//	    const int lo = input[i] & 15;
//	    output[2*i]   = hex[hi];
//	    output[2*i+1] = hex[lo];
//	  }
//	  output[2*len] = '\0';
//	}
//
//	// and back to binary
//	void dt_exif_xmp_decode (const char *input, unsigned char *output, const int len)
//	{
//	  // ascii table:
//	  // 48- 57 0-9
//	  // 97-102 a-f
//	#define TO_BINARY(a) (a > 57 ? a - 97 + 10 : a - 48)
//	  for(int i=0; i<len/2; i++)
//	  {
//	    const int hi = TO_BINARY( input[2*i  ] );
//	    const int lo = TO_BINARY( input[2*i+1] );
//	    output[i] = (hi << 4) | lo;
//	  }
//	#undef TO_BINARY
//	}
//
	// NEW CODE darktable v1.4
//
//	// encode binary blob into text:
//	char *dt_exif_xmp_encode (const unsigned char *input, const int len, int *output_len)
//	{
//	#define COMPRESS_THRESHOLD 100
//
//	  char *output = NULL;
//	  gboolean do_compress = FALSE;
//
//	  // if input data field exceeds a certain size we compress it and convert to base64;
//	  // main reason for compression: make more xmp data fit into 64k segment within
//	  // JPEG output files.
//	  char *config = dt_conf_get_string("compress_xmp_tags");
//	  if(config)
//	  {
//	    if(!strcmp(config, "always"))
//	      do_compress = TRUE;
//	    else if((len > COMPRESS_THRESHOLD) && !strcmp(config, "only large entries"))
//	      do_compress = TRUE;
//	    else
//	      do_compress = FALSE;
//	  }
//
//	  if(do_compress)
//	  {
//	    int result;
//	    uLongf destLen = compressBound(len);
//	    unsigned char *buffer1 = (unsigned char *)malloc(destLen);
//
//	    result = compress(buffer1, &destLen, input, len);
//
//	    if(result != Z_OK)
//	    {
//	      free(buffer1);
//	      return NULL;
//	    }
//
//	    // we store the compression factor
//	    const int factor = MIN(len / destLen + 1, 99);
//
//	    char *buffer2 = (char *)g_base64_encode(buffer1, destLen);
//	    free(buffer1);
//	    if(!buffer2) return NULL;
//
//	    int outlen = strlen(buffer2) + 5;  // leading "gz" + compression factor + base64 string + trailing '\0'
//	    output = (char *)malloc(outlen);
//	    if(!output)
//	    {
//	      g_free(buffer2);
//	      return NULL;
//	    }
//
//	    output[0] = 'g';
//	    output[1] = 'z';
//	    output[2] = factor / 10 + '0';
//	    output[3] = factor % 10 + '0';
//	    strcpy(output+4, buffer2);
//	    g_free(buffer2);
//
//	    if(output_len) *output_len = outlen;
//	  }
//	  else
//	  {
//	    const char hex[16] =
//	    {
//	      '0', '1', '2', '3', '4', '5', '6', '7', '8',
//	      '9', 'a', 'b', 'c', 'd', 'e', 'f'
//	    };
//
//	    output = (char *)malloc(2*len + 1);
//	    if(!output) return NULL;
//
//	    if(output_len) *output_len = 2*len + 1;
//
//	    for(int i=0; i<len; i++)
//	    {
//	      const int hi = input[i] >> 4;
//	      const int lo = input[i] & 15;
//	      output[2*i]   = hex[hi];
//	      output[2*i+1] = hex[lo];
//	    }
//	    output[2*len] = '\0';
//	  }
//
//	  return output;
//
//	#undef COMPRESS_THRESHOLD
//	}
//
//	// and back to binary
//	unsigned char *dt_exif_xmp_decode (const char *input, const int len, int *output_len)
//	{
//	  unsigned char *output = NULL;
//
//	  // check if data is in compressed format
//	  if(!strncmp(input, "gz", 2))
//	  {
//	    // we have compressed data in base64 representation with leading "gz"
//
//	    // get stored compression factor so we know the needed buffer size for uncompress
//	    const float factor = 10*(input[2] - '0') + (input[3] - '0');
//
//	    // get a rw copy of input buffer omitting leading "gz" and compression factor
//	    unsigned char *buffer = (unsigned char *)strdup(input + 4);
//	    if(!buffer) return NULL;
//
//	    // decode from base64 to compressed binary
//	    gsize compressed_size;
//	    g_base64_decode_inplace((char *)buffer, &compressed_size);
//
//	    // do the actual uncompress step
//	    int result = Z_BUF_ERROR;
//	    uLongf bufLen = factor*compressed_size;
//	    uLongf destLen;
//
//	    // we know the actual compression factor but if that fails we re-try with
//	    // increasing buffer sizes, eg. we don't know (unlikely) factors > 99
//	    do
//	    {
//	       if(output) free(output);
//	       output = (unsigned char *)malloc(bufLen);
//	       if(!output) break;
//
//	       destLen = bufLen;
//	 
//	       result = uncompress(output, &destLen, buffer, compressed_size);
//
//	       bufLen *= 2;
//
//	    } while(result == Z_BUF_ERROR);
//
//	    free(buffer);
//
//	    if(result != Z_OK)
//	    {
//	      if(output) free(output);
//	      return NULL;
//	    }
//
//	    if(output_len) *output_len = destLen;
//
//	  }
//	  else
//	  {
//	    // we have uncompressed data in hexadecimal ascii representation
//
//	    // ascii table:
//	    // 48- 57 0-9
//	    // 97-102 a-f
//	#define TO_BINARY(a) (a > 57 ? a - 97 + 10 : a - 48)
//
//	    // make sure that we don't find any unexpected characters indicating corrupted data
//	    if(strspn(input, "0123456789abcdef") != strlen(input)) return NULL;
//
//	    output = (unsigned char *)malloc(len/2);
//	    if(!output) return NULL;
//
//	    if(output_len) *output_len = len/2;
//
//	    for(int i=0; i<len/2; i++)
//	    {
//	      const int hi = TO_BINARY( input[2*i  ] );
//	      const int lo = TO_BINARY( input[2*i+1] );
//	      output[i] = (hi << 4) | lo;
//	    }
//	#undef TO_BINARY
//	  }
//
//	  return output;
//	}

	

}
