package org.modcal.model;

import java.util.List;

import javax.jws.WebService;

import org.modcal.data.NumericSample;
import org.modcal.data.ObservationData;
import org.modcal.output.EmptyModelOutput;
import org.modcal.output.IncompleteOutputException;
import org.modcal.output.ModelOutput;
import org.modcal.output.ModelOutputWrapper;
import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;

@WebService(endpointInterface = "org.modcal.model.Hydrus1D",
		serviceName = "Hydrus1D")

public class RunHydrus1DWeb implements Hydrus1D, MuleContextAware {
	MuleContext muleContext;
	
	public void setMuleContext(MuleContext context) {
		this.muleContext = context;		
	}

	public ModelOutputWrapper runModel(List<String> paramNames,
			List<String> paramValues) throws Exception {
		Object o = muleContext.getRegistry().get("Hydrus1DController");
		if (!(o instanceof Hydrus1DController)) throw new RuntimeException("No Hydrus1D controller registered!");
		Hydrus1DController ctrl = ((Hydrus1DController)o);
		ModelOutput out = ctrl.runModel(new NumericSample(paramNames, paramValues));
		o = muleContext.getRegistry().lookupObject("ObservationData");
		if (!(o instanceof ObservationData)) throw new RuntimeException(
				"No ObservationData registered!");
		ObservationData observation = (ObservationData)o;
		if (out instanceof EmptyModelOutput) throw new IncompleteOutputException();
		out.mapObservation(observation);
		//return out;
		return new ModelOutputWrapper(out);
	}
}
