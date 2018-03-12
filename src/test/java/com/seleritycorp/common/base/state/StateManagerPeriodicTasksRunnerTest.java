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

import static org.easymock.EasyMock.expectLastCall;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Injector;
import com.seleritycorp.common.base.inject.InjectorFactory;
import com.seleritycorp.common.base.state.AppStateManager;
import com.seleritycorp.common.base.state.HaStateManager;
import com.seleritycorp.common.base.state.StateManagerPeriodicTasksRunner;
import com.seleritycorp.common.base.test.InjectingTestCase;
import com.seleritycorp.common.base.test.SettableConfig;
import com.seleritycorp.common.base.test.TimeUtilsSettableClock;

public class StateManagerPeriodicTasksRunnerTest extends InjectingTestCase {
  private AppStateManager appStateManager;
  private HaStateManager haStateManager;
  private SettableConfig config;
  private StateManagerPeriodicTasksRunner runner;
  private TimeUtilsSettableClock timeUtils;

  @Before
  public void setUp() {
    appStateManager = createMock(AppStateManager.class);
    haStateManager = createMock(HaStateManager.class);
    config = new SettableConfig();
    config.setInt("StateManagerPeriodicTasksRunner.pause", 100);

    Injector injector = InjectorFactory.getInjector();
    timeUtils = injector.getInstance(TimeUtilsSettableClock.class);
    runner =
        new StateManagerPeriodicTasksRunner(config, appStateManager, haStateManager, timeUtils);
  }

  @Test
  public void testRunPlain() {
    appStateManager.readStatePaths();
    expectLastCall().once();

    appStateManager.persistState();
    expectLastCall().once();

    haStateManager.readState();
    expectLastCall().once();

    replayAll();

    runner.start();

    timeUtils.advanceClockSettled(90);

    runner.stop();

    verifyAll();
  }

  @Test
  public void testRunPause() {
    appStateManager.readStatePaths();
    expectLastCall().times(2);

    appStateManager.persistState();
    expectLastCall().times(2);

    haStateManager.readState();
    expectLastCall().times(2);

    replayAll();

    runner.start();

    timeUtils.advanceClockSettled(90);

    timeUtils.advanceClockSettled(100);

    runner.stop();

    verifyAll();
  }

  @Test
  public void testStop() {
    appStateManager.readStatePaths();
    expectLastCall().once();

    appStateManager.persistState();
    expectLastCall().once();

    haStateManager.readState();
    expectLastCall().once();

    replayAll();

    runner.start();
    // Calling stop right away either may or may not trigger before the
    // first loop run. Hence we wait until the first run passed.
    timeUtils.advanceClockSettled(90);
    runner.stop();
    timeUtils.advanceClockSettled(100);

    verifyAll();
  }
}
