package com.geosiege.game;

import com.geosiege.game.core.Preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * An activity to render game preferences. The UI is rendered using android's
 * XML preferences format. 
 * 
 * @author scott@zeddic.com (Scott Bailey)
 *
 */
public class PreferencesActivity extends PreferenceActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
    getPreferenceManager().setSharedPreferencesName(Preferences.PREFERENCES_FILE_NAME);

    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.preferences);
  }
}
