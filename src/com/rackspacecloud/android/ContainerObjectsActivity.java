package com.rackspacecloud.android;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

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
import android.widget.ListView;
import android.widget.TextView;

import com.rackspace.cloud.files.api.client.Container;
import com.rackspace.cloud.files.api.client.ContainerManager;
import com.rackspace.cloud.files.api.client.ContainerObjectManager;
import com.rackspace.cloud.files.api.client.ContainerObjects;
import com.rackspace.cloud.servers.api.client.CloudServersException;
import com.rackspace.cloud.servers.api.client.parsers.CloudServersFaultXMLParser;

/**
 * 
 * @author Phillip Toohill
 * 
 */
public class ContainerObjectsActivity extends ListActivity {

	private static final int deleteContainer = 0;
	private ContainerObjects[] files;
	private static Container container;
	public String LOG = "viewFilesActivity";
	private String cdnEnabledIs;
	public Object megaBytes;
	public Object kiloBytes;
	public int bConver = 1048576;
	public int kbConver = 1024;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		container = (Container) this.getIntent().getExtras().get("container");
		Log.v(LOG, "CDNEnabled:" + container.isCdnEnabled());
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
	}

	private void restoreState(Bundle state) {
		if (state != null && state.containsKey("container")) {
			files = (ContainerObjects[]) state.getSerializable("container");
			if (files.length == 0) {
				displayNoServersCell();
			} else {
				getListView().setDividerHeight(1); // restore divider lines
				setListAdapter(new FileAdapter());

			}
		} else {
			loadFiles();

		}
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (files != null && files.length > 0) {
			Intent viewIntent = new Intent(this, ContainerObjectDetails.class);
			viewIntent.putExtra("container", files[position]);
			viewIntent.putExtra("cdnUrl", container.getCdnUrl());
			viewIntent.putExtra("containerNames", container.getName());
			viewIntent.putExtra("isCdnEnabled", cdnEnabledIs);
			startActivityForResult(viewIntent, 55); // arbitrary number; never
													// used again
		}
	}

	private void loadFiles() {
		displayLoadingCell();
		new LoadFilesTask().execute((Void[]) null);

	}

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

		if (fileNames.length == 0) {
			displayNoServersCell();
		} else {
			getListView().setDividerHeight(1); // restore divider lines
			setListAdapter(new FileAdapter());
		}
	}

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

	private void displayNoServersCell() {
		String a[] = new String[1];
		a[0] = "No Files";
		setListAdapter(new ArrayAdapter<String>(this, R.layout.noobjectscell,
				R.id.no_files_label, a));
		getListView().setTextFilterEnabled(true);
		getListView().setDividerHeight(0); // hide the dividers so it won't look
											// like a list row
		getListView().setItemsCanFocus(false);
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

	private class LoadFilesTask extends
			AsyncTask<Void, Void, ArrayList<ContainerObjects>> {

		private CloudServersException exception;

		@Override
		protected ArrayList<ContainerObjects> doInBackground(Void... arg0) {
			ArrayList<ContainerObjects> files = null;
			try {
				files = (new ContainerObjectManager()).createList(true,
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
		}
	}

	class FileAdapter extends ArrayAdapter<ContainerObjects> {
		FileAdapter() {
			super(ContainerObjectsActivity.this,
					R.layout.listcontainerobjectcell, files);
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			ContainerObjects file = files[position];
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.listcontainerobjectcell,
					parent, false);

			TextView label = (TextView) row.findViewById(R.id.label);
			label.setText(file.getCName());

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

	// Create the Menu options
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_container_object_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete_container:
			showDialog(deleteContainer);
			return true;
		case R.id.enable_cdn:
			Intent viewIntent1 = new Intent(this, EnableCDNActivity.class);
			viewIntent1.putExtra("Cname", container.getName());
			startActivityForResult(viewIntent1, 56);
			return true;
		case R.id.refresh:
			loadFiles();
			return true;
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case deleteContainer:
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
											.execute((Void[]) null);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			// a sub-activity kicked back, so we want to refresh the server list
			loadFiles();
		}
		if (requestCode == 56) {
			if (resultCode == RESULT_OK) {
				Intent viewIntent1 = new Intent(this,
						ListContainerActivity.class);
				startActivityForResult(viewIntent1, 56);
			}
		}
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

	private class DeleteContainerTask extends
			AsyncTask<Void, Void, HttpResponse> {

		private CloudServersException exception;

		@Override
		protected HttpResponse doInBackground(Void... arg0) {
			HttpResponse resp = null;
			try {
				resp = (new ContainerManager()).delete(container.getName());
				Log.v(LOG, "container's name " + container.getName());
			} catch (CloudServersException e) {
				exception = e;
			}
			return resp;
		}

		@Override
		protected void onPostExecute(HttpResponse response) {
			if (response != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 409) {
					showAlert("Error",
							"Container must be empty in order to delete");
				}
				if (statusCode == 204) {
					setResult(Activity.RESULT_OK);
					finish();

				} else {
					CloudServersException cse = parseCloudServersException(response);
					if ("".equals(cse.getMessage())) {
						showAlert("Error",
								"There was a problem deleting your container.");
					} else {
						showAlert("Error",
								"There was a problem deleting your container: "
										+ cse.getMessage());
					}
				}
			} else if (exception != null) {
				showAlert("Error", "There was a problem deleting your server: "
						+ exception.getMessage());
			}
		}
	}

}
