/*
 * Copyright (C) 2010 Geo Siege Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geosiege.game;

import com.geosiege.game.core.GameState;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;

public class GameStagingActivity extends TabActivity {
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.gamestaging);
    
    GameState.setup(this);
    
    Resources res = getResources(); // Resource object to get Drawables
    TabHost tabHost = getTabHost();  // The activity TabHost
    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
    Intent intent;  // Reusable Intent for each tab

    // Create an Intent to launch an Activity for the tab (to be reused)

    // Initialize a TabSpec for each tab and add it to the TabHost
    spec = tabHost.newTabSpec("levels")
        .setIndicator("Levels", res.getDrawable(R.drawable.ic_tab_levels))
        .setContent(new Intent(this, LevelListActivity.class));
    tabHost.addTab(spec);
     
    spec = tabHost.newTabSpec("upgrades")
        .setIndicator("Upgrades", res.getDrawable(R.drawable.ic_tab_upgrades))
        .setContent(new Intent(this, UpgradesActivity.class));
    tabHost.addTab(spec);
    
    spec = tabHost.newTabSpec("stats")
        .setIndicator("Stats", res.getDrawable(R.drawable.ic_tab_stats))
        .setContent(new Intent(this, StatsActivity.class));
    tabHost.addTab(spec);
    
    
    tabHost.setCurrentTab(0);

    /*spec = tabHost.newTabSpec("upgrades").setIndicator("Upgrades").setContent(intent);
    tabHost.addTab(spec);
    
    spec = tabHost.newTabSpec("stats").setIndicator("Stats").setContent(intent);
    tabHost.addTab(spec); */
    /*tabHost.setCurrentTab(1);*/
  }

}