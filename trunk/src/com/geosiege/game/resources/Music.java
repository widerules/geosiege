package com.geosiege.game.resources;


import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import com.zeddic.game.common.util.RandomUtil;
import com.zeddic.game.common.util.ResourceLoader;

public class Music {

  private static final String MUSIC_PATH = "music";
  private MediaPlayer music = new MediaPlayer();
  private boolean setup = false;
  
  
  public void playRandomSong() {
    
    // Stop anything working currently.
    stop();
    
    // Get the new song.
    String path = getRandomSongPath();
    
    if (path == null) {
      return;
    }
    
    // Play.
    AssetFileDescriptor fd = getFileDescriptor(path);
    
    if (fd == null) {
      return;
    }
    
    playFileDescriptor(fd);
    setup = true;
    
    // Queue up another song after the music finishes.
    music.setOnCompletionListener(new OnCompletionListener() {
      public void onCompletion(MediaPlayer player) {
        playRandomSong();
      }
    });
  }
  
  public void playFileDescriptor(AssetFileDescriptor fd) {
    
    music = new MediaPlayer();
    try {
      music.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
      music.setAudioStreamType(AudioManager.STREAM_MUSIC);
      music.prepare();
    } catch (IllegalArgumentException e) {
      Log.e(Music.class.getName(), "Music player did not like argument.", e);
    } catch (IllegalStateException e) {
      Log.e(Music.class.getName(), "Music player in poor sate.", e);
    } catch (IOException e) {
      Log.e(Music.class.getName(), "Could not load music.", e);
    } finally { 
      
      try {
        fd.close();
      } catch (IOException e) {
        Log.e(Music.class.getName(), "Could not close music file descriptor.", e);
      }
    }
    
    music.start();
  }
  
  private AssetFileDescriptor getFileDescriptor(String path) {
    AssetFileDescriptor fd;
    try {
      fd = ResourceLoader.a.openFd(path);
    } catch (IOException e) {
      Log.e(Music.class.getName(), "Could not load music: " + path, e);
      return null;
    }
    
    return fd;
  }
  
  private String getRandomSongPath() {
    // Get a list of songs to choose from.
    String[] songPaths = ResourceLoader.getAssetList(MUSIC_PATH);
    
    if (songPaths.length == 0) {
      Log.e(Music.class.getName(), "Could not find any music files!");
      return null;
    }
    
    // Choose one and obtain a file descriptor.
    String songPath = MUSIC_PATH + "/" + songPaths[RandomUtil.nextInt(songPaths.length)];
    
    return songPath;
  }
  
  public void stop() {
    if (setup && music.isPlaying()) {
      music.stop();
    }
    music.reset();
    setup = false;
  }
  
  public void cleanup() {
    stop();
  }
  
  public void pause() {
    if (music.isPlaying()) {
      music.pause();
    }
  }
  
  public void resume() {
    if (setup) {
      music.start();      
    }
  }
}
