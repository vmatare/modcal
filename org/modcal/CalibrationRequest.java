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

import java.io.Serializable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class CalibrationRequest implements Serializable {

	private static final long serialVersionUID = -2597598653633316302L;
	private Sampler<?> sampler;
	private Model<? extends ModelInput> model;
	private List<ModelOutput> result;
	private int iteration;
	private ObservationData observed;
	private boolean finished = false;
	
	public CalibrationRequest(Sampler<?> sampler, Model<? extends ModelInput> model, String observedPath) {
		this.sampler = sampler;
		this.model = model;
		iteration = 0;
		result = Collections.synchronizedList(new LinkedList<ModelOutput>());
		observed = new ObservationData(observedPath);
	}
	
	public void incIteration() { iteration++; }
	public int getIteration() { return iteration; }
	
	public Sampler<?> getSampler() { return sampler; }
	public Model<? extends ModelInput> getModel() { return model; }
	
	public List<ModelOutput> getResult() { return result; }
	
	public ModelOutput getCurrentOutput() {
		return result.get(result.size()-1);
	}
	
	public void addResult(ModelOutput r) {
		result.add(r);
	}
	
	public boolean isFinished() { return finished; }
	public void setFinished() { finished = true; }

	public ObservationData getObserved() { return observed; }

}
