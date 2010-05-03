/**
 * 
 */
package com.rackspace.cloud.servers.api.client.parsers;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.rackspace.cloud.servers.api.client.Flavor;

/**
 * @author Mike Mayo - mike.mayo@rackspace.com - twitter.com/greenisus
 *
 */
public class FlavorsXMLParser extends DefaultHandler {
	private Flavor flavor;
	private ArrayList<Flavor> flavors;
	private StringBuffer currentData;

	public void startDocument() {
		System.out.println("Start document");
	}

	public void endDocument() {
		System.out.println("End document");
	}

	public void startElement(String uri, String name, String qName, Attributes atts) {

		currentData = new StringBuffer();
		if ("flavors".equals(name)) {
			flavors = new ArrayList<Flavor>();
		} else if ("flavor".equals(name)) {
			flavor = new Flavor();
			flavor.setDisk(atts.getValue("disk"));
			flavor.setRam(atts.getValue("ram"));
			flavor.setId(atts.getValue("id"));
			flavor.setName(atts.getValue("name"));
		}
	}

	public void endElement(String uri, String name, String qName) {
		if ("flavor".equals(name)) {
			if (flavors != null) {
				flavors.add(flavor);
			}
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
		
		
		for (int i = start; i < (start + length); i++) {
			currentData.append(ch[i]);
		}
	}

	public Flavor getFlavor() {
		return flavor;
	}

	public void setFlavor(Flavor flavor) {
		this.flavor = flavor;
	}

	/**
	 * @return the servers
	 */
	public ArrayList<Flavor> getFlavors() {
		return flavors;
	}

	/**
	 * @param servers the servers to set
	 */
	public void setFlavors(ArrayList<Flavor> flavors) {
		this.flavors = flavors;
	}

}
