package com.example.niuxes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class ConfiguracoesActivity extends PreferenceActivity {
	
	Context c = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.configuracoes);

		SharedPreferences config = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		
		/*
		 * 
		 * Som
		 * 
		 */
		
		CheckBoxPreference som = (CheckBoxPreference)findPreference("som");
		
		// Marcar se já estivesse marcado
		if (config.getBoolean("som", false) == false)
			som.setChecked(false);
		else
			som.setChecked(true);
		
		// Ao ser marcado gravar no sharedpreferences
		som.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                boolean checked = Boolean.valueOf(newValue.toString());

        		SharedPreferences config = c.getSharedPreferences("config", Context.MODE_PRIVATE);
        		SharedPreferences.Editor editor = config.edit();
        		
        		editor.putBoolean("som", checked);
        		editor.commit();
        		
                return true;
            }
        });

		/*
		 * 
		 * Modo daltonico
		 * 
		 */
		
		CheckBoxPreference daltonico = (CheckBoxPreference)findPreference("daltonico");
		
		// Marcar se já estivesse marcado
		if (config.getBoolean("daltonico", false) == false)
			daltonico.setChecked(false);
		else
			daltonico.setChecked(true);
		
		// Ao ser marcado gravar no sharedpreferences
		daltonico.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                boolean checked = Boolean.valueOf(newValue.toString());

        		SharedPreferences config = c.getSharedPreferences("config", Context.MODE_PRIVATE);
        		SharedPreferences.Editor editor = config.edit();
        		
        		editor.putBoolean("daltonico", checked);
        		editor.commit();
        		
                return true;
            }
        });
	}
}
