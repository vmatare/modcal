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

import java.io.IOException;
import java.io.Serializable;

import java.util.NavigableMap;



public class EmptyModelOutput implements ModelOutput, Serializable {
	
	int iteration;
	String target;

	private static final long serialVersionUID = -1469199341173045753L;
	public void init() throws IOException {
	}
	
	public String toString() {
		return getIteration() + ": no result (timeout reached)";
	}
	public String shortInfo() {
		return "Iteration #" + toString();
	}
	
	@Override
	public int compareRSquared(ModelOutput o) {
		return Integer.MIN_VALUE;
	}
	
	public int compareNS(ModelOutput o) {
		return Integer.MIN_VALUE;
	}
	
	public void intersect(NavigableMap<Double, Double> measured) {}

	public NavigableMap<Double, Double> getOutput() {
		return null;
	}

	public Double getRSquared() {
		return Double.MIN_VALUE;
	}

	public Double getNS() {
		return Double.MIN_VALUE;
	}

	public void setTargetParam(String targetParam) {
		this.target = targetParam;
	}

	public String getTargetParam() {
		return target;
	}

	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

	public int getIteration() {
		return iteration;
	}
	
}
