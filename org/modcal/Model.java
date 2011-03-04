package org.modcal;

import java.io.IOException;
import java.io.Serializable;

import java.util.concurrent.Callable;


public abstract class Model<InT extends ModelInput> implements Serializable, Callable<ModelOutput> {
	
	private static final long serialVersionUID = 6285825878289849130L;
	private InT input;
	
	public Model() {}
	
	public void setInput(InT data) {
		this.input = data;
	}
	public InT getInput() { return input; }
	public abstract void init() throws Exception;
	
	public abstract ModelOutput call() throws IOException, InterruptedException;
}
