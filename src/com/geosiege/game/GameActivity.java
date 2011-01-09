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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geosiege.game.core.GameState;
import com.geosiege.game.core.GeoSiegeGame;
import com.geosiege.game.menu.MenuLevel;
import com.geosiege.game.resources.GameResources;
import com.geosiege.game.storage.GameStorage;
import com.zeddic.game.common.GameSurface;
import com.zeddic.game.common.Updater;

public class GameActivity extends Activity {
  
  private GameSurface surface;
  private GeoSiegeGame game;
  private Updater updater;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.game);
    
    GameState.setup(this);
    GameStorage.load(this);
    
    // Get the choosen level from the intent parameters.
    Bundle bundle = getIntent().getExtras();
    MenuLevel selectedLevel = MenuLevel.getLevelFromBundle(bundle);
    GameStorage.analytics.trackPageView("/levels/" + selectedLevel.level.id);
    
    // Create the drawing Surface;
    surface = new GameSurface(this);
    surface.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    
    // Create the game.
    game = new GeoSiegeGame(selectedLevel);
    
    // Create the updater to coordinate the background update/render thread.
    updater = new Updater(surface);
    updater.setGame(game);
    updater.showFps(GameStorage.preferences.getShowFps());
    
    // Set the updater to listen to various game events and trigger
    // appropriate dialog boxes. The game can't do this itself from a non-UI
    // thread.
    updater.addEventHandler(GeoSiegeGame.GAME_EVENT_DEAD, new DeadHandler());
    updater.addEventHandler(GeoSiegeGame.GAME_EVENT_LOADED, new LoadedHandler());
    updater.addEventHandler(GeoSiegeGame.GAME_EVENT_WIN, new WinHandler());
    updater.addEventHandler(GeoSiegeGame.GAME_EVENT_STARTED, new GameStartHandler());
    
    // Add the drawing surface to the screen.
    LinearLayout container = (LinearLayout)findViewById(R.id.game);
    container.addView(surface);
    
    showMessage("Loading");
    
    // Start the background thread which will do our setup.
    updater.start();
  }
  
  @Override
  public void onPause() {
    super.onPause();
    GameState.player.scorer.saveHighscore();
    GameResources.music.pause();
  }
  
  @Override
  public void onResume() {
    super.onResume();
    GameResources.music.resume();
  }
  
  @Override
  public void onDestroy() {
    Log.d(GameActivity.class.getName(), "Destroying Game Activity");
    updater.stop();
    saveEarnings();
    GameState.cleanup();
    GameStorage.save();
    super.onDestroy();
  }
  
  private void saveEarnings() {
    GameState.player.scorer.saveHighscore();
    GameState.player.scorer.saveEarnedMoney();
    GameState.player.scorer.reset();
  }
    
  private void showMessage(String message) {    
    TextView messageText = (TextView)findViewById(R.id.gameMessageText);
    messageText.setText(message);
    
    LinearLayout messageContainer = (LinearLayout)findViewById(R.id.gameMessageContainer);
    messageContainer.setVisibility(View.VISIBLE);
  }
  
  private void hideMessage() {
    LinearLayout message = (LinearLayout)findViewById(R.id.gameMessageContainer);
    message.setVisibility(View.INVISIBLE);
  }
  
  private class LoadedHandler extends Handler {
    
    @Override
    public void handleMessage(Message msg) {
      hideMessage();
    }
  }
  
  private class GameStartHandler extends Handler {
   
    @Override
    public void handleMessage(Message msg) {
      if (GameStorage.preferences.getPlayMusic()) {
        GameResources.music.playRandomSong();
      }
    }
  }
  
  private class DeadHandler extends Handler {
    
    @Override
    public void handleMessage(Message msg) {
      AlertDialog dialog = new AlertDialog.Builder(GameState.context)
          .setTitle("Game Over")
          .setMessage(
              "Final Score: " + GameState.player.scorer.score + ". " +
              "Earned $" + GameState.player.scorer.getScoreAsMoney())
          .create();
      
      dialog.setButton(
          AlertDialog.BUTTON1, "Play Again",
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              game.restart();
            }
          });
      
      dialog.setButton(
          AlertDialog.BUTTON2, "Quit",
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              GameState.activity.finish();
            }
          });
      
      saveEarnings();
      dialog.show();
    }
  }
  
  private class WinHandler extends Handler {
    
    @Override
    public void handleMessage(Message msg) {
      AlertDialog dialog = new AlertDialog.Builder(GameState.context)
          .setTitle("Level Complete!")
          .setMessage(
              "Final Score: " + GameState.player.scorer.score + ". " +
              "Earned $" + GameState.player.scorer.getScoreAsMoney())
          .create();
      
      dialog.setButton(
          AlertDialog.BUTTON1, "Play Again",
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              game.restart();
            }
          });
      
      dialog.setButton(
          AlertDialog.BUTTON2, "Menu",
          new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              GameState.activity.finish();
            }
          });
      
      saveEarnings();
      dialog.show();
    }
  }
}