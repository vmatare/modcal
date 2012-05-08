package org.modcal.output;

import java.io.Serializable;

import java.util.Map;
import java.util.Set;

import org.modcal.data.DoubleSample;
import org.modcal.data.ObservationData;

public class ModelOutputWrapper2 implements Serializable {

	private static final long serialVersionUID = -1994384848968878098L;
	private String[] paramNames;
	private Double[][] output;
	private Double[][] observed;

	public ModelOutputWrapper2() {}
	
	public ModelOutputWrapper2(ModelOutput o, ObservationData observed) {
		this();
		
		Set<String> tmpNames = o.getSimulated().firstEntry().getValue().keySet();
		int numParams = tmpNames.size() + 1;
		paramNames = new String[numParams];
		paramNames[0] = "Time";
		
		int i = 1;
		for (String s : o.getSimulated().firstEntry().getValue().keySet())
			paramNames[i++] = s;		
		
		output = new Double[o.getSimulated().entrySet().size()][numParams];
		
		i = 0;
		int j;
		for (Map.Entry<Double, DoubleSample> e : o.getSimulated().entrySet()) {
			
			j = 0;
			output[i][j++] = e.getKey();
			for (; j < numParams; j++)
				output[i][j] = e.getValue().get(paramNames[j]);
			i++;
		}
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

	public Double[][] getOutput() {
		return output;
	}

	public void setOutput(Double[][] data) {
		this.output = data;
	}

	public Double[][] getObserved() {
		return observed;
	}

	public void setObserved(Double[][] observed) {
		this.observed = observed;
	}
}
