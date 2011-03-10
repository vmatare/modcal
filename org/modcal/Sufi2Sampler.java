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
import java.io.FileReader;
import java.io.IOException;

import java.util.Vector;


/**
 * This class implements a {@link Sampler} based on the output of SUFI2_LH_sample.exe
 * @author Victor Mataré
 *
 */
public class Sufi2Sampler extends Sampler<NumericSample> {

	private static final long serialVersionUID = -8049950307118999123L;
	private BufferedReader fileReader;
	private String basePath, valPath, infPath;
	private Vector<String> paramNames;
	
	public Sufi2Sampler() {
		basePath = Settings.getString("Sufi2Sampler.path").replaceFirst("\\$", "");
		valPath = basePath + "\\SUFI2.in\\par_val.sf2";
		infPath = basePath + "\\SUFI2.in\\par_inf.sf2";
		paramNames = new Vector<String>();
	}
	
	public void init() throws IOException, InterruptedException {
		String[] cmd = {
				"c:\\windows\\system32\\cmd.exe",
				"/C",
				"cd " + basePath + " && echo j | SUFI2_LH_sample.exe"
		}; // It's not like you could just *execute* a Windows program...
		
		Process sf2Sampler = Runtime.getRuntime().exec(cmd);	
		sf2Sampler.waitFor();
		this.fileReader = new BufferedReader(new FileReader(valPath));
	}
	
	public NumericSample nextSample() throws IOException {
		NumericSample rv;
		
		String line, tokens[];
		
		if ((line = fileReader.readLine()) == null) {
			fileReader.close();
			return null;
		}
		tokens = line.split("\\s+");

		if (tokens.length - 1 != getParamNames().size())
			throw new ParameterMismatchException(valPath + ": Can't match the" +
					"parameters " + getParamNames() + " to this line: \"" +
							line + "\"");
		
		rv = new NumericSample();
		for (int i = 1; i < tokens.length; i++) {
			rv.put(getParamNames().get(i-1), tokens[i]);
		}
		
		return rv;
	}
	
	/**
	 * The names of the parameters are read from the SUFI2 input file.
	 * @return
	 * @throws IOException
	 */
	private Vector<String> getParamNames() throws IOException {
		if (paramNames.size() == 0) {
			String line, tokens[];
			BufferedReader parInf = new BufferedReader(new FileReader(infPath));
			while ((line = parInf.readLine()) != null) {
				tokens = line.trim().split("\\s+");
				if (tokens.length >= 3) {
						if (NumericString.isValid(tokens[1])
								&& NumericString.isValid(tokens[2])) {
							if (!tokens[0].matches(".*\\.[0-9]+$")) throw new BrokenDataException(
									infPath + ": Parameter names must end in a dot followed by a number" +
											" designating the intended horizon.");
							if (paramNames.contains(tokens[0])) throw new BrokenDataException(
									infPath + ": Duplicate parameter name.");
							paramNames.add(tokens[0]);
						}
				}
			}
		}
		if (paramNames.size() == 0)
			throw new BrokenDataException(infPath + ": Can't determine param" +
					"eter names.");
		return paramNames;
	}
}
