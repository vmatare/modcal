package org.modcal.data;

import java.io.Serializable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class TimeSeries implements NavigableMap<Double, DoubleSample>, Serializable {

	private static final long serialVersionUID = -331198627078938808L;
	private ConcurrentSkipListMap<Double, DoubleSample> theMap;
	
	public TimeSeries() { 
		theMap = new ConcurrentSkipListMap<Double, DoubleSample>();
	}
	
	public ConcurrentSkipListMap<Double, DoubleSample> getTheMap() {
		return theMap;
	}
	
	public void setTheMap(ConcurrentSkipListMap<Double, DoubleSample> map) {
		theMap = map;
	}
	
	public DoubleSample getSample(Double time) {
		return this.get(time);
	}

	public void putAll(Map<? extends Double, ? extends DoubleSample> m) {
		theMap.putAll(m);
	}


	public int hashCode() {
		return theMap.hashCode();
	}


	public String toString() {
		return theMap.toString();
	}


	public ConcurrentSkipListMap<Double, DoubleSample> clone() {
		return theMap.clone();
	}


	public boolean containsKey(Object key) {
		return theMap.containsKey(key);
	}


	public DoubleSample get(Object key) {
		return theMap.get(key);
	}


	public DoubleSample put(Double key, DoubleSample value) {
		return theMap.put(key, value);
	}


	public DoubleSample remove(Object key) {
		return theMap.remove(key);
	}


	public boolean containsValue(Object value) {
		return theMap.containsValue(value);
	}


	public int size() {
		return theMap.size();
	}


	public boolean isEmpty() {
		return theMap.isEmpty();
	}


	public void clear() {
		theMap.clear();
	}


	public NavigableSet<Double> keySet() {
		return theMap.keySet();
	}


	public NavigableSet<Double> navigableKeySet() {
		return theMap.navigableKeySet();
	}


	public Collection<DoubleSample> values() {
		return theMap.values();
	}


	public Set<java.util.Map.Entry<Double, DoubleSample>> entrySet() {
		return theMap.entrySet();
	}


	public ConcurrentNavigableMap<Double, DoubleSample> descendingMap() {
		return theMap.descendingMap();
	}


	public NavigableSet<Double> descendingKeySet() {
		return theMap.descendingKeySet();
	}


	public boolean equals(Object o) {
		return theMap.equals(o);
	}


	public DoubleSample putIfAbsent(Double key, DoubleSample value) {
		return theMap.putIfAbsent(key, value);
	}


	public boolean remove(Object key, Object value) {
		return theMap.remove(key, value);
	}


	public boolean replace(Double key, DoubleSample oldValue,
			DoubleSample newValue) {
		return theMap.replace(key, oldValue, newValue);
	}


	public DoubleSample replace(Double key, DoubleSample value) {
		return theMap.replace(key, value);
	}


	public Comparator<? super Double> comparator() {
		return theMap.comparator();
	}


	public Double firstKey() {
		return theMap.firstKey();
	}


	public Double lastKey() {
		return theMap.lastKey();
	}


	public ConcurrentNavigableMap<Double, DoubleSample> subMap(Double fromKey,
			boolean fromInclusive, Double toKey, boolean toInclusive) {
		return theMap.subMap(fromKey, fromInclusive, toKey, toInclusive);
	}


	public ConcurrentNavigableMap<Double, DoubleSample> headMap(Double toKey,
			boolean inclusive) {
		return theMap.headMap(toKey, inclusive);
	}


	public ConcurrentNavigableMap<Double, DoubleSample> tailMap(Double fromKey,
			boolean inclusive) {
		return theMap.tailMap(fromKey, inclusive);
	}


	public ConcurrentNavigableMap<Double, DoubleSample> subMap(Double fromKey,
			Double toKey) {
		return theMap.subMap(fromKey, toKey);
	}


	public ConcurrentNavigableMap<Double, DoubleSample> headMap(Double toKey) {
		return theMap.headMap(toKey);
	}


	public ConcurrentNavigableMap<Double, DoubleSample> tailMap(Double fromKey) {
		return theMap.tailMap(fromKey);
	}


	public java.util.Map.Entry<Double, DoubleSample> lowerEntry(Double key) {
		return theMap.lowerEntry(key);
	}


	public Double lowerKey(Double key) {
		return theMap.lowerKey(key);
	}


	public java.util.Map.Entry<Double, DoubleSample> floorEntry(Double key) {
		return theMap.floorEntry(key);
	}


	public Double floorKey(Double key) {
		return theMap.floorKey(key);
	}


	public java.util.Map.Entry<Double, DoubleSample> ceilingEntry(Double key) {
		return theMap.ceilingEntry(key);
	}


	public Double ceilingKey(Double key) {
		return theMap.ceilingKey(key);
	}


	public java.util.Map.Entry<Double, DoubleSample> higherEntry(Double key) {
		return theMap.higherEntry(key);
	}


	public Double higherKey(Double key) {
		return theMap.higherKey(key);
	}


	public java.util.Map.Entry<Double, DoubleSample> firstEntry() {
		return theMap.firstEntry();
	}


	public java.util.Map.Entry<Double, DoubleSample> lastEntry() {
		return theMap.lastEntry();
	}


	public java.util.Map.Entry<Double, DoubleSample> pollFirstEntry() {
		return theMap.pollFirstEntry();
	}


	public java.util.Map.Entry<Double, DoubleSample> pollLastEntry() {
		return theMap.pollLastEntry();
	}

	
}
