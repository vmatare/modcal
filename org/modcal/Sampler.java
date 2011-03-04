package org.modcal;

import java.io.IOException;
import java.io.Serializable;


/**
 * Generates consecutive parameter samples, each for a particular simulation run.
 * @author Victor Mataré
 *
 */
public abstract class Sampler<SampleT extends ParameterSample<?>> implements Serializable {

	private static final long serialVersionUID = -7235490018581863170L;
	
	/**
	 * This is called on each simulation run and the result used as an input for the
	 * {@link Model}.
	 * @return The next {@link ParameterSample}. Return null if there are no more samples
	 * (calibration ends).
	 * @throws IOException
	 */
	public abstract SampleT nextSample() throws IOException;
	
	
	/**
	 * This method should initialize any external resources. Don't do this
	 * in the constructor to allow the object to reach its destination before
	 * reading external data (like opening files).
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public abstract void init() throws IOException, InterruptedException;
}
