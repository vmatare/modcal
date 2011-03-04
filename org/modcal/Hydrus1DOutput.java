package org.modcal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;


public class Hydrus1DOutput extends AbstractModelOutput {

	private static final long serialVersionUID = -3858997832973262221L;
	private final File outFile;
	
	public Hydrus1DOutput(String path) throws IOException {
		outFile = new File(path + "\\" + Settings.getString("Hydrus1DOutput.filename"));
		if (!outFile.canRead()) throw new IOException(
				outFile.getAbsolutePath() + "is not readable.");
	}
	
	
	public void init() throws IOException {
		
		if (getTargetParam() == null) throw new IllegalStateException(
				"Target parameter has not been set.");
		
		BufferedReader fr = new BufferedReader(new FileReader(outFile));
		int column = -1;
		String line = null;
		List<String> tokens = null;
		
		while (column < 0 && (line = fr.readLine()) != null) {
			tokens = Arrays.asList(line.trim().split("\\s+"));
			column = tokens.indexOf(getTargetParam());
		}
		if (column < 0) throw new ParameterMismatchException(
				outFile.getAbsolutePath() + ": Can't locate target parameter" +
						" " + getTargetParam() + ". ");
			
		if (!tokens.get(0).equals("Time")) throw new BrokenDataException(
				outFile.getAbsolutePath() + ": \"" + line + "\": Unrecognized file format.");
		fr.readLine();
		fr.readLine();
		
		simulated = new ConcurrentSkipListMap<Double, Double>();
		while ((line = fr.readLine()) != null) {
			tokens = Arrays.asList(line.trim().split("\\s+"));
			if (tokens.size() >= column)
				simulated.put(Double.valueOf(tokens.get(0)), Double.valueOf(tokens.get(column)));
		}
		
		this.setOutput(simulated);
	}
	
}
