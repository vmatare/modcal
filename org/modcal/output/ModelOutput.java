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

import java.io.Serializable;

import java.util.Comparator;
import java.util.Map;

import org.modcal.data.DoubleSample;
import org.modcal.data.ObservationData;
import org.modcal.data.TimeSeries;


public class ModelOutput implements Serializable {
	
	private static final long serialVersionUID = 3569717711794570180L;
	private TimeSeries simulated;
	private TimeSeries observed;
	private double rSquaredQuality;
	private double nsQuality;
	private int iteration;
	
	public ModelOutput() {}
		
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
	
	public void setSimulated(TimeSeries ts) {
		simulated = ts;
	}
	
	public TimeSeries getSimulated() {
		return simulated;
	}
	
	public Double getRSquared() { return rSquaredQuality; }
	public Double getNS() { return nsQuality; }
	
	
	public void mapObservation(ObservationData obs) {
		observed = obs.getData();
		Map.Entry<Double, DoubleSample> se1, se2;
		TimeSeries interpolatedSeries = 
			new TimeSeries();

		for (Map.Entry<Double, DoubleSample> me : observed.entrySet()) {
			if (simulated.containsKey(me.getKey()))
				interpolatedSeries.put(me.getKey(), simulated.get(me.getKey()));
			else {
				se1 = simulated.lowerEntry(me.getKey());
				se2 = simulated.higherEntry(me.getKey());
				if (se1 == null || se2 == null) throw new IncompleteOutputException();
				
				DoubleSample interpolatedSample = new DoubleSample();
				for (String paramName : se1.getValue().getParamNames()) {
					//double numerator = (se2.getValue().valueOf(paramName)
					//		- se2.getValue().valueOf(paramName));
					//double denominator = (se2.getKey() - se1.getKey());
					double interpolatedValue = (
							(se2.getValue().valueOf(paramName) 
									- se2.getValue().valueOf(paramName)
							)
							/ (se2.getKey() - se1.getKey()
							) 
							* (me.getKey() - se1.getKey())
							+ se1.getValue().valueOf(paramName));
					interpolatedSample.put(paramName, interpolatedValue);
				}
				
				interpolatedSeries.put(me.getKey(), interpolatedSample);
			}
		}
		simulated = interpolatedSeries;
	}
	
	public void calcQuality() {
		
		if (simulated.firstEntry().getValue().size() > 1)
			throw new RuntimeException("Can't calculate output quality for more than one parameter");

		double num = 0, avgS = 0, avgM = 0, denom1 = 0, denom2 = 0;
		denom1 = 0d;
		
		for (DoubleSample s : simulated.values()) avgS += s.values().iterator().next();
		avgS /= simulated.size();
		
		for (DoubleSample s : observed.values()) avgM += s.values().iterator().next();
		avgM /= observed.size();
		
		for (Map.Entry<Double, DoubleSample> me : observed.entrySet())
			num += ((me.getValue().values().iterator().next() - avgM)
					* (simulated.get(me.getKey()).values().iterator().next() - avgS));
		
		for (DoubleSample mv : observed.values())
			denom1 += ((mv.values().iterator().next() - avgM)
					* (mv.values().iterator().next() - avgM));
		for (DoubleSample sv : simulated.values())
			denom2 += ((sv.values().iterator().next() - avgS)
					* (sv.values().iterator().next() - avgS));
		
		rSquaredQuality = (num * num) / (denom1 * denom2);
		
		num = 0;
		
		for (Map.Entry<Double, DoubleSample> me : observed.entrySet()) {
			double tmp = (me.getValue().values().iterator().next()
					- simulated.get(me.getKey()).values().iterator().next());
			num += tmp * tmp;
		}
		
		nsQuality = 1 - (num / denom1);
	}
	
	public String toString() {
		StringBuilder rv = new StringBuilder();
		rv.append(String.format("%5d", getIteration()) + " ");
		rv.append(String.format("%17.11g", getNS()) + " ");
		rv.append(String.format("%17.11g", getRSquared()) + " ");
		
		for (DoubleSample k : simulated.values()) {
			rv.append(String.format("%17.11g", k.values().iterator().next()));
			rv.append(" ");
		}
		return rv.toString();
	}
	
	public String shortInfo() {
		StringBuilder rv = new StringBuilder();
		rv.append("R^2 = " + String.format("%3.4g", rSquaredQuality) + "; ");
		rv.append("NS  = " + String.format("%3.4g", nsQuality));
		return rv.toString();
	}
	
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}
	public int getIteration() {
		return iteration;
	}

	public TimeSeries getObserved() {
		return observed;
	}

	public void setObserved(TimeSeries observed) {
		this.observed = observed;
	}
}
