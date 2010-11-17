package com.geosiege.game.menu;

import java.util.Arrays;
import java.util.List;

import com.geosiege.game.R;

public class Levels {
  
  // TODO(scott@zeddic.com): Move these to a data file.
  
  public static final MenuLevelGroup BETA_LEVELS = new MenuLevelGroup(
      "Beta Levels",
      Arrays.asList(
          new MenuLevel("1.txt", Difficulty.EASY, false),
          new MenuLevel("2.txt", Difficulty.HARD, false)
      ));
  
  public static final MenuLevelGroup ENEMY_CHALLENGES = new MenuLevelGroup(
      "Enemy Challenges",
      Arrays.asList(
          new MenuLevel("1.txt", Difficulty.EASY, false),
          new MenuLevel("1.txt", Difficulty.MODERATE, false),
          new MenuLevel("2.txt", Difficulty.HARD, false)
      ));
  
  public static final MenuLevelGroup EASY_LEVELS = new MenuLevelGroup(
      "Easy",
      Arrays.asList(
          new MenuLevel("1.txt", Difficulty.SIMPLE, false),
          new MenuLevel("1.txt", Difficulty.SIMPLE, false),
          new MenuLevel("1.txt", Difficulty.EASY, false)
      ));
  
  public static final MenuLevelGroup MEDIUM_LEVELS = new MenuLevelGroup(
      "Medium",
      Arrays.asList(
          new MenuLevel("1.txt", Difficulty.EASY, false)
      ));
  
  public static final MenuLevelGroup HARD_LEVELS = new MenuLevelGroup(
      "HARD",
      Arrays.asList(
          new MenuLevel("1.txt", Difficulty.MODERATE, false)
      ));

  public static final List<MenuLevelGroup> ALL_LEVELS = Arrays.asList(
      BETA_LEVELS
      /*ENEMY_CHALLENGES,
      EASY_LEVELS,
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
  
  public static MenuLevel getLevelByFileName(String file) {
    
    for (MenuLevelGroup levelGroup : Levels.ALL_LEVELS) {
      for (MenuLevel level : levelGroup.levels) {
        if (level.file.equalsIgnoreCase(file)) {
          return level;
        }
      }
    }
    
    return null;
  }
}
