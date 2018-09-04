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


import com.google.inject.AbstractModule;

import java.nio.file.Path;

/**
 * Register this module when you want to use a free layout configuration
 * for you application, in place of the enforced layout of the {@link ConfigModule}.
 *
 */
public class CustomConfigModule extends AbstractModule {
  private final Path configFile;
  private final Config applicationDefaults;

  public CustomConfigModule(Path configFile) {
    this(configFile, Config.newEmptyConfig());
  }

  public CustomConfigModule(Path configFile, Config applicationDefaults) {
    this.configFile = configFile;
    this.applicationDefaults = applicationDefaults;
  }

  @Override
  protected void configure() {
    bind(Path.class).annotatedWith(ConfigFile.class)
            .toInstance(configFile.toAbsolutePath());
    bind(Config.class).annotatedWith(ApplicationDefaults.class)
            .toInstance(applicationDefaults);

  }
}
