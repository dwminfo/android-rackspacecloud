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
	private boolean find_Title = false;
	private boolean find_Hash = false;
	private boolean find_Bytes = false;
	private boolean find_lastMod = false;
	public String LOG = "ViewFilesXMLparser";
	private boolean find_contentType = false;
   
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
		}else if ("name".equals(name)){
			this.find_Title = true;
		}else if ("content_type".equals(name)){
			this.find_contentType  = true;
		}else if ("hash".equals(name)){
			this.find_Hash = true;
		}else if ("bytes".equals(name)){
			this.find_Bytes = true;
		}else if ("last_modified".equals(name)) {
			this.find_lastMod=true;
				
	}
			
}	

	public void endElement(String uri, String name, String qName) {
		if ("container".equals(name)) {	
			
		} else if ("object".equals(name)) {
			if (files != null) {
				files.add(object);
			}
		}else if ("name".equals(name)){
			this.find_Title = false;
		}else if ("content_type".equals(name)){
			this.find_contentType = false;
		}else if ("hash".equals(name)){
			this.find_Hash = false;
		}else if ("bytes".equals(name)){
			this.find_Bytes = false;
		}else if ("last_modified".equals(name)){
			this.find_lastMod = false;
		
					
		}
	}

	public void characters(char ch[], int start, int length) {
		
		 if(this.find_Title){
			object.setCName(new String(ch, start, length));
		 } else if (this.find_contentType){
			 object.setContentType(new String(ch, start, length));
		 } else if (this.find_Hash) {
			 object.setHash(new String(ch, start, length));
		 } else if (this.find_Bytes) {
			 object.setBytes(new String(ch, start, length));
		 } else if (this.find_lastMod){
			 object.setLastMod(new String(ch, start, length));
		 }
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
