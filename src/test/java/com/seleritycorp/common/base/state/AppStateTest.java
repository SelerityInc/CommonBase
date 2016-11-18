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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.seleritycorp.common.base.state.AppState;

public class AppStateTest {
  @Test
  public void testWeightInitializing() {
    AppState state = AppState.INITIALIZING;
    assertThat(state.getWeight()).isSameAs(3);
  }

  @Test
  public void testWeightReady() {
    AppState state = AppState.READY;
    assertThat(state.getWeight()).isSameAs(1);
  }

  @Test
  public void testWeightWarning() {
    AppState state = AppState.WARNING;
    assertThat(state.getWeight()).isSameAs(2);
  }

  @Test
  public void testWeightFaulty() {
    AppState state = AppState.FAULTY;
    assertThat(state.getWeight()).isSameAs(4);
  }

  @Test
  public void testUsableInitializing() {
    AppState state = AppState.INITIALIZING;
    assertThat(state.isUsable()).isFalse();
  }

  @Test
  public void testUsableReady() {
    AppState state = AppState.READY;
    assertThat(state.isUsable()).isTrue();
  }

  @Test
  public void testUsableWarning() {
    AppState state = AppState.WARNING;
    assertThat(state.isUsable()).isTrue();
  }

  @Test
  public void testUsableFaulty() {
    AppState state = AppState.FAULTY;
    assertThat(state.isUsable()).isFalse();
  }

  @Test
  public void testCombineInitializingInitializing() {
    AppState source = AppState.INITIALIZING;
    AppState combiner = AppState.INITIALIZING;
    AppState expected = source;
    assertThat(source.combine(combiner)).isSameAs(expected);
  }

  @Test
  public void testCombineInitializingReady() {
    AppState source = AppState.INITIALIZING;
    AppState combiner = AppState.READY;
    AppState expected = source;
    assertThat(source.combine(combiner)).isSameAs(expected);
  }

  @Test
  public void testCombineInitializingWarning() {
    AppState source = AppState.INITIALIZING;
    AppState combiner = AppState.WARNING;
    AppState expected = source;
    assertThat(source.combine(combiner)).isSameAs(expected);
  }

  @Test
  public void testCombineInitializingFaulty() {
    AppState source = AppState.INITIALIZING;
    AppState combiner = AppState.FAULTY;
    AppState expected = combiner;
    assertThat(source.combine(combiner)).isSameAs(expected);
  }

  @Test
  public void testCombineReadyInitializing() {
    AppState source = AppState.READY;
    AppState combiner = AppState.INITIALIZING;
    AppState expected = combiner;
    assertThat(source.combine(combiner)).isSameAs(expected);
  }

  @Test
  public void testCombineReadyReady() {
    AppState source = AppState.READY;
    AppState combiner = AppState.READY;
    AppState expected = source;
    assertThat(source.combine(combiner)).isSameAs(expected);
  }

  @Test
  public void testCombineReadyWarning() {
    AppState source = AppState.READY;
    AppState combiner = AppState.WARNING;
    AppState expected = combiner;
    assertThat(source.combine(combiner)).isSameAs(expected);
  }

  @Test
  public void testCombineReadyFaulty() {
    AppState source = AppState.READY;
    AppState combiner = AppState.FAULTY;
    AppState expected = combiner;
    assertThat(source.combine(combiner)).isSameAs(expected);
  }

  @Test
  public void testCombineWarningInitializing() {
    AppState source = AppState.WARNING;
    AppState combiner = AppState.INITIALIZING;
    AppState expected = combiner;
    assertThat(source.combine(combiner)).isSameAs(expected);
  }

  @Test
  public void testCombineWarningReady() {
    AppState source = AppState.WARNING;
    AppState combiner = AppState.READY;
    AppState expected = source;
    assertThat(source.combine(combiner)).isSameAs(expected);
  }

  @Test
  public void testCombineWarningWarning() {
    AppState source = AppState.WARNING;
    AppState combiner = AppState.WARNING;
    AppState expected = source;
    assertThat(source.combine(combiner)).isSameAs(expected);
  }

  @Test
  public void testCombineWarningFaulty() {
    AppState source = AppState.WARNING;
    AppState combiner = AppState.FAULTY;
    AppState expected = combiner;
    assertThat(source.combine(combiner)).isSameAs(expected);
  }

  @Test
  public void testCombineFaultyInitializing() {
    AppState source = AppState.FAULTY;
    AppState combiner = AppState.INITIALIZING;
    AppState expected = source;
    assertThat(source.combine(combiner)).isSameAs(expected);
  }

  @Test
  public void testCombineFaultyReady() {
    AppState source = AppState.FAULTY;
    AppState combiner = AppState.READY;
    AppState expected = source;
    assertThat(source.combine(combiner)).isSameAs(expected);
  }

  @Test
  public void testCombineFaultyWarning() {
    AppState source = AppState.FAULTY;
    AppState combiner = AppState.WARNING;
    AppState expected = source;
    assertThat(source.combine(combiner)).isSameAs(expected);
  }

  @Test
  public void testCombineFaultyFaulty() {
    AppState source = AppState.FAULTY;
    AppState combiner = AppState.FAULTY;
    AppState expected = source;
    assertThat(source.combine(combiner)).isSameAs(expected);
  }
}
