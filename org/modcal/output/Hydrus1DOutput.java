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

package org.modcal.output;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.modcal.ConfigException;
import org.modcal.data.BrokenDataException;
import org.modcal.data.DoubleSample;
import org.modcal.data.ParameterNotFoundException;
import org.modcal.data.TimeSeries;


public class Hydrus1DOutput extends ModelOutput {

	private static final long serialVersionUID = -3858997832973262221L;
	
	public Hydrus1DOutput(String path, Map<String, Set<String>> filenames2params) throws IOException, ConfigException {

		TimeSeries simulated = new TimeSeries();
			
		for (Map.Entry<String, Set<String>> e : filenames2params.entrySet()) {
			String filename = e.getKey();
			File outFile = new File(path + "\\" + filename);
			
			if (!outFile.canRead()) throw new IOException(
					outFile.getAbsolutePath() + "is not readable.");

			BufferedReader fr = new BufferedReader(new FileReader(outFile));
			String line = null;
			List<String> tokens = null;
			int columns[] = new int[e.getValue().size()];
			String names[] = new String[e.getValue().size()];
			int i = 0;
			Set<String> params = new CopyOnWriteArraySet<String>(e.getValue());
			
			// find the column index of each parameter in this file
			while (((line = fr.readLine()) != null) && params.size() > 0) {
				tokens = Arrays.asList(line.trim().split("\\s+"));
				for (String param : e.getValue()) {
					if (tokens.contains(param)) {
						if (tokens.indexOf(param) == 0)
							throw new ConfigException(
									"The first column is always the time and can't" +
									"be used as output parameter");
						names[i] = param;
						columns[i++] = tokens.indexOf(param);
						params.remove(param);
					}
				}
			}
			
			// names are removed from params when their index is found
			if (params.size() != 0) throw new ParameterNotFoundException(
					outFile.getAbsolutePath() + ": Can't locate target parameter(s): "
					+ params + ". ");

			if (!tokens.get(0).equals("Time")) throw new BrokenDataException(
					outFile.getAbsolutePath() + ": \"" + line + "\": Unrecognized file format.");

			while ((line = fr.readLine()) != null) {
				tokens = Arrays.asList(line.trim().split("\\s+"));
				
				if (tokens.size() < columns[columns.length-1]) continue;
				
				try {
					Double time = Double.valueOf(tokens.get(0));
					DoubleSample sample;
					for (i = 0; i < columns.length; i++) {
						if (simulated.containsKey(time)) {
							sample = simulated.get(time);
							assert (!sample.containsKey(filename + ":" + names[i]));
						}
						else {
							sample = new DoubleSample();
						}
						sample.put(filename + ":" + names[i],
								Double.valueOf(tokens.get(columns[i])));
						simulated.put(time, sample);
					}
				}
				catch (NumberFormatException nfe) {}
				
			}
		}
		
		this.setSimulated(simulated);
	}
	
}
