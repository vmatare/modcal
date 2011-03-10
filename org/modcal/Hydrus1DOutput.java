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
