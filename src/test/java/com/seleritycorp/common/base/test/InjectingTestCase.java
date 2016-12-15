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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import org.junit.Before;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.seleritycorp.common.base.config.Config;
import com.seleritycorp.common.base.config.EnvironmentConfig;
import com.seleritycorp.common.base.inject.InjectorFactory;

public class InjectingTestCase extends FileTestCase {
  Path basePath;
  SettableStaticClock clock;
  SettableUuidGenerator uuidGenerator;

  @Before
  public void setUpInjector() throws IOException {
    InjectorFactory.forceInjector(Guice.createInjector());

    basePath = createTempDirectory();
    clock = new SettableStaticClock();
    uuidGenerator = new SettableUuidGenerator();
    InjectorFactory.register(new TestModule(basePath, clock, uuidGenerator));
  }

  public Path getBasePath() {
    return basePath;
  }

  public SettableStaticClock getClock() {
    return clock;
  }

  public SettableUuidGenerator getUuidGenerator() {
    return uuidGenerator;
  }

  @Override
  protected void writeFile(Path path, String contents) throws IOException {
    super.writeFile(path, contents);
    FileTime fileTime = FileTime.fromMillis(clock.getMillisEpoch());
    Files.setLastModifiedTime(path, fileTime);
  }
  

  /**
   * Gets a Config object that wraps the OS' environment variables.
   * 
   * @return the config object that wraps the OS' environment variables
   */
  protected Config getEnvironment() {
    Injector injector = InjectorFactory.getInjector();
    Key<Config> key = Key.get(Config.class, EnvironmentConfig.class);
    return injector.getInstance(key);
  }
}
