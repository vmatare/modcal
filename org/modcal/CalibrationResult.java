package org.modcal;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * This class represents the ultimate result of a calibration - 
 * @author ich
 *
 */
public class CalibrationResult {
	
	SortedSet<ModelOutput> nsResult;
	SortedSet<ModelOutput> rSquaredResult;
	List<Double> observed;
	List<Double> time;
	private final static String header =    " i    NS               R^2              ";
	private final static String obsHeader = "                     Observed Values:  ";
	
	public CalibrationResult(CalibrationRequest rq) {
		nsResult = new ConcurrentSkipListSet<ModelOutput>(new AbstractModelOutput.CompareNS());
		nsResult.addAll(rq.getResult());
		rSquaredResult = new ConcurrentSkipListSet<ModelOutput>(new AbstractModelOutput.CompareRSquared());
		rSquaredResult.addAll(rq.getResult());
		observed = new LinkedList<Double>(rq.getObserved().getData().values());
		time = new LinkedList<Double>(rq.getObserved().getData().keySet());
	}

	public String toString() {
		StringBuilder rv = new StringBuilder();
		
		rv.append(header);
		for (Double d : time)
			rv.append("t=" + NumericString.pad(d, 17).substring(1));
		rv.append("\r\n");
		
		rv.append(obsHeader);
		for (Double d : observed)
			rv.append(NumericString.pad(d, 17) + " ");
		rv.append("\r\n");
		
		for (ModelOutput r : nsResult)
			rv.append(r + "\r\n");
		rv.append("\r\n");
		
		rv.append(header);
		for (Double d : time)
			rv.append("t=" + NumericString.pad(d, 17).substring(1));
		rv.append("\r\n");
		
		rv.append(obsHeader);
		for (Double d : observed)
			rv.append(NumericString.pad(d, 17) + " ");
		rv.append("\r\n");
		
		for (ModelOutput r : rSquaredResult)
			rv.append(r + "\r\n");
		rv.append("\r\n");
		
		return rv.toString();
	}
}
