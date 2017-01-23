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

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Provider for the application's StateManager.
 */
public class StateManagerProvider implements Provider<StateManager> {
  private AppStateManager appStateManager;
  private HaStateManager haStateManager;
  private StateManagerPeriodicTasksRunner periodicTasksRunner;

  @Inject
  StateManagerProvider(AppStateManager appStateManager, HaStateManager haStateManager,
      StateManagerPeriodicTasksRunner periodicTasksRunner) {
    this.appStateManager = appStateManager;
    this.haStateManager = haStateManager;
    this.periodicTasksRunner = periodicTasksRunner;
  }

  @Override
  public StateManager get() {
    StateManager ret = new StateManager(appStateManager, haStateManager, periodicTasksRunner);
    ret.startPeriodicTasks();
    return ret;
  }
}
