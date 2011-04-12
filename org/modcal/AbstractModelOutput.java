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

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;


public abstract class AbstractModelOutput implements Serializable, ModelOutput {
	
	private static final long serialVersionUID = 3569717711794570180L;
	protected NavigableMap<Double, Double> simulated;
	private double rSquaredQuality;
	private double nsQuality;
	private String targetParam;
	private int iteration;
	
	public static class CompareRSquared implements Comparator<ModelOutput> {
		public int compare(ModelOutput arg0, ModelOutput arg1) {
			return arg1.compareRSquared(arg0);
		}
	}
	
	public static class CompareNS implements Comparator<ModelOutput> {
		public int compare(ModelOutput arg0, ModelOutput arg1) {
			return arg1.compareNS(arg0);
		}
	}
	
	public int compareRSquared(ModelOutput o) {
		return getRSquared().compareTo(o.getRSquared());
	}
	
	public int compareNS(ModelOutput o) {
		return this.getNS().compareTo(o.getNS());
	}

	public NavigableMap<Double, Double> getOutput() {
		return simulated;
	}
	protected void setOutput(NavigableMap<Double, Double> o) {
		simulated = o;
	}
	public abstract void init() throws IOException;
	
	public Double getRSquared() { return rSquaredQuality; }
	public Double getNS() { return nsQuality; }
	
	
	public void intersect(NavigableMap<Double, Double> measured) {
		Map.Entry<Double, Double> se1, se2;
		NavigableMap<Double, Double> processedSeries = 
			new ConcurrentSkipListMap<Double, Double>();

		for (Map.Entry<Double, Double> me : measured.entrySet()) {
			if (simulated.containsKey(me.getKey()))
				processedSeries.put(me.getKey(), simulated.get(me.getKey()));
			else {
				se1 = simulated.lowerEntry(me.getKey());
				se2 = simulated.higherEntry(me.getKey());
				if (se1 == null || se2 == null) throw new IncompleteOutputException();
				
				//*
				double d = (
						(se2.getValue() - se1.getValue())
						/ (se2.getKey() - se1.getKey())
				)
				* (me.getKey() - se1.getKey())
				+ se1.getValue();
				
				processedSeries.put(me.getKey(), d);
				//*/
				
				/*if (Math.abs(se1.getKey() - me.getKey()) < Math.abs(se2.getKey() - me.getKey()))
					processedSeries.put(me.getKey(), se1.getValue());
				else processedSeries.put(me.getKey(), se2.getValue()); //*/
			}
		}
		simulated = processedSeries;
		calcQuality(measured);
	}
	
	private void calcQuality(Map<Double, Double> measured) {
		double num = 0, avgS = 0, avgM = 0, denom1 = 0, denom2 = 0;
		denom1 = 0d;
		
		for (Double i : simulated.values()) avgS += i;
		avgS /= simulated.size();
		
		for (Double i : measured.values()) avgM += i;
		avgM /= measured.size();
		
		for (Map.Entry<Double, Double> me : measured.entrySet())
			num += (me.getValue() - avgM) * (simulated.get(me.getKey()) - avgS);
		
		for (Double mv : measured.values())
			denom1 += (mv - avgM) * (mv - avgM);
		for (Double sv : simulated.values())
			denom2 += (sv - avgS) * (sv - avgS);
		
		rSquaredQuality = (num * num) / (denom1 * denom2);
		
		num = 0;
		
		for (Map.Entry<Double, Double> me : measured.entrySet()) {
			double tmp = (me.getValue() - simulated.get(me.getKey()));
			num += tmp * tmp;
		}
		
		nsQuality = 1 - (num / denom1);
	}

	public void setTargetParam(String targetParam) {
		this.targetParam = targetParam;
	}

	public String getTargetParam() {
		return targetParam;
	}
	
	public String toString() {
		StringBuilder rv = new StringBuilder();
		rv.append(String.format("%5d", getIteration()) + " ");
		rv.append(String.format("%17.11g", getNS()) + " ");
		rv.append(String.format("%17.11g", getRSquared()) + " ");
		
		for (Double k : simulated.values()) {
			rv.append(String.format("%17.11g", k));
			rv.append(" ");
		}
		return rv.toString();
	}
	
	public String shortInfo() {
		StringBuilder rv = new StringBuilder();
		rv.append("Iteration #" + iteration + ": ");
		rv.append("R^2 = " + rSquaredQuality + "; ");
		rv.append("NS  = " + nsQuality);
		return rv.toString();
	}
	
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}
	public int getIteration() {
		return iteration;
	}
}
