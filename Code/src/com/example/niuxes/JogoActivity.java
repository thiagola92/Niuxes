package com.example.niuxes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.game.BluetoothGame;
import com.example.game.Mapa;

public class JogoActivity extends Activity {
	
	// Debug
	boolean DEBUG = true;
	String TAG = "JogoActivity";

	// Usado para saber quem começa durante a partida online
	Random numeroRandom;
	int seuRandom;
	boolean decidiram=false;
	
	// Context
	Context c = this;
	
	// matrix com o mapa
	static public Mapa tabuleiro;
	
	// quadrado selecionado
	public int quadrado1X;
	public int quadrado1Y;
	
	// quadrado ao qual esta se movimentando
	public int quadrado2X;
	public int quadrado2Y;
	
	// modo de jogo (configuracoes)
	public boolean jogoOnline = false;
	public boolean daltonico = false;
	public boolean som = true;
	
	// caso o jogador já tenha selecionado uma peça
	public boolean pecaSelecionada = false;
	
	// se é ou não seu turno
	public boolean seuTurno = true;
	
	// Bluetooth
	BluetoothSocket socket;
	ConexaoThread partidaThread;
	
	// Em qual etapa do processo esta
	int etapa=0;
	
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (etapa == 0) {
				
				if (DEBUG) Log.v(TAG, ">>> etapa 0 comecou <<<");
				
				byte[] objeto = (byte[])msg.obj;
				
				String recebida = new String(objeto);
				int oponenteRandom = Integer.valueOf(recebida);
				
				if (seuRandom < oponenteRandom)
					seuTurno=true;
				else
					seuTurno=false;
				
				if (DEBUG) Log.v(TAG, ">>> seuRandom:" + seuRandom + " oponenteRandom:" + oponenteRandom + "<<<");
				
				SharedPreferences pecas = c.getSharedPreferences("pecas", Context.MODE_PRIVATE);
				
				ByteArrayOutputStream enviar = new ByteArrayOutputStream();
				enviar.write(pecas.getInt("esquerda", 1));
				enviar.write(pecas.getInt("meio", 1));
				enviar.write(pecas.getInt("direita", 1));
				partidaThread.write(enviar.toByteArray());

				etapa++;
				
				if (DEBUG) Log.v(TAG, ">>> etapa 0 terminada <<<");
			} else if (etapa == 1) {
				
				if (DEBUG) Log.v(TAG, ">>> etapa 1 comecou <<<");
				
				byte[] objeto = (byte[])msg.obj;
				
				SharedPreferences pecas = c.getSharedPreferences("pecas", Context.MODE_PRIVATE);
				
				tabuleiro = new Mapa(pecas.getInt("esquerda", 1),
										pecas.getInt("meio", 1),
										pecas.getInt("direita", 1),
										-objeto[0],
										-objeto[1],
										-objeto[2]);
				
				carregarMapa();
				etapa++;
				
				if (DEBUG) Log.v(TAG, ">>> etapa 1 terminada <<<");
			} else if (etapa == 2) {
				
				if (DEBUG) Log.v(TAG, ">>> etapa 2 comecou <<<");
				
				byte[] objeto = (byte[])msg.obj;
				tabuleiro.receberDeBytes(objeto);
				
				carregarMapa();
				
				if (DEBUG) Log.v(TAG, ">>> etapa 2 terminada <<<");
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jogo);
		
		// Preferencias do jogo
		SharedPreferences estadoDoJogo = this.getSharedPreferences("jogo", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = estadoDoJogo.edit();

		// Outras preferencias
		SharedPreferences pecas = this.getSharedPreferences("pecas", Context.MODE_PRIVATE);
		SharedPreferences config = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		
		if ((estadoDoJogo.getBoolean("emJogo", false) == false) || (tabuleiro == null)) {
			
			editor.putBoolean("emJogo", true);
			editor.commit();
			
			jogoOnline = estadoDoJogo.getBoolean("jogandoOnline", false);
			som = config.getBoolean("som", true);
			daltonico = config.getBoolean("daltonico", false);
			
			if(jogoOnline == false)	{
				
				tabuleiro = new Mapa(pecas.getInt("esquerda", 1),
										pecas.getInt("meio", 1),
										pecas.getInt("direita", 1),
										-pecas.getInt("esquerda2", 1),
										-pecas.getInt("meio2", 1),
										-pecas.getInt("direita2", 1));
				
			} else {
				
				if (DEBUG) Log.v(TAG, ">>> Partida por Bluetooth <<<");
				
				// ISSO É TEMPORARIO ENQUANTO N TEM COMO DECIDIR QUEM COMEÇAR
				seuTurno = false;
				
				socket = BluetoothGame.getBTSocket();
				partidaThread = new ConexaoThread(socket);
				partidaThread.start();
				
				numeroRandom = new Random();
				seuRandom = numeroRandom.nextInt(200);
				ByteArrayOutputStream enviar = new ByteArrayOutputStream();
				enviar.write(seuRandom);
				partidaThread.write(enviar.toByteArray());

				tabuleiro = new Mapa(0,
										0,
										0,
										0,
										0,
										0);
			}
		}
		
		carregarMapa();
		
	}
	
	@Override
	public void onBackPressed() {
		
		// Construindo um dialogo antes de sair do jogo
		AlertDialog.Builder construirDialog = new AlertDialog.Builder(this);
		construirDialog.setMessage(R.string.dialog_sair);
				
		// Caso sim
		construirDialog.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
					
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				SharedPreferences estadoDoJogo = c.getSharedPreferences("jogo", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = estadoDoJogo.edit();
				editor.putBoolean("emJogo", false);
				editor.commit();
				
				Intent i = new Intent(c, MainActivity.class);
				c.startActivity(i);
						
			}
		} );
				
		// Caso não
		construirDialog.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
					
			@Override
			public void onClick(DialogInterface dialog, int which) {
						
			}
		} );
		
		construirDialog.show();
	}
	
	@Override
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
	
	/*
	 * Responsavel por carregar todo tabuleiro assim que a activity é criada e carregar de quem é o turno.
	 */
	
	public void carregarMapa() {
		
		if (jogoOnline)
			avisoTurno();
		else {
			ImageView a = (ImageView)findViewById(R.id.turno_vez);
			a.setVisibility(View.INVISIBLE);
		}
		
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

	/*
	 * Apenas carrega o simbolo que avisa se é o seu turno.
	 */
	
	public void avisoTurno() {
		ImageView x = (ImageView)findViewById(R.id.turno_vez);
		
		if (seuTurno)
			seuTurno = false;
		else
			seuTurno = true;
		
		
		if (daltonico) {
			if (seuTurno)
				x.setImageResource(R.drawable.jogo_turno_3);
			else
				x.setImageResource(R.drawable.jogo_turno_4);
		} else {
			if (seuTurno)
				x.setImageResource(R.drawable.jogo_turno_1);
			else
				x.setImageResource(R.drawable.jogo_turno_2);
		}
	}
	
	/*
	 * Passe o valor no tabuleiro e vai devolver qual imagem deve ser desenhada lá.
	 */
	
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

	/*
	 * Passe a posição do mapa e vai retornar o id daquela posição.
	 */
	
	public int acharId(int x, int y) {
		
		if (x==0) {
			if (y==0)
				return R.id.quadrado00;
			if (y==1)
				return R.id.quadrado01;
			if (y==2)
				return R.id.quadrado02;
			if (y==3)
				return R.id.quadrado03;
			if (y==4)
				return R.id.quadrado04;
			if (y==5)
				return R.id.quadrado05;
			if (y==6)
				return R.id.quadrado06;
		}
		else if (x==1) {
			if (y==0)
				return R.id.quadrado10;
			if (y==1)
				return R.id.quadrado11;
			if (y==2)
				return R.id.quadrado12;
			if (y==3)
				return R.id.quadrado13;
			if (y==4)
				return R.id.quadrado14;
			if (y==5)
				return R.id.quadrado15;
			if (y==6)
				return R.id.quadrado16;
		}
		else if (x==2) {
			if (y==0)
				return R.id.quadrado20;
			if (y==1)
				return R.id.quadrado21;
			if (y==2)
				return R.id.quadrado22;
			if (y==3)
				return R.id.quadrado23;
			if (y==4)
				return R.id.quadrado24;
			if (y==5)
				return R.id.quadrado25;
			if (y==6)
				return R.id.quadrado26;
		}
		else if (x==3) {
			if (y==0)
				return R.id.quadrado30;
			if (y==1)
				return R.id.quadrado31;
			if (y==2)
				return R.id.quadrado32;
			if (y==3)
				return R.id.quadrado33;
			if (y==4)
				return R.id.quadrado34;
			if (y==5)
				return R.id.quadrado35;
			if (y==6)
				return R.id.quadrado36;
		}
		else if (x==4) {
			if (y==0)
				return R.id.quadrado40;
			if (y==1)
				return R.id.quadrado41;
			if (y==2)
				return R.id.quadrado42;
			if (y==3)
				return R.id.quadrado43;
			if (y==4)
				return R.id.quadrado44;
			if (y==5)
				return R.id.quadrado45;
			if (y==6)
				return R.id.quadrado46;
		}
		else if (x==5) {
			if (y==0)
				return R.id.quadrado50;
			if (y==1)
				return R.id.quadrado51;
			if (y==2)
				return R.id.quadrado52;
			if (y==3)
				return R.id.quadrado53;
			if (y==4)
				return R.id.quadrado54;
			if (y==5)
				return R.id.quadrado55;
			if (y==6)
				return R.id.quadrado56;
		}
		else if (x==6) {
			if (y==0)
				return R.id.quadrado60;
			if (y==1)
				return R.id.quadrado61;
			if (y==2)
				return R.id.quadrado62;
			if (y==3)
				return R.id.quadrado63;
			if (y==4)
				return R.id.quadrado64;
			if (y==5)
				return R.id.quadrado65;
			if (y==6)
				return R.id.quadrado66;
		}
		
		return -1;
	}
	
	/*
	 * Responsavel pela movimentação da peça, em outras palavras move uma peça para uma posição e deixa vazio onde ela tava.
	 * Muda os valores da matriz mas em vez de chamar a funcao 'carregarMapa' apenas carrega as duas posições (onde a peça tava e onde foi).
	 */
	
	public void Mover() {
		ImageView x;
		
		tabuleiro.mapaPos[quadrado2X][quadrado2Y] = tabuleiro.mapaPos[quadrado1X][quadrado1Y];
		tabuleiro.mapaPos[quadrado1X][quadrado1Y] = 0;
		
		x = (ImageView)findViewById(acharId(quadrado2X, quadrado2Y));
		x.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado2X][quadrado2Y]));
		
		x = (ImageView)findViewById(acharId(quadrado1X, quadrado1Y));
		x.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X][quadrado1Y]));

		if (som)
			tocarSom();
		
		if (jogoOnline == true) {
			partidaThread.write(tabuleiro.converterParaBytes());
			seuTurno=false;
		} else {
			tabuleiro.inverter();
			carregarMapa();
		}
		
	}
	
	/*
	 * Responsavel por tocar um som quando uma jogada é feita.
	 */
	
	public void tocarSom() {
		MediaPlayer mediaplayer = MediaPlayer.create(this, R.raw.mover);
		mediaplayer.start();
	}
	
	/*
	 * Descobri qual peça que esta fazendo o movimento.
	 * Chamar a função com os movimentos da peça.
	 */
	
	public void descobrirMovimento() {
		
		if(tabuleiro.mapaPos[quadrado1X][quadrado1Y] == 1)
			soldadoMovimentos();
		else if (tabuleiro.mapaPos[quadrado1X][quadrado1Y] == 2)
			magoMovimentos();
		else if (tabuleiro.mapaPos[quadrado1X][quadrado1Y] == 3)
			reiMovimentos();
		else if (tabuleiro.mapaPos[quadrado1X][quadrado1Y] == 4)
			arqueiroMovimentos();
	}
	
	/*
	 * Essa parte talvez seja muito complicada.
	 * 
	 * Se os movimentos da peça já tiverem aparecendo, desaparece.
	 * Se não, aparece.
	 * Aqui é onde você deve organizar os movimentos dos personagens.
	 * 
	 */
	
	public void soldadoMovimentos() {
		
		ImageView imagem;

		if ( (quadrado1X+1<=6) && (quadrado1X+1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X+1,quadrado1Y));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X+1][quadrado1Y]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}

		if ( (quadrado1X+2<=6) && (quadrado1X+2>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X+2,quadrado1Y));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X+2][quadrado1Y]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X-1<=6) && (quadrado1X-1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X-1,quadrado1Y));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X-1][quadrado1Y]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X-2<=6) && (quadrado1X-2>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X-2,quadrado1Y));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X-2][quadrado1Y]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}

		if ( (quadrado1Y+1<=6) && (quadrado1Y+1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X,quadrado1Y+1));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X][quadrado1Y+1]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}

		if ( (quadrado1Y+2<=6) && (quadrado1Y+2>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X,quadrado1Y+2));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X][quadrado1Y+2]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1Y-1<=6) && (quadrado1Y-1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X,quadrado1Y-1));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X][quadrado1Y-1]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1Y-2<=6) && (quadrado1Y-2>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X,quadrado1Y-2));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X][quadrado1Y-2]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
	}
	
	public void magoMovimentos() {
		
		ImageView imagem;
		
		if ( (quadrado1X-1<=6) && (quadrado1X-1>=0) && (quadrado1Y-1<=6) && (quadrado1Y-1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X-1,quadrado1Y-1));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X-1][quadrado1Y-1]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X-2<=6) && (quadrado1X-2>=0) && (quadrado1Y-2<=6) && (quadrado1Y-2>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X-2,quadrado1Y-2));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X-2][quadrado1Y-2]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X-3<=6) && (quadrado1X-3>=0) && (quadrado1Y-3<=6) && (quadrado1Y-3>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X-3,quadrado1Y-3));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X-3][quadrado1Y-3]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X-1<=6) && (quadrado1X-1>=0) && (quadrado1Y+1<=6) && (quadrado1Y+1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X-1,quadrado1Y+1));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X-1][quadrado1Y+1]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X-2<=6) && (quadrado1X-2>=0) && (quadrado1Y+2<=6) && (quadrado1Y+2>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X-2,quadrado1Y+2));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X-2][quadrado1Y+2]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X-3<=6) && (quadrado1X-3>=0) && (quadrado1Y+3<=6) && (quadrado1Y+3>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X-3,quadrado1Y+3));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X-3][quadrado1Y+3]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X+1<=6) && (quadrado1X+1>=0) && (quadrado1Y-1<=6) && (quadrado1Y-1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X+1,quadrado1Y-1));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X+1][quadrado1Y-1]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X+2<=6) && (quadrado1X+2>=0) && (quadrado1Y-2<=6) && (quadrado1Y-2>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X+2,quadrado1Y-2));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X+2][quadrado1Y-2]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X+3<=6) && (quadrado1X+3>=0) && (quadrado1Y-3<=6) && (quadrado1Y-3>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X+3,quadrado1Y-3));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X+3][quadrado1Y-3]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X+1<=6) && (quadrado1X+1>=0) && (quadrado1Y+1<=6) && (quadrado1Y+1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X+1,quadrado1Y+1));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X+1][quadrado1Y+1]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X+2<=6) && (quadrado1X+2>=0) && (quadrado1Y+2<=6) && (quadrado1Y+2>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X+2,quadrado1Y+2));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X+2][quadrado1Y+2]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X+3<=6) && (quadrado1X+3>=0) && (quadrado1Y+3<=6) && (quadrado1Y+3>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X+3,quadrado1Y+3));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X+3][quadrado1Y+3]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
	}
	
	public void reiMovimentos() {
		
		ImageView imagem;
		
		if ( (quadrado1X-1<=6) && (quadrado1X-1>=0) && (quadrado1Y-1<=6) && (quadrado1Y-1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X-1,quadrado1Y-1));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X-1][quadrado1Y-1]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X-1<=6) && (quadrado1X-1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X-1,quadrado1Y));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X-1][quadrado1Y]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X-1<=6) && (quadrado1X-1>=0) && (quadrado1Y+1<=6) && (quadrado1Y+1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X-1,quadrado1Y+1));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X-1][quadrado1Y+1]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1Y-1<=6) && (quadrado1Y-1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X,quadrado1Y-1));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X][quadrado1Y-1]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1Y+1<=6) && (quadrado1Y+1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X,quadrado1Y+1));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X][quadrado1Y+1]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X+1<=6) && (quadrado1X+1>=0) && (quadrado1Y-1<=6) && (quadrado1Y-1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X+1,quadrado1Y-1));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X+1][quadrado1Y-1]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X+1<=6) && (quadrado1X+1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X+1,quadrado1Y));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X+1][quadrado1Y]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X+1<=6) && (quadrado1X+1>=0) && (quadrado1Y+1<=6) && (quadrado1Y+1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X+1,quadrado1Y+1));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X+1][quadrado1Y+1]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
	}
	
	public void arqueiroMovimentos() {
		
		ImageView imagem;
		
		if ( (quadrado1X-1<=6) && (quadrado1X-1>=0) && (quadrado1Y-2<=6) && (quadrado1Y-2>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X-1,quadrado1Y-2));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X-1][quadrado1Y-2]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X-2<=6) && (quadrado1X-2>=0) && (quadrado1Y-1<=6) && (quadrado1Y-1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X-2,quadrado1Y-1));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X-2][quadrado1Y-1]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X-1<=6) && (quadrado1X-1>=0) && (quadrado1Y+2<=6) && (quadrado1Y+2>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X-1,quadrado1Y+2));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X-1][quadrado1Y+2]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X-2<=6) && (quadrado1X-2>=0) && (quadrado1Y+1<=6) && (quadrado1Y+1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X-2,quadrado1Y+1));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X-2][quadrado1Y+1]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X+1<=6) && (quadrado1X+1>=0) && (quadrado1Y-2<=6) && (quadrado1Y-2>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X+1,quadrado1Y-2));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X+1][quadrado1Y-2]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X+2<=6) && (quadrado1X+2>=0) && (quadrado1Y-1<=6) && (quadrado1Y-1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X+2,quadrado1Y-1));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X+2][quadrado1Y-1]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X+1<=6) && (quadrado1X+1>=0) && (quadrado1Y+2<=6) && (quadrado1Y+2>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X+1,quadrado1Y+2));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X+1][quadrado1Y+2]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
		if ( (quadrado1X+2<=6) && (quadrado1X+2>=0) && (quadrado1Y+1<=6) && (quadrado1Y+1>=0) ) {
			imagem = (ImageView)findViewById(acharId(quadrado1X+2,quadrado1Y+1));
			if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
				imagem.setImageResource(acharImagem(tabuleiro.mapaPos[quadrado1X+2][quadrado1Y+1]));
			} else {
				imagem.setImageResource(R.drawable.quadrado_ir);
			}
		}
		
	}
	
	/*
	 * Essa parte vai exigir um pouco de explicação.
	 * Em pensei em duas maneiras de descobrir em qual lugar do tabuleiro o usuário esta tocando.
	 * 
	 * 1) Ele tocar em qualquer lugar da tela, chamar uma função que vai varrer todos os quadrados do tabuleiro até descobrir a peça tocada.
	 * 		Ex:
	 * public void descobrirPos (View view)
	 * 		if (view.getId() == R.id.quadrado00) {
	 * 			...
	 * 		} else if (view.getId() == R.id.quadrado01) {
	 * 			...
	 * 		} ...
	 * 
	 * Nesse caso teria 49 "if" seguidos, o que faria cada vez que você selecionar uma peça ter a chance de demorar dependendo de onde ela esteja.
	 * 
	 * 2) Cada quadrado chamar uma função espefica pra ele, embora aumente o tamanho do codigo aqui, torna o jogo mais rapido para o usuário.
	 * 		Ex:
	 * public void selecionar00 (View view) {
	 * 	...
	 * }
	 * public void selecionar01 (View view) {
	 * 	...
	 * }
	 * ...
	 */

	public void selecionar00(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado00);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[0][0] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=0;
			quadrado1Y=0;
			
			descobrirMovimento();
		} else if (quadrado1X==0 && quadrado1Y==0) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=0;
			quadrado2Y=0;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar01(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado01);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[0][1] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=0;
			quadrado1Y=1;
			
			descobrirMovimento();
		} else if (quadrado1X==0 && quadrado1Y==1) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=0;
			quadrado2Y=1;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar02(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado02);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[0][2] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=0;
			quadrado1Y=2;
			
			descobrirMovimento();
		} else if (quadrado1X==0 && quadrado1Y==2) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=0;
			quadrado2Y=2;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar03(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado03);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[0][3] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=0;
			quadrado1Y=3;
			
			descobrirMovimento();
		} else if (quadrado1X==0 && quadrado1Y==3) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=0;
			quadrado2Y=3;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar04(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado04);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[0][4] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=0;
			quadrado1Y=4;
			
			descobrirMovimento();
		} else if (quadrado1X==0 && quadrado1Y==4) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=0;
			quadrado2Y=4;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar05(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado05);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[0][5] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=0;
			quadrado1Y=5;
			
			descobrirMovimento();
		} else if (quadrado1X==0 && quadrado1Y==5) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=0;
			quadrado2Y=5;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar06(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado06);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[0][6] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=0;
			quadrado1Y=6;
			
			descobrirMovimento();
		} else if (quadrado1X==0 && quadrado1Y==6) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=0;
			quadrado2Y=6;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}

	public void selecionar10(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado10);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[1][0] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=1;
			quadrado1Y=0;
			
			descobrirMovimento();
		} else if (quadrado1X==1 && quadrado1Y==0) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=1;
			quadrado2Y=0;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar11(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado11);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[1][1] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=1;
			quadrado1Y=1;
			
			descobrirMovimento();
		} else if (quadrado1X==1 && quadrado1Y==1) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=1;
			quadrado2Y=1;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar12(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado12);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[1][2] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=1;
			quadrado1Y=2;
			
			descobrirMovimento();
		} else if (quadrado1X==1 && quadrado1Y==2) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=1;
			quadrado2Y=2;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar13(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado13);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[1][3] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=1;
			quadrado1Y=3;
			
			descobrirMovimento();
		} else if (quadrado1X==1 && quadrado1Y==3) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=1;
			quadrado2Y=3;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar14(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado14);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[1][4] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=1;
			quadrado1Y=4;
			
			descobrirMovimento();
		} else if (quadrado1X==1 && quadrado1Y==4) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=1;
			quadrado2Y=4;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar15(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado15);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[1][5] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=1;
			quadrado1Y=5;
			
			descobrirMovimento();
		} else if (quadrado1X==1 && quadrado1Y==5) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=1;
			quadrado2Y=5;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar16(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado16);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[1][6] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=1;
			quadrado1Y=6;
			
			descobrirMovimento();
		} else if (quadrado1X==1 && quadrado1Y==6) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=1;
			quadrado2Y=6;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}

	public void selecionar20(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado20);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[2][0] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=2;
			quadrado1Y=0;
			
			descobrirMovimento();
		} else if (quadrado1X==2 && quadrado1Y==0) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=2;
			quadrado2Y=0;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar21(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado21);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[2][1] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=2;
			quadrado1Y=1;
			
			descobrirMovimento();
		} else if (quadrado1X==2 && quadrado1Y==1) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=2;
			quadrado2Y=1;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar22(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado22);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[2][2] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=2;
			quadrado1Y=2;
			
			descobrirMovimento();
		} else if (quadrado1X==2 && quadrado1Y==2) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=2;
			quadrado2Y=2;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar23(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado23);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[2][3] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=2;
			quadrado1Y=3;
			
			descobrirMovimento();
		} else if (quadrado1X==2 && quadrado1Y==3) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=2;
			quadrado2Y=3;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar24(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado24);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[2][4] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=2;
			quadrado1Y=4;
			
			descobrirMovimento();
		} else if (quadrado1X==2 && quadrado1Y==4) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=2;
			quadrado2Y=4;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar25(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado25);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[2][5] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=2;
			quadrado1Y=5;
			
			descobrirMovimento();
		} else if (quadrado1X==2 && quadrado1Y==5) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=2;
			quadrado2Y=5;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar26(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado26);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[2][6] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=2;
			quadrado1Y=6;
			
			descobrirMovimento();
		} else if (quadrado1X==2 && quadrado1Y==6) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=2;
			quadrado2Y=6;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}

	public void selecionar30(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado30);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[3][0] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=3;
			quadrado1Y=0;
			
			descobrirMovimento();
		} else if (quadrado1X==3 && quadrado1Y==0) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=3;
			quadrado2Y=0;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar31(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado31);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[3][1] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=3;
			quadrado1Y=1;
			
			descobrirMovimento();
		} else if (quadrado1X==3 && quadrado1Y==1) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=3;
			quadrado2Y=1;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar32(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado32);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[3][2] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=3;
			quadrado1Y=2;
			
			descobrirMovimento();
		} else if (quadrado1X==3 && quadrado1Y==2) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=3;
			quadrado2Y=2;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar33(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado33);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[3][3] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=3;
			quadrado1Y=3;
			
			descobrirMovimento();
		} else if (quadrado1X==3 && quadrado1Y==3) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=3;
			quadrado2Y=3;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar34(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado34);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[3][4] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=3;
			quadrado1Y=4;
			
			descobrirMovimento();
		} else if (quadrado1X==3 && quadrado1Y==4) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=3;
			quadrado2Y=4;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar35(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado35);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[3][5] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=3;
			quadrado1Y=5;
			
			descobrirMovimento();
		} else if (quadrado1X==3 && quadrado1Y==5) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=3;
			quadrado2Y=5;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar36(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado36);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[3][6] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=3;
			quadrado1Y=6;
			
			descobrirMovimento();
		} else if (quadrado1X==3 && quadrado1Y==6) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=3;
			quadrado2Y=6;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}

	public void selecionar40(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado40);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[4][0] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=4;
			quadrado1Y=0;
			
			descobrirMovimento();
		} else if (quadrado1X==4 && quadrado1Y==0) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=4;
			quadrado2Y=0;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar41(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado41);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[4][1] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=4;
			quadrado1Y=1;
			
			descobrirMovimento();
		} else if (quadrado1X==4 && quadrado1Y==1) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=4;
			quadrado2Y=1;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar42(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado42);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[4][2] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=4;
			quadrado1Y=2;
			
			descobrirMovimento();
		} else if (quadrado1X==4 && quadrado1Y==2) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=4;
			quadrado2Y=2;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar43(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado43);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[4][3] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=4;
			quadrado1Y=3;
			
			descobrirMovimento();
		} else if (quadrado1X==4 && quadrado1Y==3) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=4;
			quadrado2Y=3;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar44(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado44);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[4][4] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=4;
			quadrado1Y=4;
			
			descobrirMovimento();
		} else if (quadrado1X==4 && quadrado1Y==4) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=4;
			quadrado2Y=4;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar45(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado45);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[4][5] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=4;
			quadrado1Y=5;
			
			descobrirMovimento();
		} else if (quadrado1X==4 && quadrado1Y==5) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=4;
			quadrado2Y=5;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar46(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado46);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[4][6] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=4;
			quadrado1Y=6;
			
			descobrirMovimento();
		} else if (quadrado1X==4 && quadrado1Y==6) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=4;
			quadrado2Y=6;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}

	public void selecionar50(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado50);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[5][0] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=5;
			quadrado1Y=0;
			
			descobrirMovimento();
		} else if (quadrado1X==5 && quadrado1Y==0) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=5;
			quadrado2Y=0;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar51(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado51);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[5][1] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=5;
			quadrado1Y=1;
			
			descobrirMovimento();
		} else if (quadrado1X==5 && quadrado1Y==1) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=5;
			quadrado2Y=1;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar52(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado52);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[5][2] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=5;
			quadrado1Y=2;
			
			descobrirMovimento();
		} else if (quadrado1X==5 && quadrado1Y==2) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=5;
			quadrado2Y=2;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar53(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado53);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[5][3] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=5;
			quadrado1Y=3;
			
			descobrirMovimento();
		} else if (quadrado1X==5 && quadrado1Y==3) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=5;
			quadrado2Y=3;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar54(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado54);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[5][4] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=5;
			quadrado1Y=4;
			
			descobrirMovimento();
		} else if (quadrado1X==5 && quadrado1Y==4) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=5;
			quadrado2Y=4;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar55(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado55);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[5][5] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=5;
			quadrado1Y=5;
			
			descobrirMovimento();
		} else if (quadrado1X==5 && quadrado1Y==5) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=5;
			quadrado2Y=5;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar56(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado56);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[5][6] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=5;
			quadrado1Y=6;
			
			descobrirMovimento();
		} else if (quadrado1X==5 && quadrado1Y==6) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=5;
			quadrado2Y=6;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}

	public void selecionar60(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado60);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[6][0] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=6;
			quadrado1Y=0;
			
			descobrirMovimento();
		} else if (quadrado1X==6 && quadrado1Y==0) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=6;
			quadrado2Y=0;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar61(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado61);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[6][1] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=6;
			quadrado1Y=1;
			
			descobrirMovimento();
		} else if (quadrado1X==6 && quadrado1Y==1) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=6;
			quadrado2Y=1;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar62(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado62);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[6][2] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=6;
			quadrado1Y=2;
			
			descobrirMovimento();
		} else if (quadrado1X==6 && quadrado1Y==2) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=6;
			quadrado2Y=2;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar63(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado63);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[6][3] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=6;
			quadrado1Y=3;
			
			descobrirMovimento();
		} else if (quadrado1X==6 && quadrado1Y==3) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=6;
			quadrado2Y=3;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar64(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado64);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[6][4] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=6;
			quadrado1Y=4;
			
			descobrirMovimento();
		} else if (quadrado1X==6 && quadrado1Y==4) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=6;
			quadrado2Y=4;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar65(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado65);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[6][5] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=6;
			quadrado1Y=5;
			
			descobrirMovimento();
		} else if (quadrado1X==6 && quadrado1Y==5) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=6;
			quadrado2Y=5;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	public void selecionar66(View view) {
		
		if (seuTurno == false) 
			return;
		
		ImageView imagem = (ImageView)findViewById(R.id.quadrado66);
		
		if (pecaSelecionada==false) {
			
			if (tabuleiro.mapaPos[6][6] <= 0)
				return;
			
			pecaSelecionada=true;
			
			quadrado1X=6;
			quadrado1Y=6;
			
			descobrirMovimento();
		} else if (quadrado1X==6 && quadrado1Y==6) {
			pecaSelecionada=false;

			descobrirMovimento();
		} else if (imagem.getDrawable().getConstantState() == this.getResources().getDrawable(R.drawable.quadrado_ir).getConstantState()) {
			quadrado2X=6;
			quadrado2Y=6;

			descobrirMovimento();
			Mover();
			
			pecaSelecionada=false;
		}
		
	}
	
	private class ConexaoThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;
		
		public ConexaoThread(BluetoothSocket socket) {
			
			if (DEBUG) Log.v(TAG, ">>> conexaoThread <<<");
			
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) { }
			
			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}
		
		public void run() {
			
			if (DEBUG) Log.v(TAG, ">>> run/read <<<");
			
			byte[] buffer = new byte[1024]; // buffer para guardar informações da stream
			int bytes; // qtos bytes foram utilizados
			
			// Continuar lendo da inputstream
			while(true) {
				try {
					// Ler da InputStream
					bytes = mmInStream.read(buffer);
					// Ao receber coisa chamar o handler
					mHandler.obtainMessage(0, bytes, -1, buffer).sendToTarget();
				} catch(IOException e) {
					if (DEBUG) Log.v(TAG, ">>> Uma execeção ocorreu durante a leitura de dados <<<");
					cancel();
					break;
				}
			}
		}
		
		// Chamar para escrever dados
		public void write(byte[] bytes) {
			
			if (DEBUG) Log.v(TAG, ">>> write <<<");
			
			try {
				mmOutStream.write(bytes);
			} catch (IOException e) {
				if (DEBUG) Log.v(TAG, ">>> Erro na hora de escrever no buffer <<<");
			}
		}
		
		// Chamar quando for desligar conexao
		public void cancel() {
			
			if (DEBUG) Log.v(TAG, ">>> cancel Thread <<<");
			
			try {
				mmSocket.close();
			} catch (IOException e) { }
		}
		
	}
}
