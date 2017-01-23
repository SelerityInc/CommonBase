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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.state.AppState;
import com.seleritycorp.common.base.state.AppStatePushFacet;

public class AppStatePushFacetTest {
  private AppStatePushFacet facet;

  @Before
  public void setUp() {
    facet = new AppStatePushFacet();
  }

  @Test
  public void testInitialSate() {
    assertThat(facet.getAppState()).isSameAs(AppState.INITIALIZING);
  }

  @Test
  public void testSetAppStateReady() {
    facet.setAppState(AppState.READY);
    assertThat(facet.getAppState()).isSameAs(AppState.READY);
  }

  @Test
  public void testSetAppStateWarning() {
    facet.setAppState(AppState.WARNING);
    assertThat(facet.getAppState()).isSameAs(AppState.WARNING);
  }

  @Test
  public void testSetAppStateNullToFaulty() {
    facet.setAppState(null);
    assertThat(facet.getAppState()).isSameAs(AppState.FAULTY);
  }

  @Test
  public void testSetAppStateAnnotation() {
    facet.setAppState(AppState.WARNING, "AnnotationFoo");
    assertThat(facet.getAppState()).isSameAs(AppState.WARNING);
    assertThat(facet.getAppStateAnnotation()).isEqualTo("AnnotationFoo");
  }

  @Test
  public void testGettingStateMultipleTimes() {
    facet.setAppState(AppState.WARNING);
    assertThat(facet.getAppState()).isSameAs(AppState.WARNING);
    assertThat(facet.getAppState()).isSameAs(AppState.WARNING);
  }

  @Test
  public void testSetAppStateTransitionWarningToFaulty() {
    facet.setAppState(AppState.WARNING);
    assertThat(facet.getAppState()).isSameAs(AppState.WARNING);

    facet.setAppState(AppState.FAULTY);
    assertThat(facet.getAppState()).isSameAs(AppState.FAULTY);
  }
}
