package com.rackspace.cloud.files.api.client.parsers;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.rackspace.cloud.files.api.client.ContainerObjects;
/** 
 * 
 * @author Phillip Toohill
 *
 */
public class ContainerObjectXMLparser extends DefaultHandler {

	private ContainerObjects object;
	private ArrayList<ContainerObjects> files;

	private StringBuffer currentData;
	public String LOG = "ViewFilesXMLparser";
   
	public void startDocument() {
		Log.v(LOG, "startDocument");
	}

	public void endDocument() {
		Log.v(LOG, "endDocument = true");
	}

	public void startElement(String uri, String name, String qName, Attributes atts) {

		currentData = new StringBuffer();
		if ("container".equals(name)) {
			files = new ArrayList<ContainerObjects>();				
		} else if ("object".equals(name)) {
			object = new ContainerObjects();
		}
	}

	public void endElement(String uri, String name, String qName) {

		String value = currentData.toString().trim();
		
		if ("container".equals(name)) {	
			
		} else if ("object".equals(name)) {
			if (files != null) {
				files.add(object);
			}
		}else if ("name".equals(name)){
			object.setCName(value);
		}else if ("content_type".equals(name)){
			object.setContentType(value);
		}else if ("hash".equals(name)){
			object.setHash(value);
		}else if ("bytes".equals(name)){
			object.setBytes(Integer.parseInt(value));
		}else if ("last_modified".equals(name)){
			object.setLastMod(value);
		
					
		}
	}


	public void characters(char ch[], int start, int length) {
				
		System.out.print("Characters:    \"");
		
		for (int i = start; i < start + length; i++) {
			switch (ch[i]) {
			case '\\':
				System.out.print("\\\\");
				break;
			case '"':
				System.out.print("\\\"");
				break;
			case '\n':
				System.out.print("\\n");
				break;
			case '\r':
				System.out.print("\\r");
				break;
			case '\t':
				System.out.print("\\t");
				break;
			default:
				System.out.print(ch[i]);
				break;
			}
		}
		System.out.print("\"\n");
		 //String strCharacters = new String(ch, start, length);
		for (int i = start; i < (start + length); i++) {
			currentData.append(ch[i]);
		}
	}
		 
	public ContainerObjects getObject() {
		return object;
	}

	public void setObject(ContainerObjects object) {
		this.object = object;
	}

	/**
	 * @return the files
	 */
	public ArrayList<ContainerObjects> getViewFiles() {
		return files;
	}

	/**
	 * @param files the servers to set
	 */
	public void setFiles(ArrayList<ContainerObjects> files) {
		this.files = files;
	}

	
}
