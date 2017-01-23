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


import com.seleritycorp.common.base.config.ApplicationConfig;
import com.seleritycorp.common.base.config.ApplicationPaths;
import com.seleritycorp.common.base.config.Config;
import com.seleritycorp.common.base.logging.Log;
import com.seleritycorp.common.base.logging.LogFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HaStateManager implements HaStateManagerAccessor {
  private static final Log log = LogFactory.getLog(StateManager.class);

  /**
   * The file to pick up HA fencer state.
   *
   * <p>The first line of this file has to hold the state the HA fencers think
   * that the application is in. It is typically either MASTER, BACKUP, or
   * FAULT.
   */
  private final Path statePath;

  /**
   * Whether or not the application uses HA.
   *
   * <p>If false, a dummy HA is assumed that always thinks that the current
   * application is in state MASTER.
   */
  private boolean dynamic;

  /**
   * The state eventual HA fencers think this application is in.
   *
   * <p>See {@link #getHaState()}.
   */
  private HaState state;

  /**
   * Creates a State Manager for the High Availability State.
   * 
   * @param config The application's config
   * @param paths The application's paths
   */
  @Inject
  HaStateManager(@ApplicationConfig Config config, ApplicationPaths paths) {
    this.dynamic = config.getBoolean("HaStateManager.enabled", false);
    this.statePath = paths.getDataStatePath().resolve("ha-state");

    setHaState((this.dynamic) ? HaState.FAULT : HaState.MASTER);
  }

  @Override
  public HaState getHaState() {
    return state;
  }

  @Override
  public boolean isHaMaster() {
    return state == HaState.MASTER;
  }

  @Override
  public boolean isHaBackup() {
    return state == HaState.BACKUP;
  }

  @Override
  public boolean isHaFault() {
    return state == HaState.FAULT;
  }

  @Override
  public boolean isHaHealthy() {
    return isHaMaster() || isHaBackup();
  }

  @Override
  public boolean isHaUnhealthy() {
    return !isHaHealthy();
  }

  /**
   * Sets the state of the manager.
   */
  void setHaState(HaState state) {
    this.state = (state == null) ? HaState.FAULT : state;
  }

  /**
   * Reads the state of HA fencers, if manager is dynamic.
   */
  void readState() {
    if (dynamic) {
      readStateForced();
    }
  }

  /**
   * Reads the state of HA fencers.
   *
   * <p>State is read from the file even if the manager is not dynamic.
   */
  void readStateForced() {
    List<String> contents;
    HaState newState = HaState.FAULT;
    try {
      contents = Files.readAllLines(statePath, Charset.defaultCharset());
      if (!contents.isEmpty()) {
        String newStateString = contents.get(0);
        try {
          newState = HaState.valueOf(newStateString);
        } catch (IllegalArgumentException e) {
          log.warn("First line '" + newStateString + "' of HA state file '" + statePath
              + "' does not parse to a proper state", e);
        } catch (NullPointerException e) {
          log.warn("First line '" + newStateString + "' of HA state file '" + statePath
              + "' does not parse to a proper state", e);
        }
      }
    } catch (IOException e) {
      // Reading failed. So we silently move on with the default
      // state.
    }
    if (state != newState) {
      log.info("HA state switch: '" + state + "' -> '" + newState + "'");
    }
    state = newState;
  }
}
