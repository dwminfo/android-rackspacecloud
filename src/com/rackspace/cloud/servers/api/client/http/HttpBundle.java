package com.rackspace.cloud.servers.api.client.http;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

/*
 * HttpBundle stores a request and response for
 * a call it allows you to be able to display the
 * curl request and the http response to the user
 * if the call fails
 */
public class HttpBundle {
	
	private String curlRequest;
	private HttpResponse response;
	
	public void setCurlRequest(HttpEntityEnclosingRequestBase request){
		curlRequest = getCurl(request);
	}
	
	public void setCurlRequest(HttpRequestBase request){
		curlRequest = getCurl(request);
	}
	
	public String getCurlRequest(){
		return curlRequest;
	}
	
	public HttpResponse getResponse(){
		return response;
	}
	
	public void setHttpResponse(HttpResponse response){
		this.response = response;
	}
	
	public String getResponseText(){
		HttpEntity responseEntity = response.getEntity();
		StringBuilder result = new StringBuilder();
		HeaderIterator itr = response.headerIterator();
		while(itr.hasNext()){
			result.append(itr.nextHeader() + "\n");
		}
		
		String xml = "\n\n";
		try {
			xml = EntityUtils.toString(responseEntity);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		result.append(xml);
		return result.toString();
	}

	/*
	 * convert the HttpRequest into the equivalent curl
	 * statement
	 */
	private String getCurl(HttpEntityEnclosingRequestBase message){
		StringBuilder result = new StringBuilder("curl -verbose -X ");
		result.append(message.getMethod());
		HeaderIterator itr = message.headerIterator();
		while(itr.hasNext()){
			Header header = itr.nextHeader();
			String key = header.getName();
			String value = header.getValue();
			// protect authentication info from being exposed
			/**/
			if(key.equals("X-Auth-Token") || key.equals("X-Auth-Key")){
				value = "<secret>";
			}
			/**/
			result.append(" -H \"" + key + ": " + value + "\"");
		}

		HttpEntity entity = message.getEntity();
		String xmlBody = null;
		if(entity != null){
			try {
				xmlBody = EntityUtils.toString(entity);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(xmlBody != null && !xmlBody.equals("")){
			result.append(" -d \"<?xml version=\"1.0\" encoding=\"UTF-8\"?> " + xmlBody + "\"");
		}
		
		result.append(" " + message.getURI());
		return result.toString();
	}
	
	private String getCurl(HttpRequestBase message){
		StringBuilder result = new StringBuilder("curl -verbose -X ");
		result.append(message.getMethod());
		HeaderIterator itr = message.headerIterator();
		while(itr.hasNext()){
			Header header = itr.nextHeader();
			String key = header.getName();
			String value = header.getValue();
			// protect authentication info from being exposed
			/**/
			if(key.equals("X-Auth-Token") || key.equals("X-Auth-Key")){
				value = "<secret>";
			}
			/**/
			result.append(" -H \"" + key + ": " + value + "\"");
		}

		/* no body for just HttpRequestBase
		HttpEntity entity = message.getEntity();
		String xmlBody = null;
		try {
			xmlBody = EntityUtils.toString(entity);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(xmlBody != null && !xmlBody.equals("")){
			result.append(" -d \"<?xml version=\"1.0\" encoding=\"UTF-8\"?> " + xmlBody + "\"");
		}
		*/
		result.append(" " + message.getURI());
		return result.toString();
	}
	
}
