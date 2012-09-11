/*
 * Copyright 2011 - AndroidQuery.com (tinyeeliu@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

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
