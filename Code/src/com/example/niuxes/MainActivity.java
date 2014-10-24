package com.example.niuxes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnMenuItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void jogar (View view) {
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
    	Intent i = new Intent(this, JogoActivity.class);
    	switch(item.getItemId()) {
    	case R.id.menu_offline:
    		i.putExtra("online", false);
    		startActivity(i);
    	return true;
    	case R.id.menu_online:
    		i.putExtra("online", true);
    		startActivity(i);
    	return true;
    	default:
    	return false;
    	}
    }
}
