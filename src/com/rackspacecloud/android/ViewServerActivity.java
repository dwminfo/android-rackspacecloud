/**
 * 
 */
package com.rackspacecloud.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.rackspace.cloud.servers.api.client.Flavor;
import com.rackspace.cloud.servers.api.client.Image;
import com.rackspace.cloud.servers.api.client.Server;
import com.rackspace.cloud.servers.api.client.ServerManager;

/**
 * @author mike
 *
 */
public class ViewServerActivity extends Activity {
	private Image[] images;
	private Flavor[] flavors;
	private String selectedImageId;
	private String selectedFlavorId;
	private EditText serverName;
	private Spinner imageSpinner;
	private Spinner flavorSpinner;
	private Server server;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        server = (Server) this.getIntent().getExtras().get("server");
        setContentView(R.layout.viewserver);
        
        loadServerData();
        
        
        //serverName = (EditText) findViewById(R.id.server_name);
        //((Button) findViewById(R.id.save_button)).setOnClickListener(this);
        //loadImageSpinner();
        //loadFlavorSpinner();
    }

    private void loadServerData() {
    	TextView name = (TextView) findViewById(R.id.view_server_name);
    	name.setText(server.getName());
    	
    	TextView os = (TextView) findViewById(R.id.view_server_os);
    	os.setText(server.getImage().getName());
    	
    	TextView memory = (TextView) findViewById(R.id.view_server_memory);
    	memory.setText(server.getFlavor().getRam() + " MB");
    	
    	TextView disk = (TextView) findViewById(R.id.view_server_disk);
    	disk.setText(server.getFlavor().getDisk() + " GB");
    	
    	TextView status = (TextView) findViewById(R.id.view_server_status);
    	status.setText(server.getStatus());
    	
    	// public IPs
    	
    	// private IPs
    	
    	// actions
    	// Button softReboot = (Button) findViewById(R.id.view_server_soft_reboot_button);
    	
    	ImageView osLogo = (ImageView) findViewById(R.id.view_server_os_logo);
    	osLogo.setAlpha(100);
    	osLogo.setImageResource(server.getImage().logoResourceId());
    }
    
    /*
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if ("".equals(serverName.getText().toString())) {
			showAlert("Required Fields Missing", "Server name is required.");
		} else {
			showActivityIndicators();
			server = new Server();
			server.setName(serverName.getText().toString());
			server.setImageId(selectedImageId);
			server.setFlavorId(selectedFlavorId);
			new SaveServerTask().execute((Void[]) null);
		}
	}
	*/
	
	// TODO: extract to a util class?
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
	
    private void setActivityIndicatorsVisibility(int visibility) {
        ProgressBar pb = (ProgressBar) findViewById(R.id.save_server_progress_bar);
    	TextView tv = (TextView) findViewById(R.id.saving_server_label);
        pb.setVisibility(visibility);
        tv.setVisibility(visibility);
    }

    private void showActivityIndicators() {
    	setActivityIndicatorsVisibility(View.VISIBLE);
    }
    
    private void hideActivityIndicators() {
    	setActivityIndicatorsVisibility(View.INVISIBLE);
    }
        
    
    /**
	 * @return the server
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * @param server the server to set
	 */
	public void setServer(Server server) {
		this.server = server;
	}


	private class SaveServerTask extends AsyncTask<Void, Void, Server> {
    	
		@Override
		protected Server doInBackground(Void... arg0) {
			(new ServerManager()).create(server);
			return server;
		}
    	
		@Override
		protected void onPostExecute(Server result) {
			//setServerList(result);
			//this.
			hideActivityIndicators();
			System.out.println("done");
		}
    }
}
