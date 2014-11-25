package com.example.background;

import android.R;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

public class Busca extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "Buscando adversário", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Busca cancelada", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO
		
		
		return super.onStartCommand(intent, flags, startId);
	}
	
}
