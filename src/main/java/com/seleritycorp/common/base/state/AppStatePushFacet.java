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
 * An AppStateFacet for states that rarely change.
 */
public class AppStatePushFacet implements AnnotatedAppStateFacet {
  interface Factory {
    AppStatePushFacet create();
  }

  private AppState state;
  private String annotation;

  public AppStatePushFacet() {
    state = AppState.INITIALIZING;
  }

  @Override
  public AppState getAppState() {
    return state;
  }

  @Override
  public String getAppStateAnnotation() {
    return annotation;
  }

  /**
   * Set the state for this facet.
   *
   * <p>The annotation gets reset.
   * 
   * @param state The application state to set. If null, it is taken as
   *        FAULTY. But avoid passing null; if the state should get set to
   *        FAULTY, please explicitly pass FAULTY.
   */
  public void setAppState(AppState state) {
    if (state == null) {
      state = AppState.FAULTY;
    }
    setAppState(state, null);
  }

  /**
   * Set the state and annotation for this facet.
   * 
   * @param state the state to set
   * @param annotation the annotation to set
   */
  public void setAppState(AppState state, String annotation) {
    // If we'd set the state before the annotation, and the state manager queries the facet after
    // setting state but before setting the annotation, it would log the state change with the
    // wrong annotation.
    // That would mess up our logs.
    // As we want to avoid locking, and as annotation changes to not cause log entries, we first
    // set the annotation, and only then set the state.
    // That way, we get good log entries without locking.
    // (Periodic state dumps may show the new annotation for the old state due to the same race
    // condition. But that is not much of a concern)
    this.annotation = annotation;
    this.state = state;
  }
}
