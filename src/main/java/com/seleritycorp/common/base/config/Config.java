/*
 * Copyright (C) 2016-2018 Selerity, Inc. (support@seleritycorp.com)
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
 * Accessor for Configuration data.
 */
public interface Config extends ConfigBase {
  /**
   * Gets the value ot the given key as String with default
   * 
   * @param key key to get value for
   * @param defaultValue default value to use if there is no value at key
   * @return the value at the given key. If there is no value for the given key, defaultValue is
   *         returned.
   */
  public String get(String key, String defaultValue);

  /**
   * Gets the value ot the given key as int
   * 
   * @param key key to get value for
   * @return the value at the given key. If there is no value for the given key, 0 is returned.
   */
  public int getInt(String key);

  /**
   * Gets the value ot the given key as int with default
   * 
   * @param key key to get value for
   * @param defaultValue default value to use if there is no value at key
   * @return the value at the given key. If there is no value for the given key, defaultValue is
   *         returned.
   */
  public int getInt(String key, int defaultValue);

  /**
   * Gets the value ot the given key as long
   * 
   * @param key key to get value for
   * @return the value at the given key. If there is no value for the given key, 0 is returned.
   */
  public long getLong(String key);

  /**
   * Gets the value of the given key as long with default
   * 
   * @param key key to get value for
   * @param defaultValue default value to use if there is no value at key
   * @return the value at the given key. If there is no value for the given key, defaultValue is
   *         returned.
   */
  public long getLong(String key, long defaultValue);

  /**
   * Gets the value ot the given key as float
   * 
   * @param key key to get value for
   * @return the value at the given key. If there is no value for the given key, Nan is returned.
   */
  public float getFloat(String key);

  /**
   * Gets the value ot the given key as float with default
   * 
   * @param key key to get value for
   * @param defaultValue default value to use if there is no value at key
   * @return the value at the given key. If there is no value for the given key, defaultValue is
   *         returned.
   */
  public float getFloat(String key, float defaultValue);

  /**
   * Gets the value ot the given key as double
   * 
   * @param key key to get value for
   * @return the value at the given key. If there is no value for the given key, NaN is returned.
   */
  public double getDouble(String key);

  /**
   * Gets the value ot the given key as double with default
   * 
   * @param key key to get value for
   * @param defaultValue default value to use if there is no value at key
   * @return the value at the given key. If there is no value for the given key, defaultValue is
   *         returned.
   */
  public double getDouble(String key, double defaultValue);

  /**
   * Gets the value ot the given key as boolean
   * 
   * @param key key to get value for
   * @return the value at the given key. If there is no value for the given key, false is returned.
   */
  public boolean getBoolean(String key);

  /**
   * Gets the value ot the given key as boolean with default
   * 
   * @param key key to get value for
   * @param defaultValue default value to use if there is no value at key
   * @return the value at the given key. If there is no value for the given key, defaultValue is
   *         returned.
   */
  public boolean getBoolean(String key, boolean defaultValue);

  /**
   * Gets the value ot the given key as enum.
   * 
   * @param <T> enum to parse.
   * @param clazz class of the enum to parse.
   * @param key key to get the value for
   * @return the value at the given key as clazz. If there is no value for the given key, null is
   *         returned.
   */
  public <T extends Enum<T>> T getEnum(Class<T> clazz, String key);

  /**
   * Gets the value ot the given key as enum with default.
   * 
   * @param <T> enum to parse.
   * @param clazz class of the enum to parse.
   * @param key key to get the value for
   * @param defaultValue default value to use if there is no value at key
   * @return the value at the given key as clazz. If there is no value for the given key,
   *         defaultValue is
   *         returned.
   */
  public <T extends Enum<T>> T getEnum(Class<T> clazz, String key, T defaultValue);

  /**
   * Gets the a duration as milliseconds.
   * 
   * <p>If there is no value at key, it is assumed to be 0.
   *
   * <p>If there is a value at (key + "Unit), it automatically will get picked up and interpreted
   * as time unit for the value at key. If there is no value at (key + "Unit"), it is assumed to
   * be MILLISECONDS.
   * 
   * @param key key to get the value for
   * @return the duration converted to milliseconds
   */
  long getDurationMillis(String key);

  /**
   * Gets the a duration as milliseconds.
   * 
   * <p>If there is a value at (key + "Unit), it automatically will get picked up and interpreted
   * as time unit for the value at key. If there is no value at (key + "Unit"), it is assumed to
   * be MILLISECONDS.
   * 
   * @param key key to get the value for
   * @param defaultDuration default value to use if there is no value at key.
   * @return the duration converted to milliseconds
   */
  long getDurationMillis(String key, long defaultDuration);

  /**
   * Gets the a duration as milliseconds.
   * 
   * <p>If there is a value at (key + "Unit), it automatically will get picked up and interpreted
   * as time unit for the value at key.
   * 
   * @param key key to get the value for
   * @param defaultDuration default value to use if there is no value at key.
   * @param defaultUnit Default time unit to interpret the value at key (or defaultDuration) as.
   * @return the duration converted to milliseconds
   */
  long getDurationMillis(String key, long defaultDuration, TimeUnit defaultUnit);

  /**
   * Gets the a duration as seconds.
   * 
   * <p>If there is no value at key, it is assumed to be 0.
   *
   * <p>If there is a value at (key + "Unit), it automatically will get picked up and interpreted
   * as time unit for the value at key. If there is no value at (key + "Unit"), it is assumed to
   * be SECONDS.
   * 
   * @param key key to get the value for
   * @return the duration converted to seconds
   */
  long getDurationSeconds(String key);

  /**
   * Gets the a duration as seconds.
   * 
   * <p>If there is a value at (key + "Unit), it automatically will get picked up and interpreted
   * as time unit for the value at key. If there is no value at (key + "Unit"), it is assumed to
   * be SECONDS.
   * 
   * @param key key to get the value for
   * @param defaultDuration default value to use if there is no value at key.
   * @return the duration converted to seconds
   */
  long getDurationSeconds(String key, long defaultDuration);

  /**
   * Gets the a duration as seconds.
   * 
   * <p>If there is a value at (key + "Unit), it automatically will get picked up and interpreted
   * as time unit for the value at key.
   * 
   * @param key key to get the value for
   * @param defaultDuration default value to use if there is no value at key.
   * @param defaultUnit Default time unit to interpret the value at key (or defaultDuration) as.
   * @return the duration converted to seconds
   */
  long getDurationSeconds(String key, long defaultDuration, TimeUnit defaultUnit);
}
