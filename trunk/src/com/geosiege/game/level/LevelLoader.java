package com.geosiege.game.level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.geosiege.game.highscore.HighScores;
import com.zeddic.game.common.util.ObjectStockpile;
import com.zeddic.game.common.util.ResourceLoader;

public class LevelLoader {
  
  private static final String PROPERTY_SEPERATOR = ":";
  private static final String PROPERTY_LEVEL_NAME = "LevelName";
  private static final String PROPERTY_LEVEL_ID = "LevelId";
  private static final String PROPERTY_PATTERN_SCALE = "Scale";
  private static final String PROPERTY_TRIGGER = "Trigger";
  private static final String PROPERTY_SPAWN_DELAY = "SpawnDelay";
  private static final String PROPERTY_SPAWN_TIME = "SpawnTime";
  private static final String PROPERTY_PATTERN = "Pattern";
  private static final String PROPERTY_TRIGGER_DEATH_RATIO = "DeadRatio";
  
  private static final String PATTERN_LINE_START = "[";
  private static final String PATTERN_LINE_END = "]";
  
  private static final String TRIGGER_TYPE_DELAY = "Delay";
  private static final String TRIGGER_TYPE_PRIOR_DEAD = "LastDead";
  private static final String TRIGGER_TYPE_PRIOR_DEAD_RATIO = "LastDeadRatio";
  
  private final HighScores scores;
  private final ObjectStockpile enemyStockpile;
  private Level level;
  private long firstSpawnDelay;
  
  public LevelLoader(HighScores scores, ObjectStockpile enemyStockpile) {
    this(scores, enemyStockpile, 0);
  }
  
  public LevelLoader(HighScores scores, ObjectStockpile enemyStockpile, long firstSpawnDelay) {
    this.scores = scores;
    this.enemyStockpile = enemyStockpile;
    this.firstSpawnDelay = firstSpawnDelay;
  }
  
  public Level loadLevel(String fileName) throws IOException {
    return loadLevel(fileName, false);
  }
  
  public Level loadLevel(String fileName, boolean metadataOnly) throws IOException {
    level = new Level(firstSpawnDelay);
    
    InputStream inputStream;
    inputStream = ResourceLoader.loadAsset(fileName);
    
    try {
      InputStreamReader inputReader = new InputStreamReader(inputStream);
      BufferedReader reader = new BufferedReader(inputReader);
      
      loadMetadata(reader);
      if (!metadataOnly) {
        loadSwarms(reader);
      }
    } catch (IOException e) {
      inputStream.close();
      throw e;
    }
    
    if (level.swarms.size() == 0 && !metadataOnly) {
      throw new IOException("No swarms were loaded in the level " + fileName);
    }

    return level;
  }
  
  public void loadMetadata(BufferedReader reader) throws IOException {
    
    level.name = getValue(reader, PROPERTY_LEVEL_NAME);
    level.id = parseInt(getValue(reader, PROPERTY_LEVEL_ID));
    level.loadScores(scores);
    
    reader.readLine();
  }
  
  private String getValue(BufferedReader reader, String propName) throws IOException {
    SimpleProperty prop = loadProperty(reader.readLine());

    if (!prop.name.equalsIgnoreCase(propName)) {
      throw new IOException("Could not load property :" + propName);
    }
    
    return prop.value;

  }
  
  /**
   * Loads the name of the level.
   */
  /*private void loadLevelName(BufferedReader reader) throws IOException {
    SimpleProperty prop = loadProperty(reader.readLine());

    if (!prop.name.equalsIgnoreCase(PROPERTY_LEVEL_NAME)) {
      throw new IOException("Could not find level name!");
    }
    
    level.name = prop.value;
    
    reader.readLine();
  }*/
  
  /**
   * Loads all the swarms in the file and adds them to the level instance.
   */
  private void loadSwarms(BufferedReader reader) throws IOException {
    
    Swarm loaded;
    while ( (loaded = loadSwarm(reader)) != null) {
      level.addSwarm(loaded);
    }
  }
  
  /**
   * Returns a single swarm loaded from the reader. Returns null if the
   * end of the file has been reached and there is no more swarms to be read.
   */
  private Swarm loadSwarm(BufferedReader reader) throws IOException {
    
    SwarmBuilder builder = new SwarmBuilder(enemyStockpile, level.map);
    
    String line;

    // A swarm is defined by a number of name/value properties, a pattern, 
    // then either a newline or an end of file. For example:
    //
    // Trigger:Delay
    // SpawnDelay:50
    // Pattern:
    // [xxx]
    // [x x]
    // [xxx]

    while ((line = reader.readLine()) != null) {
      
      SimpleProperty prop = loadProperty(line);
      if (prop.name.equalsIgnoreCase(PROPERTY_PATTERN_SCALE)) {
        builder.withSpawnScale(parseInt(prop.value));
        
      } else if (prop.name.equalsIgnoreCase(PROPERTY_TRIGGER)) {
        parseTriggerType(builder, prop.value);
        
      } else if (prop.name.equalsIgnoreCase(PROPERTY_SPAWN_TIME)) {
        builder.withSpawnTime(parseInt(prop.value));
      
      } else if (prop.name.equalsIgnoreCase(PROPERTY_SPAWN_DELAY)) {
        builder.withSpawnDelay(parseInt(prop.value));
        
      } else if (prop.name.equalsIgnoreCase(PROPERTY_TRIGGER_DEATH_RATIO)) {
        builder.withPreviousSwarmDeathRatio(parseFloat(prop.value));
        
      } else if (prop.name.equalsIgnoreCase(PROPERTY_PATTERN)) {
        builder.withPattern(parsePattern(reader));
        
        // Pattern marks the end of swarm data!
        return builder.build();
      } 
    }
    
    return null;
  }
  
  /**
   * Parses a trigger type from a string value and updates the swarm builder.
   */
  private void parseTriggerType(SwarmBuilder builder, String value) throws IOException {
    
    if (value.equalsIgnoreCase(TRIGGER_TYPE_DELAY)) {
      builder.spawnedAfterDelay();
    } else if (value.equalsIgnoreCase(TRIGGER_TYPE_PRIOR_DEAD)) {
      builder.spawnedAfterPreviousSwarmDead();
    } else if (value.equalsIgnoreCase(TRIGGER_TYPE_PRIOR_DEAD_RATIO)) {
      builder.spawnedAfterPreviousSwarmDeathRatio();
    } else {
      throw new IOException("Unknown trigger type: " + value);
    }
  }
  
  /**
   * Parses a pattern until either the end of the file or a new line is reached.
   */
  private SpawnPattern parsePattern(BufferedReader reader) throws IOException {
    
    StringBuilder pattern = new StringBuilder();
    String line = reader.readLine();
    
    boolean firstLine = true;
    while (line != null && line.length() > 0 ) {
    
      if (!line.endsWith(PATTERN_LINE_END) || !line.startsWith(PATTERN_LINE_START)) {
        throw new IOException(
            "Pattern line '" +line + "' did not have a line start/end character.");
      }
      
      if (firstLine) {
        firstLine = false;
      } else {
        pattern.append("\n");
      }
      
      String patternLine = line.substring(1, line.length() - 1);
      pattern.append(patternLine);
      
      line = reader.readLine();
    }
    
    SpawnPattern spawnPattern = new SpawnPattern();
    spawnPattern.parsePattern(pattern.toString());
    return spawnPattern;
  }
  
  /**
   * Loads a key/value pair from a single line.
   */
  private SimpleProperty loadProperty(String line) throws IOException {
    
    int propertySplitPoint = line.indexOf(PROPERTY_SEPERATOR);
    if (propertySplitPoint == -1) {
      throw new IOException(
          String.format("Unable to find seperator %s when parsing line '%s'",
              PROPERTY_SEPERATOR, line));
    }
    
    // Get the name.
    String name = line.substring(0, propertySplitPoint);
    
    // Load the value, checking for a case where there is no value after
    // the separator.
    String value;
    if (line.length() - 1 == propertySplitPoint) {
      value = "";
    } else {
      value = line.substring(propertySplitPoint + 1);
    }
    
    return new SimpleProperty(name, value);
  }
  
  /**
   * Parses an int from string. Returns 0 on error.
   */
  private int parseInt(String value) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
  
  /**
   * Parses a float from string. Returns 0 on error.
   */
  private float parseFloat(String value) {
    try {
      return Float.parseFloat(value);
    } catch (NumberFormatException e) {
      return 0f;
    }
  }
  
  private class SimpleProperty {
    public final String name;
    public final String value;
    public SimpleProperty(String name, String value) {
      this.name = name;
      this.value = value;
    }
  }
}
