package com.example.niuxes;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class SobreActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.sobre));
		
		this.setListAdapter(adapter);
	}
}
