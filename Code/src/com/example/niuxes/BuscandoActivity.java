package com.example.niuxes;

import com.example.background.Busca;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class BuscandoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buscando);
		
		Intent i = new Intent(this, Busca.class);
		i.setPackage("com.example.background");
		this.startService(i);
		}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent(this, Busca.class);
		i.setPackage("com.example.background");
		this.stopService(i);
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		Intent i = new Intent(this, Busca.class);
		i.setPackage("com.example.background");
		this.stopService(i);
		super.onDestroy();
	}
}
