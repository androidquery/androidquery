package com.androidquery.util;

import java.io.ByteArrayOutputStream;

/**
 * AQuery internal use only.
 * 
 * Return the buffered array as is if the predefined size matches exactly the result byte array length.
 * Reduce memory allocation by half by avoiding array expand and copy.
 * 
 */

public class PredefinedBAOS extends ByteArrayOutputStream{

	public PredefinedBAOS(int size){
		super(size);
	}
	
	@Override
	public byte[] toByteArray(){
		
		if(count == buf.length){
			return buf;
		}
		
		return super.toByteArray();
	
	}
	
}
