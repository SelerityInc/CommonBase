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

public enum AppState {
  /**
   * The application is initializing. It should not yet receive traffic.
   *
   * <p>This state is stronger than READY, and WARNING, but weaker than FAULTY.
   */
  INITIALIZING(false, 3),

  /**
   * The application is ready and does not show issues.
   *
   * <p>This state is the weakest state and weaker than FAULTY, INITIALIZING,
   * and WARNING.
   */
  READY(true, 1),

  /**
   * The application is still ok but is having minor issues.
   *
   * <p>If an application is in this state, it is still ok to send new traffic.
   * But the applications needs attention. So this state alerts for example
   * in monitoring.
   *
   * <p>This state is stronger than READY, but weaker than FAULTY, INITIALIZING.
   */
  WARNING(true, 2),

  /**
   * The application is unhealthy and should currently not be used.
   *
   * <p>This state is the strongest state and stronger than WARNKNG, READY, and
   * INITIALIZING.
   */
  FAULTY(false, 4);

  private final boolean usable;
  private final int weight;

  AppState(boolean usable, int weight) {
    this.usable = usable;
    this.weight = weight;
  }

  public boolean isUsable() {
    return this.usable;
  }

  int getWeight() {
    return this.weight;
  }

  /**
   * Picks the worse of two states.
   * 
   * @param other state to combine this one with
   * @return the heavier of the two states
   */
  public AppState combine(AppState other) {
    AppState ret = this;
    if (other != null && other.weight > this.weight) {
      ret = other;
    }
    return ret;
  }
}
