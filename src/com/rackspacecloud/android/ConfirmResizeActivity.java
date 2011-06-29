package com.rackspacecloud.android;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.BasicResponseHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.rackspace.cloud.servers.api.client.CloudServersException;
import com.rackspace.cloud.servers.api.client.Server;
import com.rackspace.cloud.servers.api.client.ServerManager;
import com.rackspace.cloud.servers.api.client.http.HttpBundle;
import com.rackspace.cloud.servers.api.client.parsers.CloudServersFaultXMLParser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class ConfirmResizeActivity extends Activity {

	private Context context;
	private Server server;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.viewresize);     
		server = (Server) this.getIntent().getExtras().get("server");
		context = getApplicationContext();
		restoreState(savedInstanceState);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("server", server);
	}
	
	private void restoreState(Bundle state) {
		if (server == null && state != null && state.containsKey("server")) {
			server = (Server) state.getSerializable("server");
		}
		setupButtons();
	}

	private void setupButtons(){
		Button confirm = (Button)findViewById(R.id.confirm_resize_button);
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new ConfirmResizeTask().execute((Void[]) null);
				finish();
			}
		});

		Button rollback = (Button)findViewById(R.id.rollback_server_button);
		rollback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new RollbackResizeTask().execute((Void[]) null);	
				finish();
			}
		});
	}
	
	private CloudServersException parseCloudServersException(HttpResponse response) {
		CloudServersException cse = new CloudServersException();
		try {
			BasicResponseHandler responseHandler = new BasicResponseHandler();
			String body = responseHandler.handleResponse(response);
			CloudServersFaultXMLParser parser = new CloudServersFaultXMLParser();
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(parser);
			xmlReader.parse(new InputSource(new StringReader(body)));		    	
			cse = parser.getException();		    	
		} catch (ClientProtocolException e) {
			cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
		} catch (IOException e) {
			cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
		} catch (ParserConfigurationException e) {
			cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
		} catch (SAXException e) {
			cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
		} catch (FactoryConfigurationError e) {
			cse = new CloudServersException();
			cse.setMessage(e.getLocalizedMessage());
		}
		return cse;
	}
	
	private void startServerError(String message, HttpBundle bundle){
		Intent viewIntent = new Intent(getApplicationContext(), ServerErrorActivity.class);
		viewIntent.putExtra("errorMessage", message);
		viewIntent.putExtra("response", bundle.getResponseText());
		viewIntent.putExtra("request", bundle.getCurlRequest());
		startActivity(viewIntent);
	}
	
	private void showToast(String message) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, message, duration);
		toast.show();
	}
	
	private class ConfirmResizeTask extends AsyncTask<Void, Void, HttpBundle> {

		private CloudServersException exception;

		@Override
		//let user know their process has started
		protected void onPreExecute(){
			showToast("Confirm process has begun");
		}

		@Override
		protected HttpBundle doInBackground(Void... arg0) {
			HttpBundle bundle = null;
			try {
				bundle = (new ServerManager()).confirmResize(server, context);
			} catch (CloudServersException e) {
				exception = e;
			}
			return bundle;
		}

		@Override
		protected void onPostExecute(HttpBundle bundle) {
			HttpResponse response = bundle.getResponse();
			if (response != null) {
				int statusCode = response.getStatusLine().getStatusCode();	
				if(statusCode == 204){ showToast("Server resize was successfully confirmed."); }
				else {
					CloudServersException cse = parseCloudServersException(response);
					if ("".equals(cse.getMessage())) {
						startServerError("There was a problem confirming your resize.", bundle);
					} else {
						startServerError("There was a problem confirming your resize." + cse.getMessage(), bundle);
					}
				}
			} else if (exception != null) {
				startServerError("There was a problem confirming your resize." + exception.getMessage(), bundle);

			}
		}
	}
	
	
	private class RollbackResizeTask extends AsyncTask<Void, Void, HttpBundle> {

		private CloudServersException exception;

		@Override
		//let user know their process has started
		protected void onPreExecute(){
			showToast("Reverting your server.");
		}

		@Override
		protected HttpBundle doInBackground(Void... arg0) {
			HttpBundle bundle = null;
			try {
				bundle = (new ServerManager()).revertResize(server, context);
			} catch (CloudServersException e) {
				exception = e;
			}
			return bundle;
		}

		@Override
		protected void onPostExecute(HttpBundle bundle) {
			HttpResponse response = bundle.getResponse();
			if (response != null) {
				int statusCode = response.getStatusLine().getStatusCode();	
				if(statusCode == 202){ showToast("Server was successfully reverted."); }
				else {
					CloudServersException cse = parseCloudServersException(response);
					if ("".equals(cse.getMessage())) {
						startServerError("There was a problem reverting your server.", bundle);
					} else {
						startServerError("There was a problem reverting your server." + cse.getMessage(), bundle);
					}
				}
			} else if (exception != null) {
				startServerError("There was a problem reverting your server." + exception.getMessage(), bundle);

			}
		}
	}
	
}
