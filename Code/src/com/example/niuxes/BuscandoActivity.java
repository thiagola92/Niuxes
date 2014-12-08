package com.example.niuxes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class BuscandoActivity extends Activity {
	
	// Debug
	boolean DEBUG = true;
	String TAG = "BuscandoActivity";
	
	// Fixo
	int REQUEST_ENABLE_BT = 1;
	int REQUEST_BE_DISCOVERABLE = 2;
	int TEMPO_DESCOBERTO = 30;
	String NOME = "Niuxes";
	String RANDOM_UUID = "ecb5e585-e248-4582-ae10-9f8f2f990954";
	Context c = this;
	
	// Variaveis
	BluetoothAdapter bluetoothAdapter;
	ArrayList<BluetoothDevice> listaDeAparelhos;
	int indice;
	int threadsTrabalhando;
	boolean procurandoConexao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buscando);
		
		if (DEBUG) Log.v(TAG, ">>> onCreate <<<");
		
		// Representa o bluetooth do usuário
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		// Vendo se o celular tem bluetooth
		if (bluetoothAdapter == null) {
			Toast.makeText(this,  R.string.nao_tem_bt, Toast.LENGTH_LONG).show();
			this.onBackPressed();
		}
		
		// Inicializando a lista
		listaDeAparelhos = new ArrayList<BluetoothDevice> ();
		
		// Para que a activity sejá avisada sobre ter descoberto um aparelho, precisa registrar um intentFilter
		IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(bdr, intentFilter);
		intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(bdr, intentFilter);
		
	}
	
	@Override
	protected void onStart() {
		
		if (DEBUG) Log.v(TAG, ">>> onStart <<<");
		
		procurandoConexao = true;
		indice = 0;
		threadsTrabalhando = 0;

		// Se o bluetooth estiver desativado
		if (bluetoothAdapter.isEnabled() == false) {
			Intent ativarBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(ativarBT, REQUEST_ENABLE_BT);
		} else {
			btBusca();
		}
		
		if (DEBUG) Log.v(TAG, ">>> Aparelho visivel para outros por 300s <<<");
		
		// Permitir que sejá descoberto por outros aplicativos
		Intent serDescoberto = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		serDescoberto.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, TEMPO_DESCOBERTO);
		startActivityForResult(serDescoberto, REQUEST_BE_DISCOVERABLE);
		
		super.onStart();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (DEBUG) Log.v(TAG, ">>> onActivityResult <<<");
		
		// Resultado da tentativa de ligar bluetooth
		if (requestCode == REQUEST_ENABLE_BT) {
			// Se ocorreu tudo certo
			if (resultCode == Activity.RESULT_OK) {
				btBusca();
			} 
			// Caso não tenha ocorrido tudo certo
			else if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(this, R.string.bluetooth_desativado, Toast.LENGTH_SHORT).show();
				this.onBackPressed();
			}
		}
		
		// Caso o usuário tenha se recusado a ser descoberto por outros aparelhos
		if (requestCode == REQUEST_BE_DISCOVERABLE) {
			if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, R.string.bluetooth_nao_descoberto, Toast.LENGTH_LONG).show();
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/*
	 *  Começa após ter certeza e bluetooth esta funcionando
	 */
	public void btBusca() {
		
		if (DEBUG) Log.v(TAG, ">>> btBusca <<<");
		
		// Pegando os celulares já paired
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

		// Salvando eles em um ArrayAdapter para poder depois se comunicar com eles
		if (pairedDevices.size() > 0) {
			
			if (DEBUG) Log.v(TAG, ">>> pairedDevices.size > 0 <<<");
			
			// Adicionando cada um dos aparelhos na lista
			for (BluetoothDevice aparelho : pairedDevices) {
				listaDeAparelhos.add(aparelho);
			}
			
		}
		
		// Caso já esteja fazendo busca, cancelar
		if (bluetoothAdapter.isDiscovering()) {
			bluetoothAdapter.cancelDiscovery();
		}
		
		// Começar uma busca por aparelhos e caso tenha ocorrido algum erro, avisar.
		if (!bluetoothAdapter.startDiscovery()) {
			
			if (DEBUG) Log.v(TAG, ">>> não conseguio inicializar o startDiscovery <<<");
			
			Toast.makeText(this, R.string.erro_ao_tentar_descobrir, Toast.LENGTH_LONG).show();
			onDestroy();
		}

	}
	
	// Broadcast responsavel por ouvir se o foi encontrado algum aparelho com bluetooth
	public final BroadcastReceiver bdr = new BroadcastReceiver () {
		@Override
		public void onReceive(android.content.Context context, Intent intent) {
			
			if (DEBUG) Log.v(TAG, ">>> onReceive <<<");
			
			// String para guardar a ação
			String action = intent.getAction();
			
			// Se encontrar um aparelho
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				
				if (DEBUG) Log.v(TAG, ">>> Encontrou um aparelho pelo broadcast <<<");
				
				// Pegar as informações do aparelho encontrado (que estão dentro do intent)
				BluetoothDevice aparelho = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				
				// Só botar na lista se já não estiver emparelhado
				if (aparelho.getBondState() != BluetoothDevice.BOND_BONDED) {
					
					if (DEBUG) Log.v(TAG, ">>> Aparelho encontrado não estava na lista <<<");
					
					// Adiciona novo aparelho na lista
					listaDeAparelhos.add(aparelho);

					if (DEBUG) Log.v(TAG, ">>> Aparelho: " + aparelho.getName() + " <<<");
				}	
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				if (DEBUG) Log.v(TAG, ">>> startDiscovery terminou <<<");
				
				// Após o discovery ter terminado, começar a servir de server
				Thread x = new Thread(new ConexaoServerThread());
				x.start();
				
				Thread y = new Thread(new ConexaoClientThread());
				y.start();
			}
			
		};
	};
	
	// 
	public class ConexaoServerThread extends Thread {
		
		private final BluetoothServerSocket mmServerSocket;
		
		public ConexaoServerThread() {

			if (DEBUG) Log.v(TAG, ">>> ConexaoServerThread <<<");
			
			// Usar um serverSocket temporario já que o outro é final
			BluetoothServerSocket tmp = null;
			try {
				UUID uuid = UUID.fromString(RANDOM_UUID);
				tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NOME, uuid);
			} catch (IOException e) { 
				e.printStackTrace();
			}
			mmServerSocket = tmp;
		}
		
		public void run() {
			BluetoothSocket socket = null;
			
			if (DEBUG) Log.v(TAG, ">>> run do ConexaoServerThread <<<");
			
			// Continuar esperando resposta para o socket
			while (procurandoConexao) {
				
				if (DEBUG) Log.v(TAG, ">>> Esperando resposta por socket <<<");
				
				try {
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					break;
				}
				
				// Caso tenha resposta
				if (socket != null) {
					if (DEBUG) Log.v(TAG, ">>> Alguem se connectou a você (socket) <<<");
					procurandoConexao = false;
					// TODO chamar uma função que vai cuidar da conexao, passando o socket
					this.cancel();
					break;
				}
			}
		}
		
		public void cancel() {
			if (DEBUG) Log.v(TAG, ">>> cancel do FazerConexaoThread <<<");
			try {
				mmServerSocket.close();
			} catch (IOException e) { }
		}
		
	}
	
	public class ConexaoClientThread extends Thread {
		
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		
		boolean rodar=true;
		
		public ConexaoClientThread() {

			if (DEBUG) Log.v(TAG, ">>> ConexaoClientThread <<<");
			
			BluetoothDevice aparelho = null;
			
			// pegar um dos aparelhos na lista para tentar fazer conexao
			if (indice < listaDeAparelhos.size() && threadsTrabalhando < 1) {
				
				if (DEBUG) Log.v(TAG, ">>> ClientThread: " + Integer.toString(indice) + "s<<<");
				
				aparelho = listaDeAparelhos.get(indice);
				indice ++;
				
				// Usar um serverSocket temporario já que o outro é final
				BluetoothSocket tmp = null;
				mmDevice = aparelho;
				
				try {
					UUID uuid = UUID.fromString(RANDOM_UUID);
					tmp = aparelho.createRfcommSocketToServiceRecord(uuid);
				} catch (IOException e) { 
					e.printStackTrace();
				}
				mmSocket = tmp;
				
				threadsTrabalhando++;
			} else {
				mmSocket = null;
				mmDevice = null;
				this.rodar=false;
				
				if (DEBUG) Log.v(TAG, ">>> ConexaoClientThread cancelada <<<");
				
			}
		}
		
		public void run() {
			
			if (!rodar) return;
			
			if (DEBUG) Log.v(TAG, ">>> run da ConexaoClientThread <<<");
			
			// Cancelar discovery porque deixa mais lerda a conexao
			bluetoothAdapter.cancelDiscovery();
			
			try {
				// Tentar conectar com o outro aparelho pelo socket, vai parar qnd completar ou ocorrer uma exception
				mmSocket.connect();
			} catch (IOException connectException) {
				// Nao conseguio se conectar
				try {
					mmSocket.close();
				} catch (IOException closeException) { }
				threadsTrabalhando--;
				Thread x = new Thread(new ConexaoClientThread());
				x.start();
				return;
			}
			
			if (DEBUG) Log.v(TAG, ">>> ConexaoClientThread conseguio se conectar a alguém <<<");
			
			procurandoConexao = false;
			// TODO fazer uma função para controlar o socket, passando socket
		}
		
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) { }
		}
		
	}
	
	@Override
	public void onBackPressed() {
		
		if (DEBUG) Log.v(TAG, ">>> onBackPressed <<<");
		
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		
		if (DEBUG) Log.v(TAG, ">>> onDestroy <<<");
		
		// Cancelar a busca para que não gaste mais do aparelho
		if (bluetoothAdapter != null)
			bluetoothAdapter.cancelDiscovery();
		
		// Desregistrando o broadcastReceiver
		this.unregisterReceiver(bdr);
		
		super.onDestroy();
	}
	
}
