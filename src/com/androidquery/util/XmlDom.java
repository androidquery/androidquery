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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Xml;


/**
 * Specialized class for simple and easy XML parsing. Designed to be used in basic Android api 4+
 * runtime without any dependency. There's no support to modify the dom object and serialization.
 * 
 * The toString method return a string that represents the original xml content.
 * 
 */

public class XmlDom {

	private Element root;
	private byte[] data;
	
	/**
	 * Instantiates a new xml dom.
	 */
	public XmlDom(){
	
	}
	
	/**
	 * Gets the element that this node represent.
	 *
	 * @return the element
	 */
	public Element getElement(){
		return root;
	}
	
	
	/**
	 * Check if this node exists.
	 *
	 * @return exist
	 */
	public boolean exist(){
		return root != null;
	}
	
	/**
	 * Instantiates a new xml dom.
	 *
	 * @param element the element
	 */
	public XmlDom(Element element){
		this.root = element;
	}
	
	/**
	 * Instantiates a new xml dom.
	 *
	 * @param str Raw XML
	 * @throws SAXException the sAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public XmlDom(String str) throws SAXException, IOException{
		this(str.getBytes());
	}
	
	/**
	 * Instantiates a new xml dom.
	 *
	 * @param data Raw XML
	 * @throws SAXException the sAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public XmlDom(byte[] data) throws SAXException, IOException{
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(data));			
			this.root = (Element) doc.getDocumentElement();
			this.data = data;
		} catch (ParserConfigurationException e) {			
		}
	
	}
	
	/**
	 * Return a node that represents the first matched tag.
	 *
	 * @param tag tag name
	 * @return the xml dom
	 */
	public XmlDom tag(String tag){
		
		if(root == null) return this;
		
		NodeList nl = root.getElementsByTagName(tag);
		
		XmlDom result = null;
		
		if(nl != null && nl.getLength() > 0){
			result = new XmlDom((Element) nl.item(0));
		}
		
		if(result == null){
			result = new XmlDom();
		}
		
		return result;
	}
	
	/**
	 * Return a list of nodes that represents the matched tags.
	 *
	 * @param tag tag name
	 * @return the list of xml dom
	 */
	public List<XmlDom> tags(String tag){
		
		if(root == null) return Collections.emptyList();
		
		NodeList nl = root.getElementsByTagName(tag);
		
		return convert(nl);
	}
	
	private static List<XmlDom> convert(NodeList nl){
		
		List<XmlDom> result = new ArrayList<XmlDom>();
		
		for(int i = 0; i < nl.getLength(); i++){
			result.add(new XmlDom((Element) nl.item(i)));;
		}
		
		return result;
	}
	
	/**
	 * Return the text content of the first matched tag.
	 *
	 * @param tag the tag
	 * @return text
	 */
	public String text(String tag){
		
		if(root == null) return null;
		
		XmlDom dom = tag(tag);
		if(dom == null) return null;
		return dom.text();
	}
	
	
	/**
	 * Return the text content of the current node.
	 *
	 * @return text
	 */
	public String text(){
		
		if(root == null) return null;
		
		NodeList list = root.getChildNodes();
		if(list.getLength() == 1) return list.item(0).getNodeValue();
		
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < list.getLength(); i++){
			String frag = list.item(i).getNodeValue();
			sb.append(frag);
		}
		
		return sb.toString();
	}
	
	/**
	 * Return the value of the attribute of current node.
	 *
	 * @param name attribute name
	 * @return value
	 */
	public String attr(String name){
		
		String result = root.getAttribute(name);
		return result;
	}
	
	/**
	 * Return the raw xml if current node is root of document. Otherwise return default toString of node object.
	 *
	 * @return raw xml
	 */
	public String toString(){
		
		if(data != null){
			return new String(data);
		}
		
		return super.toString();
	}
	
}
