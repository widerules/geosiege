package com.geosiege.game.effects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;

import com.geosiege.game.resources.GameResources;
import com.zeddic.game.common.GameObject;
import com.zeddic.game.common.transistions.Range;
import com.zeddic.game.common.transistions.Transitions;
import com.zeddic.game.common.util.Countdown;

public class TextFlash extends GameObject {
  
  private static final Paint TEXT_PAINT;
  private static final LinearGradient SHADER;
  static {
    SHADER = new LinearGradient(0, 0, 0, 20,
        new int[] { Color.rgb(227, 178, 4), Color.rgb(234, 243, 7), Color.rgb(227, 178, 4) },
        null, Shader.TileMode.MIRROR);
    
    TEXT_PAINT = new Paint();
    TEXT_PAINT.setTextSize(35);
    TEXT_PAINT.setStrikeThruText(false);
    TEXT_PAINT.setUnderlineText(false);
    TEXT_PAINT.setStrokeWidth(3.5f);
    TEXT_PAINT.setTypeface(GameResources.squareFont);
    TEXT_PAINT.setColor(Color.argb(255, 30, 30, 30));
    TEXT_PAINT.setStyle(Style.FILL);
    TEXT_PAINT.setTextAlign(Align.CENTER);
    TEXT_PAINT.setShader(SHADER);  
  }
  
  private String message = "!";
  private Countdown flashCountdown = new Countdown(2000);
  private Range alphaRange = new Range(255, 0);
  private Range scaleRange = new Range(.5f, 2f);
  private float x;
  private float y;
  
  private boolean flashing;
  
  public TextFlash() {
    reset();
  }

  public void flashMessage(float x, float y, String message) {
    reset();
    this.message = message;
    this.flashing = true;
    this.flashCountdown.start();
    this.x = x;
    this.y = y;
  }
  
  @Override
  public void reset() {
    enable();
    x = 0;
    y = 0;
    flashing = false;
    flashCountdown.reset();
  }
  
  @Override
  public void draw(Canvas c) {
    if (!flashing) {
      return;
    }

    float progress = (float) flashCountdown.getProgress();
    TEXT_PAINT.setAlpha((int) alphaRange.getValue(progress));
    float scale = scaleRange.getValue(progress, Transitions.LINEAR);
    
    c.save();
    
    c.translate(x, y);
    c.scale(scale, scale);
    
    c.drawText(message, 0, 0, TEXT_PAINT);
    c.scale(scale, scale);
    c.restore();
  }
  
  @Override
  public void update(long time) {
    flashCountdown.update(time);
    if (flashCountdown.isDone()) {
      flashing = false;
      kill();
    }
  }
}
