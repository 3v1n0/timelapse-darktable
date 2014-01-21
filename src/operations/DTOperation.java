package operations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.DatatypeConverter;

import operations.iop.Anlfyeni;
import operations.iop.Atrous;
import operations.iop.Basecurve;
import operations.iop.Bilat;
import operations.iop.Bilateral;
import operations.iop.Bloom;
import operations.iop.Cacorrect;
import operations.iop.Channelmixer;
import operations.iop.Clahe;
import operations.iop.Clipping;
import operations.iop.Colorzones;
import operations.iop.Denoiseprofile;
import operations.iop.Exposure;
import operations.iop.Gamma;
import operations.iop.Graduatednd;
import operations.iop.Hotpixels;
import operations.iop.Lens;
import operations.iop.Levels;
import operations.iop.Monochrome;
import operations.iop.Nlmeans;
import operations.iop.Rawdenoise;
import operations.iop.Shadhi;
import operations.iop.Sharpen;
import operations.iop.Splittoning;
import operations.iop.Spots;
import operations.iop.Temperature;
import operations.iop.Tonecurve;
import operations.iop.Velvia;
import operations.iop.Vibrance;
import operations.iop.Vignette;

public abstract class DTOperation extends LinkedHashMap<String, DTParameter> {

	/**
	 * Generic class defining DTOperation DTOperation is the generic name to
	 * define darktable filters such as exposure, levels, temperature... and its
	 * properties for a given image settings
	 */
	private static final long serialVersionUID = 8821139471068698706L;

	public String name;
	/** dt_iop name (e.g. "exposure ") */
	public String version;
	/** version of iop */
	public Boolean enabled;
	/** enable flag */
	public String blendVersion;
	/** version of blendop */
	public String blendParams;
	/** parameterization of blendop => to be updated with OPType */
	public String multiPriority;
	/** multi_priority property (for duplicated iop), (default = 0) */
	public String multiName;
	/** multi_name property (default empty string) */
	public boolean isInterpolatable;

	/** interpolation is possible for this operation **/

	public DTOperation(String name, boolean isInterpolatable) {
		super();
		this.name = name;
		this.isInterpolatable = isInterpolatable;
		this.version = "0";
	}

	public DTOperation(String name) {
		this(name, true);
	}

	/**
	 * readOperation reads all parameters (gets all values) of a known filter
	 * (in dt/operations/iop) and updates its properties (enable, multi, etc...)
	 * 
	 * @param op
	 *            : iop/filter (e.g. clipping)
	 * @param ver
	 *            : version (e.g. 1) // not used for now, could be used for
	 *            support of new/older DT releases
	 * @param ena
	 *            : enabled flag
	 * @param par
	 *            : parameter read in XML (e.g. "12df42ddf23df2fd")
	 * @param blendver
	 * @param blendpar
	 *            // TODO read blendparam => inspired from iop
	 * @param multiprio
	 * @param multiname
	 * @return
	 */
	public static DTOperation readOperation(String op, String ver, String ena,
			String par, String blendver, String blendpar, String multiprio,
			String multiname) {
		for (Class<?> classe : availableOperations) {
			// scan all classes defined in operations/iop
			try {

				DTOperation dtOp = (DTOperation) classe.newInstance();
				if (dtOp.name.equals(op)) {
					// current operation name (op) matches to one class
					// definition
					// => we read the parameters of current operation
					if (!ena.equals("1")) {
						// iop is not active, but we still scan parameters
						dtOp.enabled = false;
					} else {
						dtOp.enabled = true;
					}

					// update parameters definition according to DT_MODULE
					// version
					dtOp.version = ver;
					dtOp.addParam();
					
					// par: could be compressed since darktable 1.4
					// decompress to old hexa format
					// TODO: change parameter type to byte[]
					Boolean isCompressed = par.startsWith("gz");
					if(isCompressed){
						par = par.substring(4); // remove gz##
						byte[] bufferBase64 = DatatypeConverter.parseBase64Binary(par);
						byte[] decomp = decompress(bufferBase64);
						// uncompressed hexadecimal parameter (as in dtVer<1.4)
						par=DatatypeConverter.printHexBinary(decomp);	
					}

					// initialise object corresponding to current operation
					// parameters (defined in dt/operation/iop/"Operation".java)
					Set<String> opParamNames = dtOp.keySet();
					for (String param : opParamNames) {
						// each DTParameter reads the first words of the the
						// string par
						// and returns remaining string (for next parameters)
						// System.out.println(op+" "+param);
						par = dtOp.get(param).read(par);
					}
					dtOp.blendParams = blendpar;
					dtOp.blendVersion = blendver;
					dtOp.multiPriority = multiprio;
					dtOp.multiName = multiname;
					return dtOp;
				}

			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		System.err.println("No class found to read " + op);
		return null;
	}

	public abstract void addParam();

	public void printVersionError() {
		System.err
				.println("operation:"
						+ this.name
						+ " version:"
						+ this.version
						+ " not yet supported... contact project member or update sources");
	}

	@SuppressWarnings("unchecked")
	// dynamic scanning of available operations (put in ./operations/iop folder)
	// public static Class<? extends DTOperation> [] availableOperations =
	// (Class<? extends DTOperation>[]) getClasses("operations.iop");
	// static definition
	// TODO : update the list each time you add a class in operations/iop and
	// then CTRL+SHIFT+O to update imports in eclipse
	public static Class<? extends DTOperation>[] availableOperations = (Class<? extends DTOperation>[]) getClasses();

	public static Class<?>[] getClasses() {
		// shell command to generate/update the list:
		// for fic in `ls src/operations/iop` ; do echo ${fic%%.*}.class,; done
		return new Class<?>[] {
				Anlfyeni.class,
				Atrous.class,
				Basecurve.class,
				Bilateral.class,
				Bilat.class,
				Bloom.class,
				Cacorrect.class,
				Channelmixer.class,
				Clahe.class,
				Clipping.class,
				Colorzones.class,
				Denoiseprofile.class,
				Exposure.class,
				Gamma.class,
				Graduatednd.class,
				Hotpixels.class,
				Lens.class,
				Levels.class,
				Monochrome.class,
				Nlmeans.class,
				Rawdenoise.class,
				Shadhi.class,
				Sharpen.class,
				Splittoning.class,
				Spots.class,
				Temperature.class,
				Tonecurve.class,
				Velvia.class,
				Vibrance.class,
				Vignette.class,
		};
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Class<?>[] getClasses(String packageName) {
		try {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			assert classLoader != null;
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			List<String> dirs = new ArrayList<String>();
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(resource.getFile());
			}
			TreeSet<String> classes = new TreeSet<String>();
			for (String directory : dirs) {
				classes.addAll(findClasses(directory, packageName));
			}
			ArrayList<Class<?>> classList = new ArrayList<Class<?>>();
			for (String clazz : classes) {
				classList.add(Class.forName(clazz));
			}
			return classList.toArray(new Class[classes.size()]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static TreeSet<String> findClasses(String directory,
			String packageName) throws Exception {
		TreeSet<String> classes = new TreeSet<String>();
		if (directory.startsWith("file:") && directory.contains("!")) {
			String[] split = directory.split("!");
			URL jar = new URL(split[0]);
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			ZipEntry entry = null;
			while ((entry = zip.getNextEntry()) != null) {
				if (entry.getName().endsWith(".class")) {
					String className = entry.getName().replaceAll("[$].*", "")
							.replaceAll("[.]class", "").replace('/', '.');
					classes.add(className);
				}
			}
		}
		File dir = new File(directory);
		if (!dir.exists()) {
			return classes;
		}
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file.getAbsolutePath(), packageName
						+ "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(packageName
						+ '.'
						+ file.getName().substring(0,
								file.getName().length() - 6));
			}
		}
		return classes;
	}

	public static String writeParams(DTOperation dtOp) {
		String params = "";
		Set<String> opParamNames = dtOp.keySet();
		for (String param : opParamNames) {
			params = params + dtOp.get(param).write();
		}
		// TODO: compress directly byte[] instead of hexadecimal param
		int COMPRESS_THRESHOLD = 100;
		if(params.length()>COMPRESS_THRESHOLD){
			byte[] paramsBytes = DatatypeConverter.parseHexBinary(params);
			byte[] paramsBytesCompressed = compress(paramsBytes);
			String paramsCompressed = DatatypeConverter.printBase64Binary(paramsBytesCompressed);
			int compFactor = Math.min(99, paramsBytes.length/paramsBytesCompressed.length + 1);  
			params = "gz"+compFactor+paramsCompressed;
		}
		return params;
	}
	
	public static String writeEnable(DTOperation dtOp) {
		String opEna = "";
		if (dtOp.enabled) {
			opEna = "1";
		} else { 
			opEna = "0";
		}
		return opEna;
	}
	
	public static byte[] compress(byte[] inBytes) {
		// Compress bytes array
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			OutputStream out = new DeflaterOutputStream(baos);
			out.write(inBytes);
			out.close();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		return baos.toByteArray();
	}

	public static byte[] decompress(byte[] bytes) {
		// Decompress bytes array
		InputStream in = new InflaterInputStream(new ByteArrayInputStream(bytes));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[8192];
			int len;
			while((len = in.read(buffer))>0)
				baos.write(buffer, 0, len);
			return baos.toByteArray();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

}
