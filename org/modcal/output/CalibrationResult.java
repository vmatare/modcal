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

import java.io.IOException;

import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.modcal.data.DoubleSample;
import org.modcal.data.ObservationData;

/**
 * This class represents the ultimate result of a calibration - 
 * @author Victor Mataré
 *
 */
public class CalibrationResult {
	
	SortedSet<ModelOutput> nsResult;
	SortedSet<ModelOutput> rSquaredResult;
	ObservationData observation;
	private final static String header =    "                                      t:  ";
	private final static String obsHeader = "                        Observed Values:  ";
	private final static String colHeader = "    i       NS                R^2\n";
	
	public CalibrationResult(ObservationData obs) throws IOException {
		nsResult = new ConcurrentSkipListSet<ModelOutput>(
				new ModelOutput.CompareNS());
		rSquaredResult = new ConcurrentSkipListSet<ModelOutput>(
				new ModelOutput.CompareRSquared());
		observation = obs;
	}
	
	public void add(ModelOutput mo) {
		nsResult.add(mo);
		rSquaredResult.add(mo);		
	}
	

	public String toString() {
		StringBuilder rv = new StringBuilder();
		
		rv.append(header);
		for (Double d : observation.getData().keySet())
			rv.append(String.format("%17.11g ", d));
		rv.append("\r\n");
		
		rv.append(obsHeader);
		for (DoubleSample d : observation.getData().values())
			rv.append(String.format("%17.11g ", d.values().iterator().next()));
		rv.append("\r\n");
		
		rv.append(colHeader);
		for (ModelOutput r : nsResult)
			rv.append(r + "\r\n");
		rv.append("\r\n");
		
		rv.append(header);
		for (Double d : observation.getData().keySet())
			rv.append(String.format("%17.11g ", d));
		rv.append("\r\n");
		
		rv.append(obsHeader);
		for (DoubleSample d : observation.getData().values())
			rv.append(String.format("%17.11g ", d.values().iterator().next()));
		rv.append("\r\n");
		
		rv.append(colHeader);
		for (ModelOutput r : rSquaredResult)
			rv.append(r + "\r\n");
		rv.append("\r\n");
		
		return rv.toString();
	}
}
