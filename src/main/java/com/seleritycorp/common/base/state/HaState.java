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
 * State signaled by high availability fencers.
 */
public enum HaState {
  /**
   * Application is healthy and the master of the high availability group.
   */
  MASTER(1),

  /**
   * Application is healthy and a backup in the high availability group.
   */
  BACKUP(2),

  /**
   * Application is unhealthy.
   */
  FAULT(3);
  
  private final int haStateNumber;
  
  HaState(int haStateNumber) {
    this.haStateNumber = haStateNumber;
  }
  
  public int getHaStateNumber() {
    return haStateNumber;
  }
}
