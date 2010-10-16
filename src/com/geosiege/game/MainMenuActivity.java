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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.geosiege.game.core.GameState;
import com.geosiege.game.resources.GameResources;

public class MainMenuActivity extends Activity {

  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.mainmenu);
    
    GameState.setup(this);
    
    TextView title = (TextView)findViewById(R.id.mainMenuTitle);
    title.setTypeface(GameResources.spaceFont);
    
    //GameResources.init(getApplicationContext());
    
    // turn off the window's title bar

    AnimationSet set = new AnimationSet(true);

    //Animation animation = new AlphaAnimation(0.0f, 1.0f);
    
    Animation animation = new ScaleAnimation(2f, 3f, 2f, 3f, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
    animation.setInterpolator(new AccelerateDecelerateInterpolator());
    animation.setStartTime(Animation.START_ON_FIRST_FRAME);
    animation.setDuration(15000);
    animation.setRepeatMode(Animation.REVERSE);
    animation.setRepeatCount(Animation.INFINITE);
    set.addAnimation(animation);
    
    animation = new RotateAnimation(0f, 360f, RotateAnimation.RELATIVE_TO_SELF, .5f, RotateAnimation.RELATIVE_TO_SELF, .5f);
    animation.setInterpolator(new LinearInterpolator());
    animation.setStartTime(Animation.START_ON_FIRST_FRAME);
    animation.setDuration(45000);
    animation.setRepeatMode(Animation.RESTART);
    animation.setRepeatCount(Animation.INFINITE);
    set.addAnimation(animation);

    /*animation = new TranslateAnimation(
        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
        Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
    );
    animation.setDuration(500);
    set.addAnimation(animation);*/

    /*LayoutAnimationController controller =
        new LayoutAnimationController(set, 0.25f);
    controller.start(); */
    
    
    ImageView background = (ImageView) findViewById(R.id.mainMenuBackground);
    //background.startAnimation(animation);
    background.startAnimation(set);
    
    Button startButton = (Button) findViewById(R.id.startGameButton);
    startButton.setOnClickListener(new OnClickListener() {
        public void onClick(View view) {
          onStartGameClick();
        }
    });
    
    Button optionsButton = (Button) findViewById(R.id.optionsButton);
    optionsButton.setOnClickListener(new OnClickListener() {
        public void onClick(View view) {
          onOptionsButtonClick();
        }
    });
  }
  
  private void onStartGameClick() {
    Intent intent = new Intent(this, GameStagingActivity.class);
    startActivity(intent);
  }
  
  private void onOptionsButtonClick() {
    Intent intent = new Intent(this, PreferencesActivity.class);
    startActivity(intent);
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    GameState.cleanup();
  }
}