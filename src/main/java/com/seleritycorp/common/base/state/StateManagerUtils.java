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

package com.seleritycorp.common.base.state;

import com.google.inject.Injector;

import com.seleritycorp.common.base.inject.InjectorFactory;

public class StateManagerUtils {
  /**
   * Creates a new, registered AppStatePushFacet
   *
   * <p>The facet is initially in state INITIALIZING.
   * 
   * @param name The name to register the facet at
   * @return The registered facet, or null if registering failed.
   */
  public static AppStatePushFacet createRegisteredAppStatePushFacet(String name) {
    Injector injector = InjectorFactory.getInjector();
    StateManager stateManager = injector.getInstance(StateManager.class);
    return stateManager.createRegisteredAppStatePushFacet(name);
  }

  /**
   * Registers a AppStateFacet for the singleton state manager
   *
   * <p>Upon successful registration, the AppStateFacet will contribute to
   * the application state reported by this state manager.
   * 
   * @param name The name to register the facet under
   * @param facet The facet to register
   * @return True, if the registering was successful. False otherwise.
   */
  public static boolean registerAppStateFacet(String name, AppStateFacet facet) {
    Injector injector = InjectorFactory.getInjector();
    StateManager stateManager = injector.getInstance(StateManager.class);
    return stateManager.registerAppStateFacet(name, facet);
  }
}
