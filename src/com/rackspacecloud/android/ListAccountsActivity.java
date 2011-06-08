package com.rackspacecloud.android;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.TreeMap;

import com.rackspace.cloud.servers.api.client.Account;
import com.rackspace.cloud.servers.api.client.Flavor;
import com.rackspace.cloud.servers.api.client.FlavorManager;
import com.rackspace.cloud.servers.api.client.Image;
import com.rackspace.cloud.servers.api.client.ImageManager;
import com.rackspace.cloud.servers.api.client.http.Authentication;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ListAccountsActivity extends ListActivity{

	private ArrayList<Account> accounts;
	private final String FILENAME = "accounts.data";
	private Intent tabViewIntent;
	private boolean authenticating;
	ProgressDialog dialog;
	Context context;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreState(savedInstanceState);
        registerForContextMenu(getListView());
        context = getApplicationContext();
        tabViewIntent = new Intent(this, TabViewActivity.class);
    }
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("authenticating", authenticating);
		writeAccounts();
	}
	
	private void restoreState(Bundle state) {
		if (state != null && state.containsKey("authenticating") && state.getBoolean("authenticating")) {
    		showActivityIndicators();
    	} else {
    		hideActivityIndicators();
    	}
		if (state != null && state.containsKey("accounts")) {
    		accounts = readAccounts();
    		if (accounts.size() == 0) {
    			displayNoAccountsCell();
    		} else {
    			getListView().setDividerHeight(1); // restore divider lines 
    			setListAdapter(new AccountAdapter());
    		}
    	} else {
            loadAccounts();        
    	} 	
    }
	
	private void loadAccounts() {
		if(accounts != null)
			Log.d("loadAccounts", "captin the lenght is: " + accounts.size());
		//check and see if there are any in memory
		if(accounts == null){
			accounts = readAccounts();
		}
		//if nothing was written before accounts will still be null
		if(accounts == null){
			accounts = new ArrayList<Account>();
		}
		Log.d("loadAccounts2", "captin the lenght is: " + accounts.size());

		setAccountList();
	}

	private void setAccountList() {
	
		if (accounts.size() == 0) {
			displayNoAccountsCell();
		} else {
			getListView().setDividerHeight(1); // restore divider lines 
			this.setListAdapter(new AccountAdapter());
		}
	}

	private void writeAccounts(){
		FileOutputStream fos;
		ObjectOutputStream out = null;
		try{
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			out = new ObjectOutputStream(fos);
			out.writeObject(accounts);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ArrayList<Account> readAccounts(){
		FileInputStream fis;
		ObjectInputStream in;
		try {
			fis = openFileInput(FILENAME);
			in = new ObjectInputStream(fis);
			ArrayList<Account> file = (ArrayList<Account>)in.readObject();
			in.close();
			Log.d("captin", Boolean.toString(file == null));
			return file;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

	private void displayNoAccountsCell() {
    	String a[] = new String[1];
    	a[0] = "No Accounts";
        setListAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.noaccountscell, R.id.no_accounts_label, a));
        getListView().setTextFilterEnabled(true);
        getListView().setDividerHeight(0); // hide the dividers so it won't look like a list row
        getListView().setItemsCanFocus(false);
    }
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (accounts != null && accounts.size() > 0) {
			setActivityIndicatorsVisibility(View.VISIBLE, v);
			Account.setAccount(accounts.get(position));
			login();
		}		
    }
	
	public void login() {
        //showActivityIndicators();
        //setLoginPreferences();
		dialog = ProgressDialog.show(ListAccountsActivity.this, "", "Authenticating...", true);
        new AuthenticateTask().execute((Void[]) null);
    }
	
	//setup menu for when menu button is pressed
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.accounts_list_menu, menu);
		return true;
	} 
    
    @Override 
    //in options menu, when add account is selected go to add account activity
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_account:
			startActivityForResult(new Intent(this, AddAccountActivity.class), 78); // arbitrary number; never used again
			return true;
		}
		return false;
	} 

    //the context menu for a long press on an account
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.account_context_menu, menu);
	}

	//removes the selected account from account list if remove is clicked
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		accounts.remove(info.position);
		writeAccounts();
		loadAccounts();
		return true;
	}

	class AccountAdapter extends ArrayAdapter<Account> {

		AccountAdapter() {
			super(ListAccountsActivity.this, R.layout.listaccountcell, accounts);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.listaccountcell, parent, false);

			TextView label = (TextView) row.findViewById(R.id.label);
			label.setText(accounts.get(position).getUsername());
			
			TextView sublabel = (TextView) row.findViewById(R.id.sublabel);
			sublabel.setText(getAccountServer(accounts.get(position)));
			
			ImageView icon = (ImageView) row.findViewById(R.id.account_type_icon);
			icon.setImageResource(R.drawable.rackspace60);
			
			return row;
		}
	}
	
	public String getAccountServer(Account account){
		String authServer = account.getAuthServer();
		String result = "Rackspace Cloud";
		if(authServer.contains("lon")){
			result += " (UK)";
		}
		else{
			result += " (US)";
		}
		return result;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK && requestCode == 78) {	  
			Account acc = new Account();
			Bundle b = data.getBundleExtra("accountInfo");
			acc.setApiKey(b.getString("apiKey"));
			acc.setUsername(b.getString("username"));
			acc.setAuthServer(b.getString("server"));
			accounts.add(acc);
			writeAccounts();
			loadAccounts();
		}
	}	

	private void setActivityIndicatorsVisibility(int visibility) {
		//FINISH THIS TO LET USER KNOW PROGRAM IS STILL WORKING
		
        //ProgressBar pb = new ProgressBar();
    	//TextView tv = (TextView) findViewById(R.id.login_authenticating_label);
        //pb.setVisibility(visibility);
        //tv.setVisibility(visibility);
    }
	
	private void setActivityIndicatorsVisibility(int visibility, View v) {
		//FINISH THIS TO LET USER KNOW PROGRAM IS STILL WORKING
		
        //ProgressBar pb = new ProgressBar();
    	//TextView tv = (TextView) findViewById(R.id.login_authenticating_label);
        //pb.setVisibility(visibility);
        //tv.setVisibility(visibility);
    }
	
	private void showActivityIndicators() {
    	setActivityIndicatorsVisibility(View.VISIBLE);
    }
    
    private void hideActivityIndicators() {
    	setActivityIndicatorsVisibility(View.INVISIBLE);
    }
	
	private class AuthenticateTask extends AsyncTask<Void, Void, Boolean> {
    	
		@Override
		protected Boolean doInBackground(Void... arg0) {

			authenticating = true;
			return new Boolean(Authentication.authenticate(context));
			//return true;
		}
    	
		@Override
		protected void onPostExecute(Boolean result) {
			authenticating = false;
			if (result.booleanValue()) {
				//startActivity(tabViewIntent);
	        	new LoadImagesTask().execute((Void[]) null);				
			} else {
				dialog.dismiss();
				showAlert("Login Failure", "Authentication failed.  Please check your User Name and API Key.");
			}
		}
    }

    private class LoadFlavorsTask extends AsyncTask<Void, Void, ArrayList<Flavor>> {
    	
		@Override
		protected ArrayList<Flavor> doInBackground(Void... arg0) {
			Log.d("auth", "task2");
			return (new FlavorManager()).createList(true, context);
		}
    	
		@Override
		protected void onPostExecute(ArrayList<Flavor> result) {
			if (result != null && result.size() > 0) {
				TreeMap<String, Flavor> flavorMap = new TreeMap<String, Flavor>();
				for (int i = 0; i < result.size(); i++) {
					Flavor flavor = result.get(i);
					flavorMap.put(flavor.getId(), flavor);
				}
				Flavor.setFlavors(flavorMap);
				dialog.dismiss();
				startActivity(tabViewIntent);
			} else {
				dialog.dismiss();
				showAlert("Login Failure", "There was a problem loading server flavors.  Please try again.");
			}
			hideActivityIndicators();
		}
    }

    private class LoadImagesTask extends AsyncTask<Void, Void, ArrayList<Image>> {
    	
		@Override
		protected ArrayList<Image> doInBackground(Void... arg0) {
			Log.d("auth", "task3");
			return (new ImageManager()).createList(true, context);
		}
    	
		@Override
		protected void onPostExecute(ArrayList<Image> result) {
			if (result != null && result.size() > 0) {
				TreeMap<String, Image> imageMap = new TreeMap<String, Image>();
				for (int i = 0; i < result.size(); i++) {
					Image image = result.get(i);
					imageMap.put(image.getId(), image);
				}
				Image.setImages(imageMap);
				new LoadFlavorsTask().execute((Void[]) null);
				//startActivity(tabViewIntent);
			} else {
				dialog.dismiss();
				showAlert("Login Failure", "There was a problem loading server images.  Please try again.");
			}
			hideActivityIndicators();
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
		hideActivityIndicators();
    }
	
		
}
