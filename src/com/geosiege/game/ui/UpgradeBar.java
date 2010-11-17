package com.geosiege.game.ui;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.geosiege.game.R;

public class UpgradeBar extends LinearLayout {

  private static final int MAX_DASHES = 20;
  private Context context;
  private LinearLayout container;
  private int amountVisible = MAX_DASHES;
  private int amountEnabled = 0;
  
  private ArrayList<UpgradeDash> dashes;
  
  public UpgradeBar(Context context) {
    super(context);
    init(context);
  }
  
  public UpgradeBar(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
    init(context);
  }
  
  private void init(Context context) {
    this.context = context;

    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.upgrade_bar, this);
    
    container = (LinearLayout)findViewById(R.id.upgrade_bar_container);
    
    createDashes(MAX_DASHES);
  }
  
  public void setColor(int color) {
    for(int i = 0; i < MAX_DASHES; i++) {
      dashes.get(i).setColor(color);
    }
  }
  
  public void setVisibleDashes(int amount) {
    if (amount == amountVisible) {
      return;
    }
    
    this.amountVisible = amount;
    for(int i = 0; i < MAX_DASHES; i++) {
      dashes.get(i).setVisibility(i >= amount ? View.INVISIBLE : View.VISIBLE);
    }
  }
  
  public void setEnabledDashes(int amount) {
    if (amount == amountEnabled) {
      return;
    }
    
    this.amountEnabled = amount;
    for(int i = 0; i < MAX_DASHES; i++) {
      dashes.get(i).setEnabled(i >= amount ? false : true);
    }
  }
  
  private void createDashes(int amount) {
    dashes = new ArrayList<UpgradeDash>();
    
    // Clear out the container of any existing dashes.
    container.removeAllViews();
    
    // Create a new set.
    for (int i = 0 ; i < amount ; i++) {
      UpgradeDash dash = new UpgradeDash(context);
      dashes.add(dash);
      container.addView(dash);
    }
  }
}
