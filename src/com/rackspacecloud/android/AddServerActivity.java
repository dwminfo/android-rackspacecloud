/**
 * 
 */
package com.rackspacecloud.android;

import java.util.Iterator;

import com.rackspace.cloud.servers.api.client.Flavor;
import com.rackspace.cloud.servers.api.client.Image;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * @author mike
 *
 */
public class AddServerActivity extends Activity {

	private Image[] images;
	private Flavor[] flavors;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createserver);
        loadImageSpinner();
        loadFlavorSpinner();
    }

    private void loadImageSpinner() {
		Spinner imageSpinner = (Spinner) findViewById(R.id.image_spinner);
		//imageSpinner.setOnItemSelectedListener(this);
		String imageNames[] = new String[Image.getImages().size()]; 
		images = new Image[Image.getImages().size()];

		Iterator<Image> iter = Image.getImages().values().iterator();
		int i = 0;
		while (iter.hasNext()) {
			Image image = iter.next();
			images[i] = image;
			imageNames[i] = image.getName();
			i++;
		}
		ArrayAdapter<String> imageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, imageNames);
		imageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		imageSpinner.setAdapter(imageAdapter);
    }
    
    private void loadFlavorSpinner() {
		Spinner flavorSpinner = (Spinner) findViewById(R.id.flavor_spinner);
		//imageSpinner.setOnItemSelectedListener(this);
		String flavorNames[] = new String[Flavor.getFlavors().size()]; 
		flavors = new Flavor[Flavor.getFlavors().size()];

		Iterator<Flavor> iter = Flavor.getFlavors().values().iterator();
		int i = 0;
		while (iter.hasNext()) {
			Flavor flavor = iter.next();
			flavors[i] = flavor;
			flavorNames[i] = flavor.getName() + ", " + flavor.getDisk() + " GB disk";
			i++;
		}
		ArrayAdapter<String> flavorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, flavorNames);
		flavorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		flavorSpinner.setAdapter(flavorAdapter);
    }
	
}
