/*
 * Copyright (C) 2016-2017 Selerity, Inc. (support@seleritycorp.com)
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

import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Provider for an application's main Config.
 */
public class ApplicationConfigProvider implements Provider<Config> {
  private static final String JSON_NAME = "application.json";
  private static final String PROPERTIES_NAME = "application.properties";

  private final Path applicationPath;

  @Inject
  ApplicationConfigProvider(@ApplicationPath Path applicationPath) {
    this.applicationPath = applicationPath;
  }

  @Override
  public Config get() {
    // First, we create the temporary defaults, from defaults and enforced
    // defaults. These will get used to bootstrap paths.
    ConfigImpl defaultConfig = new DefaultConfig();
    ConfigImpl enforcedConfig = new EnforcedDefaultConfig();
    enforcedConfig.setParent(defaultConfig);

    String pathStr = enforcedConfig.get("paths.confAnsiblized");
    Path confAnsiblizedPath = applicationPath.resolve(pathStr);

    // Now with the constructed paths, we load the user supplied configs.
    // First, from the ansiblized directory, then from the manual config
    // directory. In each direcotry, first the json file, then the
    // property file. Using the overridable defaults as fallback.

    ConfigImpl parent = defaultConfig;
    ConfigImpl config = ConfigUtils.load(confAnsiblizedPath.resolve(JSON_NAME));
    config.setParent(defaultConfig);

    parent = config;
    config = ConfigUtils.load(confAnsiblizedPath.resolve(PROPERTIES_NAME));
    config.setParent(parent);

    pathStr = enforcedConfig.get("paths.conf");
    Path confPath = applicationPath.resolve(pathStr);

    parent = config;
    config = ConfigUtils.load(confPath.resolve(JSON_NAME));
    config.setParent(parent);

    parent = config;
    config = ConfigUtils.load(confPath.resolve(PROPERTIES_NAME));
    config.setParent(parent);

    // Finally enforce the enforced defaults;
    parent = config;
    config = enforcedConfig;
    config.setParent(parent);

    return config;
  }
}
