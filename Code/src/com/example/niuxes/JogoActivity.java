package com.example.niuxes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.game.Mapa;

public class JogoActivity extends Activity {
	
	static public Mapa tabuleiro;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jogo);
		
		SharedPreferences estadoDoJogo = this.getSharedPreferences("jogo", Context.MODE_PRIVATE);
		SharedPreferences pecas = this.getSharedPreferences("pecas", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = estadoDoJogo.edit();
		
		if ((estadoDoJogo.getBoolean("emJogo", false) == false) || (tabuleiro == null)) {
			
			editor.putBoolean("emJogo", true);
			editor.commit();
			
			if(estadoDoJogo.getBoolean("jogandoOnline", false) == false)	{
				
				tabuleiro = new Mapa(pecas.getInt("esquerda", 1),
										pecas.getInt("meio", 2),
										pecas.getInt("direita", 3));
				
				editor.putBoolean("jogandoOnline", false);
				editor.commit();
				
			} else {
				
				editor.putBoolean("jogandoOnline", true);
				editor.commit();
				
				// Caso o jogo seja online como deve ser tratado?

				tabuleiro = new Mapa(pecas.getInt("esquerda", 1),
										pecas.getInt("meio", 2),
										pecas.getInt("direita", 3),
										-1,
										-1,
										-1);
			}
		}
		
		carregarMapa();
		
	}
	
	public void onDestroy() {
		SharedPreferences estadoDoJogo = this.getSharedPreferences("jogo", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = estadoDoJogo.edit();
		
		editor.putBoolean("emJogo", false);
		editor.commit();
		
		super.onDestroy();
	}
	
	public void onBackPressed() {
		
		SharedPreferences estadoDoJogo = this.getSharedPreferences("jogo", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = estadoDoJogo.edit();

		editor.putBoolean("emJogo", false);
		editor.commit();
		
		super.onBackPressed();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_jogo, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == R.id.novoJogo) {
			onBackPressed();
    		return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	
	public void carregarMapa() {

		ImageView x = (ImageView)findViewById(R.id.quadrado00);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[0][0]));
		x = (ImageView)findViewById(R.id.quadrado01);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[0][1]));
		x = (ImageView)findViewById(R.id.quadrado02);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[0][2]));
		x = (ImageView)findViewById(R.id.quadrado03);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[0][3]));
		x = (ImageView)findViewById(R.id.quadrado04);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[0][4]));
		x = (ImageView)findViewById(R.id.quadrado05);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[0][5]));
		x = (ImageView)findViewById(R.id.quadrado06);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[0][6]));
		
		x = (ImageView)findViewById(R.id.quadrado10);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[1][0]));
		x = (ImageView)findViewById(R.id.quadrado11);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[1][1]));
		x = (ImageView)findViewById(R.id.quadrado12);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[1][2]));
		x = (ImageView)findViewById(R.id.quadrado13);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[1][3]));
		x = (ImageView)findViewById(R.id.quadrado14);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[1][4]));
		x = (ImageView)findViewById(R.id.quadrado15);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[1][5]));
		x = (ImageView)findViewById(R.id.quadrado16);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[1][6]));
		
		x = (ImageView)findViewById(R.id.quadrado20);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[2][0]));
		x = (ImageView)findViewById(R.id.quadrado21);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[2][1]));
		x = (ImageView)findViewById(R.id.quadrado22);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[2][2]));
		x = (ImageView)findViewById(R.id.quadrado23);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[2][3]));
		x = (ImageView)findViewById(R.id.quadrado24);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[2][4]));
		x = (ImageView)findViewById(R.id.quadrado25);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[2][5]));
		x = (ImageView)findViewById(R.id.quadrado26);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[2][6]));
		
		x = (ImageView)findViewById(R.id.quadrado30);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[3][0]));
		x = (ImageView)findViewById(R.id.quadrado31);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[3][1]));
		x = (ImageView)findViewById(R.id.quadrado32);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[3][2]));
		x = (ImageView)findViewById(R.id.quadrado33);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[3][3]));
		x = (ImageView)findViewById(R.id.quadrado34);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[3][4]));
		x = (ImageView)findViewById(R.id.quadrado35);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[3][5]));
		x = (ImageView)findViewById(R.id.quadrado36);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[3][6]));
		
		x = (ImageView)findViewById(R.id.quadrado40);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[4][0]));
		x = (ImageView)findViewById(R.id.quadrado41);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[4][1]));
		x = (ImageView)findViewById(R.id.quadrado42);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[4][2]));
		x = (ImageView)findViewById(R.id.quadrado43);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[4][3]));
		x = (ImageView)findViewById(R.id.quadrado44);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[4][4]));
		x = (ImageView)findViewById(R.id.quadrado45);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[4][5]));
		x = (ImageView)findViewById(R.id.quadrado46);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[4][6]));
		
		x = (ImageView)findViewById(R.id.quadrado50);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[5][0]));
		x = (ImageView)findViewById(R.id.quadrado51);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[5][1]));
		x = (ImageView)findViewById(R.id.quadrado52);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[5][2]));
		x = (ImageView)findViewById(R.id.quadrado53);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[5][3]));
		x = (ImageView)findViewById(R.id.quadrado54);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[5][4]));
		x = (ImageView)findViewById(R.id.quadrado55);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[5][5]));
		x = (ImageView)findViewById(R.id.quadrado56);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[5][6]));
		
		x = (ImageView)findViewById(R.id.quadrado60);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[6][0]));
		x = (ImageView)findViewById(R.id.quadrado61);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[6][1]));
		x = (ImageView)findViewById(R.id.quadrado62);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[6][2]));
		x = (ImageView)findViewById(R.id.quadrado63);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[6][3]));
		x = (ImageView)findViewById(R.id.quadrado64);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[6][4]));
		x = (ImageView)findViewById(R.id.quadrado65);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[6][5]));
		x = (ImageView)findViewById(R.id.quadrado66);
		x.setImageResource(acharImagem(tabuleiro.mapaPos[6][6]));
	}

	public int acharImagem(int x) {
		
		if(x>0) {
			if (x==1)
				return R.drawable.peca_soldado_branco;
			if (x==2)
				return R.drawable.peca_mago_branco;
			if (x==3)
				return R.drawable.peca_rei_branco;
			if (x==4)
				return R.drawable.peca_arqueiro_branco;
		} else if (x<0) {
			if (x==-1)
				return R.drawable.peca_soldado_preto;
			if (x==-2)
				return R.drawable.peca_mago_preto;
			if (x==-3)
				return R.drawable.peca_rei_preto;
			if (x==-4)
				return R.drawable.peca_arqueiro_preto;
		}
		
		return R.drawable.quadrado_vazio;
	}
}
