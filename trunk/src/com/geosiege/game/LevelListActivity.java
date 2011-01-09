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

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.geosiege.game.menu.Levels;
import com.geosiege.game.menu.MenuLevel;
import com.geosiege.game.menu.MenuLevelGroup;
import com.geosiege.game.storage.GameStorage;
import com.zeddic.game.common.ui.SeparatedListAdapter;

public class LevelListActivity extends Activity {
  
  private ListView listView;
  private SeparatedListAdapter adapter;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    GameStorage.load(this);
    GameStorage.analytics.trackPageView("/levels");
    
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.level_list);
 
    adapter = new SeparatedListAdapter(this);
    for (MenuLevelGroup group : Levels.ALL_LEVELS) {
      adapter.addSection(group.name, getLevelAdapter(group.levels));
    }
      
    listView = (ListView)findViewById(R.id.levelsListView);
    listView.setAdapter(adapter);
    
    listView.setOnItemClickListener(new OnItemClickListener() {

      public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
        
        MenuLevel selected = (MenuLevel) listView.getAdapter().getItem(position);
        startLevel(selected);
      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    GameStorage.save();
  }
  
  private void startLevel(MenuLevel level) {
    Intent intent = new Intent(this, GameActivity.class);
    
    // Store the desired level in a bundle that can be parsed from the game
    // activity.
    Bundle bundle = new Bundle();
    level.storeInBundle(bundle);    
    intent.putExtras(bundle);
    
    startActivity(intent);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    adapter.notifyDataSetChanged();
  }
  
  private MenuLevelAdapter getLevelAdapter(List<MenuLevel> levels) {
    return new MenuLevelAdapter(this, R.layout.levelrow, levels);
  }
  
  private class MenuLevelAdapter extends ArrayAdapter<MenuLevel> {

    private List<MenuLevel> items;

    public MenuLevelAdapter(Context context, int rowLayoutId, List<MenuLevel> items) {
      super(context, rowLayoutId, items);
      this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View v = convertView;
      if (v == null) {
        LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.levelrow, parent, false);
      }
      MenuLevel o = items.get(position);
      if (o != null) {
        TextView title = (TextView) v.findViewById(R.id.level_row_title);
        title.setText(o.level.name);
        
        TextView score = (TextView) v.findViewById(R.id.level_row_score);
        
        // Force reloading the scores in case a new one was set since initially
        // loading the file.
        o.level.loadScores(GameStorage.scores);
        String scoreText = o.level.highscoreSet ? Integer.toString(o.level.highscore) : "-";
        score.setText("Highscore: " + scoreText);
        // score.setVisibility(View.GONE);
        
        ImageView levelIcon = (ImageView) v.findViewById(R.id.level_icon);
        levelIcon.setImageResource(o.getLevelIconResource());
      }
      return v;
    }
  }
}