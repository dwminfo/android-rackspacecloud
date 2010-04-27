/**
 * 
 */
package com.rackspace.cloud.servers.api.client;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.RequestExpectContinue;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.rackspace.cloud.servers.api.client.parsers.ServersXMLParser;

/**
 * @author mike
 *
 */
public class EntityManager {
	
	//
	// CRUD Operations
	//
	
	public void create(Server entity) {
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(Account.getServerUrl() + "/servers.xml");
				
		post.addHeader("X-Auth-Token", Account.getAuthToken());
		post.addHeader("Content-Type", "application/xml");

		StringEntity tmp = null;
		try {
			tmp = new StringEntity(entity.toXML());
		} catch (UnsupportedEncodingException e) {
			System.out.println("HTTPHelp : UnsupportedEncodingException : " + e);
			// TODO: handle?
		}
		post.setEntity(tmp);
		
		httpclient.removeRequestInterceptorByClass(RequestExpectContinue.class);

		try {			
			HttpResponse resp = httpclient.execute(post);
		    System.out.println(resp.getStatusLine().toString());
		    
		    if (resp.getStatusLine().getStatusCode() == 202) {		    	
		    	// TODO: handle success and failure
		    	
			    BasicResponseHandler responseHandler = new BasicResponseHandler();
			    String body = responseHandler.handleResponse(resp);
		    	
		    	ServersXMLParser serversXMLParser = new ServersXMLParser();
		    	SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
		    	XMLReader xmlReader = saxParser.getXMLReader();
		    	xmlReader.setContentHandler(serversXMLParser);
		    	xmlReader.parse(new InputSource(new StringReader(body)));		    	
		    	entity = serversXMLParser.getServer();		    	
		    	
		    }
		} catch (ClientProtocolException cpe) {
			// TODO Auto-generated catch block
			cpe.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//return false;
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void remove(Entity e) {
		
	}
	
	public void update(Entity e) {
		
	}
	
	public void refresh(Entity e) {
		
	}
	
	public Entity find(long id) {
		return null;
	}
	
	//
	// Polling Operations
	//
	public void wait(Entity e) {
		
	}
	
	public void wait(Entity e, long timeout) {
	
	}
	
	public void notify(Entity e, ChangeListener ch) {
		
	}
	
	public void stopNotify(Entity e, ChangeListener ch) {
		
	}
	
	//
	// Lists
	//
	//public EntityList createList(boolean detail) {
	public ArrayList createList(boolean detail) {
		
		// cache busting
		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());		
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(Account.getServerUrl() + "/servers/detail.xml?now=" + currentTimestamp.getTime());
		ArrayList<Server> servers = new ArrayList<Server>();
		
		get.addHeader("X-Auth-Token", Account.getAuthToken());
		
		try {			
			HttpResponse resp = httpclient.execute(get);
		    System.out.println(resp.getStatusLine().toString());
		    //System.out.println("body:\n\n" + getResponseBody(resp));
		    
		    BasicResponseHandler responseHandler = new BasicResponseHandler();
		    String body = responseHandler.handleResponse(resp);
		    System.out.println("body:\n\n" + body);
		    
		    if (resp.getStatusLine().getStatusCode() == 200 || resp.getStatusLine().getStatusCode() == 203) {		    	
		    	ServersXMLParser serversXMLParser = new ServersXMLParser();
		    	SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
		    	XMLReader xmlReader = saxParser.getXMLReader();
		    	xmlReader.setContentHandler(serversXMLParser);
		    	xmlReader.parse(new InputSource(new StringReader(body)));		    	
		    	servers = serversXMLParser.getServers();		    	
		    }
		} catch (ClientProtocolException cpe) {
			// TODO Auto-generated catch block
			cpe.printStackTrace();
			//return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//return false;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return servers;
	}
	
	public EntityList createDeltaList(boolean detail, long changesSince) {
		return null;
	}
	
	public EntityList createList(boolean detail, long offset, long limit) {
		return null;
	}
	
	public EntityList createDeltaList(boolean detail, long changesSince, long offset, long limit) {
		return null;
	}

}
