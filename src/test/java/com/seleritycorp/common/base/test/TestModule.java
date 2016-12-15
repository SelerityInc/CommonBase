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

import static com.google.inject.Scopes.SINGLETON;

import java.nio.file.Path;

import com.google.common.base.Ticker;
import com.seleritycorp.common.base.config.AbstractBaseModule;
import com.seleritycorp.common.base.config.ApplicationPath;
import com.seleritycorp.common.base.config.Config;
import com.seleritycorp.common.base.config.EnvironmentConfig;
import com.seleritycorp.common.base.config.EnvironmentConfigProvider;
import com.seleritycorp.common.base.time.Clock;
import com.seleritycorp.common.base.time.TimeUtils;
import com.seleritycorp.common.base.uuid.UuidGenerator;

/**
 * Sets up the basic application environment needed to bootstrap an application
 */
public class TestModule extends AbstractBaseModule {
  Path applicationPath;
  SettableStaticClock clock;
  SettableUuidGenerator uuidGenerator;

  public TestModule(Path applicationPath, SettableStaticClock clock,
      SettableUuidGenerator uuidGenerator) {
    this.applicationPath = applicationPath;
    this.clock = clock;
    this.uuidGenerator = uuidGenerator;
  }

  @Override
  protected void configure() {
    bind(Path.class).annotatedWith(ApplicationPath.class).toInstance(applicationPath);
    bind(Clock.class).toInstance(clock);
    bind(SettableStaticClock.class).toInstance(clock);
    bind(Ticker.class).toInstance(clock);
    bind(TimeUtils.class).to(TimeUtilsSettableClock.class);
    bind(UuidGenerator.class).toInstance(uuidGenerator);
    bind(Config.class).annotatedWith(EnvironmentConfig.class)
      .toProvider(EnvironmentConfigProvider.class).in(SINGLETON);
  }
}
