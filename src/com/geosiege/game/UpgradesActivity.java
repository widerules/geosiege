package com.geosiege.game;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class UpgradesActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("Ship upgrades not yet available in the beta.");
        setContentView(textview);
    }
}