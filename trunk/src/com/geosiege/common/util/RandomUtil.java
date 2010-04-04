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

package com.geosiege.common.util;

import java.util.Random;

public class RandomUtil {

  static Random random = new Random();
  
  public static float nextFloat(float max) {
    return random.nextFloat() * max;
  }
  
  public static float nextFloat(float min, float max) {
    float range = max - min;
    return random.nextFloat() * range + min;
  }
  
}
