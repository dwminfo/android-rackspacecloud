/**
 * 
 */
package com.rackspace.cloud.servers.api.client;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
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
public class ServerManager extends EntityManager {

	public static final String SOFT_REBOOT = "SOFT";
	public static final String HARD_REBOOT = "HARD";
	
	public HttpResponse reboot(Server server, String rebootType) {
		HttpResponse resp = null;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(Account.getServerUrl() + "/servers/" + server.getId() + "/action.xml");
				
		post.addHeader("X-Auth-Token", Account.getAuthToken());
		post.addHeader("Content-Type", "application/xml");

		StringEntity tmp = null;
		try {
			tmp = new StringEntity("<reboot xmlns=\"http://docs.rackspacecloud.com/servers/api/v1.0\" type=\"" + rebootType + "\"/>");
		} catch (UnsupportedEncodingException e) {
			System.out.println("HTTPHelp : UnsupportedEncodingException : " + e);
			// TODO: handle?
		}
		post.setEntity(tmp);
		
		httpclient.removeRequestInterceptorByClass(RequestExpectContinue.class);

		try {			
			resp = httpclient.execute(post);
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
		}	
		return resp;
	}

	public HttpResponse delete(Server server) {
		HttpResponse resp = null;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpDelete delete = new HttpDelete(Account.getServerUrl() + "/servers/" + server.getId() + ".xml");
				
		delete.addHeader("X-Auth-Token", Account.getAuthToken());
		delete.addHeader("Content-Type", "application/xml");
		httpclient.removeRequestInterceptorByClass(RequestExpectContinue.class);

		try {			
			resp = httpclient.execute(delete);
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
		}	
		return resp;
	}

}
