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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;


/**
 * Specialized class for simple and easy XML parsing. Designed to be used in basic Android API 4+
 * runtime without any dependency. There's no support to modify the dom object.
 * 
 * The toString method return a string that represents the xml content.
 * WARNING: The toString methods are for debugging only and do not guarantee a proper XML transformation.
 * 
 */

public class XmlDom {

	private Element root;
	
	
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
	 * Return the first child node that represent the matched tag that has attribute attr=value.
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
							
		return convert(root.getChildNodes(), tag, attr, value);
	
	}
	
	
	/**
	 * Return a list of nodes that represents the matched tags that has attribute attr=value.
	 * If attr == null, any tag with specified name matches.
	 * If value == null, any nodes that has the attr matches.
	 *
	 * @param tag tag name
	 * @param attr attr name to match
	 * @param value attr value to match
	 * @return the list of xml dom
	 * 
	 * @see testTags2
	 */
	public List<XmlDom> tags(String tag, String attr, String value){
		
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
	 * Short cut for "xml.child(tag).text()"
	 *
	 * Return null if there's no matched tag.
	 * 
	 * @param tag tag name
	 * @return text
	 * 
	 * @see testText2
	 */
	public String text(String tag){
			
		XmlDom dom = child(tag);
		if(dom == null) return null;
		return dom.text();
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
	 * Return the raw xml of this node.
	 *
	 * WARNING: This method is for debugging only. Does not guarantee a proper XML transformation.
	 *
	 * @return raw xml
	 * 
	 * @see testToString
	 */
	public String toString(){
		return toString(0);
	}
	
	/**
	 * Return the raw xml of this node.
	 *
	 * WARNING: This method is for debugging only. Does not guarantee a proper XML transformation.
	 *
	 * @param intentSpaces number of white spaces to intent
	 * @return raw xml
	 * 
	 * @see testToString2
	 */
	
	public String toString(int intentSpaces){		
		return serialize(root, intentSpaces);
	}
	
	private String serialize(Element e, int intent){
		
		try{
		
			XmlSerializer s = Xml.newSerializer();
			StringWriter sw = new StringWriter();
		
			s.setOutput(sw);
			s.startDocument("utf-8", null);
			
			String spaces = null;
			if(intent > 0){
				char[] chars = new char[intent];
				Arrays.fill(chars, ' ');
				spaces = new String(chars);
			}
			
			serialize(root, s, 0, spaces);
			s.endDocument();
			
			return sw.toString();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		return null;
	}
	
	private void writeSpace(XmlSerializer s, int depth, String spaces) throws Exception{
		
		if(spaces != null){			
			s.text("\n");			
			for(int i = 0; i < depth; i++){
				s.text(spaces);
			}
		}
	}
	
	/**
	 * Return the text content of the current node. Returns empty string if there's no text or cdata child elements.
	 *
	 * @return text
	 * 
	 * @see testText
	 */
	public String text(){
		
		NodeList list = root.getChildNodes();
		if(list.getLength() == 1) return list.item(0).getNodeValue();
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < list.getLength(); i++){
			sb.append(text(list.item(i)));
		}
		
		return sb.toString();
	}
	
	private String text(Node n){
		
		String text = null;
		
		switch(n.getNodeType()){
			case Node.TEXT_NODE:
				text = n.getNodeValue();
				if(text != null) text = text.trim();
				break;
			case Node.CDATA_SECTION_NODE:
				text = n.getNodeValue();
				break;
			default:
				//AQUtility.debug("unknown", n);
		}
		
		if(text == null) text = "";
		
		return text;
		
	}
	
	private void serialize(Element e, XmlSerializer s, int depth, String spaces) throws Exception{
		
		String name = e.getTagName();
		
		writeSpace(s, depth, spaces);
		
		s.startTag("", name);
		
		if(e.hasAttributes()){
			NamedNodeMap nm = e.getAttributes();
			for(int i = 0; i < nm.getLength(); i++){
				Attr attr = (Attr) nm.item(i);
				s.attribute("", attr.getName(), attr.getValue());
			}
		}
		
		if(e.hasChildNodes()){
			
			NodeList nl = e.getChildNodes();
			
			int elements = 0;
			
			for(int i = 0; i < nl.getLength(); i++){
				
				Node n = nl.item(i);
				
				short type = n.getNodeType();
				
				switch(type){				
					case Node.ELEMENT_NODE:
						serialize((Element) n, s, depth + 1, spaces);
						elements++;
						break;
					case Node.TEXT_NODE:
						s.text(text(n));
						break;
					case Node.CDATA_SECTION_NODE:					
						s.cdsect(text(n));
						break;
				}
				
				
			}
			
			if(elements > 0){
				writeSpace(s, depth, spaces);
			}
		}
		
		s.endTag("", name);
		
	}
	
	
}
