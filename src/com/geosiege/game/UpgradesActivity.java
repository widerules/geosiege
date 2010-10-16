package com.geosiege.game;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.geosiege.game.core.GameState;
import com.geosiege.game.upgrade.Upgrade;
import com.zeddic.game.common.ui.SeparatedListAdapter;

public class UpgradesActivity extends Activity {
  
  private ListView listView;
  private SeparatedListAdapter adapter;
  
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    GameState.setup(this);
    
    GameState.analytics.trackPageView("/upgrades");

    /*TextView textview = new TextView(this);
    textview.setText("Ship upgrades not yet available in the beta.");
    setContentView(textview);*/
    
    setContentView(R.layout.upgrades);
    
    List<Upgrade> upgrades = new ArrayList<Upgrade>();
    upgrades.add(new Upgrade("blah", "Speed"));
    upgrades.add(new Upgrade("blah", "Health"));
    upgrades.add(new Upgrade("blah", "Gun"));
    
    
    adapter = new SeparatedListAdapter(this);
    adapter.addSection("Ship", getUpgradesAdapter(upgrades));
    /*for (MenuLevelGroup group : Levels.ALL_LEVELS) {
      adapter.addSection(group.name, getLevelAdapter(group.levels));
    }*/
      
    listView = (ListView)findViewById(R.id.upgradesListView);
    
    //View header = (View)getLayoutInflater().inflate(R.layout.upgrades_header, null);
    //listView.addHeaderView(header);
    
    listView.setAdapter(adapter);
    
    listView.setOnItemClickListener(new OnItemClickListener() {

      public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
        
        //MenuLevel selected = (MenuLevel) listView.getAdapter().getItem(position);
      }
    });
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    GameState.cleanup();
  }
  
  private MenuLevelAdapter getUpgradesAdapter(List<Upgrade> upgrades) {
    return new MenuLevelAdapter(this, R.layout.upgraderow, upgrades);
  }
  
  private class MenuLevelAdapter extends ArrayAdapter<Upgrade> {

    private List<Upgrade> upgrades;

    public MenuLevelAdapter(Context context, int rowLayoutId, List<Upgrade> upgrades) {
      super(context, rowLayoutId, upgrades);
      this.upgrades = upgrades;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View v = convertView;
      if (v == null) {
        LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.upgraderow, parent, false);
      }
      Upgrade o = upgrades.get(position);
      if (o != null) {
        TextView title = (TextView) v.findViewById(R.id.upgrade_name);
        title.setText(o.name);
        
        TextView score = (TextView) v.findViewById(R.id.upgrade_details);
        
        /*ImageView levelIcon = (ImageView) v.findViewById(R.id.level_icon);
        levelIcon.setImageResource(o.getLevelIconResource());*/
      }
      return v;
    }
  }
}