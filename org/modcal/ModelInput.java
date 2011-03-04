package org.modcal;

import java.io.IOException;


public interface ModelInput {
	
	public abstract void useSample(ParameterSample<?> data) throws IOException;

}
