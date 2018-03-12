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

package com.seleritycorp.common.base.application;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

import com.seleritycorp.common.base.inject.InjectorFactory;
import com.seleritycorp.common.base.state.AppState;
import com.seleritycorp.common.base.state.StateManager;

import javax.inject.Inject;

/**
 * Manages an application's life-cycle. 
 */
public class ApplicationRunner {
  interface Factory {
    ApplicationRunner create(String[] args);
  }

  private final Application application;
  private final StateManager stateManager;

  /**
   * Creates an application runner.
   * 
   * @param application The application to run.
   * @param stateManager The application's state manager.
   */
  @Inject
  ApplicationRunner(Application application, StateManager stateManager) {
    this.application = application;
    this.stateManager = stateManager;
  }

  /**
   * Runs the application.
   * 
   * <p>First, the application gets initialized. Then the application transitions to READY state,
   * and the application's run method is run. Once that method returns, the applications
   * transitions to FAULTY state, and the application is shut down.
   * 
   * @throws Exception if errors occur.
   */
  public synchronized void run() throws Exception {
    application.init();

    stateManager.setMainAppState(AppState.READY);

    application.run();

    stateManager.setMainAppState(AppState.FAULTY);

    application.shutdown();
  }

  /**
   * Runs the application registered with Guice.
   * 
   * @param args The command line arguments to register for the application.
   * @param modules Additional Guice modules to load before running the application.
   * 
   * @throws Exception if errors occur
   */
  public static void run(final String[] args, AbstractModule... modules) throws Exception {
    for (AbstractModule module : modules) {
      InjectorFactory.register(module);
    }
    InjectorFactory.register(new AbstractModule() {
      @Override
      protected void configure() {
        bind(String[].class).annotatedWith(CommandLineArguments.class).toInstance(args);
      }
    });
    Injector injector = InjectorFactory.getInjector();
    ApplicationRunner runner = injector.getInstance(ApplicationRunner.class);
    runner.run();

    // Hard exit to avoid leaving an (otherwise dead) application hanging with still running
    // non-daemon threads. 
    System.exit(0);
  }
}
