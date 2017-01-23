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

import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ApplicationPaths {
  private static final Log log = LogFactory.getLog(ApplicationPaths.class);

  private final Path basePath;
  private final Path confPath;
  private final Path confAnsiblizedPath;
  private final Path dataPath;
  private final Path dataStatePath;

  @Inject
  ApplicationPaths(@ApplicationPath Path basePath, @ApplicationConfig Config config) {
    this.basePath = basePath;
    this.confPath = initPath(basePath, config, "paths.conf");
    this.confAnsiblizedPath = initPath(basePath, config, "paths.confAnsiblized");
    this.dataPath = initPath(basePath, config, "paths.data");
    this.dataStatePath = initPath(dataPath, config, "paths.dataState");
  }

  private Path initPath(Path base, Config config, String key) {
    Path ret = base.resolve(config.get(key));
    try {
      if (!Files.isDirectory(ret)) {
        Files.createDirectories(ret);
      }
    } catch (IOException e) {
      log.error("Could not create directory " + ret.toString() + " for " + key, e);
    }
    return ret;
  }

  /**
   * Application's base path.
   * 
   * @return the basePath
   */
  public Path getBasePath() {
    return basePath;
  }

  /**
   * Path for directory holding manual configuration.
   * 
   * @return Path for directory holding manual configuration
   */
  public Path getConfPath() {
    return confPath;
  }

  /**
   * Path for directory holding ansiblized configuration.
   * 
   * @return Path for directory holding ansiblized configuration
   */
  public Path getConfAnsiblizedPath() {
    return confAnsiblizedPath;
  }

  /**
   * Path for data files.
   * 
   * @return Path for data files
   */
  public Path getDataPath() {
    return dataPath;
  }

  /**
   * Path for data files.
   * 
   * @return Path for data files
   */
  public Path getDataStatePath() {
    return dataStatePath;
  }
}
