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
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.RequestExpectContinue;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.util.Log;

import com.rackspace.cloud.files.api.client.parsers.ContainerObjectXMLparser;
import com.rackspace.cloud.servers.api.client.Account;
import com.rackspace.cloud.servers.api.client.CloudServersException;
import com.rackspace.cloud.servers.api.client.EntityManager;
import com.rackspace.cloud.servers.api.client.parsers.CloudServersFaultXMLParser;

/** 
 * 
 * @author Phillip Toohill
 *
 */
public class ContainerObjectManager extends EntityManager {

	public String LOG = "ContainerObjectManager";
	public static final String storageToken = Account.getStorageToken();
	


	public ArrayList<ContainerObjects> createList(boolean detail, String passName) throws CloudServersException {
		
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(Account.getStorageUrl()+"/"+passName+"?format=xml");
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

	public HttpResponse deleteObject(String Container, String Object) throws CloudServersException {
		HttpResponse resp = null;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpDelete deleteObject = new HttpDelete(Account.getStorageUrl() + "/" + Container + "/" + Object);
		Log.v(LOG, "the container (deleteObject) vairble "+Container+" "+Object);
				
		deleteObject.addHeader("X-Auth-Token", Account.getAuthToken());
		httpclient.removeRequestInterceptorByClass(RequestExpectContinue.class);

		try {			
			resp = httpclient.execute(deleteObject);
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
		return resp;
	}
	

}