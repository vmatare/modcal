package org.modcal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This represents a set of Hydrus 1D input data. The main purpose of this
 * class is to import a {@link ParameterSample} into an otherwise constant
 * set of input data
 * @author Victor Mataré
 *
 */
public class Hydrus1DInput implements ModelInput {

	private static final long serialVersionUID = 8903117660425881144L;
	private final File dataFile, projectPath;
	private BufferedWriter dataWriter;
	private BufferedReader dataReader;
	
	public Hydrus1DInput(String path) throws IOException {
		projectPath = new File(path);
		if (!projectPath.isDirectory()) throw new FileNotFoundException(
				"Not a directory: " + path);
		dataFile = new File(projectPath + "\\Selector.in");
		if (!dataFile.canRead()) throw new IOException(
				dataFile.getAbsolutePath() + "is not readable.");
	}
	
	/**
	 * Parameter names must end with a dot and a number starting from 1, designating the
	 * intended horizon.
	 * @param sample The sample to be incorporated into the Hydrus 1D input data
	 */
	public void useSample(ParameterSample<?> sample) throws IOException {
		String line, tmp;
		int lineNum = 0, l;
		List<String> tokens;
		Map<Integer, String> replaceIndices = new HashMap<Integer, String>();
		Map<Integer, Map<Integer, String>> horizons = new HashMap<Integer, Map<Integer, String>>();
		
		dataReader = new BufferedReader(new FileReader(dataFile));
		dataWriter = new BufferedWriter(new FileWriter(dataFile + ".ModCalTMP"));
		
		while ((line = dataReader.readLine()) != null) {
			tokens = Arrays.asList(line.trim().split("\\s+"));

			if (tokens.size() > 0) {
				if (lineNum <= 0) {
					for (String name : sample.keySet()) {
						
						String[] s = name.split("\\.");
						l = Integer.valueOf(s[1]);
						
						if (tokens.contains(s[0])) {
							
							if (!horizons.containsKey(l)) {
								replaceIndices = new HashMap<Integer, String>();
								horizons.put(l, replaceIndices);
							}
							else replaceIndices = horizons.get(l);
							
							replaceIndices.put(tokens.indexOf(s[0]), name);
							lineNum = 1;
							
						}
						else lineNum = 0;
					}
				}
				else if (!line.matches("^\\s*$")) {
					if (horizons.containsKey(lineNum)) {
						tmp = replaceParms(line, horizons.get(lineNum), sample);
						if (horizons.get(lineNum).isEmpty()) horizons.remove(lineNum);
						if (tmp != null) line = tmp;
					}
					lineNum++;
				}
			}
			dataWriter.write(line + "\r\n");
		}
		dataReader.close();
		dataWriter.close();
		if (horizons.size() != 0) throw new ParameterMismatchException(
				dataFile.getAbsolutePath() + ": Couldn't locate parameters: " +
				horizons.values());
		File tmpFile = new File(dataFile + ".ModCalTMP");
		if (!dataFile.delete()) throw new IOException(
				"Can't delete " + dataFile + ".");
		if (!tmpFile.renameTo(dataFile)) throw new IOException(
				"Can't rename " + dataFile + ".ModCalTMP" + " to " + dataFile);
	}
	
	/**
	 * Replaces given indices in a line with give parameters.
	 * @param input The line to be processed
	 * @param indices (index, parameterName) pairs
	 * @param data The data to be inserted at the specified indices, matched by name.
	 * @return the modified input line, if and only if all indices were successfully replaced. Else null.
	 */
	private String replaceParms(
			String input,
			Map<Integer, String> indices,
			ParameterSample<?> data) {
		StringBuilder rv = new StringBuilder();
		int idx = 0, si = 0, wi = 0;
		
		Pattern word = Pattern.compile("\\S+");
		Pattern space = Pattern.compile("\\s+");
		
		Matcher wm = word.matcher(input);
		Matcher sm = space.matcher(input);
		
		while (si < input.length() && wi < input.length()) {
			if (sm.find()) {
				si = sm.end();
				rv.append(sm.group());
			}
			if (wm.find()) {
				wi = wm.end();
				if (indices.containsKey(idx) && NumericString.isValid(wm.group())) {
					rv.append(data.get(indices.get(idx)));
					indices.remove(idx);
				}
				else rv.append(wm.group());
				idx++;
			}
		}
		if (indices.size() == 0) return rv.toString();
		else return null;
	}

}
