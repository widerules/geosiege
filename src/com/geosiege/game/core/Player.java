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

package com.geosiege.game.core;

public class Player {

  public float maxHealth;
  public int experience;
  public int lives;
  public String experienceString;
  
  public Player() {
    maxHealth = 60;
    experience = 0;
    addExp(0);
  }
  
  public void addExp(int exp) {
    experience += exp;
    experienceString = "Score: " + Integer.toString(experience);
  }
}
