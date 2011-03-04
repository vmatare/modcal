package org.modcal;

public class IncompleteModelOutput extends EmptyModelOutput {

	private static final long serialVersionUID = 5308279560995180638L;
	
	public String toString() {
		return getIteration() + ": Incomplete simulation - it doesn't cover the" +
				" observed time frame. Using bad input data?";
	}
}
