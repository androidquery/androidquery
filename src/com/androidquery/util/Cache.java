package com.androidquery.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class Cache<K, V> extends LinkedHashMap<K, V>{

	private static final long serialVersionUID = 1L;
	
	private int max;
	
	public Cache(int max){
		
		super(max, 0.75F, true);
		this.max = max;
		
	}
	
	@Override
	public boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        
		return size() > max;
    }
	
}
