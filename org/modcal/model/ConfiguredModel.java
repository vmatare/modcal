package org.modcal.model;

import org.modcal.output.ModelOutput;

public interface ConfiguredModel<SampleT> {
	public ModelOutput runModel(SampleT s) throws Exception;
}
