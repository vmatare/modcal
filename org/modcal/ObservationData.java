package org.modcal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;


public class ObservationData implements Serializable {

	private static final long serialVersionUID = -5017969980532748068L;
	private NavigableMap<Double, Double> data;
	private String varName;
	private final File obsFile;
	
	public ObservationData(String path) {
		obsFile = new File(path);
	}
	
	public void init() throws IOException {
		BufferedReader fr = new BufferedReader(new FileReader(obsFile));
		String line = fr.readLine();
		String[] tokens = line.split("\\s+");
		data = new ConcurrentSkipListMap<Double, Double>();
		
		if (tokens.length != 2) throw new BrokenDataException(
				obsFile.getAbsolutePath() + ": \"" + line + "\": Invalid column header.");
		varName = tokens[1];
		
		while ((line = fr.readLine()) != null) {
			tokens = line.split("\\s+");
			if (tokens.length == 2 && NumericString.isValid(tokens[0])
					&& NumericString.isValid(tokens[1]))
				data.put(Double.parseDouble(tokens[0]), Double.parseDouble(tokens[1]));
			else
				throw new BrokenDataException(obsFile.getAbsolutePath() +
						": \"" + line + "\": Invalid data row.");
		}
	}
	
	public String getVarName() { return varName; }
	public NavigableMap<Double, Double> getData() { return data; }

}
