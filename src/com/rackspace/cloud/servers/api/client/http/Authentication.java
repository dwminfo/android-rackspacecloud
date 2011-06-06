/**
 * 
 */
package com.rackspace.cloud.servers.api.client.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.rackspace.cloud.servers.api.client.Account;
import com.rackspace.cloud.servers.api.client.AccountCopy;

/**
 * @author Mike Mayo - mike.mayo@rackspace.com - twitter.com/greenisus
 *
 */
public class Authentication {

	public static boolean authenticate(AccountCopy account) {
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(Account.getAuthServer());

		get.addHeader("X-Auth-User", account.getUsername());
		get.addHeader("X-Auth-Key", account.getApiKey());
		
		try {			
			HttpResponse resp = httpclient.execute(get);
		    			
		    if (resp.getStatusLine().getStatusCode() == 204) {
		    	Account.setAuthToken(resp.getFirstHeader("X-Auth-Token").getValue());
		    	Account.setServerUrl(resp.getFirstHeader("X-Server-Management-Url").getValue());
		    	Account.setStorageUrl(resp.getFirstHeader("X-Storage-Url").getValue());
		    	Account.setStorageToken(resp.getFirstHeader("X-Storage-Token").getValue());
		    	Account.setCdnManagementUrl(resp.getFirstHeader("X-Cdn-Management-Url").getValue());
		    	return true;
		    } else {
		    	Log.d("status code", Integer.toString(resp.getStatusLine().getStatusCode()));
		    	return false;
		    }
		} catch (ClientProtocolException cpe) {
			return false;
		} catch (IOException e) {
			Log.v("info", e.toString());
			return false;
		}
	}
}
