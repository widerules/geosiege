package com.geosiege.game.core;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;

import com.geosiege.game.resources.GameResources;
import com.geosiege.game.ships.PlayerShip;
import com.zeddic.game.common.GameObject;
import com.zeddic.game.common.util.Polygon.PolygonBuilder;

public class GameStatusBar extends GameObject {


  private static final Paint TEXT_PAINT;
  private static final Paint SMALL_TEXT_PAINT;
  private static final Paint HEALTH_PAINT;
  private static final Paint TOP_PAINT;
  private static final Path RIGHT_PATH;
  private static final Path LEFT_PATH;
  private static final Path MIDDLE_SMALL_PATH;
  private static final LinearGradient SHADER;
  private static final LinearGradient HEALTH_SHADER;
  private static final float FULL_HEALTH_WIDTH = 130;
  static {
    TEXT_PAINT = new Paint();
    TEXT_PAINT.setTextSize(35);
    TEXT_PAINT.setStrikeThruText(false);
    TEXT_PAINT.setUnderlineText(false);
    TEXT_PAINT.setStrokeWidth(3.5f);
    TEXT_PAINT.setTypeface(GameResources.squareFont);
    TEXT_PAINT.setTextAlign(Align.RIGHT);
    TEXT_PAINT.setColor(Color.argb(255, 30, 30, 30));

    SMALL_TEXT_PAINT = new Paint(TEXT_PAINT);
    SMALL_TEXT_PAINT.setTextSize(17);
    
    SHADER = new LinearGradient(0, 0, 0, 20,
        new int[] { Color.rgb(227, 178, 4), Color.rgb(234, 243, 7), Color.rgb(227, 178, 4) },
        null, Shader.TileMode.MIRROR);
    HEALTH_SHADER = new LinearGradient(0, 0, FULL_HEALTH_WIDTH, 0,
        new int[] { Color.rgb(227, 178, 4), Color.rgb(234, 243, 7) },
        null, Shader.TileMode.MIRROR);
    

    
    TEXT_PAINT.setShader(SHADER);
    
    HEALTH_PAINT = new Paint(TEXT_PAINT);
    HEALTH_PAINT.setShader(HEALTH_SHADER);
    HEALTH_PAINT.setStyle(Style.FILL);
    
    TOP_PAINT = new Paint();
    TOP_PAINT.setColor(Color.BLACK);
    TOP_PAINT.setStyle(Style.FILL);
    TOP_PAINT.setAlpha(160);
    
    RIGHT_PATH = new PolygonBuilder()
      .add(0, 0)
      .add(0, 40)
      .add(-110, 40)
      .add(-130, 0)
      .build().path;
    
    LEFT_PATH = new PolygonBuilder()
      .add(0, 0)
      .add(0, 40)
      .add(110, 40)
      .add(130, 0)
      .build().path;
    
    MIDDLE_SMALL_PATH = new PolygonBuilder()
      .add(-120, 0)
      .add(120, 0)
      .add(100, 40)
      .add(-100, 40)
      .build().path;
  }

  @Override
  public void draw(Canvas canvas) {

    drawBackground(canvas);
    
    TEXT_PAINT.setStyle(Style.FILL);
    TEXT_PAINT.setTextAlign(Align.CENTER);
    drawTextWithShadow(canvas, TEXT_PAINT, GameState.player.scorer.scoreString, GameState.screenWidth / 2, 30);
    
    if (GameState.player.scorer.multiplier > 1) {
      drawTextWithShadow(
          canvas,
          SMALL_TEXT_PAINT,
          GameState.player.scorer.multiplierString,
          GameState.screenWidth / 2 + 200,
          20);
    }
    
    TEXT_PAINT.setTextAlign(Align.LEFT);
    drawTextWithShadow(canvas, TEXT_PAINT, GameState.player.livesString, 25, 30);
    
    TEXT_PAINT.setStyle(Style.STROKE);
    drawPathWithShadow(canvas,
        PlayerShip.SHAPE.path,
        (float) 80,
        (float) 18,
        .8f,
        -130f);
    
    drawHealth(canvas);
  }
  
  private void drawBackground(Canvas c) {
    drawBackgroundPath(c, LEFT_PATH, -1, -1);
    drawBackgroundPath(c, RIGHT_PATH, GameState.screenWidth, -1);
    drawBackgroundPath(c, MIDDLE_SMALL_PATH, GameState.screenWidth / 2, -1);
  }
  
  private void drawBackgroundPath(Canvas c, Path path, float x, float y) {
    c.save();
    c.translate(x, y);
    TOP_PAINT.setStyle(Style.FILL);
    TOP_PAINT.setColor(Color.BLACK);
    TOP_PAINT.setAlpha(100);
    c.drawPath(path, TOP_PAINT);
    TOP_PAINT.setStyle(Style.STROKE);
    TOP_PAINT.setColor(Color.WHITE);
    c.drawPath(path, TOP_PAINT);
    c.restore();
  }
  
  private void drawHealth(Canvas c) {
    float offset = FULL_HEALTH_WIDTH - FULL_HEALTH_WIDTH * GameState.player.ship.getPercentHealth();
    drawHealth(c, offset);
  }
  
  private void drawHealth(Canvas c, float offset) {
    c.save();
    c.translate(GameState.screenWidth + offset, -1);
    c.drawPath(RIGHT_PATH, HEALTH_PAINT);
    c.restore();
  }
  
  private void drawTextWithShadow(Canvas c, Paint paint, String text, float x, float y) {
    paint.setShader(null);
    c.drawText(text, x + 2, y + 2, paint);
    paint.setShader(SHADER);
    c.drawText(text, x, y, paint);
  }
  
  private void drawPathWithShadow(Canvas c, Path path, float x, float y, float scale, float rotation) {
    
    c.save();
    
    TEXT_PAINT.setShader(null);
    c.translate(x + 2, y + 2);
    c.save();
    c.rotate(rotation);
    c.scale(scale, scale);
    c.drawPath(path, TEXT_PAINT);
    c.restore();

    TEXT_PAINT.setShader(SHADER);
    c.translate(-2, -2);
    c.save();
    c.rotate(rotation);
    c.scale(scale, scale);
    c.drawPath(path, TEXT_PAINT);
    c.restore();
    
    c.restore();
  }
  
  
  @Override
  public void update(long time) {

  }
  
}
