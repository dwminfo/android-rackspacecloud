package com.rackspace.cloud.servers.api.client.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.rackspace.cloud.servers.api.client.Account;

public class HttpHelper {

	DefaultHttpClient httpClient = new DefaultHttpClient();
	HttpContext localContext = new BasicHttpContext();
	private boolean abort;
	private String ret;

	HttpResponse response = null;
	HttpPost httpPost = null;

	public HttpHelper() {

	}

	public void clearCookies() {

		httpClient.getCookieStore().clear();

	}

	public void abort() {

		try {
			if (httpClient != null) {
				System.out.println("Abort.");
				httpPost.abort();
				abort = true;
			}
		} catch (Exception e) {
			System.out.println("HTTPHelp : Abort Exception : " + e);
		}
	}

	public String postPage(String url, String data) {

		ret = null;

//		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
//				CookiePolicy.RFC_2109);

		httpPost = new HttpPost(url);
		response = null;

		StringEntity tmp = null;

		httpPost.setHeader("X-Auth-Token", Account.getAuthToken());
		httpPost.addHeader("Content-Type", "application/xml");
		try {
			tmp = new StringEntity(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("HTTPHelp : UnsupportedEncodingException : " + e);
		}

		httpPost.setEntity(tmp);

		try {
			response = httpClient.execute(httpPost, localContext);
		} catch (ClientProtocolException e) {
			System.out.println("HTTPHelp : ClientProtocolException : " + e);
		} catch (IOException e) {
			System.out.println("HTTPHelp : IOException : " + e);
		}
		ret = response.getStatusLine().toString();

		return ret;
	}
}