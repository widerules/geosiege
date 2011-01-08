package com.geosiege.game;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.geosiege.game.storage.GameStorage;
import com.geosiege.game.storage.Preferences;

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
    
    GameStorage.load(this);
    GameStorage.analytics.trackPageView("/preferences");

    getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
    getPreferenceManager().setSharedPreferencesName(Preferences.PREFERENCES_FILE_NAME);

    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.preferences);
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    GameStorage.save();
  }
}
