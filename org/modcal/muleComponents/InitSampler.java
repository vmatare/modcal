package org.modcal.muleComponents;

import org.modcal.CalibrationRequest;

public class InitSampler {
	
	public Object call(CalibrationRequest rq) {
		try {
			rq.getSampler().init();
		} catch (Exception e) {
			return e;
		}
		return rq;
	}

}
