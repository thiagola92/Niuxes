package com.example.niuxes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class PecasActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pecas);
		
		SharedPreferences preferencias = getSharedPreferences("pecas", Context.MODE_PRIVATE);
		marcarEsquerda(preferencias.getInt("esquerda", 1));
		marcarMeio(preferencias.getInt("meio", 1));
		marcarDireita(preferencias.getInt("direita", 1));
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
		
		Toast.makeText(this, R.string.pecas_salvas, Toast.LENGTH_SHORT).show();
		
		this.onBackPressed();
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
		radioId = R.id.radioEsquerda4;
		if (radioId == pecaMarcada)
			return 4;
		
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
		radioId = R.id.radioMeio4;
		if (radioId == pecaMarcada)
			return 4;
		
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
		radioId = R.id.radioDireita4;
		if (radioId == pecaMarcada)
			return 4;
		
		return 0;
	}

	/*
	 * Marcar as peças que você já tinha escolhido
	 * Esquerda
	 * Meio
	 * Direita
	 */
	
	private void marcarEsquerda(int numeroDaPeca) {
		
		RadioButton radioButton;
		
		switch(numeroDaPeca) {
		case 1:
			radioButton = (RadioButton)findViewById(R.id.radioEsquerda1);
			radioButton.setChecked(true);
		break;
		case 2:
			radioButton = (RadioButton)findViewById(R.id.radioEsquerda2);
			radioButton.setChecked(true);
		break;
		case 3:
			radioButton = (RadioButton)findViewById(R.id.radioEsquerda3);
			radioButton.setChecked(true);
		break;
		case 4:
			radioButton = (RadioButton)findViewById(R.id.radioEsquerda4);
			radioButton.setChecked(true);
		break;
		default:
			Toast.makeText(this, "Não foi encontrado peça a esquerda", Toast.LENGTH_SHORT).show();
		break;
			
		}
	}
	
	private void marcarMeio(int numeroDaPeca) {
		
		RadioButton radioButton;
		
		switch(numeroDaPeca) {
		case 1:
			radioButton = (RadioButton)findViewById(R.id.radioMeio1);
			radioButton.setChecked(true);
		break;
		case 2:
			radioButton = (RadioButton)findViewById(R.id.radioMeio2);
			radioButton.setChecked(true);
		break;
		case 3:
			radioButton = (RadioButton)findViewById(R.id.radioMeio3);
			radioButton.setChecked(true);
		break;
		case 4:
			radioButton = (RadioButton)findViewById(R.id.radioMeio4);
			radioButton.setChecked(true);
		break;
		default:
			Toast.makeText(this, "Não foi encontrado peça no meio", Toast.LENGTH_SHORT).show();
		break;
			
		}
	}
	
	private void marcarDireita(int numeroDaPeca) {
		
		RadioButton radioButton;
		
		switch(numeroDaPeca) {
		case 1:
			radioButton = (RadioButton)findViewById(R.id.radioDireita1);
			radioButton.setChecked(true);
		break;
		case 2:
			radioButton = (RadioButton)findViewById(R.id.radioDireita2);
			radioButton.setChecked(true);
		break;
		case 3:
			radioButton = (RadioButton)findViewById(R.id.radioDireita3);
			radioButton.setChecked(true);
		break;
		case 4:
			radioButton = (RadioButton)findViewById(R.id.radioDireita4);
			radioButton.setChecked(true);
		break;
		default:
			Toast.makeText(this, "Não foi encontrado peça a direita", Toast.LENGTH_SHORT).show();
		break;
			
		}
	}
}
