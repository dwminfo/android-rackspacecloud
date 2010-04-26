/**
 * 
 */
package com.rackspace.cloud.servers.api.client;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.rackspace.cloud.servers.api.client.parsers.ImagesXMLParser;

/**
 * @author mike
 *
 */
public class ImageManager extends EntityManager {

	public ArrayList<Image> createList(boolean detail) {
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(Account.getServerUrl() + "/images/detail.xml?now=cache_time2");
		ArrayList<Image> images = new ArrayList<Image>();
		
		get.addHeader("X-Auth-Token", Account.getAuthToken());
		
		try {			
			HttpResponse resp = httpclient.execute(get);
		    System.out.println(resp.getStatusLine().toString());
		    //System.out.println("body:\n\n" + getResponseBody(resp));
		    
		    BasicResponseHandler responseHandler = new BasicResponseHandler();
		    String body = responseHandler.handleResponse(resp);
		    System.out.println("body:\n\n" + body);
		    
		    if (resp.getStatusLine().getStatusCode() == 200 || resp.getStatusLine().getStatusCode() == 203) {		    	
		    	
		    	ImagesXMLParser imagesXMLParser = new ImagesXMLParser();
		    	SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
		    	XMLReader xmlReader = saxParser.getXMLReader();
		    	xmlReader.setContentHandler(imagesXMLParser);
		    	xmlReader.parse(new InputSource(new StringReader(body)));		    	
		    	images = imagesXMLParser.getImages();		    	
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
		
		
		return images;
	}
}
