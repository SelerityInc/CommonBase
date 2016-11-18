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

import com.seleritycorp.common.base.config.ApplicationConfig;
import com.seleritycorp.common.base.config.Config;
import com.seleritycorp.common.base.time.TimeUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Carries out periodic tasks for a StateManager.
 */
@Singleton
public class StateManagerPeriodicTasksRunner implements Runnable {
  /**
   * State manager for application state.
   */
  private final AppStateManager appStateManager;

  /**
   * State manager for high availability state.
   */
  private final HaStateManager haStateManager;

  /**
   * The currently running thread.
   *
   * <p>Will be null, if no thread is considered to be working on periodic
   * tasks.
   */
  private Thread runner;

  /**
   * Helper for pausing threads.
   */
  private TimeUtils timeUtils;

  /**
   * How long to pause in milliseconds between two runs of periodic
   * tasks.
   */
  private final long pauseMillis;

  /**
   * Creates a stopped instance.
   * 
   * @param config The Application's config
   * @param appStateManager The AppStateManager to use
   * @param haStateManager The HaStateManager to use
   * @param timeUtils the timeUtils used for sleeping
   */
  @Inject
  public StateManagerPeriodicTasksRunner(@ApplicationConfig Config config,
      AppStateManager appStateManager, HaStateManager haStateManager, TimeUtils timeUtils) {
    this.appStateManager = appStateManager;
    this.haStateManager = haStateManager;
    this.timeUtils = timeUtils;
    this.pauseMillis = config.getInt("StateManagerPeriodicTasksRunner.pauseMillis", 2000);
    stop();
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
  public void start() {
    Thread newRunner = new Thread(this);
    runner = newRunner;
    newRunner.start();
  }

  /**
   * Stops the thread for periodic state synchronization
   *
   * <p>It is safe to call this method multiple times. If there is currently no
   * thread running for periodic tasks, the call is silently ignored.
   */
  public void stop() {
    runner = null;
  }

  @Override
  public void run() {
    InterruptedException exception = null;
    while (runner == Thread.currentThread() && exception == null) {
      appStateManager.readStatePaths();
      appStateManager.persistState();
      haStateManager.readState();

      exception = timeUtils.sleepForMillis(pauseMillis);
    }
  }
}
