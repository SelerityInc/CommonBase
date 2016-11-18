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

import com.seleritycorp.common.base.jmx.MBeanUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Manages application and high-availability state
 *
 * <p>Application state will automatically get written to disk
 * periodically, so other applications (E.g.: HA fencers) can pick it up and
 * base decisions upon it.
 *
 * <p>Also, it periodically reads state from HA fencers and allows easy querying
 * of the HA state.
 *
 */
@Singleton
public class StateManager
    implements StateManagerMBean, AppStateManagerAccessor, HaStateManagerAccessor {
  /**
   * State manager for application state.
   */
  private final AppStateManager appStateManager;

  /**
   * State manager for high availability state.
   */
  private final HaStateManager haStateManager;

  /**
   * The runner taking care of periodic tasks.
   *
   * <p>Periodic tasks are for example writing out state, or reading HA state.
   */
  private final StateManagerPeriodicTasksRunner periodicTasksRunner;

  /**
   * Creates a StateManager.
   */
  @Inject
  StateManager(AppStateManager appStateManager, HaStateManager haStateManager,
      StateManagerPeriodicTasksRunner periodicTasksRunner) {
    this.appStateManager = appStateManager;
    this.haStateManager = haStateManager;
    this.periodicTasksRunner = periodicTasksRunner;

    MBeanUtils.register("com.seleritycorp.common.base.state:name=StateManager", this);
  }


  @Override
  public HaState getHaState() {
    return haStateManager.getHaState();
  }

  @Override
  public boolean isHaMaster() {
    return haStateManager.isHaMaster();
  }

  @Override
  public boolean isHaBackup() {
    return haStateManager.isHaBackup();
  }

  @Override
  public boolean isHaFault() {
    return haStateManager.isHaFault();
  }

  @Override
  public boolean isHaHealthy() {
    return haStateManager.isHaHealthy();
  }

  @Override
  public boolean isHaUnhealthy() {
    return haStateManager.isHaUnhealthy();
  }

  @Override
  public void setMainAppState(AppState state) {
    appStateManager.setMainAppState(state);
  }

  @Override
  public AppState getAppState() {
    return appStateManager.getAppState();
  }

  @Override
  public boolean isAppInitializing() {
    return appStateManager.isAppInitializing();
  }

  @Override
  public boolean isAppReady() {
    return appStateManager.isAppReady();
  }

  @Override
  public boolean isAppWarning() {
    return appStateManager.isAppWarning();
  }

  @Override
  public boolean isAppFaulty() {
    return appStateManager.isAppFaulty();
  }

  @Override
  public boolean isAppUsable() {
    return appStateManager.isAppUsable();
  }

  @Override
  public boolean getAppUsable() {
    return isAppUsable();
  }

  @Override
  public int getAppStateNumber() {
    return getAppState().getWeight();
  }

  @Override
  public boolean isAppUnusable() {
    return appStateManager.isAppUnusable();
  }

  @Override
  public AppStatePushFacet createRegisteredAppStatePushFacet(String name) {
    return appStateManager.createRegisteredAppStatePushFacet(name);
  }


  @Override
  public boolean registerAppStateFacet(String name, AppStateFacet facet) {
    return appStateManager.registerAppStateFacet(name, facet);
  }

  /**
   * Starts the thread for periodic state synchronization
   *
   * <p>It is safe to call this method multiple times. If the thread is running
   * already, the call is silently ignored.
   *
   * <p>If the thread got stopped before, calling this method will start a
   * fresh thread.
   */
  void startPeriodicTasks() {
    periodicTasksRunner.start();
  }

  /**
   * Stops the thread for periodic state synchronization
   *
   * <p>It is safe to call this method multiple times. If there is currently no
   * thread running for periodic tasks, the call is silently ignored.
   */
  void stopPeriodicTasks() {
    periodicTasksRunner.stop();
  }
}
