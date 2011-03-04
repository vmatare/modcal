package org.modcal.muleComponents;

import org.modcal.CalibrationRequest;
import org.modcal.CalibrationResult;
import org.modcal.IncompleteModelOutput;
import org.modcal.IncompleteOutputException;

public class CollectResult {

	public Object call(CalibrationRequest rq) {
		try {
			if (!rq.isFinished()) {
				rq.getObserved().init();
				rq.getCurrentOutput().setTargetParam(rq.getObserved().getVarName());
				rq.getCurrentOutput().init();
				try { rq.getCurrentOutput().intersect(rq.getObserved().getData()); }
				catch (IncompleteOutputException e) {
					rq.getResult().set(rq.getResult().size()-1, new IncompleteModelOutput());
					rq.getCurrentOutput().setIteration(rq.getIteration());
				}
				rq.incIteration();
			}
			else return new CalibrationResult(rq);
		} catch (Exception e) {
			return e;
		}
		return rq;
	}
	
	public Object call(Exception e) { return e; }

}
