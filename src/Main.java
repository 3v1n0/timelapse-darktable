import gui.LaunchGUI;

import java.io.IOException;

import com.martiansoftware.jsap.JSAPException;

import core.TLDTCore;

public class Main {

	/**
	 * @param args
	 * @throws JSAPException
	 * @throws IOException
	 */
	public static void main(String[] args) throws JSAPException, IOException {

		if (args.length == 0) {
			new LaunchGUI();
		} else {
			TLDTCore core = new TLDTCore(args);
			core.generateTimelapse();
		}
	}

}
