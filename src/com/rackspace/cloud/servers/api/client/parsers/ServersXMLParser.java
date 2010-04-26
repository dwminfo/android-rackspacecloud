/**
 * 
 */
package com.rackspace.cloud.servers.api.client.parsers;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.rackspace.cloud.servers.api.client.Server;


/**
 * @author mike
 *
 */
public class ServersXMLParser extends DefaultHandler {

	private Server server;
	private ArrayList<Server> servers;
	private ArrayList<String> publicAddresses;
	private ArrayList<String> privateAddresses;
	private boolean parsingPublicAddresses;
	private StringBuffer currentData;

	public void startDocument() {
		System.out.println("Start document");
	}

	public void endDocument() {
		System.out.println("End document");
	}

	public void startElement(String uri, String name, String qName, Attributes atts) {

		if ("".equals(uri)) {
			System.out.println("Start element: " + qName);
		} else {
			System.out.println("Start element: {" + uri + "}" + name);
		}
		
		currentData = new StringBuffer();
		if ("servers".equals(name)) {
			servers = new ArrayList<Server>();
		} else if ("server".equals(name)) {
			server = new Server();
			server.setStatus(atts.getValue("status"));
			server.setProgress(atts.getValue("progress"));
			server.setHostId(atts.getValue("hostId"));
			server.setFlavorId(atts.getValue("flavorId"));
			server.setImageId(atts.getValue("imageId"));
			server.setId(atts.getValue("id"));
			server.setName(atts.getValue("name"));
		} else if ("addresses".equals(name)) {
			//addresses = new ArrayList<String>();
		} else if ("public".equals(name)) {
			parsingPublicAddresses = true;
			publicAddresses = new ArrayList<String>();
		} else if ("private".equals(name)) {
			parsingPublicAddresses = false;
			privateAddresses = new ArrayList<String>();
		} else if ("ip".equals(name)) {
			String ipAddress = atts.getValue("addr");
			if (parsingPublicAddresses) {
				publicAddresses.add(ipAddress);
			} else {
				privateAddresses.add(ipAddress);
			}
		}
	}

	public void endElement(String uri, String name, String qName) {
		if ("".equals(uri)) {
			System.out.println("End element: " + qName);
		} else {
			System.out.println("End element:   {" + uri + "}" + name);
		}
		
		if ("servers".equals(name)) {
			
		} else if ("server".equals(name)) {
			if (servers != null) {
				servers.add(server);
			}
		} else if ("addresses".equals(name)) {
			
			String[] privateIpAddresses = new String[privateAddresses.size()];
			String[] publicIpAddresses = new String[publicAddresses.size()];
			
			for (int i = 0; i < privateAddresses.size(); i++) {
				privateIpAddresses[i] = privateAddresses.get(i);
			}
			for (int i = 0; i < publicAddresses.size(); i++) {
				publicIpAddresses[i] = publicAddresses.get(i);
			}
			
			server.setPublicIpAddresses(publicIpAddresses);
			server.setPrivateIpAddresses(privateIpAddresses);
			
		/*
		} else if ("id".equals(name)) {
			slice.setId(currentData.toString());
		} else if ("name".equals(name)) {
			slice.setName(currentData.toString());
		} else if ("flavor-id".equals(name)) {
			slice.setFlavorId(currentData.toString());
		} else if ("flavor-name".equals(name)) {
			slice.setFlavorName(currentData.toString());
		} else if ("image-id".equals(name)) {
			slice.setImageId(currentData.toString());
		} else if ("image-name".equals(name)) {
			slice.setImageName(currentData.toString());
		} else if ("backup-id".equals(name)) {
			slice.setBackupId(currentData.toString());
		} else if ("status".equals(currentData.toString())) {
			slice.setStatus(currentData.toString());
		} else if ("progress".equals(currentData.toString())) {
			slice.setProgress(currentData.toString());
		} else if ("bw-in".equals(name)) {
			slice.setBwIn(currentData.toString());
		} else if ("bw-out".equals(name)) {
			slice.setBwOut(currentData.toString());
		} else if ("addresses".equals(name)) {
			slice.setAddresses(addresses);
		} else if ("address".equals(name)) {
			addresses.add(currentData.toString());
		} else if ("ip-address".equals(name)) {
			slice.setIpAddress(currentData.toString());
		} else if ("root-password".equals(name)) {
			slice.setRootPassword(currentData.toString());
		*/
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

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	/**
	 * @return the servers
	 */
	public ArrayList<Server> getServers() {
		return servers;
	}

	/**
	 * @param servers the servers to set
	 */
	public void setServers(ArrayList<Server> servers) {
		this.servers = servers;
	}

	
}
