package com.example.niuxes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class MainActivity extends Activity implements OnMenuItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void jogar (View view) {
    	SharedPreferences estadoDoJogo = this.getSharedPreferences("jogo", Context.MODE_PRIVATE);
    	Intent i = new Intent(this, JogoActivity.class);
    	
    	if (estadoDoJogo.getBoolean("emJogo", false) == true) {
    		startActivity(i);
    		return;
    	}
    	
    	PopupMenu popup = new PopupMenu(this, view);
    	popup.setOnMenuItemClickListener(this);
    	popup.inflate(R.menu.menu_online_offline);
    	popup.show();
    }
    
    public void pecas (View view) {
    	Intent i = new Intent(this, PecasActivity.class);
    	startActivity(i);
    }
    
    public void configuracoes (View view) {
    	Intent i = new Intent(this, ConfiguracoesActivity.class);
    	startActivity(i);
    }
    
    public void sobre (View view) {
    	Intent i = new Intent(this, SobreActivity.class);
    	startActivity(i);
    }
    
    @Override
    public boolean onMenuItemClick(MenuItem item) {
    	
    	SharedPreferences estadoDoJogo = this.getSharedPreferences("jogo", Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = estadoDoJogo.edit();
    	Intent i = new Intent(this, JogoActivity.class);
    	
    	switch(item.getItemId()) {
	    	case R.id.menu_offline:
	    		editor.putBoolean("jogandoOnline", false);
	    		editor.commit();
	    		startActivity(i);
	    	return true;
	    	
	    	case R.id.menu_online:
	    		editor.putBoolean("jogandoOnline", true);
	    		editor.commit();
	    		startActivity(i);
	    	return true;
	    	
	    	default:
	    	return false;
    	}
    }
}
