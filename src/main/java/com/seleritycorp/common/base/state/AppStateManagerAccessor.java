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

package com.seleritycorp.common.base.state;

/**
 * Methods to be exposed by all classes that expose AppState.
 */
public interface AppStateManagerAccessor {
  /**
   * Sets the main application state
   *
   * <p>The "main application state" is a default facet registered with state
   * managers. It is not different from other facets you can register. Only,
   * it comes pre-registered. So users can use it without having to bother
   * about handling facets.
   *
   * <p>The state set here need not be returned by {@link #getAppState()}, as
   * other facets may affect that state. So for example if you set the main
   * application state to READY, but some other facet is FAULTY,
   * {@link #getAppState()} will return FAULTY.
   * 
   * @param state The state to set the main application state to.
   */
  public void setMainAppState(AppState state);

  /**
   * Gets the current application state
   *
   * <p>If you are only interested in whether the appliction thinks it is still
   * usable, use the {@link #isAppUsable()} and {@link #isAppUnusable()} methods
   * instead.
   * If you only need to check for a given state, use the
   * {@link #isAppInitializing()}, {@link #isAppReady()}, {@link #isAppWarning()},
   * and {@link #isAppFaulty()} methods instead.
   * 
   * @return the current application state
   */
  public AppState getAppState();

  /**
   * Checks if the application is initializing
   *
   * <p>If you are only interested in whether the appliction thinks it is still
   * usable, use the {@link #isAppUsable()} and {@link #isAppUnusable()} methods
   * instead.
   * 
   * @return true iff the application thinks it is initializing.
   */
  public boolean isAppInitializing();

  /**
   * Checks if the application is fully up and healthy.
   *
   * <p>If you are only interested in whether the appliction thinks it is still
   * usable, use the {@link #isAppUsable()} and {@link #isAppUnusable()} methods
   * instead.
   * 
   * @return true iff the application thinks it is fully up and healthy
   */
  public boolean isAppReady();

  /**
   * Checks if the application is up but having minor issues.
   *
   * <p>If you are only interested in whether the appliction thinks it is still
   * usable, use the {@link #isAppUsable()} and {@link #isAppUnusable()} methods
   * instead.
   * 
   * @return true iff the application thinks it ok, but having minor issues,
   *         which do not render it non-functional.
   */
  public boolean isAppWarning();

  /**
   * Checks if the application is suffering major issues.
   *
   * <p>If you are only interested in whether the appliction thinks it is still
   * usable, use the {@link #isAppUsable()} and {@link #isAppUnusable()} methods
   * instead.
   * 
   * @return true iff the application thinks is non-functional.
   */
  public boolean isAppFaulty();

  /**
   * @return true iff the application thinks it is usable.
   */
  public boolean isAppUsable();

  /**
   * @return true iff the application thinks it is unusable.
   */
  public boolean isAppUnusable();

  /**
   * Creates a new, registered AppStatePushFacet
   *
   * <p>The facet is initially in state INITIALIZING.
   * 
   * @param name The name to register the facet at
   * @return The registered facet, or null if registering failed.
   */
  public AppStatePushFacet createRegisteredAppStatePushFacet(String name);

  /**
   * Registers a AppStateFacet for this state manager
   *
   * <p>Upon successful registration, the AppStateFacet will contribute to
   * the application state reported by this state manager.
   * 
   * @param name The name to register the facet under
   * @param facet The facet to register
   * @return True, if the registering was successful. False otherwise.
   */
  public boolean registerAppStateFacet(String name, AppStateFacet facet);
}
