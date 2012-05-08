package org.modcal.output;

import java.io.Serializable;

import java.util.Map;

import org.modcal.data.DoubleSample;
import org.modcal.data.TimeSeries;

public class ModelOutputWrapper implements Serializable {

	private static final long serialVersionUID = -1994384848968878098L;
	private String[] paramNames;
	private Double[][] simulated;
	private Double[][] observed;
	
	public ModelOutputWrapper() {}
	
	public ModelOutputWrapper(ModelOutput o) {
		this();
		
		paramNames = new String[o.getSimulated().firstEntry().getValue().keySet().size() + 1];
		paramNames[0] = "Time";
		int i = 1;
		for (String pn : o.getSimulated().firstEntry().getValue().keySet())
			paramNames[i++] = pn;
		
		simulated = renderData(o.getSimulated());
		observed = renderData(o.getObserved());
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
	
	public Double[][] getSimulated() {
		return simulated;
	}

	public void setSimulated(Double[][] simulated) {
		this.simulated = simulated;
	}

	public Double[][] renderData(TimeSeries o) {
		if (o == null) return null;
		Double[][] rv = new Double[o.entrySet().size()][paramNames.length];
		
		int i=0, j=0;
		
		for (Map.Entry<Double, DoubleSample> e : o.entrySet()) {
			
			j = 0;
			rv[i][j++] = e.getKey();
			for (; j < paramNames.length; j++)
				rv[i][j] = e.getValue().get(paramNames[j]);
			i++;
		}
		return rv;
	}

	public Double[][] getObserved() {
		return observed;
	}

	public void setObserved(Double[][] observed) {
		this.observed = observed;
	}
}
