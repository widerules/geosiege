package com.geosiege.game;

import com.geosiege.game.core.GameState;
import com.geosiege.game.storage.Preferences;

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
    
    GameState.setup(this);
    GameState.analytics.trackPageView("/preferences");

    getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
    getPreferenceManager().setSharedPreferencesName(Preferences.PREFERENCES_FILE_NAME);

    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.preferences);
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    GameState.cleanup();
  }
}
