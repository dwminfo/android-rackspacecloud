package com.rackspacecloud.android;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rackspace.cloud.files.api.client.ContainerObjectManager;
import com.rackspace.cloud.files.api.client.ContainerObjects;
import com.rackspace.cloud.servers.api.client.CloudServersException;
import com.rackspace.cloud.servers.api.client.parsers.CloudServersFaultXMLParser;


/** 
 * 
 * @author Phillip Toohill
 *
 */

public class ContainerObjectDetails extends Activity {
	
	private static final int deleteObject = 0;
	private ContainerObjects objects;
	private String containerNames;
	private String cdnURL;
	private String cdnEnabled;
	public String LOG = "ViewObject";
	private int bConver = 1048576;
	private int kbConver = 1024;
	private double megaBytes;
	private double kiloBytes;
	public Button previewButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        objects = (ContainerObjects) this.getIntent().getExtras().get("container");
        containerNames =  (String) this.getIntent().getExtras().get("containerNames");
        cdnURL = (String) this.getIntent().getExtras().get("cdnUrl");
        cdnEnabled = (String) this.getIntent().getExtras().get("isCdnEnabled");
        

        setContentView(R.layout.viewobject);       
        restoreState(savedInstanceState);
        
    }
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("container", objects);
	}

    private void restoreState(Bundle state) {
    	if (state != null && state.containsKey("container")) {
    		objects = (ContainerObjects) state.getSerializable("container");
    	}
        loadObjectData();
        
        if ( cdnEnabled.equals("true"))  {
        	this.previewButton = (Button) findViewById(R.id.preview_button);
        	previewButton.setOnClickListener(new MyOnClickListener());
        } else {
        	this.previewButton = (Button) findViewById(R.id.preview_button);
        	previewButton.setVisibility(View.GONE);
      }
        
    }
   
    

    private void loadObjectData() {
    	//Object Name
    	TextView name = (TextView) findViewById(R.id.view_container_name);
    	name.setText(objects.getCName().toString());
    	
    	//File size
    	if (objects.getBytes() >= bConver) {
			megaBytes = Math.abs(objects.getBytes()/bConver + 0.2);
				TextView sublabel = (TextView) findViewById(R.id.view_file_bytes);
				sublabel.setText(megaBytes + " MB");
		} else if (objects.getBytes() >= kbConver){
			kiloBytes = Math.abs(objects.getBytes()/kbConver + 0.2);
				TextView sublabel = (TextView) findViewById(R.id.view_file_bytes);
				sublabel.setText(kiloBytes + " KB");
		} else {
				TextView sublabel = (TextView) findViewById(R.id.view_file_bytes);
				sublabel.setText(objects.getBytes() + " B");
		}	
    	
    	//Content Type
    	TextView cType = (TextView) findViewById(R.id.view_content_type);
    	cType.setText(objects.getContentType().toString());
        
    	//Last Modification date
        String strDate = objects.getLastMod();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.ssssss");
        Date dateStr = null;
			try {
				dateStr = formatter.parse(strDate);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
        String formattedDate = formatter.format(dateStr);
        Date date1 = null;
			try {
				date1 = formatter.parse(formattedDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}      
        formatter = new SimpleDateFormat("MMM-dd-yyyy");
        formattedDate = formatter.format(date1);
    	TextView lastmod = (TextView) findViewById(R.id.view_file_modification);
    	lastmod.setText(formattedDate);    	  
    	    	
    }
    
    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
        	Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(cdnURL + "/" + objects.getCName()));
        	startActivity(viewIntent);  

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
  //Create the Menu options
    @Override 
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.container_object_list_menu, menu);
		return true;
	} 
    
    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete_object:
			showDialog(deleteObject); 
			return true;
		case R.id.refresh:
			loadObjectData();
	        return true;
		}
		return false;
	} 
    
    @Override
    protected Dialog onCreateDialog(int id ) {
    	switch (id) {
			case deleteObject:
            return new AlertDialog.Builder(ContainerObjectDetails.this)
        	.setIcon(R.drawable.alert_dialog_icon)
        	.setTitle("Delete File")
        	.setMessage("Are you sure you want to delete this file?")
        	.setPositiveButton("Delete File", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int whichButton) {
        			// User clicked OK so do some stuff
        			new ContainerObjectDeleteTask().execute((Void[]) null);
        		}
        	})
        	.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int whichButton) {
        			// User clicked Cancel so do some stuff
        		}
        	})
        	.create();
        }
        return null;
    }
    /**
	 * @return the file
	 */
	public ContainerObjects getViewFile() {
		return objects;
	}

	/**
	 * @param File the file to set
	 */
	public void setViewFile(ContainerObjects object) {
		this.objects = object;
	}
	
	//Task's
	    	
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
	    	 
	    	 private class ContainerObjectDeleteTask extends AsyncTask<Void, Void, HttpResponse> {
	    	    	
	    			private CloudServersException exception;

	    			@Override
	    			protected HttpResponse doInBackground(Void... arg0) {
	    				HttpResponse resp = null;
	    				try {
	    					resp = (new ContainerObjectManager()).deleteObject(containerNames, objects.getCName() );
	    					Log.v(LOG, "container name " + objects.getCName() + " " + containerNames);
	    				} catch (CloudServersException e) {
	    					exception = e;
	    				}
	    				return resp;
	    			}
	    	    	
	    			@Override
	    			protected void onPostExecute(HttpResponse response) {
	    				if (response != null) {
	    					int statusCode = response.getStatusLine().getStatusCode();
	    					if (statusCode == 204) {
	    						setResult(Activity.RESULT_OK);
	    						finish();
	    						
	    					} else {
	    						CloudServersException cse = parseCloudServersException(response);
	    						if ("".equals(cse.getMessage())) {
	    							showAlert("Error", "There was a problem deleting your File.");
	    						} else {
	    							showAlert("Error", "There was a problem deleting your file: " + cse.getMessage());
	    						}
	    					}
	    				} else if (exception != null) {
	    					showAlert("Error", "There was a problem deleting your file: " + exception.getMessage());				
	    				}			
	    			}
	    	    }
	
  }
