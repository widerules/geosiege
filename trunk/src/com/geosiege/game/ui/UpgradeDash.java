package com.geosiege.game.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.geosiege.game.R;

public class UpgradeDash extends LinearLayout {
  
  private static int DISABLED = R.drawable.upgrade_bar_empty;
  public static int COLOR_RED = R.drawable.upgrade_bar_red;
  private int color = COLOR_RED;
  
  private ImageView dash;
  private boolean enabled = false;

  public UpgradeDash(Context context) {
    super(context);
    init(context);
  }
  
  public UpgradeDash(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
    init(context);
  }
  
  private void init(Context context) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.upgrade_dash, this);
    
    dash = (ImageView)findViewById(R.id.upgrade_dash_image);
    
    refresh();
  }
  
  public void setColor(int color) {
    this.color = color;
    refresh();
  }
  
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    refresh();
  }
  
  private void refresh() {
    int resource = enabled ? color : DISABLED;
    dash.setImageResource(resource);
  }
}
