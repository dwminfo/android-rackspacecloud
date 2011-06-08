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
import com.rackspace.cloud.servers.api.client.parsers.CloudServersFaultXMLParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

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

	public void onClick(View v) {
		new BackupServerTask().execute((Void[]) null);
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
	
	private class BackupServerTask extends AsyncTask<Void, Void, HttpResponse> {
    	
		private CloudServersException exception;
		
		@Override
		protected HttpResponse doInBackground(Void... arg0) {
			HttpResponse resp = null;
			try {
				resp = (new ServerManager()).backup(server, selectedWeeklyBackup, selectedDailyBackup, enableCheckBox.isChecked(), getApplicationContext());
			} catch (CloudServersException e) {
				exception = e;
			}
			return resp;
		}
    	
		@Override
		protected void onPostExecute(HttpResponse response) {

			if (response != null) {
				int statusCode = response.getStatusLine().getStatusCode();	
				Log.d("statuscode", Integer.toString(statusCode));
				if(statusCode == 204 || statusCode == 202){
					String mustMatch = "The server's backup schedule has been changed.";
					Toast passwordError = Toast.makeText(getApplicationContext(), mustMatch, Toast.LENGTH_SHORT);
					passwordError.show();
					finish();
				}
				else if (statusCode != 204 && statusCode != 202) {
					CloudServersException cse = parseCloudServersException(response);
					if ("".equals(cse.getMessage())) {
						showAlert("Error", "There was a problem rebooting your server.");
					} else {
						Log.d("info", "here");
						showAlert("Error", "There was a problem rebooting your server: " + cse.getMessage() + " " + statusCode);
					}
				}
			} else if (exception != null) {
				showAlert("Error", "There was a problem rebooting your server: " + exception.getMessage());
				
			}
		}
    }
}
