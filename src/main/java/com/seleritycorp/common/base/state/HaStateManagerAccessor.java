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

package com.seleritycorp.common.base.state;

/**
 * Methods to be exposed by all classes that expose HaState.
 */
public interface HaStateManagerAccessor {
  /**
   * The state eventual HA fencers think this application is in.
   *
   * <p>This is either MASTER (if the HA fencers thing that this application is
   * healthy and the main running instance), BACKUP (if the HA fencers think
   * that this application is healthy, but others are the main running
   * instances), or FAULT (if the HA fencers think that this application has
   * problems).
   *
   * <p>Instead of using this method directly, you can also directly check for
   * a given state by {@link #isHaMaster()}, {@link #isHaBackup()},
   * and {@link #isHaFault()}.
   * 
   * @return the HA state
   */
  public HaState getHaState();

  /**
   * @return true iff HA fencers think the application is healthy and the
   *         main running instance.
   */
  public boolean isHaMaster();

  /**
   * @return true iff HA fencers think the application is healthy but other
   *         instances are the main instances.
   */
  public boolean isHaBackup();

  /**
   * @return true iff HA fencers think the application has problems.
   */
  public boolean isHaFault();

  /**
   * @return true iff HA fencers think the application is healthy.
   */
  public boolean isHaHealthy();

  /**
   * @return true iff HA fencers think the application is unhealthy.
   */
  public boolean isHaUnhealthy();
}
