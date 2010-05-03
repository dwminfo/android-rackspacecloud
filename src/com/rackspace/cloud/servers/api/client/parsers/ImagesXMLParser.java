/**
 * 
 */
package com.rackspace.cloud.servers.api.client.parsers;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.rackspace.cloud.servers.api.client.Image;

/**
 * @author Mike Mayo - mike.mayo@rackspace.com - twitter.com/greenisus
 *
 */
public class ImagesXMLParser extends DefaultHandler {
	private Image image;
	private ArrayList<Image> images;
	private StringBuffer currentData;

	public void startDocument() {
	}

	public void endDocument() {
	}

	public void startElement(String uri, String name, String qName, Attributes atts) {

		currentData = new StringBuffer();
		if ("images".equals(name)) {
			images = new ArrayList<Image>();
		} else if ("image".equals(name)) {
			image = new Image();
			image.setStatus(atts.getValue("status"));
			image.setUpdated(atts.getValue("updated"));
			image.setId(atts.getValue("id"));
			image.setName(atts.getValue("name"));
		}
	}

	public void endElement(String uri, String name, String qName) {
		if ("image".equals(name)) {
			if (images != null) {
				images.add(image);
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

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * @return the servers
	 */
	public ArrayList<Image> getImages() {
		return images;
	}

	/**
	 * @param servers the servers to set
	 */
	public void setImages(ArrayList<Image> images) {
		this.images = images;
	}


}
