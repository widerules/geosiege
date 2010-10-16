package com.geosiege.game.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.geosiege.game.ships.Arrow;
import com.geosiege.game.ships.Blinker;
import com.geosiege.game.ships.DaBomb;
import com.geosiege.game.ships.DeathStar;
import com.geosiege.game.ships.EnemyShip;
import com.geosiege.game.ships.SimpleEnemyShip;

public class SpawnPattern {
  
  private static final Map<Character, Class<? extends EnemyShip>> SHIP_KEY;
  
  private static final Character PLAYER_SHIP_TOKEN = 'p';
  private static final Character EMPTY_SPACE_TOKEN = ' ';
  
  static {
    SHIP_KEY = new HashMap<Character, Class<? extends EnemyShip>>();
    SHIP_KEY.put('x', SimpleEnemyShip.class);
    SHIP_KEY.put('*', DeathStar.class);
    SHIP_KEY.put('b', DaBomb.class);
    SHIP_KEY.put('?', Blinker.class);
    SHIP_KEY.put('>', Arrow.class);
    SHIP_KEY.put('<', Arrow.class);
    SHIP_KEY.put('v', Arrow.class);
    SHIP_KEY.put('^', Arrow.class);
  }
  
  public String patternString;
  public boolean centeredOnPlayer;
  public List<SpawnPoint> spawnPoints;
  
  public SpawnPattern() {
    
  }
  
  public SpawnPattern(String patternString) {
    parsePattern(patternString);
  }
  
  public void parsePattern(String pattern) {
    this.patternString = pattern;
    this.spawnPoints = new ArrayList<SpawnPoint>();
    
    // Keeps track of whether a player token is found within the pattern.
    boolean playerFound = false;
    float playerX = 0f;
    float playerY = 0f;
    
    // Split the pattern into rows
    String[] rows = patternString.split("\n");
    if (rows.length < 2 || rows[0].length() < 2) {
      throw new IllegalArgumentException("Pattern must have at least 2x2");
    }
    
    int patternHeight = rows.length;
    int patternWidth = rows[0].length();
    Character token;
    
    // Step through each cell in the pattern matrix. Assign it a relative
    // x,y position based on a 0-1 scale.
    for (int y = 0; y < patternHeight; y++) {
      String row = rows[y];

      if (row.length() != patternWidth) {
        throw new IllegalArgumentException(
            "All rows should be the same length in the pattern.");
      }
      
      for (int x = 0; x < patternWidth; x++) {
        
        // Get the relative position and token for this cell
        token = row.charAt(x);
        float xPercent = (float) (x) / (float) (patternWidth - 1);
        float yPercent = (float) (y) / (float) (patternHeight - 1);
        
        if (token == EMPTY_SPACE_TOKEN) {
          // Don't process white space.
          continue;
        } else if (token == PLAYER_SHIP_TOKEN) {
          // If this is a player token, remember the stats for later.
          playerFound = true;
          playerX = xPercent;
          playerY = yPercent;
        } else if (!SHIP_KEY.containsKey(token)) {
          // Check for invalid tokens.
          throw new IllegalArgumentException("Unknown token '" + token + "'");
        } else {
          // All other tokens represent some ship - remember this spawn point.
          Class<? extends EnemyShip> shipType = SHIP_KEY.get(token);
          
          SpawnPoint spawn;
          
          // The Arrow ship is unique in that the same ship is spawned with
          // different tokens just determining the angle at which they spawn.
          if (shipType == Arrow.class) {
            spawn = new SpawnPoint(xPercent, yPercent, shipType, getArrowShipAngle(token)); 
          } else {
            spawn = new SpawnPoint(xPercent, yPercent, shipType);
          }
          
          spawnPoints.add(spawn);
        }
      }
    }
    
    // If a player token was found, make all the spawn points relative to it.
    if (playerFound) {
      makeRelativeTo(playerX, playerY);
    }
  }
  
  private float getArrowShipAngle(char token) {
    if (token == '>')
      return 0;
    else if (token == '<')
      return 180;
    else if (token == 'v')
      return 90;
    else
      return 270;
  }
  
  /**
   * Makes the spawn points relative to a given x and y position. The x and y
   * positions should be relative on a scale from 0 to 1
   */
  public void makeRelativeTo(float xPercent, float yPercent) {
    centeredOnPlayer = true;
    for (SpawnPoint point : spawnPoints) {
      point.xPercent -= xPercent;
      point.yPercent -= yPercent;
    }
  }
  
  public static class SpawnPoint {
    
    private static final float NO_SPAWN_ANGLE = Float.MIN_VALUE;
    
    public float xPercent;
    public float yPercent;
    public Class<? extends EnemyShip> shipType;
    public float shipSpawnAngle;
    
    public SpawnPoint(
        float xPercent,
        float yPercent,
        Class<? extends EnemyShip> shipType) {
      
      this(xPercent, yPercent, shipType, Float.MIN_VALUE);
    }
    
    public SpawnPoint(
        float xPercent,
        float yPercent,
        Class<? extends EnemyShip> shipType,
        float shipSpawnAngle) {
      
      this.xPercent = xPercent;
      this.yPercent = yPercent;
      this.shipType = shipType;
      this.shipSpawnAngle = shipSpawnAngle;
    }
    
    public boolean hasSpawnAngle() {
      return this.shipSpawnAngle != NO_SPAWN_ANGLE;
    }
  }
}
