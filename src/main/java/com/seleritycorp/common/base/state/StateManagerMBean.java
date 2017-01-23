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

public interface StateManagerMBean {
  /**
   * Gets number for whether the application thinks it is usable
   *
   * @return true if the app thinks it is usable. false otherwise.
   */
  public boolean getAppUsable();

  /**
   * Gets number for the application state
   *
   * <p>This method is mostly useful for numeric timeseries data collection. If
   * you want to check in code if the application is in a given state, see
   * {@link StateManager#isAppInitializing()} and its siblings.
   * 
   * @return The weight of the application state.
   */
  public int getAppStateNumber();
}
