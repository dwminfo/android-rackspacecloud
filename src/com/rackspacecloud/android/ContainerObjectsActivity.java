package com.rackspacecloud.android;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

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
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.rackspace.cloud.files.api.client.Container;
import com.rackspace.cloud.files.api.client.ContainerManager;
import com.rackspace.cloud.files.api.client.ContainerObjectManager;
import com.rackspace.cloud.files.api.client.ContainerObjects;
import com.rackspace.cloud.servers.api.client.CloudServersException;
import com.rackspace.cloud.servers.api.client.http.HttpBundle;
import com.rackspace.cloud.servers.api.client.parsers.CloudServersFaultXMLParser;

/**
 * 
 * @author Phillip Toohill
 * 
 */
public class ContainerObjectsActivity extends ListActivity {

	private static final int deleteContainer = 0;
	private static final int deleteFolder = 1;
	private ContainerObjects[] files;
	private static Container container;
	public String LOG = "viewFilesActivity";
	private String cdnEnabledIs;
	public Object megaBytes;
	public Object kiloBytes;
	public int bConver = 1048576;
	public int kbConver = 1024;
	private Context context;
	private String currentPath;
	private ArrayList<ContainerObjects> curDirFiles;
	//private ProgressDialog dialog;
	private boolean loadingFiles;
	private FileAdapter adapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		container = (Container) this.getIntent().getExtras().get("container");
		Log.v(LOG, "CDNEnabled:" + container.isCdnEnabled());
        context = getApplicationContext();
		if (container.isCdnEnabled() == true) {
			cdnEnabledIs = "true";
		} else {
			cdnEnabledIs = "false";
		}
		restoreState(savedInstanceState);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("container", files);
		outState.putString("path", currentPath);
		//outState.putSerializable("curFiles", curDirFiles);
		outState.putBoolean("loadingFiles", loadingFiles);
	}
	
	

	private void restoreState(Bundle state) {
		
		if(state != null){
			if(state.containsKey("path")){
				currentPath = state.getString("path");
			}
			else{
				currentPath = "";
			}
			
			if(state.containsKey("loadingFiles") && state.getBoolean("loadingFiles")){
				loadFiles();
			}
			else{
				if(state.containsKey("container")){// && state.containsKey("curFiles")){
					files = (ContainerObjects[]) state.getSerializable("container");
					//curDirFiles = (ArrayList<ContainerObjects>) state.getSerializable("curFiles");
					
					ArrayList<Object> container = (ArrayList<Object>) getLastNonConfigurationInstance();
					adapter = (FileAdapter)container.get(0);
					curDirFiles = (ArrayList<ContainerObjects>)container.get(1);
					if(adapter == null){
						adapter = new FileAdapter();
					}
					setListAdapter(adapter);
				}
			}
		}
		else {
			currentPath = "";
			loadFiles();
		}	
		
		
	}
	
	public Object onRetainNonConfigurationInstance(){
		ArrayList<Object> container = new ArrayList<Object>();
		container.add(adapter);
		container.add(curDirFiles);
		return adapter;
	}

	/*
	 * overriding back button press, because we are not actually changing
	 * activities when we navigate the file structure
	 */
	public void onBackPressed() {
		if(currentPath.equals("")){
			finish();
		}
		else{
			goUpDirectory();
		}
	}
	
	/*
	 * go to the current directory's parent and display that data
	 */
	private void goUpDirectory(){
		currentPath = currentPath.substring(0, currentPath.substring(0, currentPath.length()-2).lastIndexOf("/")+1);
		displayCurrentFiles();
	}

	/*
	 * load all file that are in the container
	 */
	private void loadFiles() {
		//displayLoadingCell();
		new LoadFilesTask().execute();
	}
	
	/*
	private void displayLoadingCell() {
		String a[] = new String[1];
		a[0] = "Loading...";
		setListAdapter(new ArrayAdapter<String>(this, R.layout.loadingcell,
				R.id.loading_label, a));
		getListView().setTextFilterEnabled(true);
		getListView().setDividerHeight(0); // hide the dividers so it won't look
											// like a list row
		getListView().setItemsCanFocus(false);
	}
	*/
	
	/* load only the files that should display for the 
	 * current directory in the curDirFiles[]
	 */
	private void loadCurrentDirectoryFiles(){
		ArrayList<ContainerObjects> curFiles = new ArrayList<ContainerObjects>();

		if(files != null){
			for(int i = 0 ; i < files.length; i ++){
				if(fileBelongsInDir(files[i])){
					curFiles.add(files[i]);
				}
			}

			curDirFiles = new ArrayList<ContainerObjects>();
			for(int i = 0; i < curFiles.size(); i++){
				curDirFiles.add(i, curFiles.get(i));
			}
		}
	}
	
	/*
	 * determines if a file should be displayed in current 
	 * directory
	 */
	private Boolean fileBelongsInDir(ContainerObjects obj){
		String objPath = obj.getCName();
		if(!objPath.startsWith(currentPath)){
			return false;
		}
		else{
			objPath = objPath.substring(currentPath.length());
			return !objPath.contains("/");
		}
	}

	
	/*
	 * loads all the files that are in the container
	 * into one array
	 */
	private void setFileList(ArrayList<ContainerObjects> files) {
		if (files == null) {
			files = new ArrayList<ContainerObjects>();
		}
		String[] fileNames = new String[files.size()];
		this.files = new ContainerObjects[files.size()];

		
		
		if (files != null) {
			for (int i = 0; i < files.size(); i++) {
				ContainerObjects file = files.get(i);
				this.files[i] = file;
				fileNames[i] = file.getName();
			}
		}
	}
	
	private void displayCurrentFiles(){
		Log.d("info", "captin, display current files");
		Log.d("info", "the size of curdirfiles is1 " + curDirFiles.size());
		if (curDirFiles.size() == 0) {
			displayNoServersCell();
		} else {
			Log.d("info", "captin displaying curfiles");
			if(adapter == null){
				adapter = new FileAdapter();
			}
			ArrayList<ContainerObjects> tempList = new ArrayList<ContainerObjects>();
			for(int i = 0; i < curDirFiles.size(); i++){
				tempList.add(curDirFiles.get(i));
			}
			
			adapter.clear();
			for(int i = 0; i < tempList.size(); i++){
				adapter.add(tempList.get(i));
				Log.d("info", "the count is: " + adapter.getCount());
			}
			setListAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}

	/*
	 * display a different empty page depending
	 * of if you are at top of container or
	 * in a folder
	 */
	
	private void displayNoServersCell() {
		Log.d("info", "captin no files to display");
		/*
		String a[] = new String[1];
		if(currentPath.equals("")){
			a[0] = "Empty Container";
			setListAdapter(new ArrayAdapter<String>(this, R.layout.noobjectscell,
				R.id.no_files_label, a));
		}
		else{
			a[0] = "No Files";
			setListAdapter(new ArrayAdapter<String>(this, R.layout.nofilescell,
						R.id.no_files_label, a));
		}
		getListView().setTextFilterEnabled(true);
		getListView().setDividerHeight(0); // hide the dividers so it won't look
											// like a list row
		getListView().setItemsCanFocus(false);
		*/
	}
	

	private void showAlert(String title, String message) {
		// Can't create handler inside thread that has not called
		// Looper.prepare()
		// Looper.prepare();
		try {
			AlertDialog alert = new AlertDialog.Builder(this).create();
			alert.setTitle(title);
			alert.setMessage(message);
			alert.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* just get the last part of the filename
	 * so the entire file path does not show
	*/
	private String getShortName(String longName){
		String s = longName;
		if(!s.contains("/")){
			return s;
		}
		else {
			return s.substring(s.lastIndexOf('/')+1);
		}
	}
	
	/*
	 * removed a specified object from the array of 
	 * all the files in the container
	 */
	private void removeFromList(String path){
		ArrayList<ContainerObjects> temp = new ArrayList<ContainerObjects>(Arrays.asList(files));
		for(int i = 0; i < files.length; i++){
			if(files[i].getCName().equals(path.substring(0, path.length()-1))){
				temp.remove(i);
			}
		}
		files = new ContainerObjects[temp.size()];
		for(int i = 0; i < temp.size(); i++){
			files[i] = temp.get(i);
		}
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (curDirFiles != null && curDirFiles.size() > 0) {
			Intent viewIntent;
			if(curDirFiles.get(position).getContentType().equals("application/directory")){			
				currentPath = curDirFiles.get(position).getCName() + "/";
				displayCurrentFiles();
			}
	
			else{
				viewIntent = new Intent(this, ContainerObjectDetails.class);
				viewIntent.putExtra("container", curDirFiles.get(position));
				viewIntent.putExtra("cdnUrl", container.getCdnUrl());
				viewIntent.putExtra("containerNames", container.getName());
				viewIntent.putExtra("isCdnEnabled", cdnEnabledIs);
				startActivityForResult(viewIntent, 55); // arbitrary number; never
				// used again
			}
		}
	}

	/* 
	 * Create the Menu options
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_container_object_list_menu, menu);
		return true;
	}

	@Override
	/*
	 * option performed for delete depends on if you
	 * are at the top of a container or in a folder
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete_container:
			if(currentPath.equals("")){
				showDialog(deleteContainer);
			}
			else{
				showDialog(deleteFolder);
			}
			return true;
		case R.id.enable_cdn:
			Intent viewIntent1 = new Intent(this, EnableCDNActivity.class);
			viewIntent1.putExtra("Cname", container.getName());
			startActivityForResult(viewIntent1, 56);
			return true;
		case R.id.refresh:
			loadFiles();
			return true;
		case R.id.add_folder:
			showDialog(R.id.add_folder);
			return true;
		case R.id.add_file:
			Intent viewIntent2 = new Intent(this, AddFileActivity.class);
			viewIntent2.putExtra("Cname", container.getName());
			viewIntent2.putExtra("curPath", currentPath);
			startActivityForResult(viewIntent2, 56);
			return true;	
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case deleteContainer:
			if(curDirFiles.size() == 0){
				return new AlertDialog.Builder(ContainerObjectsActivity.this)
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle("Delete Container")
				.setMessage(
				"Are you sure you want to delete this Container?")
				.setPositiveButton("Delete Container",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						// User clicked OK so do some stuff
						new DeleteContainerTask()
						.execute(currentPath);
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
			else{
				return new AlertDialog.Builder(ContainerObjectsActivity.this)
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle("Delete Container")
				.setMessage("Container must be empty to delete")
				.setNegativeButton("OK",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						// User clicked Cancel so do some stuff
					}
				}).create();
			}
		case deleteFolder:
			if(curDirFiles.size() == 0){
				return new AlertDialog.Builder(ContainerObjectsActivity.this)
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle("Delete Folder")
				.setMessage(
				"Are you sure you want to delete this Folder?")
				.setPositiveButton("Delete Folder",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						// User clicked OK so do some stuff
						new DeleteObjectTask().execute();
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
			else{
				return new AlertDialog.Builder(ContainerObjectsActivity.this)
				.setIcon(R.drawable.alert_dialog_icon)
				.setTitle("Delete Folder")
				.setMessage(
				"Folder must be empty to delete")
				.setNegativeButton("OK",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						// User clicked Cancel so do some stuff
					}
				}).create();
			}
		case R.id.add_folder:
			final EditText input = new EditText(this);
            return new AlertDialog.Builder(ContainerObjectsActivity.this)
        	.setIcon(R.drawable.alert_dialog_icon)
            .setView(input)
        	.setTitle("Add Folder")
        	.setMessage("Enter new name for folder: ")        	         
        	.setPositiveButton("Add", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int whichButton) {
        			//User clicked OK so do some stuff
        			String[] info = {input.getText().toString(), "application/directory"};
        			new AddFolderTask().execute(info);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			// a sub-activity kicked back, so we want to refresh the server list
			loadFiles();
		}
		/*
		if (requestCode == 56) {
			if (resultCode == RESULT_OK) {
				Intent viewIntent1 = new Intent(this,
						ListContainerActivity.class);
				startActivityForResult(viewIntent1, 56);
			}
		}
		*/
	}

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
	
	class FileAdapter extends ArrayAdapter<ContainerObjects> {
		FileAdapter() {
			super(ContainerObjectsActivity.this,
					R.layout.listcontainerobjectcell, curDirFiles);	
		}
	
		public View getView(int position, View convertView, ViewGroup parent) {
			
			Log.d("info", "at position " + position + " with length " + curDirFiles.size());
			ContainerObjects file = curDirFiles.get(position);
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.listcontainerobjectcell,
					parent, false);
	
			TextView label = (TextView) row.findViewById(R.id.label);
			//label.setText(file.getCName());
			label.setText(getShortName(file.getCName()));
	
			if (file.getBytes() >= bConver) {
				megaBytes = Math.abs(file.getBytes() / bConver + 0.2);
				TextView sublabel = (TextView) row.findViewById(R.id.sublabel);
				sublabel.setText(megaBytes + " MB");
			} else if (file.getBytes() >= kbConver) {
				kiloBytes = Math.abs(file.getBytes() / kbConver + 0.2);
				TextView sublabel = (TextView) row.findViewById(R.id.sublabel);
				sublabel.setText(kiloBytes + " KB");
			} else {
				TextView sublabel = (TextView) row.findViewById(R.id.sublabel);
				sublabel.setText(file.getBytes() + " B");
			}
	
			return (row);
		}
	}

	private class LoadFilesTask extends
			AsyncTask<String, Void, ArrayList<ContainerObjects>> {

		private CloudServersException exception;
		protected void onPreExecute(){
			loadingFiles = true;
		}
		
		@Override
		protected ArrayList<ContainerObjects> doInBackground(String... path) {
			ArrayList<ContainerObjects> files = null;
			try {
				files = (new ContainerObjectManager(context)).createList(true,
						container.getName());
			} catch (CloudServersException e) {
				exception = e;
				e.printStackTrace();
			}
			return files;
		}
	
		@Override
		protected void onPostExecute(ArrayList<ContainerObjects> result) {
			if (exception != null) {
				showAlert("Error", exception.getMessage());
			}
			
			setFileList(result);
			loadCurrentDirectoryFiles();
			displayCurrentFiles();
			loadingFiles = false;
		}

	}

	private class DeleteObjectTask extends
	AsyncTask<Void, Void, HttpBundle> {

		private CloudServersException exception;
		
		protected void onPreExecute(){
			//dialog = ProgressDialog.show(ContainerObjectsActivity.this, "", "Deleting...", true);
		}
		
		@Override
		protected HttpBundle doInBackground(Void... arg0) {
			HttpBundle bundle = null;
			try {
				//subtring because the current directory contains a "/" at the end of the string
				bundle = (new ContainerObjectManager(context)).deleteObject(container.getName(), currentPath.substring(0, currentPath.length()-1));
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
				if (statusCode == 409) {
					showAlert("Error",
					"Folder must be empty in order to delete");
				}
				if (statusCode == 204) {
					setResult(Activity.RESULT_OK);
					removeFromList(currentPath);
					goUpDirectory();
					
				} else {
					CloudServersException cse = parseCloudServersException(response);
					if ("".equals(cse.getMessage())) {
						startFileError("There was a problem deleting your folder.", bundle);
					} else {
						startFileError("There was a problem deleting your folder: "
									+ cse.getMessage(), bundle);
					}
				}
			} else if (exception != null) {
				startFileError("There was a problem deleting your folder: "
						+ exception.getMessage(), bundle);
			}
		}
	}
	
	private class AddFolderTask extends
	AsyncTask<String, Void, HttpBundle> {
	
		private CloudServersException exception;
		
		@Override
		protected HttpBundle doInBackground(String... data) {
			HttpBundle bundle = null;
			try {
				bundle = (new ContainerObjectManager(context)).addObject(container.getName(), currentPath, data[0], data[1]);
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
					loadFiles();
				} else {
					CloudServersException cse = parseCloudServersException(response);
					if ("".equals(cse.getMessage())) {
						startFileError("There was a problem deleting your folder.", bundle);
					} else {
						startFileError("There was a problem deleting your folder: "
									+ cse.getMessage(), bundle);
					}
				}
			} else if (exception != null) {
				startFileError("There was a problem deleting your folder: "
						+ exception.getMessage(), bundle);
			}
		}
	}

	private class DeleteContainerTask extends
	AsyncTask<String, Void, HttpBundle> {

		private CloudServersException exception;

		@Override
		protected HttpBundle doInBackground(String... object) {
			HttpBundle bundle = null;
			try {
				bundle = (new ContainerManager(context)).delete(container.getName());
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
				if (statusCode == 409) {
					startFileError("Container must be empty in order to delete", bundle);
				}
				if (statusCode == 204) {
					setResult(Activity.RESULT_OK);
					loadFiles();
					finish();

				} else {
					CloudServersException cse = parseCloudServersException(response);
					if ("".equals(cse.getMessage())) {
						startFileError("There was a problem deleting your container.", bundle);
					} else {
						startFileError("There was a problem deleting your container: "
									+ cse.getMessage(), bundle);
					}
				}
			} else if (exception != null) {
				startFileError("There was a problem deleting your server: "
						+ exception.getMessage(), bundle);
			}
		}
	}

}
