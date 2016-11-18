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
 * A facet of the total application state
 *
 * <p>Each AppStateFacet instance reports the state it thinks it is in. The
 * facets register with an AppStateManager, which in turn combines all the
 * registered AppStateFacets' states to the total application state.
 * 
 * <p>There, each individual facet of an application (E.g.: Trout part,
 * web-server, resource managers, ...) can individually report their own state
 * without having to be careful not to overwrite states of other parts. And
 * still HA-fencers and monitoring get a wholisting view of the application
 * with available details.
 */
public interface AppStateFacet {
  /**
   * Gets the AppState this facet is in
   *
   * <p>This method will be called at least every few seconds, maybe from ever
   * different threads. Make sure it returns quickly and is thread-safe.
   * 
   * @return The AppState this facet in
   */
  public AppState getAppState();
}
