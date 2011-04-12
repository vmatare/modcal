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

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * This class represents the ultimate result of a calibration - 
 * @author Victor Mataré
 *
 */
public class CalibrationResult {
	
	SortedSet<ModelOutput> nsResult;
	SortedSet<ModelOutput> rSquaredResult;
	List<Double> observed;
	List<Double> time;
	private final static String header =    "                                      t:  ";
	private final static String obsHeader = "                        Observed Values:  ";
	private final static String colHeader = "    i       NS                R^2\n";
	
	public CalibrationResult(CalibrationRequest rq) {
		nsResult = new ConcurrentSkipListSet<ModelOutput>(new AbstractModelOutput.CompareNS());
		nsResult.addAll(rq.getResult());
		rSquaredResult = new ConcurrentSkipListSet<ModelOutput>(new AbstractModelOutput.CompareRSquared());
		rSquaredResult.addAll(rq.getResult());
		observed = new LinkedList<Double>(rq.getObserved().getData().values());
		time = new LinkedList<Double>(rq.getObserved().getData().keySet());
	}

	public String toString() {
		StringBuilder rv = new StringBuilder();
		
		rv.append(header);
		for (Double d : time)
			rv.append(String.format("%17.11g ", d));
		rv.append("\r\n");
		
		rv.append(obsHeader);
		for (Double d : observed)
			rv.append(String.format("%17.11g ", d));
		rv.append("\r\n");
		
		rv.append(colHeader);
		for (ModelOutput r : nsResult)
			rv.append(r + "\r\n");
		rv.append("\r\n");
		
		rv.append(header);
		for (Double d : time)
			rv.append(String.format("%17.11g ", d));
		rv.append("\r\n");
		
		rv.append(obsHeader);
		for (Double d : observed)
			rv.append(String.format("%17.11g ", d));
		rv.append("\r\n");
		
		rv.append(colHeader);
		for (ModelOutput r : rSquaredResult)
			rv.append(r + "\r\n");
		rv.append("\r\n");
		
		return rv.toString();
	}
}
