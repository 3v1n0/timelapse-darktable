package operations;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import operations.iop.Anlfyeni;
import operations.iop.Atrous;
import operations.iop.Basecurve;
import operations.iop.Bilat;
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
		return new Class<?>[] { Anlfyeni.class, Atrous.class, Basecurve.class,
				Bilat.class, Bloom.class, Cacorrect.class, Channelmixer.class,
				Clahe.class, Clipping.class, Colorzones.class,
				Denoiseprofile.class, Exposure.class, Gamma.class,
				Graduatednd.class, Hotpixels.class, Lens.class, Levels.class,
				Monochrome.class, Nlmeans.class, Rawdenoise.class,
				Shadhi.class, Sharpen.class, Splittoning.class, Spots.class,
				Temperature.class, Tonecurve.class, Velvia.class,
				Vibrance.class, Vignette.class, };
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

}
