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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
	 * Gets the element that this node represent.
	 *
	 * @return the element
	 * 
	 * @see testGetElement
	 */
	public Element getElement(){
		return root;
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
	 * @throws SAXException the SAX exception
	 */
	public XmlDom(String str) throws SAXException{
		this(str.getBytes());
	}
	
	/**
	 * Instantiates a new xml dom.
	 *
	 * @param data Raw XML
	 * @throws SAXException the SAX exception
	 */
	public XmlDom(byte[] data) throws SAXException{
		
		this(new ByteArrayInputStream(data));		
		this.data = data;
		
	}
	
	/**
	 * Instantiates a new xml dom.
	 *
	 * @param is Raw XML.
	 * @throws SAXException the SAX exception
	 */
	public XmlDom(InputStream is) throws SAXException{
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(is);			
			this.root = (Element) doc.getDocumentElement();
		}catch(ParserConfigurationException e) {			
		}catch(IOException e){
			throw new SAXException(e);
		}
	
	}
	
	/**
	 * Return a node that represents the first matched tag.
	 *
	 * A dummy node is returned if none found.
	 *
	 * @param tag tag name
	 * @return the xml dom
	 * 
	 * @see testTag
	 */
	public XmlDom tag(String tag){
		
		if(root == null) return null;
		
		NodeList nl = root.getElementsByTagName(tag);
		
		XmlDom result = null;
		
		if(nl != null && nl.getLength() > 0){
			result = new XmlDom((Element) nl.item(0));
		}
		
		return result;
	}
	
	/**
	 * Return a node that represents the first matched tag.
	 *
	 * If value == null, node that has the attr are considered a match.
	 *
	 * A dummy node is returned if none found.
	 *
	 * @param tag tag name
	 * @param attr attr name to match
	 * @param value attr value to match
	 * @return the xml dom
	 * 
	 * @see testTag2
	 */
	public XmlDom tag(String tag, String attr, String value){
		
		List<XmlDom> tags = tags(tag, attr, value);
		
		if(tags.size() == 0){
			return null;
		}else{
			return tags.get(0);
		}
		
	}	
	
	/**
	 * Return a list of nodes that represents the matched tags.
	 *
	 * @param tag tag name
	 * @return the list of xml dom
	 * 
	 * @see testTags
	 */
	public List<XmlDom> tags(String tag){		
		return tags(tag, null, null);
	}
	
	/**
	 * Return the first child node that represent the matched tag.
	 * A dummy node is returned if none found.
	 *
	 * @param tag tag name
	 * @return the list of xml dom
	 * 
	 * @see testChild
	 */
	public XmlDom child(String tag){
		return child(tag, null, null);
	}
	
	/**
	 * Return the first child node that represent the matched tag.
	 * A dummy node is returned if none found.
	 *
	 * @param tag tag name
	 * @param attr attr name to match
	 * @param value attr value to match
	 * @return the list of xml dom
	 * 
	 * @see testChild2
	 */
	
	public XmlDom child(String tag, String attr, String value){
		List<XmlDom> c = children(tag, attr, value);
		if(c.size() == 0) return null;
		return c.get(0);
	}
	
	
	/**
	 * Return a list of child nodes that represents the matched tags.
	 *
	 * @param tag tag name
	 * @return the list of xml dom
	 * 
	 * @see testChildren
	 */
	public List<XmlDom> children(String tag){
		return children(tag, null, null);
	}
	
	/**
	 * Return a list of child nodes that represents the matched tags.
	 *
	 * @param tag tag name
	 * @param attr attr name to match
	 * @param value attr value to match
	 * @return the list of xml dom
	 * 
	 * @see testChildren2
	 */
	public List<XmlDom> children(String tag, String attr, String value){
		
		if(root == null) return Collections.emptyList();					
		return convert(root.getChildNodes(), tag, attr, value);
	
	}
	
	
	/**
	 * Return a list of nodes that represents the matched tags that has attribute attr=value.
	 * If attr == null, any tag with input name match.
	 * If value == null, any nodes that has the attr are considered a match.
	 *
	 * @param tag tag name
	 * @param attr attr name to match
	 * @param value attr value to match
	 * @return the list of xml dom
	 * 
	 * @see testTags2
	 */
	public List<XmlDom> tags(String tag, String attr, String value){
		
		if(root == null) return Collections.emptyList();
		
		NodeList nl = root.getElementsByTagName(tag);		
		return convert(nl, null, attr, value);
	}
	
	//convert to list and filter to nodes that has attr=value
	private static List<XmlDom> convert(NodeList nl, String tag, String attr, String value){
		
		List<XmlDom> result = new ArrayList<XmlDom>();
		
		for(int i = 0; i < nl.getLength(); i++){			
			XmlDom xml = convert(nl.item(i), tag, attr, value);
			if(xml != null) result.add(xml);
		}
		
		return result;
	}
	
	private static XmlDom convert(Node node, String tag, String attr, String value){
		
		if(node.getNodeType() != Node.ELEMENT_NODE){
			return null;
		}
		
		Element e = (Element) node;
		
		XmlDom result = null;
		
		if(tag == null || tag.equals(e.getTagName())){		
			if(attr == null || e.hasAttribute(attr)){			
				if(value == null || value.equals(e.getAttribute(attr))){
					result = new XmlDom(e);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Return the text content of the first matched tag.
	 * Short cut for "xml.tag(tag).text()"
	 *
	 * @param tag the tag
	 * @return text
	 * 
	 * @see testText2
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
	 * 
	 * @see testText
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
	 * 
	 * @see testAttr
	 */
	public String attr(String name){
		
		String result = root.getAttribute(name);
		return result;
	}
	
	/**
	 * Return the raw xml if current node is root of document. Otherwise return default toString of node object.
	 *
	 * @return raw xml
	 * 
	 * @see testToString
	 */
	public String toString(){
		
		if(data != null){
			return new String(data);
		}
		
		return super.toString();
	}
	
}
