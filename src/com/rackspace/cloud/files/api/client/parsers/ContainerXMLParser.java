package com.rackspace.cloud.files.api.client.parsers;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.rackspace.cloud.files.api.client.Container;
/** 
 * 
 * @author Phillip Toohill
 *
 */
public class ContainerXMLParser extends DefaultHandler {

	private Container container;
	private ArrayList<Container> containers;
	private StringBuffer currentData;
	
	public void startElement(String uri, String name, String qName, Attributes atts) {
		
		currentData = new StringBuffer();
		if ("account".equals(name)) {
			containers = new ArrayList<Container>();
		} else if ("container".equals(name)) {
			container = new Container();
		} 
	}

	public void endElement(String uri, String name, String qName) {
		
		String value = currentData.toString().trim();
		
		if ("account".equals(name)) {

		} else if ("container".equals(name)) {

			if (containers == null) {
				containers = new ArrayList<Container>();
			}
			containers.add(container);
			
		} else if ("name".equals(name)) {
			container.setName(value);
		} else if ("count".equals(name)) {
			container.setCount(Integer.parseInt(value));
		} else if ("bytes".equals(name)) {
			container.setBytes(Integer.parseInt(value));
		} else if ("cdn_enabled".equals(name)) {
			container.setCdnEnabled("True".equals(value));
		} else if ("ttl".equals(name)) {
			container.setTtl(Integer.parseInt(value));
		} else if ("cdn_url".equals(name)) { 
			container.setCdnUrl(value);
		} else if ("log_retention".equals(name)) {
			container.setLogRetention("True".equals(value));
		}
		
	}

	public void characters(char ch[], int start, int length) {
		
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

		for (int i = start; i < (start + length); i++) {
			currentData.append(ch[i]);
		}
	}
		 
	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	/**
	 * @return the files
	 */
	public ArrayList<Container> getContainers() {
		return containers;
	}

	/**
	 * @param containers the servers to set
	 */
	public void setContainers(ArrayList<Container> containers) {
		this.containers = containers;
	}

}
