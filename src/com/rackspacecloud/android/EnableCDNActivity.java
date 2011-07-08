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
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.rackspace.cloud.files.api.client.ContainerManager;
import com.rackspace.cloud.servers.api.client.CloudServersException;
import com.rackspace.cloud.servers.api.client.http.HttpBundle;
import com.rackspace.cloud.servers.api.client.parsers.CloudServersFaultXMLParser;

public class EnableCDNActivity extends Activity implements OnClickListener,
		OnItemSelectedListener {

	public static String containerName = null;
	private String selectedTtlId;
	private String selectedLogRetId;
	private String selectedCdnId;
	private Spinner ttlSpinner;
	private Spinner logRetSpinner;
	private Spinner cdnSpinner;
	private Context context;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enable_cdn_container);
		context = getApplicationContext();
		containerName = (String) this.getIntent().getExtras().get("Cname");
		setupButtons();
		loadTtlSpinner();
		loadLogRetSpinner();
		loadCdnSpinner();

	}

	private void setupButton(int resourceId, OnClickListener onClickListener) {
		Button button = (Button) findViewById(resourceId);
		button.setOnClickListener(onClickListener);
	}

	private void setupButtons() {
		setupButton(R.id.enable_button, new OnClickListener() {
			public void onClick(View v) {
				showDialog(R.id.enable_button);
			}
		});

		setupButton(R.id.disable_button, new OnClickListener() {
			public void onClick(View v) {
				showDialog(R.id.disable_button);
			}
		});
	}

	private void loadCdnSpinner() {
		cdnSpinner = (Spinner) findViewById(R.id.cdn_spinner);
		cdnSpinner.setOnItemSelectedListener(this);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.cdn, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cdnSpinner.setAdapter(adapter);

	}

	private void loadTtlSpinner() {
		ttlSpinner = (Spinner) findViewById(R.id.ttl_spinner);
		ttlSpinner.setOnItemSelectedListener(this);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.ttl, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ttlSpinner.setAdapter(adapter);

	}

	private void loadLogRetSpinner() {
		logRetSpinner = (Spinner) findViewById(R.id.log_retention_spinner);
		logRetSpinner.setOnItemSelectedListener(this);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.logRet, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		logRetSpinner.setAdapter(adapter);

	}

	public void onItemSelected(AdapterView<?> parent, View view, int id,
			long arg3) {
		if (parent == cdnSpinner) {
			selectedCdnId = cdnSpinner.getSelectedItem().toString();
		}
		if (parent == ttlSpinner) {
			selectedTtlId = ttlSpinner.getSelectedItem().toString();
		} else if (parent == logRetSpinner) {
			selectedLogRetId = logRetSpinner.getSelectedItem().toString();
		}
	}

	public void onNothingSelected(AdapterView<?> parent) {

	}
	
	private void showToast(String message) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, message, duration);
		toast.show();
    }
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.enable_button:
			return new AlertDialog.Builder(EnableCDNActivity.this)
					.setIcon(R.drawable.alert_dialog_icon)
					.setTitle("Enable CDN")
					.setMessage(
							"Are you sure you want to enable CDN on this container?")
					.setPositiveButton("Enable CDN",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// User clicked OK so do some stuff
									new EnableCDNTask().execute((Void[]) null);
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// User clicked Cancel so do some stuff
								}
							}).create();
		case R.id.disable_button:
			return new AlertDialog.Builder(EnableCDNActivity.this)
					.setIcon(R.drawable.alert_dialog_icon)
					.setTitle("Change Attributes")
					.setMessage(
							"Are you sure you want to disable CDN, and/or change attributes?")
					.setPositiveButton("Change Attributes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// User clicked OK so do some stuff
									new ChangeAttributesCDNTask().execute((Void[]) null);
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// User clicked Cancel so do some stuff
								}
							}).create();
		}
		return null;
	}
	/*
	private void setActivityIndicatorsVisibility(int visibility) {
		ProgressBar pb = (ProgressBar) findViewById(R.id.save_container_progress_bar);
		TextView tv = (TextView) findViewById(R.id.saving_container_label);
		pb.setVisibility(visibility);
		tv.setVisibility(visibility);
	}

	private void showActivityIndicators() {
		setActivityIndicatorsVisibility(View.VISIBLE);
	}

	private void hideActivityIndicators() {
		setActivityIndicatorsVisibility(View.INVISIBLE);
	}
	*/

	// using CloudServersException, it works for us too
	private CloudServersException parseCloudServersException(
			HttpResponse response) {
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
	
	private void startFileError(String message, HttpBundle bundle){
		Intent viewIntent = new Intent(getApplicationContext(), ServerErrorActivity.class);
		viewIntent.putExtra("errorMessage", message);
		viewIntent.putExtra("response", bundle.getResponseText());
		viewIntent.putExtra("request", bundle.getCurlRequest());
		startActivity(viewIntent);
	}

	public class EnableCDNTask extends AsyncTask<Void, Void, HttpBundle> {
		private CloudServersException exception;

		@Override
		protected HttpBundle doInBackground(Void... arg0) {
			HttpBundle bundle = null;
			try {
				bundle = (new ContainerManager(context)).enable(containerName,
						selectedTtlId, selectedLogRetId);
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
				if (statusCode == 201) {
					setResult(Activity.RESULT_OK);
					finish();
				} else if (statusCode == 202) {
					showToast("Accepted, container is already cdn enabled");
					finish();
				} else {
					CloudServersException cse = parseCloudServersException(response);
					if ("".equals(cse.getMessage())) {
						startFileError("There was a problem creating your container.", bundle);
					} else {
						startFileError("There was a problem creating your container: "
										+ cse.getMessage()
										+ " Check container name and try again", bundle);
					}
				}
			} else if (exception != null) {
				startFileError("There was a problem creating your container: "
								+ exception.getMessage()
								+ " Check container name and try again", bundle);
			}
		}
	}

	public class ChangeAttributesCDNTask extends AsyncTask<Void, Void, HttpBundle> {
		private CloudServersException exception;

		@Override
		protected HttpBundle doInBackground(Void... arg0) {
			HttpBundle bundle = null;
			try {
				bundle = (new ContainerManager(context)).disable(containerName,
						selectedCdnId, selectedTtlId, selectedLogRetId);
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
				if (statusCode == 202) {
					setResult(Activity.RESULT_OK);
					finish();
				} else {
					CloudServersException cse = parseCloudServersException(response);
					if ("".equals(cse.getMessage())) {
						startFileError("There was a problem creating your container.", bundle);
					} else {
						startFileError("There was a problem creating your container: "
										+ cse.getMessage()
										+ " Check container name and try again", bundle);
					}
				}
			} else if (exception != null) {
				startFileError("There was a problem creating your container: "
								+ exception.getMessage()
								+ " Check container name and try again", bundle);
			}
		}
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}