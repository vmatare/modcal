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

package org.modcal.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class ObservationData implements Serializable {

	private static final long serialVersionUID = -5017969980532748068L;
	private TimeSeries data;
	
	public ObservationData() {};
	
	public ObservationData(String path, Set<String> paramNames) throws IOException  {
		File obsFile = new File(path);
		BufferedReader fr = new BufferedReader(new FileReader(obsFile));
		String line = fr.readLine();
		double time;
		List<String> tokens;
		List<String> heading = Arrays.asList(line.trim().split("\\s+"));
		int lineCount = 1;
		
		if (!(heading.get(0).equalsIgnoreCase("time")))
			throw new BrokenDataException(obsFile.getAbsolutePath() + ":\""
					+ lineCount + "\": The first column heading must be \"Time\".");
		
		for (String paramName : paramNames)
			if (!heading.contains(paramName)) throw new BrokenDataException(
					obsFile.getAbsolutePath() + ": line " + lineCount
					+ ": Missing parameter: " + paramName);
		
		data = new TimeSeries();
		
		while ((line = fr.readLine()) != null) {
			lineCount++;
			tokens = Arrays.asList(line.trim().split("\\s+"));
			if (tokens.size() != paramNames.size() + 1)
				throw new BrokenDataException(obsFile.getAbsolutePath() +
						": line " + line + ": Length of data row doesn't match heading.");

			for (String s : tokens)
				if (!NumericString.isValid(s))
					throw new BrokenDataException(obsFile.getAbsolutePath()
							+ ": line " + lineCount + ": invalid value: "
							+ s);

			time = Double.parseDouble(tokens.get(0));

			DoubleSample params = new DoubleSample();
			for (int i = 1; i < tokens.size(); i++)
				params.put(heading.get(i), Double.parseDouble(tokens.get(i++)));					

			data.put(time, params);
		}
		fr.close();
	}
		
	public TimeSeries getData() { return data; }
	public void setData(TimeSeries data) { this.data = data; }

}
