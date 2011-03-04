package org.modcal.muleComponents;

import org.modcal.CalibrationRequest;

public class InitModel {
	
	public Object call(CalibrationRequest rq) {
		try {
			rq.getModel().init();
		} catch (Exception e) {
			return e;
		}
		return rq;
	}
	
	public Object call(Exception e) { return e; }

}
