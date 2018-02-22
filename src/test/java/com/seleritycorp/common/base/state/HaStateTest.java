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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class HaStateTest {
  @Test
  public void testGetHaStateNumberMaster() {
    HaState state = HaState.MASTER;
    assertThat(state.getHaStateNumber()).isSameAs(1);
  }

  @Test
  public void testGetHaStateNumberBackup() {
    HaState state = HaState.BACKUP;
    assertThat(state.getHaStateNumber()).isSameAs(2);
  }

  @Test
  public void testGetHaStateNumberFault() {
    HaState state = HaState.FAULT;
    assertThat(state.getHaStateNumber()).isSameAs(3);
  }
}