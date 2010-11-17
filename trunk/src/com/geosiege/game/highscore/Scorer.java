package com.geosiege.game.highscore;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;

import com.geosiege.game.core.GameState;
import com.geosiege.game.effects.TextFlasher;
import com.geosiege.game.ships.Arrow;
import com.geosiege.game.ships.Blinker;
import com.geosiege.game.ships.DaBomb;
import com.geosiege.game.ships.DeathStar;
import com.geosiege.game.ships.EnemyShip;
import com.geosiege.game.ships.SimpleEnemyShip;
import com.zeddic.game.common.stage.Stage;
import com.zeddic.game.common.stage.StageIncrementor;

/**
 * Calculates and keeps track of the players current score.
 * 
 * @author scott@zeddic.com (Scott Bailey)
 */
public class Scorer {

  private static final int SCORE_TO_MONEY_DIVISOR = 10;
  private static final long TIME_BETWEEN_MULTIKILLS = 500;
  private static final int DEFAULT_POINTS = 5;
  private static final String NEW_HIGHSCORE = "New Highscore!";
  private static final Map<Class<? extends EnemyShip>, Integer> SCORE_MAP;
  static {
    SCORE_MAP = new HashMap<Class<? extends EnemyShip>, Integer>();
    SCORE_MAP.put(SimpleEnemyShip.class, 10);
    SCORE_MAP.put(DeathStar.class, 10);
    SCORE_MAP.put(DaBomb.class, 10);
    SCORE_MAP.put(Blinker.class, 15);
    SCORE_MAP.put(Arrow.class, 8);
  }
 
  private static DecimalFormat format = new DecimalFormat();
  static {
    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
    dfs.setGroupingSeparator(',');
    format.setDecimalFormatSymbols(dfs);
  }

  private StageIncrementor<MultiKillLevel> multiKillTracker =
      new StageIncrementor.Builder<MultiKillLevel>()
          .add(new MultiKillLevel(10, "Multi Kill", 25))
          .add(new MultiKillLevel(20, "Ultra Kill", 50))
          .add(new MultiKillLevel(30, "Inconceivable", 100))
          .add(new MultiKillLevel(45, "Unstoppable!", 250))
          .add(new MultiKillLevel(60, "Impossible!", 500))
          .add(new MultiKillLevel(75, "Godlike!", 1000))
          .add(new MultiKillLevel(100, "HOLY SHIT!", 2000))
          .build();
  
  private StageIncrementor<KillStreakLevel> killStreakTracker =
    new StageIncrementor.Builder<KillStreakLevel>()
        .add(new KillStreakLevel(25, 1.5f))
        .add(new KillStreakLevel(50, 1.7f))
        .add(new KillStreakLevel(100, 2f))
        .add(new KillStreakLevel(150, 2.5f))
        .add(new KillStreakLevel(200, 3f))
        .add(new KillStreakLevel(250, 3.5f))
        .add(new KillStreakLevel(300, 4f))
        .build();
  
  public int score = 0;
  public String scoreString;
  public float multiplier;
  public String multiplierString;
  public boolean hitHighscore = false;
  private long lastKillTime;
  private TextFlasher textFlasher;
  
  public Scorer() {
    this.textFlasher = new TextFlasher();
  }
  
  public Scorer(TextFlasher textFlasher) {
    this.textFlasher = textFlasher;
  }
  
  public void reset() {
    setScore(0);
    resetMultipliers();
    hitHighscore = false;
  }
  
  public void resetMultipliers() {
    multiKillTracker.reset();
    multiplier = 1;
  }
  
  public void recordKill(Class<? extends EnemyShip> enemyType) {
    int points = getPointsForShip(enemyType);
    
    handleKillStreak();
    handleMultiKill();
    
    addPoints(points);
  }
  
  public void saveHighscore() {
    if (hitHighscore) {
      GameState.scores.setHighScore(GameState.level.id, score);
    }
  }

  public int getScoreAsMoney() {
    return score / SCORE_TO_MONEY_DIVISOR;
  }
  
  public void addPoints(int points) {

    points = (int) ((float) points * multiplier);
    
    setScore(score + points);
    
    if (!hitHighscore && GameState.level != null && score > GameState.level.highscore) {
      textFlasher.flashMessage(NEW_HIGHSCORE);
      hitHighscore = true;
    }
  }
  
  private void setScore(int score) {
    this.score = score;
    scoreString = format.format(score);
  }
 
  private void handleKillStreak() {
    killStreakTracker.add();
  }
  
  private void handleMultiKill() {
    
    long now = System.currentTimeMillis();
    if (now - lastKillTime < TIME_BETWEEN_MULTIKILLS) {
      multiKillTracker.add();
    } else if (multiKillTracker.isIncrementing()) {
      multiKillTracker.reset();
      multiKillTracker.add();
    }
    lastKillTime = now;
  }
  
  private int getPointsForShip(Class<? extends EnemyShip> enemyType) {
    if (SCORE_MAP.containsKey(enemyType)) {
      return SCORE_MAP.get(enemyType);
    } else {
      return DEFAULT_POINTS;
    }
  }
  
  private void setMultiplier(float multiplier) {
    this.multiplier = multiplier;
    this.multiplierString = "x " + multiplier;
  }
  
  private class MultiKillLevel implements Stage {
    private int kills;
    private String phrase;
    private int pointsRewarded;
    
    public MultiKillLevel(int kills, String phrase, int pointsRewarded) {
      this.kills = kills;
      this.phrase = phrase;
      this.pointsRewarded = pointsRewarded;
    }

    public int getValue() {
      return kills;
    }

    public void onTrigger() {
      textFlasher.flashMessage(phrase, 20);
      textFlasher.flashMessage("+" + pointsRewarded, 60);
      addPoints(pointsRewarded);
    }
  }
  
  private class KillStreakLevel implements Stage {
    private int kills;
    private float streakMultiplier;
    
    public KillStreakLevel(int kills, float streakMultiplier) {
      this.kills = kills;
      this.streakMultiplier = streakMultiplier;
    }

    public int getValue() {
      return kills;
    }

    public void onTrigger() {
      textFlasher.flashMessage(kills + " Kill Streak", -30);
      setMultiplier(streakMultiplier);
    }
  }
}
