package org.modcal.data;

import java.io.Serializable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DoubleSample  implements ParameterSample<Double>, Serializable {
	private static final long serialVersionUID = -2826209667847347312L;
	private HashMap<String, Double> theMap;
	
	public DoubleSample() {
		theMap = new HashMap<String, Double>();
	}
	
	public Collection<String> getParamNames() {
		return this.keySet();
	}
	
	public HashMap<String, Double> getTheMap() { return theMap; }
	public void setTheMap(HashMap<String, Double> map) { theMap = map; }
	
	public Double valueOf(String paramName) {
		return this.get(paramName);
	}

	public int size() {
		return theMap.size();
	}

	public boolean isEmpty() {
		return theMap.isEmpty();
	}

	public Double get(Object key) {
		return theMap.get(key);
	}

	public boolean equals(Object o) {
		return theMap.equals(o);
	}

	public boolean containsKey(Object key) {
		return theMap.containsKey(key);
	}

	public Double put(String key, Double value) {
		return theMap.put(key, value);
	}

	public int hashCode() {
		return theMap.hashCode();
	}

	public String toString() {
		return theMap.toString();
	}

	public void putAll(Map<? extends String, ? extends Double> m) {
		theMap.putAll(m);
	}

	public Double remove(Object key) {
		return theMap.remove(key);
	}

	public void clear() {
		theMap.clear();
	}

	public boolean containsValue(Object value) {
		return theMap.containsValue(value);
	}

	public Object clone() {
		return theMap.clone();
	}

	public Set<String> keySet() {
		return theMap.keySet();
	}

	public Collection<Double> values() {
		return theMap.values();
	}

	public Set<java.util.Map.Entry<String, Double>> entrySet() {
		return theMap.entrySet();
	}
	
	
}
