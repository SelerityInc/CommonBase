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

import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nullable;

/**
 * Basic config implementation that allows setting of variables.
 */
public class ConfigImpl implements Config {
  private static final Log log = LogFactory.getLog(ConfigImpl.class);

  ConcurrentMap<String, String> values;
  Config parent;

  public ConfigImpl() {
    values = new ConcurrentHashMap<>();
    parent = null;
  }

  @Override
  public String get(String key) {
    String ret = values.get(key);
    if (ret == null && parent != null) {
      ret = parent.get(key);
    }
    return ret;
  }

  @Override
  public String get(String key, String defaultValue) {
    String ret = get(key);
    if (ret == null) {
      ret = defaultValue;
    }
    return ret;
  }

  /**
   * Sets the parent for this instance.
   *
   * <p>When getting a value for a key that is not in this instance, it will be looked up on the
   * parent instance.
   * 
   * @param parent The parent instance to set
   */
  public void setParent(Config parent) {
    this.parent = parent;
  }

  @Override
  public int getInt(String key) {
    return getInt(key, 0);
  }

  @Override
  public int getInt(String key, int defaultValue) {
    String value = get(key);
    int ret = defaultValue;
    if (value != null) {
      try {
        ret = Integer.parseInt(value);
      } catch (NumberFormatException e) {
        // Not parsable as integer.
        // We retry parsing as double and coercing to integer. That
        // will help making parsing more robust.
        try {
          double retDouble = getDouble(key, Double.NaN, false);
          if (!Double.isNaN(retDouble)) {
            ret = (int) retDouble;
          }
        } catch (NumberFormatException e2) {
          log.error("Value at \"" + key + "\" is not null, but " + " does not parse to an integer",
              e);
        }
      }
    }
    return ret;
  }


  @Override
  public long getLong(String key) {
    return getLong(key, 0L);
  }

  @Override
  public long getLong(String key, long defaultValue) {
    String value = get(key);
    long ret = defaultValue;
    if (value != null) {
      try {
        ret = Long.parseLong(value);
      } catch (NumberFormatException e) {
        // Not parsable as integer.
        // We retry parsing as double and coercing to integer. That
        // will help making parsing more robust.
        try {
          double retDouble = getDouble(key, Double.NaN, false);
          if (!Double.isNaN(retDouble)) {
            ret = (long) retDouble;
          }
        } catch (NumberFormatException e2) {
          log.error("Value at \"" + key + "\" is not null, but " + " does not parse to a long", e);
        }
      }
    }
    return ret;
  }

  @Override
  public float getFloat(String key) {
    return getFloat(key, Float.NaN);
  }

  @Override
  public float getFloat(String key, float defaultValue) {
    String value = get(key);
    float ret = defaultValue;
    if (value != null) {
      try {
        ret = Float.parseFloat(value);
      } catch (NumberFormatException e) {
        log.error("Value at \"" + key + "\" is not null, but " + " does not parse to a float", e);
      }
    }
    return ret;
  }

  @Override
  public double getDouble(String key) {
    return getDouble(key, Double.NaN);
  }

  @Override
  public double getDouble(String key, double defaultValue) {
    return getDouble(key, defaultValue, true);
  }

  /**
   * Gets the value ot the given key as double with default
   * 
   * @param key key to get value for
   * @param defaultValue the default value to use.
   * @param doLog if true, log number format issues
   * @return the value at the given key. If there is no value for the given key, defaultValue is
   *         returned.
   */
  public double getDouble(String key, double defaultValue, boolean doLog) {
    String value = get(key);
    double ret = defaultValue;
    if (value != null) {
      try {
        ret = Double.parseDouble(value);
      } catch (NumberFormatException e) {
        if (!doLog) {
          log.error("Value at \"" + key + "\" is not null, but " + " does not parse to a double",
              e);
        }
      }
    }
    return ret;
  }

  @Override
  public boolean getBoolean(String key) {
    return getBoolean(key, false);
  }

  @Override
  public boolean getBoolean(String key, boolean defaultValue) {
    String value = get(key);
    boolean ret = defaultValue;
    if (value != null) {
      ret = ("true".compareToIgnoreCase(value.trim()) == 0);
      if (!ret) {
        double doubleValue = getDouble(key, Double.NaN, false);
        if (!Double.isNaN(doubleValue)) {
          ret = (doubleValue != 0);
        }
      }
    }
    return ret;
  }

  /**
   * Sets a key to a value.
   * 
   * @param key The key to set the value for
   * @param value The value to set for the given key
   */
  public void set(String key, @Nullable String value) {
    if (value == null) {
      values.remove(key);
    } else {
      values.put(key, value);
    }
  }
}
