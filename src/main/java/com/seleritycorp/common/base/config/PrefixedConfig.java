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

package com.seleritycorp.common.base.config;

import java.util.concurrent.TimeUnit;

/**
 * View on a Config at a given prefix.
 */
public class PrefixedConfig implements Config {
  private final String prefix;
  private final Config config;

  /**
   * Creates a view on the given config at the given prefix.
   * 
   * @param config The Config to get a view on
   * @param prefix The key prefix to get the view at
   */
  PrefixedConfig(Config config, String prefix) {
    this.config = config;
    this.prefix = prefix + ".";
  }

  @Override
  public String get(String key) {
    return config.get(prefix + key);
  }

  @Override
  public String get(String key, String defaultValue) {
    return config.get(prefix + key, defaultValue);
  }

  @Override
  public int getInt(String key) {
    return config.getInt(prefix + key);
  }

  @Override
  public int getInt(String key, int defaultValue) {
    return config.getInt(prefix + key, defaultValue);
  }

  @Override
  public long getLong(String key) {
    return config.getLong(prefix + key);
  }

  @Override
  public long getLong(String key, long defaultValue) {
    return config.getLong(prefix + key, defaultValue);
  }

  @Override
  public float getFloat(String key) {
    return config.getFloat(prefix + key);
  }

  @Override
  public float getFloat(String key, float defaultValue) {
    return config.getFloat(prefix + key, defaultValue);
  }

  @Override
  public double getDouble(String key) {
    return config.getDouble(prefix + key);
  }

  @Override
  public double getDouble(String key, double defaultValue) {
    return config.getDouble(prefix + key, defaultValue);
  }

  @Override
  public boolean getBoolean(String key) {
    return config.getBoolean(prefix + key);
  }

  @Override
  public boolean getBoolean(String key, boolean defaultValue) {
    return config.getBoolean(prefix + key, defaultValue);
  }

  @Override
  public <T extends Enum<T>> T getEnum(Class<T> clazz, String key) {
    return config.getEnum(clazz, prefix + key);
  }

  @Override
  public <T extends Enum<T>> T getEnum(Class<T> clazz, String key, T defaultValue) {
    return config.getEnum(clazz, prefix + key, defaultValue);
  }

  @Override
  public long getDurationMillis(String key) {
    return config.getDurationMillis(prefix + key);
  }

  @Override
  public long getDurationMillis(String key, long defaultDuration) {
    return config.getDurationMillis(prefix + key, defaultDuration);
  }

  @Override
  public long getDurationMillis(String key, long defaultDuration, TimeUnit defaultUnit) {
    return config.getDurationMillis(prefix + key, defaultDuration, defaultUnit);
  }

  @Override
  public long getDurationSeconds(String key) {
    return config.getDurationSeconds(prefix + key);
  }

  @Override
  public long getDurationSeconds(String key, long defaultDuration) {
    return config.getDurationSeconds(prefix + key, defaultDuration);
  }

  @Override
  public long getDurationSeconds(String key, long defaultDuration, TimeUnit defaultUnit) {
    return config.getDurationSeconds(prefix + key, defaultDuration, defaultUnit);
  }

}
