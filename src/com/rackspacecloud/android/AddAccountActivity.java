package com.rackspacecloud.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class AddAccountActivity extends Activity implements OnClickListener, OnItemSelectedListener{
	
	EditText usernameText;
	EditText apiKeyText;
	Spinner providerSpinner;
	String authServer;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount);
        usernameText = (EditText) findViewById(R.id.username);
        apiKeyText = (EditText) findViewById(R.id.apikey);
        ((Button) findViewById(R.id.submit_new_account)).setOnClickListener(this);
        
        final CheckBox show_clear = (CheckBox) findViewById(R.id.show_clear);
        show_clear.setOnClickListener(new OnClickListener() {
        	@Override 
			public void onClick(View v) {
		        if (((CheckBox) v).isChecked()) {
		        	apiKeyText.setTransformationMethod(new SingleLineTransformationMethod());
		        } else {
		        	apiKeyText.setTransformationMethod(new PasswordTransformationMethod());	
		        }
		        apiKeyText.requestFocus();
		    }	
		});
        
        loadProviderSpinner();
    }
	
	private void loadProviderSpinner(){
		//set the auth server default to us
		authServer = "https://auth.api.rackspacecloud.com/v1.0";
		providerSpinner = (Spinner) findViewById(R.id.provider_spinner);
		String[] providers = {"Rackspace Cloud (US)", "Rackspace Cloud (UK)"};
		ArrayAdapter<String> imageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, providers);
		imageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		providerSpinner.setAdapter(imageAdapter);
	}
	
	public void onClick(View arg0) {
		
		if (hasValidInput()) {
			//showActivityIndicators();
			Intent result = new Intent();
			Bundle b = new Bundle();
			b.putString("username", usernameText.getText().toString());
			b.putString("apiKey", apiKeyText.getText().toString());
			b.putString("server", authServer);
			result.putExtra("accountInfo", b);
			setResult(RESULT_OK, result);
			finish();
		} else {
			showAlert("Required Fields Missing", "Username and API Key are required.");
		}
		
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
	
	private boolean hasValidInput() {
    	String username = usernameText.getText().toString();
    	String apiKey = apiKeyText.getText().toString();
    	return !"".equals(username) && !"".equals(apiKey);
    }
	
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if(position == 0){
			authServer = "https://auth.api.rackspacecloud.com/v1.0";
		}
		else{
			authServer = "https://lon.auth.api.rackspacecloud.com/v1.0";
		}
	}
	
	public void onNothingSelected(AdapterView<?> parent) {
	}
	

	
	

}
