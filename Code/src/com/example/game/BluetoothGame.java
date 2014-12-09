package com.example.game;

import android.bluetooth.BluetoothSocket;

public class BluetoothGame {
	
	static BluetoothSocket socket=null;
	static byte[] buffer=null;
	static int bytes;
	
	public static void inicializar (BluetoothSocket socketRecebido) {
		socket = socketRecebido;
	}
	
	public static BluetoothSocket getBTSocket() {
		return socket;
	}

	public static void setBuffer(byte[] x, int y) {
		buffer = x;
		bytes = y;
	}
	
	public static byte[] getBuffer() {
		return buffer;
	}
	
	public static int getByte() {
		return bytes;
	}
}
