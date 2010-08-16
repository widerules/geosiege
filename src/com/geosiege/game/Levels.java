package com.geosiege.game;

import java.util.Arrays;
import java.util.List;

public class Levels {
  
  // TODO(scott@zeddic.com): Move these to a data file.
  
  
  public static final MenuLevelGroup BETA_LEVELS = new MenuLevelGroup(
      "Beta Levels",
      Arrays.asList(
          new MenuLevel("1.txt", "Simple", Difficulty.EASY, false),
          new MenuLevel("test.txt", "Survive This!", Difficulty.HARD, false)
      ));
  
  public static final MenuLevelGroup EASY_LEVELS = new MenuLevelGroup(
      "Easy",
      Arrays.asList(
          new MenuLevel("test.txt", "Slow Beginnings", Difficulty.SIMPLE, false),
          new MenuLevel("1.txt", "Shot to kill", Difficulty.SIMPLE, false),
          new MenuLevel("1.txt", "Easy Pickings", Difficulty.EASY, false)
      ));
  
  public static final MenuLevelGroup MEDIUM_LEVELS = new MenuLevelGroup(
      "Medium",
      Arrays.asList(
          new MenuLevel("1.txt", "The Spawning", Difficulty.EASY, false),
          new MenuLevel("1.txt", "Green Diamonds!!", Difficulty.MODERATE, false),
          new MenuLevel("1.txt", "Thats no moon", Difficulty.MODERATE, false)
      ));
  
  public static final MenuLevelGroup HARD_LEVELS = new MenuLevelGroup(
      "HARD",
      Arrays.asList(
          new MenuLevel("1.txt", "Dodge this.", Difficulty.MODERATE, false),
          new MenuLevel("1.txt", "Are you serious?", Difficulty.HARD, false),
          new MenuLevel("1.txt", "What the hell man", Difficulty.INSANE, false)
      ));

  public static final List<MenuLevelGroup> ALL_LEVELS = Arrays.asList(
      BETA_LEVELS
      /*EASY_LEVELS,
      MEDIUM_LEVELS,
      HARD_LEVELS*/);
  
  public static enum Difficulty { SIMPLE, EASY, MODERATE, HARD, INSANE };
  
  private static int[] DIFFICULTY_TO_ICON_MAP = {
      R.drawable.level1,
      R.drawable.level2,
      R.drawable.level3,
      R.drawable.level4,
      R.drawable.level5,
  };
  
  public static int getDifficultyIcon(Difficulty difficulty) {
    return DIFFICULTY_TO_ICON_MAP[difficulty.ordinal()];
  }
  
  public static MenuLevel getLevelWithName(String name) {
    
    for (MenuLevelGroup levelGroup : Levels.ALL_LEVELS) {
      for (MenuLevel level : levelGroup.levels) {
        if (level.name.equalsIgnoreCase(name)) {
          return level;
        }
      }
    }
    
    return null;
  }
}
