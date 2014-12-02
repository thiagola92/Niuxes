package com.example.niuxes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class BuscandoActivity extends Activity {
	
	final Context essaActivity = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buscando);
		
		this.startActivity(new Intent(this, JogoActivity.class));
	}
	
	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}
	
}
