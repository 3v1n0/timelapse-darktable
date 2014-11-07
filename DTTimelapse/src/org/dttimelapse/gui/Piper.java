package org.dttimelapse.gui;

// Setting up a thread which reads and writes a stream as long as data is available.
// To use it, setup two processes, give their streams to an instance of Piper 
// and wait for the second process to finish and grab its output.

public class Piper implements Runnable {

	private java.io.InputStream input;

	private java.io.OutputStream output;

	public Piper(java.io.InputStream input, java.io.OutputStream output) {
		this.input = input;
		this.output = output;
	}

	public void run() {
		try {
			// Create 512 bytes buffer
			byte[] b = new byte[512];
			int read = 1;
			// As long as data is read; -1 means EOF
			while (read > -1) {
				// Read bytes into buffer
				read = input.read(b, 0, b.length);
				// System.out.println("read: " + new String(b));
				if (read > -1) {
					// Write bytes to output
					output.write(b, 0, read);
				}
			}
		} catch (Exception e) {
			// Something happened while reading or writing streams; pipe is
			// broken
			throw new RuntimeException("Broken pipe", e);
		} finally {
			try {
				input.close();
			} catch (Exception e) {
			}
			try {
				output.close();
			} catch (Exception e) {
			}
		}
	}


}