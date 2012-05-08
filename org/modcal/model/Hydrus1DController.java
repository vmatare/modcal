/*
 *   This file is part of ModCal.
 *
 *   ModCal is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ModCal is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ModCal.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *   Additional permission under GNU GPL version 3 section 7
 *   
 *   If you modify this Program, or any covered work, by linking or
 *   combining it with Mule ESB (or a modified version of Mule ESB),
 *   containing parts covered by the terms of CPAL, the licensors of
 *   this Program grant you additional permission to convey the
 *   resulting work.
 *   {Corresponding Source for a non-source form of such a combination
 *   shall include the source code for the parts of Mule ESB used as
 *   well as that of the covered work.}
 *   
 */

package org.modcal.model;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.modcal.ConfigException;
import org.modcal.Settings;
import org.modcal.data.NumericSample;
import org.modcal.data.NumericString;
import org.modcal.data.ParameterNotFoundException;
import org.modcal.data.ParameterSample;
import org.modcal.output.EmptyModelOutput;
import org.modcal.output.Hydrus1DOutput;
import org.modcal.output.ModelOutput;

public class Hydrus1DController implements ConfiguredModel<NumericSample> {
	
	private final String dataPath, h1dExe;
	private long timeout;
	private Process h1dProcess;
	private Timer h1dTimer;
	private final File dataFile, projectPath;
	private BufferedWriter dataWriter;
	private BufferedReader dataReader;
	private Map<String, Set<String>> param2Filename;

	public Hydrus1DController(String dataPath, Map<String, Set<String>> param2Filename) throws IOException {
		this.param2Filename = param2Filename;
		this.dataPath = dataPath;
		h1dExe = Settings.getString("Hydrus1D.path");
		timeout = Long.valueOf(Settings.getString("Hydrus1D.timeout")) * 1000;
		projectPath = new File(dataPath);
		if (!projectPath.isDirectory()) throw new FileNotFoundException(
				"Not a directory: " + dataPath);
		dataFile = new File(projectPath + "\\Selector.in");
		if (!dataFile.canRead()) throw new IOException(
				dataFile.getAbsolutePath() + " is not readable.");
		File h1dFile = new File(h1dExe);
		if (!h1dFile.canExecute()) throw new FileNotFoundException(
				h1dExe + ": Not an executable file.");
	}
	
	public ModelOutput runModel(NumericSample sample) throws IOException, InterruptedException, ConfigException {
		
		useSample(sample);
		
		ProcessBuilder pb = new ProcessBuilder(h1dExe, dataPath);
		pb.directory(new File(dataPath));
		pb.redirectErrorStream(true);
		h1dProcess = pb.start();
		
		KillHydrus1D killer = new KillHydrus1D(h1dProcess);
		h1dTimer = new Timer("Hydrus1DTimer");
		h1dTimer.schedule(killer, timeout); // starts a timer thread

		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(h1dProcess.getOutputStream()));
		w.newLine();
		w.flush();
		// We have to pick up all the output or the thing won't exit, even if we write a newline.
		BufferedReader r = new BufferedReader(new InputStreamReader(h1dProcess.getInputStream()));
		while (r.readLine() != null);
		r.close();
		w.close();
		
		h1dProcess.waitFor(); // returns when finished or killed
		
		if (killer.killed()) return new EmptyModelOutput();
		h1dTimer.cancel();
		return new Hydrus1DOutput(dataPath, param2Filename);
	}
	
	
	/**
	 * Parameter names must end with a dot and a number starting from 1, designating the
	 * intended horizon.
	 * @param sample The sample to be incorporated into the Hydrus 1D input data
	 */
	private void useSample(NumericSample sample) throws IOException {
		String line, tmp;
		int horizCnt = 0;
		
		Map<Integer, Map<Integer, String>> replaceHorizons = new HashMap<Integer, Map<Integer, String>>();
		dataReader = new BufferedReader(new FileReader(dataFile));
		dataWriter = new BufferedWriter(new FileWriter(dataFile + ".ModCalTMP"));
		
		while ((line = dataReader.readLine()) != null) {
			List<String> lineTokens = Arrays.asList(line.trim().split("\\s+"));

			if (lineTokens.size() > 0) {
				
				if (replaceHorizons.isEmpty())
					replaceHorizons = findParams(sample, lineTokens);
				else {
					horizCnt++;
					if (replaceHorizons.containsKey(horizCnt)) {
						tmp = replaceParms(line, replaceHorizons.get(horizCnt), sample);
						if (replaceHorizons.get(horizCnt).isEmpty()) replaceHorizons.remove(horizCnt);
						if (tmp != null) line = tmp;
					}
					if (replaceHorizons.isEmpty()) horizCnt = 0;
				}
			}
			dataWriter.write(line + "\r\n");
		}
		dataReader.close();
		dataWriter.close();
		if (replaceHorizons.size() != 0) throw new ParameterNotFoundException(
				dataFile.getAbsolutePath() + ": Couldn't locate parameters: " +
				replaceHorizons.values());
		File tmpFile = new File(dataFile + ".ModCalTMP");
		if (!dataFile.delete()) throw new IOException(
				"Can't delete " + dataFile + ".");
		if (!tmpFile.renameTo(dataFile)) throw new IOException(
				"Can't rename " + dataFile + ".ModCalTMP" + " to " + dataFile);
	}
	
	private Map<Integer, Map<Integer, String>> findParams(
			NumericSample sample, List<String> lineTokens) {
		
		Map<Integer, Map<Integer, String>> horizonIndices =
				new HashMap<Integer, Map<Integer, String>>();
		
		Map<Integer, String> indices = new HashMap<Integer, String>();
		
		for (String name : sample.keySet()) {
			
			String[] param = name.split("\\&");
			Integer parHoriz = Integer.valueOf(param[1]);
			String parName = param[0];
			
			if (lineTokens.contains(parName)) {
				
				if (!horizonIndices.containsKey(parHoriz)) {
					indices = new HashMap<Integer, String>();
					horizonIndices.put(parHoriz, indices);
				}
				else indices = horizonIndices.get(parHoriz);
				
				indices.put(lineTokens.indexOf(parName), name);
			}
		}
		
		return horizonIndices;
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
