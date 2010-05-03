/**
 * 
 */
package com.rackspace.cloud.servers.api.client.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.rackspace.cloud.servers.api.client.CloudServersException;

/**
 * @author Mike Mayo - mike.mayo@rackspace.com - twitter.com/greenisus
 *
 */
public class CloudServersFaultXMLParser extends DefaultHandler {

	//<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	//<cloudServersFault xmlns="http://docs.rackspacecloud.com/servers/api/v1.0" code="400">
	//<message>422 Unprocessable Entity: Cannot perform requested action until reboot is complete.</message>
	//<details>com.rackspace.cloud.service.servers.CloudServersFault: 422 Unprocessable Entity: Cannot perform requested action until reboot is complete.</details>
	//</cloudServersFault>

	private CloudServersException exception;
	private StringBuffer currentData;

	public void startDocument() {
		exception = new CloudServersException();
	}

	public void endDocument() {
	}

	public void startElement(String uri, String name, String qName, Attributes atts) {

		currentData = new StringBuffer();
		if ("cloudServersFault".equals(name)) {
			exception.setCode(Integer.parseInt(atts.getValue("code")));
		}
	}

	public void endElement(String uri, String name, String qName) {
		if ("message".equals(name)) {
			exception.setMessage(currentData.toString());
		} else if ("details".equals(name)) {
			exception.setDetails(currentData.toString());
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

	/**
	 * @return the exception
	 */
	public CloudServersException getException() {
		return exception;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(CloudServersException exception) {
		this.exception = exception;
	}

	
}
