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
