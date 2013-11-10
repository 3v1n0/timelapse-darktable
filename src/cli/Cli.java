package cli;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.QualifiedSwitch;
import com.martiansoftware.jsap.UnflaggedOption;

public class Cli {
	
	public JSAP jsap = new JSAP();
	public JSAPResult config;
	

	public Cli(String[] args) throws JSAPException {
		super();
		this.defineOptions(); // update jsap
		this.parseOptions(args); // update config
	}

	
	public void defineOptions() throws JSAPException { 

		// ---- JAVA CLI inputs management ----
	    
		// main arguments img/xmp/out
	    FlaggedOption optImgSrc = new FlaggedOption("imgSrc")
	                            .setStringParser(JSAP.STRING_PARSER)
	                            .setDefault("imgSrc") 
	                            .setRequired(true) 
	                            .setShortFlag('i') 
	                            .setLongFlag("imgSrc");
	    optImgSrc.setHelp("Image source folder (raw or jpg)");
	    this.jsap.registerParameter(optImgSrc);                        
	
	    FlaggedOption optXmpSrc = new FlaggedOption("xmpSrc")
	                            .setStringParser(JSAP.STRING_PARSER)
	                            .setDefault("xmpSrc") 
	                            .setRequired(true) 
	                            .setShortFlag('x') 
	                            .setLongFlag("xmpSrc");
	    optXmpSrc.setHelp("XMP source folder (to interpolate)");
	    this.jsap.registerParameter(optXmpSrc);
	    
	    FlaggedOption optOut = new FlaggedOption("out")
	                            .setStringParser(JSAP.STRING_PARSER)
	                            .setDefault("out") 
	                            .setRequired(true) 
	                            .setShortFlag('o') 
	                            .setLongFlag("out");
	    optOut.setHelp("output folder");
	    this.jsap.registerParameter(optOut);
		
		// optional height / width
	    FlaggedOption optHeight = new FlaggedOption("height")
	                            .setStringParser(JSAP.INTEGER_PARSER)
	                            .setDefault("0") 
	                            .setRequired(false) 
	                            .setShortFlag('h') 
	                            .setLongFlag("height");
	    optHeight.setHelp("Height of the exported JPG");
	    this.jsap.registerParameter(optHeight);
	    
	
	    FlaggedOption optWidth = new FlaggedOption("width")
	                            .setStringParser(JSAP.INTEGER_PARSER)
	                            .setDefault("0") 
	                            .setRequired(false) 
	                            .setShortFlag('w') 
	                            .setLongFlag("width");
	    optWidth.setHelp("Width of the exported JPG");
	    this.jsap.registerParameter(optWidth);
	    
	    // optional splie/linear interpolation
	    FlaggedOption optInterp = new FlaggedOption("interpType")
	    						.setStringParser(JSAP.STRING_PARSER)
	    						.setDefault("linear")
	    						.setRequired(false)
	    						.setShortFlag('t')
	    						.setLongFlag("interpolation-type");
	    optInterp.setHelp("Interpolation type: linear|spline");
	    this.jsap.registerParameter(optInterp);
	
	    
	    // optional export/movie/deflickering
		QualifiedSwitch optIsExportJpg = (QualifiedSwitch) 
								new QualifiedSwitch("isExportJpg")
	                            .setShortFlag('j')
	                            .setLongFlag("export-jpg");
		optIsExportJpg.setHelp("Final JPG export is required");
	    this.jsap.registerParameter(optIsExportJpg);
	    
	    QualifiedSwitch optIsExportMovie = (QualifiedSwitch) 
	    						new QualifiedSwitch("isExportMovie")
	    						.setShortFlag('m')
	    						.setLongFlag("export-movie");
	    optIsExportMovie.setHelp("Timelapse movie making is required");
	    this.jsap.registerParameter(optIsExportMovie);
	
	    QualifiedSwitch optIsDeflick = (QualifiedSwitch) 
	    		new QualifiedSwitch("isDeflick")
	    		.setShortFlag('d')
	    		.setLongFlag("deflick");
	    optIsDeflick.setHelp("Deflikering will be applied");
	    this.jsap.registerParameter(optIsDeflick);
	    
	    // extra arguments
	    UnflaggedOption optRemain = new UnflaggedOption("extra")
	                            .setStringParser(JSAP.STRING_PARSER)
	                            .setDefault("")
	                            .setRequired(false)
	                            .setGreedy(true);
	    optRemain.setHelp("Extra arguments parser");
	    this.jsap.registerParameter(optRemain);
	    
	    // HELP
		QualifiedSwitch optHelp = (QualifiedSwitch) 
				new QualifiedSwitch("help")
                .setShortFlag('H')
                .setLongFlag("help");
		optHelp.setHelp("Display this help");
		this.jsap.registerParameter(optHelp);
    
	}
	
	public void parseOptions(String[] args) {
		// parse input args
		this.config = this.jsap.parse(args);
		

        // failure/help management
        if (!config.success() || config.getBoolean("help")) {
            
            System.err.println();

            // print out specific error messages describing the problems
            // with the command line, THEN print usage, THEN print full
            // help.  This is called "beating the user with a clue stick."
            for (java.util.Iterator<?> errs = config.getErrorMessageIterator();
                    errs.hasNext();) {
                System.err.println("Error: " + errs.next());
            }
            
            System.err.println();
            System.err.println("Usage for arguments:");
            System.err.println("        "+ jsap.getUsage());
            System.err.println();
            System.err.println(jsap.getHelp());
            System.exit(1);
        }
	}
    
}
