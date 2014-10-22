package com.example.niuxes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
//    
//    public void jogar (View view) {
//    	Intent i = new Intent(this, JogarActivity.class);
//    	startActivity(i);
//    }
//    
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
}
