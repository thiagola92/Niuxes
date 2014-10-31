package com.example.niuxes;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;

public class ConfiguracoesActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.configuracoes);
		CheckBoxPreference som = (CheckBoxPreference)findPreference("som");
		som.setChecked(true);
	}
}
