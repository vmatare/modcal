package org.modcal.model;

import org.modcal.data.NumericSample;
import org.modcal.data.ObservationData;
import org.modcal.output.ModelOutput;
import org.mule.api.MuleContext;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

public class RunHydrus1D implements Callable {

	public Object onCall(MuleEventContext eventContext) {
		MuleContext muleContext = eventContext.getMuleContext();
		try {
			Object o = muleContext.getRegistry().lookupObject("Hydrus1DController");
			if (!(o instanceof Hydrus1DController)) throw new RuntimeException(
					"No Hydrus1DController registered!");
			Hydrus1DController ctrl = (Hydrus1DController)o;
			ModelOutput rv = ctrl.runModel(eventContext.getMessage().getPayload(
					(new NumericSample()).getClass()));
			o = muleContext.getRegistry().lookupObject("ObservationData");
			if (!(o instanceof ObservationData)) throw new RuntimeException(
					"No ObservationData registered!");
			ObservationData observation = (ObservationData)o;
			rv.mapObservation(observation);
			if (observation.getData().firstEntry().getValue().size() == 1)
				rv.calcQuality();
			return rv;
		}
		catch (Exception e) {
			e.printStackTrace();
			return e;
		}
	}
}
