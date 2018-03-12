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

package com.seleritycorp.common.base.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import org.junit.Before;
import org.junit.internal.AssumptionViolatedException;

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

  /**
   * Checks whether or not the test is running in an CI environment.
   * 
   * <p>You can use this predicate in an {@link Assume} block to allow skipping tests in
   * local/dev environments, but enforce them on CI servers. 
   * 
   * @return false, if the test is running in an CI environment. Otherwise, true.
   */
  protected boolean isRunningOutsideCi() {
    Config environment = getEnvironment();
    return environment.get("JENKINS_URL", "").isEmpty();
  }
  
  /**
   * Checks whether or not the test is running in an CI environment.
   * 
   * <p>You can use this predicate in an {@link Assume} block to allow skipping tests in
   * local/dev environments, but enforce them on CI servers. 
   * 
   * @return true, if the test is running in an CI environment. Otherwise, false.
   */
  protected boolean isRunningInsideCi() {
    return !isRunningOutsideCi();
  }
  
  /**
   * Fail the test inside CI environments, skip the test otherwise.
   * 
   * <p>This method helps to to abort tests that have unmet external dependencies
   * (e.g.: Databases, network connection, ...). As not all developers can be bothered to setup
   * all dependencies, the test is skipped for developers. But in CI environments, where we expect
   * that all external dependencies are met, the test will fail hard.
   * 
   * @param reason The reason for the skip/failure.
   * @throws Throwable always. Thereby the test is skipped/fails.
   */
  protected void failInCiSkipOtherwise(String reason) throws Throwable {
    failInCiSkipOtherwise(reason, null);
  }

  /**
   * Fail the test inside CI environments, skip the test otherwise.
   * 
   * <p>This method helps to to abort tests that have unmet external dependencies
   * (e.g.: Databases, network connection, ...). As not all developers can be bothered to setup
   * all dependencies, the test is skipped for developers. But in CI environments, where we expect
   * that all external dependencies are met, the test will fail hard.
   * 
   * @param reason The reason for the skip/failure.
   * @param cause The exception leading to the skip/failure. 
   * @throws Throwable always. Thereby the test is skipped/fails.
   */
  protected void failInCiSkipOtherwise(String reason, Throwable cause) throws Throwable {
    final Throwable t;
    if (isRunningInsideCi()) {
      if (reason != null) {
        if (cause == null) {
          t = new AssertionError(reason);
        } else {
          t = new AssertionError(reason, cause);          
        }
      } else { 
        if (cause == null) {
          t = new AssertionError();
        } else {
          t = new AssertionError(cause);          
        }
      }
    } else {
      if (cause == null) {
        t = new AssumptionViolatedException((reason != null) ? reason : "<no reason given>");
      } else {
        t = new AssumptionViolatedException((reason != null) ? reason : "<no reason given>", cause);        
      }
    }
    throw t;
  }
}
