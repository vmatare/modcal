package org.modcal;

import java.io.IOException;
import java.io.Serializable;

import java.util.NavigableMap;



public class EmptyModelOutput implements ModelOutput, Serializable {
	
	int iteration;
	String target;

	private static final long serialVersionUID = -1469199341173045753L;
	public void init() throws IOException {
	}
	
	public String toString() {
		return getIteration() + ": no result (timeout reached)";
	}
	public String shortInfo() {
		return "Iteration #" + toString();
	}
	
	@Override
	public int compareRSquared(ModelOutput o) {
		return Integer.MIN_VALUE;
	}
	
	public int compareNS(ModelOutput o) {
		return Integer.MIN_VALUE;
	}
	
	public void intersect(NavigableMap<Double, Double> measured) {}

	public NavigableMap<Double, Double> getOutput() {
		return null;
	}

	public Double getRSquared() {
		return Double.MIN_VALUE;
	}

	public Double getNS() {
		return Double.MIN_VALUE;
	}

	public void setTargetParam(String targetParam) {
		this.target = targetParam;
	}

	public String getTargetParam() {
		return target;
	}

	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

	public int getIteration() {
		return iteration;
	}
	
}
