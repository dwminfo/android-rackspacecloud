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

import com.rackspace.cloud.servers.api.client.Backup;
import com.rackspace.cloud.servers.api.client.CloudServersException;
import com.rackspace.cloud.servers.api.client.Server;
import com.rackspace.cloud.servers.api.client.ServerManager;
import com.rackspace.cloud.servers.api.client.http.HttpBundle;
import com.rackspace.cloud.servers.api.client.parsers.CloudServersFaultXMLParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

public class BackupServerActivity extends Activity implements OnItemSelectedListener, OnClickListener {
		
	private Server server;
	private Spinner weeklyBackupSpinner;
	private Spinner dailyBackupSpinner;
	private CheckBox enableCheckBox;
	private String selectedWeeklyBackup;
	private String selectedDailyBackup;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        server = (Server) this.getIntent().getExtras().get("server");
        setContentView(R.layout.viewbackup); 
        setupSpinners();
    	setupButtons();
    	setupCheckBox();        
    }

    private void setupSpinners(){
    	weeklyBackupSpinner = (Spinner) findViewById(R.id.weekly_backup_spinner);
    	ArrayAdapter<CharSequence> weeklyAdapter = ArrayAdapter.createFromResource(this, R.array.weeklyBackupValues, android.R.layout.simple_spinner_item);
    	weeklyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	weeklyBackupSpinner.setAdapter(weeklyAdapter);
    	weeklyBackupSpinner.setOnItemSelectedListener(this);
    	
    	
    	dailyBackupSpinner = (Spinner) findViewById(R.id.daily_backup_spinner);
    	ArrayAdapter<CharSequence> dailyAdapter = ArrayAdapter.createFromResource(this, R.array.dailyBackupValues, android.R.layout.simple_spinner_item);
    	dailyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	dailyBackupSpinner.setAdapter(dailyAdapter);
    	dailyBackupSpinner.setOnItemSelectedListener(this);
    }
    
	private void setupButtons() {
		Button update = (Button) findViewById(R.id.backup_update_button);
		update.setOnClickListener(this);
	}
	
	private void setupCheckBox(){
		enableCheckBox = (CheckBox) findViewById(R.id.enable_backup_checkbox);
	}
	
	private void showAlert(String title, String message) {
    	try {
		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setButton("OK", new DialogInterface.OnClickListener() {
	      public void onClick(DialogInterface dialog, int which) {
	        return;
	    } }); 
		alert.show();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

	public void onClick(View v) {
		/*
		 * server maybe null if another task is
		 * currently processing
		 */
		if(server == null){
			showAlert("Error", "Server is busy.");
		}
		else{
			new BackupServerTask().execute((Void[]) null);
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		if(parent == weeklyBackupSpinner){
			selectedWeeklyBackup = Backup.getWeeklyValue(pos);
		}
		if(parent == dailyBackupSpinner){
			selectedDailyBackup = Backup.getDailyValue(pos);
		}
		
	}

	public void onNothingSelected(AdapterView<?> parent) {
		//do nothing
	}
	
    private void showToast(String message) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, message, duration);
		toast.show();
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
	
	private class BackupServerTask extends AsyncTask<Void, Void, HttpBundle> {
    	
		private CloudServersException exception;
		
		@Override
		//let user know their process has started
		protected void onPreExecute(){
			showToast("Changing backup schedule process has begun");
		}
		
		@Override
		protected HttpBundle doInBackground(Void... arg0) {
			HttpBundle bundle = null;
			try {
				bundle = (new ServerManager()).backup(server, selectedWeeklyBackup, selectedDailyBackup, enableCheckBox.isChecked(), getApplicationContext());
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
				Log.d("statuscode", Integer.toString(statusCode));
				if(statusCode == 204 || statusCode == 202){
					showToast("The server's backup schedule has been change.");
					finish();
				}
				else if (statusCode != 204 && statusCode != 202) {
					CloudServersException cse = parseCloudServersException(response);
					if ("".equals(cse.getMessage())) {
						startServerError("There was a problem changing the backup schedule.", bundle);
					} else {
						Log.d("info", "here");
						startServerError("There was a problem changing the backup schedule: " + cse.getMessage() + " " + statusCode, bundle);
					}
				}
			} else if (exception != null) {
				startServerError("There was a problem changing the backup schedule: " + exception.getMessage(), bundle);
				
			}
		}
    }
}
