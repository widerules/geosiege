package com.geosiege.game;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.geosiege.game.menu.MenuStat;
import com.geosiege.game.storage.GameStorage;

public class StatsActivity extends Activity {
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.stat_list);
    
    GameStorage.load(this);
    GameStorage.analytics.trackPageView("/stats");
    
    loadList();
  }
  
  @Override
  public void onResume() {
    super.onResume();
    loadList();
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    GameStorage.save();
  }
  
  private void loadList() {

    /*SeparatedListAdapter adapter = new SeparatedListAdapter(this);
    adapter.addSection(EASY_LEVELS_TITLE, getLevelAdapter(EASY_LEVELS));
    adapter.addSection(MEDIUM_LEVELS_TITLE, getLevelAdapter(MEDIUM_LEVELS));
    adapter.addSection(HARD_LEVELS_TITLE, getLevelAdapter(HARD_LEVELS));*/
    
    ListView listView = (ListView)findViewById(R.id.statListView);
    listView.setAdapter(getBasicStats());
  }
  
  private MenuStatAdapter getBasicStats() {
    List<MenuStat> stats = new ArrayList<MenuStat>();
    stats.add(new MenuStat(
        "Enemies Killed",
        GameStorage.stats.getNumberOfEnemeiesKilled()));
    
    stats.add(new MenuStat(
        "Time Played",
        GameStorage.stats.getTimePlayedAsString()));
    
    return getStatAdapter(stats);
  }
  
  private MenuStatAdapter getStatAdapter(List<MenuStat> stats) {
    return new MenuStatAdapter(this, R.layout.statrow, stats);
  }
  
  private class MenuStatAdapter extends ArrayAdapter<MenuStat> {

    private List<MenuStat> items;

    public MenuStatAdapter(Context context, int rowLayoutId, List<MenuStat> items) {
      super(context, rowLayoutId, items);
      this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View v = convertView;
      if (v == null) {
        LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.statrow, parent, false);
      }
      MenuStat o = items.get(position);
      if (o != null) {
        TextView title = (TextView) v.findViewById(R.id.stat_row_name);
        title.setText(o.name);
        
        TextView score = (TextView) v.findViewById(R.id.stat_row_value);
        score.setText(o.value);
      }
      return v;
    }
  }
}