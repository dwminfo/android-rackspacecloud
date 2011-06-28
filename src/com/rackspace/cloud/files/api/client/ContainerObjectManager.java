package com.rackspace.cloud.files.api.client;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.protocol.RequestExpectContinue;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;

import com.rackspace.cloud.files.api.client.parsers.ContainerObjectXMLparser;
import com.rackspace.cloud.servers.api.client.Account;
import com.rackspace.cloud.servers.api.client.CloudServersException;
import com.rackspace.cloud.servers.api.client.EntityManager;
import com.rackspace.cloud.servers.api.client.http.HttpBundle;
import com.rackspace.cloud.servers.api.client.parsers.CloudServersFaultXMLParser;

/** 
 * 
 * @author Phillip Toohill
 *
 */
public class ContainerObjectManager extends EntityManager {

	public String LOG = "ContainerObjectManager";
	private Context context;
	public static final String storageToken = Account.getAccount().getStorageToken();
	
	public ContainerObjectManager(Context context) {
		this.context = context;
	}

	public ArrayList<ContainerObjects> createList(boolean detail, String passName) throws CloudServersException {
		
		CustomHttpClient httpclient = new CustomHttpClient(context);
		HttpGet get = new HttpGet(Account.getAccount().getStorageUrl()+"/"+passName + "?format=xml");
		ArrayList<ContainerObjects> files = new ArrayList<ContainerObjects>();
		
		
		get.addHeader("Content-Type", "application/xml");
		get.addHeader("X-Storage-Token", storageToken);
		
		
				
		try {			
			HttpResponse resp = httpclient.execute(get);		    
		    BasicResponseHandler responseHandler = new BasicResponseHandler();
		    String body = responseHandler.handleResponse(resp);
		    
		    if (resp.getStatusLine().getStatusCode() == 200 || resp.getStatusLine().getStatusCode() == 203) {		    	
		    	ContainerObjectXMLparser filesXMLParser = new ContainerObjectXMLparser();
		    	SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
		    	XMLReader xmlReader = saxParser.getXMLReader();
		    	xmlReader.setContentHandler(filesXMLParser);
	            
		    	xmlReader.parse(new InputSource(new StringReader(body)));
		    	files = filesXMLParser.getViewFiles();
		    	
		    } else {
		    	CloudServersFaultXMLParser parser = new CloudServersFaultXMLParser();
		    	SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
		    	XMLReader xmlReader = saxParser.getXMLReader();
		    	xmlReader.setContentHandler(parser);
		    	xmlReader.parse(new InputSource(new StringReader(body)));		    	
		    	CloudServersException cse = parser.getException();		    	
		    	throw cse;
		    }
		} catch (ClientProtocolException e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		} catch (IOException e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		} catch (ParserConfigurationException e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		} catch (SAXException e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		} catch (FactoryConfigurationError e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		}
		return files;
		
	}

	public HttpBundle deleteObject(String Container, String Object) throws CloudServersException {
		HttpResponse resp = null;
		CustomHttpClient httpclient = new CustomHttpClient(context);
		HttpDelete deleteObject = new HttpDelete(Account.getAccount().getStorageUrl() + "/" + Container + "/" + Object);
				
		deleteObject.addHeader("X-Auth-Token", Account.getAccount().getAuthToken());
		httpclient.removeRequestInterceptorByClass(RequestExpectContinue.class);

		HttpBundle bundle = new HttpBundle();
		bundle.setCurlRequest(deleteObject);
		
		try {			
			resp = httpclient.execute(deleteObject);
			bundle.setHttpResponse(resp);
		} catch (ClientProtocolException e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		} catch (IOException e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		} catch (FactoryConfigurationError e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		}	
		return bundle;
	}
	
	public HttpBundle getObject(String Container, String Object) throws CloudServersException {
		HttpResponse resp = null;
		CustomHttpClient httpclient = new CustomHttpClient(context);
		HttpGet getObject = new HttpGet(Account.getAccount().getStorageUrl() + "/" + Container + "/" + Object);
				
		getObject.addHeader("X-Auth-Token", Account.getAccount().getAuthToken());
		httpclient.removeRequestInterceptorByClass(RequestExpectContinue.class);

		HttpBundle bundle = new HttpBundle();
		bundle.setCurlRequest(getObject);
		
		try {			
			resp = httpclient.execute(getObject);
			bundle.setHttpResponse(resp);
		} catch (ClientProtocolException e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		} catch (IOException e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		} catch (FactoryConfigurationError e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		}	
		return bundle;
	}
	
	public HttpBundle addObject(String Container, String Path, String Object, String type) throws CloudServersException {
		HttpResponse resp = null;
		CustomHttpClient httpclient = new CustomHttpClient(context);
		HttpPut addObject = new HttpPut(Account.getAccount().getStorageUrl() + "/" + Container + "/" + Path + Object);
				
		addObject.addHeader("X-Auth-Token", Account.getAccount().getAuthToken());
		addObject.addHeader("Content-Type", type);
		httpclient.removeRequestInterceptorByClass(RequestExpectContinue.class);
		
		HttpBundle bundle = new HttpBundle();
		bundle.setCurlRequest(addObject);

		try {			
			resp = httpclient.execute(addObject);
			bundle.setHttpResponse(resp);
		} catch (ClientProtocolException e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		} catch (IOException e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		} catch (FactoryConfigurationError e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		}	
		return bundle;
	}
	
	/*
	 * used for adding text files, requires an extra parameter to 
	 * store the data for the file
	 */
	public HttpBundle addObject(String Container, String Path, String Object, String type, String data) throws CloudServersException {
		HttpResponse resp = null;
		CustomHttpClient httpclient = new CustomHttpClient(context);
		HttpPut addObject = new HttpPut(Account.getAccount().getStorageUrl() + "/" + Container + "/" + Path + Object);
				
		addObject.addHeader("X-Auth-Token", Account.getAccount().getAuthToken());
		addObject.addHeader("Content-Type", type);
		httpclient.removeRequestInterceptorByClass(RequestExpectContinue.class);

		HttpBundle bundle = new HttpBundle();
		bundle.setCurlRequest(addObject);
		
		try {			
			resp = httpclient.execute(addObject);
			bundle.setHttpResponse(resp);
		} catch (ClientProtocolException e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		} catch (IOException e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		} catch (FactoryConfigurationError e) {
			CloudServersException cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
			throw cse;
		}	
		return bundle;
	}

}