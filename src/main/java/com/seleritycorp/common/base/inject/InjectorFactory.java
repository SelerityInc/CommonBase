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

package com.seleritycorp.common.base.inject;


import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import com.seleritycorp.common.base.config.ConfigModule;
import com.seleritycorp.common.base.config.ProductionModule;
import com.seleritycorp.common.base.http.client.HttpClientModule;
import com.seleritycorp.common.base.http.server.HttpServerModule;
import com.seleritycorp.common.base.logging.LoggingModule;
import com.seleritycorp.common.base.state.StateModule;
import com.seleritycorp.common.base.time.TimeModule;
import com.seleritycorp.common.base.uuid.UuidModule;

import java.util.ArrayList;
import java.util.List;

public class InjectorFactory {
  private static Injector injector = null;

  /**
   * Gets the current injector
   *
   * <p>The current injector is a point in time snapshot, and will not reflect
   * modules registered in the future.
   *
   * <p>If no injector has been set up, the production injector will be created.
   * 
   * @return The current injector.
   */
  public static synchronized Injector getInjector() {
    if (injector == null) {
      List<Module> modules = new ArrayList<>(5);
      modules.add(new ProductionModule());
      modules.add(new TimeModule());
      modules.add(new ConfigModule());
      modules.add(new LoggingModule());
      modules.add(new StateModule());
      modules.add(new UuidModule());
      modules.add(new HttpClientModule());
      modules.add(new HttpServerModule());
      injector = Guice.createInjector(modules);
    }
    return injector;
  }

  /**
   * Registers an additional module
   *
   * <p>The current injector gets replaced by one that contains all the
   * previously registered module, and the currently passed one.
   * 
   * @param module The module to register
   */
  public static synchronized void register(AbstractModule module) {
    Injector parent = getInjector();
    injector = parent.createChildInjector(module);
  }

  /**
   * Forces the factory to use the given
   *
   * <p>You should not need this method in production code. This is only useful
   * in tests.
   * 
   * @param injector The injector to force. If null, the injector gets
   *        reset, and re-initialized upon the getting anInjector next time.
   */
  public static synchronized void forceInjector(Injector injector) {
    InjectorFactory.injector = injector;
  }
}
