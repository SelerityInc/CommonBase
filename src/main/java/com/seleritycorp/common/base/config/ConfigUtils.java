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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Utility methods for Configs.
 */
public class ConfigUtils {
  private static final Log log = LogFactory.getLog(ConfigUtils.class);

  /**
   * Add a value to a property key.
   * 
   * @param key The key to add to (may be null or the empty string)
   * @param addendum The key to add
   * @return The concatenated key
   */
  private static String addToKey(String key, String addendum) {
    String ret = key;

    if (ret == null) {
      ret = "";
    }

    if (!ret.equals("")) {
      ret += ".";
    }

    ret += addendum;

    return ret;
  }

  /**
   * Loads a properties file into a Config instance.
   * 
   * @param path The file to load the content from
   * @return The config instance.with key/values from the properties file.
   * @throws IOException for IO errors
   */
  private static ConfigImpl loadProperties(Path path) throws IOException {
    final ConfigImpl ret;
    try (FileInputStream stream = new FileInputStream(path.toFile())) {
      try (InputStreamReader reader = new InputStreamReader(stream, UTF_8)) {
        ret = loadProperties(reader);
      }
    }
    return ret;
  }

  /**
   * Loads a properties file reader into a Config instance.
   * 
   * @param reader The reader to load the content from
   * @return The config instance.with key/values from the properties file.
   * @throws IOException if errors occur reading the source file.
   */
  private static ConfigImpl loadProperties(Reader reader) throws IOException {
    ConfigImpl ret = new ConfigImpl();

    Properties properties = new Properties();
    properties.load(reader);
    for (String key : properties.stringPropertyNames()) {
      ret.set(key, properties.getProperty(key));
    }

    return ret;
  }

  /**
   * Loads a JSON file reader into a Config instance.
   * 
   * @param reader The reader to load the content from
   * @return The config instance with key/values from the reader.
   * @throws FileNotFoundException if path cannot be opened for reading
   * @throws IOException for IO errors
   */
  private static ConfigImpl loadJson(Path path) throws IOException {
    final ConfigImpl ret;
    try (FileInputStream stream = new FileInputStream(path.toFile())) {
      try (InputStreamReader reader = new InputStreamReader(stream, UTF_8)) {
        ret = loadJson(reader);
      }
    }
    return ret;
  }

  /**
   * Loads a JSON file reader into a Config instance.
   * 
   * @param reader The reader to load the content from
   * @return The config instance with key/values from the reader.
   * @throws JsonParseException if reader contains invalid Json.
   */
  private static ConfigImpl loadJson(Reader reader) {
    ConfigImpl ret = new ConfigImpl();
    JsonParser parser = new JsonParser();
    JsonElement element = parser.parse(reader);
    loadJson(element, ret, "");
    return ret;
  }

  /**
   * Adds a JSON element to a Config.
   * 
   * @param element The element to add
   * @param config The config instance to add the element to
   * @param key The key in the config space
   */
  private static void loadJson(JsonElement element, ConfigImpl config, String key) {
    if (element.isJsonObject()) {
      loadJson(element.getAsJsonObject(), config, key);
    } else if (element.isJsonArray()) {
      loadJson(element.getAsJsonArray(), config, key);
    } else if (element.isJsonPrimitive()) {
      loadJson(element.getAsJsonPrimitive(), config, key);
    } else if (element.isJsonNull()) {
      // null does not need a dedicated representation, so we
      // skip this case.
    } else {
      throw new UnsupportedOperationException("Unimplemented " + "JsonElement state");
    }
  }

  /**
   * Adds a JSON object to a Config.
   * 
   * @param object The object to add
   * @param config The config instance to add the object to
   * @param key The key in the config space
   */
  private static void loadJson(JsonObject object, ConfigImpl config, String key) {
    for (Entry<String, JsonElement> entry : object.entrySet()) {
      String newKey = addToKey(key, entry.getKey());
      loadJson(entry.getValue(), config, newKey);
    }
  }

  /**
   * Adds a JSON array to a Config.
   * 
   * @param array The array to add
   * @param config The config instance to add the array to
   * @param key The key in the config space
   */
  private static void loadJson(JsonArray array, ConfigImpl config, String key) {
    int index = 0;
    for (JsonElement element : array) {
      String newKey = addToKey(key, Integer.toString(index));
      loadJson(element, config, newKey);
      index++;
    }
  }

  /**
   * Adds a JSON primitive to a Config.
   * 
   * @param primitive The primitive to add
   * @param config The config instance to add the primitive to
   * @param key The key in the config space
   */
  private static void loadJson(JsonPrimitive primitive, ConfigImpl config, String key) {
    String value = null;
    if (primitive.isBoolean()) {
      boolean bool = primitive.getAsBoolean();
      value = bool ? "true" : "false";
    } else if (primitive.isString()) {
      value = primitive.getAsString();
    } else if (primitive.isNumber()) {
      value = Double.toString(primitive.getAsDouble());
    }
    config.set(key, value);
  }

  /**
   * Loads a properties file into a Config instance.
   *
   * <p>If the properties file does not exist, an empty config is silently assumed, and the fact
   * gets logged.
   * 
   * @param source The properties file to load
   * @return The config instance.with key/values from the properties file.
   */
  static ConfigImpl load(Path source) {
    log.info("Loading config from " + source);

    ConfigImpl ret = null;

    if (Files.exists(source)) {
      try {
        try {
          ret = loadJson(source);
        } catch (JsonParseException e) {
          // It's invalid Json ... maybe it is a properties file instead?
          ret = loadProperties(source);
        }
      } catch (IOException e) {
        log.info("Failed to load config file " + source + ". Assuming empty config", e);
      }
    } else {
      log.info("Config source " + source + " not found. Assuming empty config");
    }

    if (ret == null) {
      ret = new ConfigImpl();
    }

    return ret;
  }

  /**
   * Collects keys starting in a common prefix.
   * 
   * <p>If the prefix is "foo.bar", then the keys
   * <ul>
   * <li>"foo.bar.baz" would be in the result Config, and</li>
   * <li>"foo.bar.baz.quux" would be in the result Config, but</li>
   * <li>"foo.bar" would not be in the result Config,</li> <li>"foo.barbaz" would not be in the
   * result Config, and</li>
   * <li>"foo.barbaz.quux" would not be in the result Config.</li>
   * </ul>
   *
   * <p>The returned Config need not be independent from the current Config. So for example setting
   * a value on the current Config after a subconfig has been taken of it, may (but need not) be
   * visible in the SubConfig. Also setting a value on the SubConfig may (but need not) set the
   * corresponding value on the current Config.
   * 
   * @param config The Config to take a subconfig of.
   * @param prefix The prefix to take the subconfig at.
   * @return Config object holding those keys starting in the prefix.
   */
  public static Config subconfig(Config config, String prefix) {
    return new PrefixedConfig(config, prefix);
  }
}
