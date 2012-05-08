package org.modcal.output;

import org.modcal.data.ObservationData;
import org.mule.api.MuleEventContext;

public class MapObservation implements org.mule.api.lifecycle.Callable {

	public ModelOutput onCall(MuleEventContext eventContext) throws Exception {
		Object o = eventContext.getMuleContext().getRegistry().lookupObject("ObservationData");
		if (!(o instanceof ObservationData)) throw new RuntimeException(
				"No ObservationData registered!");
		ObservationData observation = (ObservationData)o;
		
		o = eventContext.getMessage().getPayload();
		if (o instanceof Exception) throw ((Exception)o);
		if (!(o instanceof ModelOutput)) throw new RuntimeException(
				"Model returned an object of unknown type: " + o.getClass() + ".");
		ModelOutput output = ((ModelOutput)o);
		output.mapObservation(observation);

		return output;
	}

}
