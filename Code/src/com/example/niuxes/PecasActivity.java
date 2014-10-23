package com.example.niuxes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.Toast;

public class PecasActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pecas);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_pecas, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		SharedPreferences preferencias = getSharedPreferences("pecas", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferencias.edit();
		
		editor.putInt("esquerda", pecaMarcadaEsquerda());
		editor.putInt("meio", pecaMarcadaMeio());
		editor.putInt("direita", pecaMarcadaDireita());
		
		editor.commit();
		return true;
	}
	
	
	/*
	 * Descobri as peças escolhidas do radioGrupo
	 * Esquerda
	 * Meio
	 * Direita
	 */
	
	private int pecaMarcadaEsquerda() {
		RadioGroup radioGrupoEsquerda = (RadioGroup)findViewById(R.id.radioGrupoEsquerda);
		int pecaMarcada = radioGrupoEsquerda.getCheckedRadioButtonId();
		
		int radioId = R.id.radioEsquerda1;
		if (radioId == pecaMarcada)
			return 1;
		radioId = R.id.radioEsquerda2;
		if (radioId == pecaMarcada)
			return 2;
		radioId = R.id.radioEsquerda3;
		if (radioId == pecaMarcada)
			return 3;
		
		Toast.makeText(this, "Nenhuma peça escolhida para a esquerda", Toast.LENGTH_LONG).show();
		return 0;
	}
	
	private int pecaMarcadaMeio() {
		RadioGroup radioGrupoMeio = (RadioGroup)findViewById(R.id.radioGrupoMeio);
		int pecaMarcada = radioGrupoMeio.getCheckedRadioButtonId();
		
		int radioId = R.id.radioMeio1;
		if (radioId == pecaMarcada)
			return 1;
		radioId = R.id.radioMeio2;
		if (radioId == pecaMarcada)
			return 2;
		radioId = R.id.radioMeio3;
		if (radioId == pecaMarcada)
			return 3;
		
		Toast.makeText(this, "Nenhuma peça escolhida para o meio", Toast.LENGTH_LONG).show();
		return 0;
	}
	
	private int pecaMarcadaDireita() {
		RadioGroup radioGrupoDireita = (RadioGroup)findViewById(R.id.radioGrupoDireita);
		int pecaMarcada = radioGrupoDireita.getCheckedRadioButtonId();
		
		int radioId = R.id.radioDireita1;
		if (radioId == pecaMarcada)
			return 1;
		radioId = R.id.radioDireita2;
		if (radioId == pecaMarcada)
			return 2;
		radioId = R.id.radioDireita3;
		if (radioId == pecaMarcada)
			return 3;
		
		Toast.makeText(this, "Nenhuma peça escolhida para a direita", Toast.LENGTH_LONG).show();
		return 0;
	}
}
