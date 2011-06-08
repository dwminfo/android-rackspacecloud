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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rackspace.cloud.servers.api.client.CloudServersException;
import com.rackspace.cloud.servers.api.client.Server;
import com.rackspace.cloud.servers.api.client.ServerManager;
import com.rackspace.cloud.servers.api.client.parsers.CloudServersFaultXMLParser;

public class PasswordServerActivity extends Activity implements OnClickListener{
	
	private Server server;
	private String modifiedPassword;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewchangepassword); 
        server = (Server) this.getIntent().getExtras().get("server");
    	setupButtons();       
    }

	private void setupButtons() {
		Button update = (Button) findViewById(R.id.password_change_button);
		update.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		String password = ((EditText)findViewById(R.id.password_edittext)).getText().toString();
		String confirm = ((EditText)findViewById(R.id.password_confirm_edittext)).getText().toString();
		if(password.equals(confirm)){
			new PasswordServerTask().execute((Void[]) null);	
		}
		else{
			String mustMatch = "The password and confirmation do not match.";
			Toast passwordError = Toast.makeText(getApplicationContext(), mustMatch, Toast.LENGTH_SHORT);
			passwordError.show();
		}
	}
	
	private void showAlert(String title, String message) {
		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			} }); 
		alert.show();
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

	private class PasswordServerTask extends AsyncTask<Void, Void, HttpResponse> {

		private CloudServersException exception;

		@Override
		protected HttpResponse doInBackground(Void... arg0) {
			HttpResponse resp = null;
			try {
				resp = (new ServerManager()).changePassword(server, modifiedPassword, getApplicationContext());
			} catch (CloudServersException e) {
				exception = e;
			}
			return resp;
		}

		@Override
		protected void onPostExecute(HttpResponse response) {
			if (response != null) {
				int statusCode = response.getStatusLine().getStatusCode();	
				if(statusCode == 204){
					String mustMatch = "The server's root password has successfully been changed.";
					Toast passwordError = Toast.makeText(getApplicationContext(), mustMatch, Toast.LENGTH_SHORT);
					passwordError.show();
					finish();
				}
				if (statusCode != 204) {
					CloudServersException cse = parseCloudServersException(response);
					if ("".equals(cse.getMessage())) {
						showAlert("Error", "There was a problem changing your password.");
					} else {
						showAlert("Error", "There was a problem changing your password: " + cse.getMessage() + " " + statusCode);
					}
				}
			} else if (exception != null) {
				showAlert("Error", "There was a problem changing your password: " + exception.getMessage());
				
			}
		}


	}
}
