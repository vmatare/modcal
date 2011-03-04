package org.modcal;

import java.io.IOException;

import java.util.NavigableMap;

public interface ModelOutput {

	public abstract int compareRSquared(ModelOutput o);

	public abstract int compareNS(ModelOutput o);

	public abstract NavigableMap<Double, Double> getOutput();

	public abstract void init() throws IOException;

	public abstract Double getRSquared();

	public abstract Double getNS();

	public abstract void intersect(NavigableMap<Double, Double> measured);

	public abstract void setTargetParam(String targetParam);

	public abstract String getTargetParam();

	public abstract String toString();

	public abstract String shortInfo();

	public abstract void setIteration(int iteration);

	public abstract int getIteration();

}