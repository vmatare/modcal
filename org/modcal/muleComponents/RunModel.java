package org.modcal.muleComponents;

import org.modcal.CalibrationRequest;
import org.modcal.ParameterSample;

public class RunModel {

	public Object run(CalibrationRequest rq) {
		ParameterSample<?> sample;
		try {
			if ((sample = rq.getSampler().nextSample()) == null) {
				rq.setFinished();
				return rq;
			}
			rq.getModel().getInput().useSample(sample);
			rq.addResult(rq.getModel().call());
			rq.getCurrentOutput().setIteration(rq.getIteration());
		} catch (Exception e) {
			return e;
		}
		return rq;
	}

	public Object call(Exception e) { return e; }

}
