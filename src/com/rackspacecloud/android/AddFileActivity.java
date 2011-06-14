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

import com.rackspace.cloud.files.api.client.ContainerObjectManager;
import com.rackspace.cloud.servers.api.client.CloudServersException;
import com.rackspace.cloud.servers.api.client.parsers.CloudServersFaultXMLParser;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddFileActivity extends Activity implements OnClickListener{
	
	private Context context;	
	private EditText fileName;
	private EditText contents;
	private String containerName;
	private String path;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtextfile);
        context = getApplicationContext();
        containerName = (String) this.getIntent().getExtras().get("Cname");
        path = (String) this.getIntent().getExtras().get("curPath");
        setUpInputs();
    }
    
    private void setUpInputs(){
    	((Button) findViewById(R.id.new_file_button)).setOnClickListener(this);
    	fileName = ((EditText)findViewById(R.id.file_name_text));
    	contents = ((EditText)findViewById(R.id.new_file_text));
    }
    
    public void onClick(View arg0) {
		if ("".equals(fileName.getText().toString())) {
			showAlert("Required Fields Missing", " File name is required.");
		} else {
			//showActivityIndicators();
			new SaveFileTask().execute((Void[]) null);
		}
	}
    
    //using cloudServersException, it works for us too
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
	
	private void showAlert(String title, String message) {
		/*
		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setButton("OK", new DialogInterface.OnClickListener() {
	      public void onClick(DialogInterface dialog, int which) {
	        return;
	    } }); 
		alert.show();
		hideActivityIndicators();
		*/
    }

	private class SaveFileTask extends AsyncTask<Void, Void, HttpResponse> {
    	private CloudServersException exception;
    	
    	@Override
		protected HttpResponse doInBackground(Void... arg0) {
			HttpResponse resp = null;
			try {
				resp = (new ContainerObjectManager(context)).addObject(containerName, path, fileName.getText().toString(), "text/plain", contents.getText().toString());
			} catch (CloudServersException e) {
				exception = e;
			}
			return resp;
		}
    	
		@Override
		protected void onPostExecute(HttpResponse response) {
			if (response != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 201) {
					setResult(Activity.RESULT_OK);
					finish();
				} else {
					CloudServersException cse = parseCloudServersException(response);
					if ("".equals(cse.getMessage())) {
						showAlert("Error", "There was a problem creating your container.");
					} else {
						showAlert("Error", "There was a problem creating your container: " + cse.getMessage() + " Check container name and try again");
					}
				}
			} else if (exception != null) {
				showAlert("Error", "There was a problem creating your container: " + exception.getMessage()+" Check container name and try again");				
			}			
		}
    }
}
