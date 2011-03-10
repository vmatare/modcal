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
import java.io.Serializable;

import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;


public class ObservationData implements Serializable {

	private static final long serialVersionUID = -5017969980532748068L;
	private NavigableMap<Double, Double> data;
	private String varName;
	private final File obsFile;
	
	public ObservationData(String path) {
		obsFile = new File(path);
	}
	
	public void init() throws IOException {
		BufferedReader fr = new BufferedReader(new FileReader(obsFile));
		String line = fr.readLine();
		String[] tokens = line.split("\\s+");
		data = new ConcurrentSkipListMap<Double, Double>();
		
		if (tokens.length != 2) throw new BrokenDataException(
				obsFile.getAbsolutePath() + ": \"" + line + "\": Invalid column header.");
		varName = tokens[1];
		
		while ((line = fr.readLine()) != null) {
			tokens = line.split("\\s+");
			if (tokens.length == 2 && NumericString.isValid(tokens[0])
					&& NumericString.isValid(tokens[1]))
				data.put(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]));
			else
				throw new BrokenDataException(obsFile.getAbsolutePath() +
						": \"" + line + "\": Invalid data row.");
		}
	}
	
	public String getVarName() { return varName; }
	public NavigableMap<Double, Double> getData() { return data; }

}
