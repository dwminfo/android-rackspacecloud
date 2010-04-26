/**
 * 
 */
package com.rackspacecloud.android;

import java.util.ArrayList;

import com.rackspace.cloud.servers.api.client.EntityList;
import com.rackspace.cloud.servers.api.client.Server;
import com.rackspace.cloud.servers.api.client.ServerManager;
import com.rackspace.cloud.servers.api.client.http.Authentication;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author mike
 *
 */
public class ListServersActivity extends ListActivity {

	private Server[] servers;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadServers();        
    }

	
    protected void onListItemClick(ListView l, View v, int position, long id) {	
    	//startActivity(new Intent(this, ViewSliceActivity.class));
    }
    
    private void loadServers() {
    	displayLoadingCell();
    	new LoadServersTask().execute((Void[]) null);
    }
    
    private void setServerList(ArrayList<Server> servers) {
    	String[] serverNames = new String[servers.size()];
    	this.servers = new Server[servers.size()];
    	
		if (servers != null) {
			for (int i = 0; i < servers.size(); i++) {
				Server server = servers.get(i);
				this.servers[i] = server;
				serverNames[i] = server.getName();
			}
		}
		
		// TODO: throws ClassCastException with empty list
		if (serverNames.length == 0) {
			displayNoServersCell();
		} else {
			
			getListView().setDividerHeight(1); // restore divider linesan 
			//setListAdapter(new ArrayAdapter<String>(this, R.layout.listservercell, R.id.label, serverNames));
			setListAdapter(new ServerAdapter());
		}
    }
    
    private void displayLoadingCell() {
    	String a[] = new String[1];
    	a[0] = "Loading...";
        setListAdapter(new ArrayAdapter<String>(this, R.layout.loadingcell, R.id.loading_label, a));
        getListView().setTextFilterEnabled(true);
        getListView().setDividerHeight(0); // hide the dividers so it won't look like a list row
        getListView().setItemsCanFocus(false);
        //getListView().setClickable(false); // TODO: so it will never be highlighted on touch
    }
    
    private void displayNoServersCell() {
    	String a[] = new String[1];
    	a[0] = "No Servers";
        setListAdapter(new ArrayAdapter<String>(this, R.layout.noserverscell, R.id.no_servers_label, a));
        getListView().setTextFilterEnabled(true);
        getListView().setDividerHeight(0); // hide the dividers so it won't look like a list row
        //getListView().setClickable(false); // so it will never be highlighted on touch
        getListView().setItemsCanFocus(false);
    }
    
    private class LoadServersTask extends AsyncTask<Void, Void, ArrayList<Server>> {
    	
		@Override
		protected ArrayList<Server> doInBackground(Void... arg0) {
			return (new ServerManager()).createList(true);
		}
    	
		@Override
		protected void onPostExecute(ArrayList<Server> result) {
			setServerList(result);
		}
    }
    
    
    //public void setList() {
    	//guiThread.post(new RefreshSlicesListTask(this));
    //}
        
    /*
    @Override 
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.slices_list_menu, menu);
		return true;
	} 
    
    @Override 
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
			// More items go here (if any) ...
		case R.id.refresh:
			Slicehost.setSlices(null);
			initThreading();
	        guiThread.post(updateTask);        
	        displayLoadingCell();
	        return true;
		case R.id.dns_menu_add_zone:
			startActivity(new Intent(this, AddZoneActivity.class));
			return true;
			// More items go here (if any) ...
		}
		return false;
	} 
	*/
    
	class ServerAdapter extends ArrayAdapter<Server> {
		ServerAdapter() {
			super(ListServersActivity.this, R.layout.listservercell, servers);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			
			Server server = servers[position];
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.listservercell, parent, false);

			TextView label = (TextView) row.findViewById(R.id.label);
			label.setText(server.getName());
			
			TextView sublabel = (TextView) row.findViewById(R.id.sublabel);
			sublabel.setText(server.getFlavorId() + " - " + server.getImageId());
			
			ImageView icon = (ImageView) row.findViewById(R.id.icon);
			if (position == 0) {
				icon.setImageResource(R.drawable.ubuntu_icon);
			} else {
				icon.setImageResource(R.drawable.centos_icon);
			}
			return(row);
		}
	}
    
}
