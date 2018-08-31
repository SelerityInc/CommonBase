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

import static com.google.inject.Scopes.SINGLETON;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;

import com.seleritycorp.common.base.inject.InjectorFactory;

import java.nio.file.Path;
import javax.inject.Singleton;

public class ConfigModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Config.class).annotatedWith(EnvironmentConfig.class)
        .toProvider(EnvironmentConfigProvider.class).in(SINGLETON);
  }

  @Provides
  @Singleton
  @ApplicationConfig
  Config provideApplicationConfig() {
    Injector injector = InjectorFactory.getInjector();

    if (injector.getExistingBinding(Key.get(Path.class, ConfigFile.class)) == null) {
      return injector.getInstance(ApplicationConfigProvider.class).get();
    } else {
      return injector.getInstance(SingleFileConfig.class);
    }
  }
}
