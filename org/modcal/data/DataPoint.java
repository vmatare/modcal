package org.modcal.data;

import java.util.Map;

public class DataPoint {
	
	private Double time;
	private DoubleSample params;
	
	public DataPoint() {}
	
	public DataPoint(Map.Entry<Double, DoubleSample> e) {
		setTime(e.getKey());
		setParams(e.getValue());
	}

	public Double getTime() {
		return time;
	}

	public void setTime(Double time) {
		this.time = time;
	}

	public DoubleSample getParams() {
		return params;
	}

	public void setParams(DoubleSample params) {
		this.params = params;
	}
	
	

}
