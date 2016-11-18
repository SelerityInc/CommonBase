/*
 * Copyright (C) 2016 Selerity, Inc. (support@seleritycorp.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seleritycorp.common.base.test;

import com.seleritycorp.common.base.config.ConfigImpl;

public class SettableConfig extends ConfigImpl {
  public void setInt(String key, int value) {
    set(key, Integer.toString(value));
  }

  public void setLong(String key, int value) {
    set(key, Long.toString(value));
  }

  public void setFloat(String key, float value) {
    set(key, Float.toString(value));
  }

  public void setDouble(String key, double value) {
    set(key, Double.toString(value));
  }

  public void setBoolean(String key, boolean value) {
    set(key, value ? "true" : "false");
  }
}
