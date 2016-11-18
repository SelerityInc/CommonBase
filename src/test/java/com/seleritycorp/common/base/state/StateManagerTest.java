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
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.state.AppState;
import com.seleritycorp.common.base.state.AppStateFacet;
import com.seleritycorp.common.base.state.AppStateManager;
import com.seleritycorp.common.base.state.AppStatePushFacet;
import com.seleritycorp.common.base.state.HaState;
import com.seleritycorp.common.base.state.HaStateManager;
import com.seleritycorp.common.base.state.StateManager;
import com.seleritycorp.common.base.state.StateManagerPeriodicTasksRunner;

public class StateManagerTest extends EasyMockSupport {
  private AppStateManager appStateManager;
  private HaStateManager haStateManager;
  private StateManagerPeriodicTasksRunner runner;
  private StateManager stateManager;

  @Before
  public void setUp() {
    appStateManager = createMock(AppStateManager.class);
    haStateManager = createMock(HaStateManager.class);
    runner = createMock(StateManagerPeriodicTasksRunner.class);

    stateManager = new StateManager(appStateManager, haStateManager, runner);
  }

  @Test
  public void testGetHaStateBackup() {
    HaState expected = HaState.BACKUP;
    expect(haStateManager.getHaState()).andReturn(expected).once();

    replayAll();

    HaState actual = stateManager.getHaState();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testGetHaStateMaster() {
    HaState expected = HaState.MASTER;
    expect(haStateManager.getHaState()).andReturn(expected).once();

    replayAll();

    HaState actual = stateManager.getHaState();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsHAMasterTrue() {
    boolean expected = true;
    expect(haStateManager.isHaMaster()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isHaMaster();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsHAMasterFalse() {
    boolean expected = false;
    expect(haStateManager.isHaMaster()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isHaMaster();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsHABackupTrue() {
    boolean expected = true;
    expect(haStateManager.isHaBackup()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isHaBackup();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsHABackupFalse() {
    boolean expected = false;
    expect(haStateManager.isHaBackup()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isHaBackup();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsHAFaultTrue() {
    boolean expected = true;
    expect(haStateManager.isHaFault()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isHaFault();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsHAFaultFalse() {
    boolean expected = false;
    expect(haStateManager.isHaFault()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isHaFault();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsHAHealthyTrue() {
    boolean expected = true;
    expect(haStateManager.isHaHealthy()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isHaHealthy();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsHAHealthyFalse() {
    boolean expected = false;
    expect(haStateManager.isHaHealthy()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isHaHealthy();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsHAUnhealthyTrue() {
    boolean expected = true;
    expect(haStateManager.isHaUnhealthy()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isHaUnhealthy();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsHAUnhealthyFalse() {
    boolean expected = false;
    expect(haStateManager.isHaUnhealthy()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isHaUnhealthy();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testSetAppStateInitializing() {
    appStateManager.setMainAppState(AppState.INITIALIZING);
    expectLastCall().once();

    replayAll();

    stateManager.setMainAppState(AppState.INITIALIZING);

    verifyAll();
  }

  @Test
  public void testSetAppStateWarning() {
    appStateManager.setMainAppState(AppState.WARNING);
    expectLastCall().once();

    replayAll();

    stateManager.setMainAppState(AppState.WARNING);

    verifyAll();
  }

  @Test
  public void testGetAppStateBackup() {
    AppState expected = AppState.READY;
    expect(appStateManager.getAppState()).andReturn(expected).once();

    replayAll();

    AppState actual = stateManager.getAppState();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testGetAppStateFaulty() {
    AppState expected = AppState.FAULTY;
    expect(appStateManager.getAppState()).andReturn(expected).once();

    replayAll();

    AppState actual = stateManager.getAppState();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsAppInitializingTrue() {
    boolean expected = true;
    expect(appStateManager.isAppInitializing()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isAppInitializing();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsAppInitializingFalse() {
    boolean expected = false;
    expect(appStateManager.isAppInitializing()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isAppInitializing();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsAppReadyTrue() {
    boolean expected = true;
    expect(appStateManager.isAppReady()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isAppReady();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsAppReadyFalse() {
    boolean expected = false;
    expect(appStateManager.isAppReady()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isAppReady();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsAppWarningTrue() {
    boolean expected = true;
    expect(appStateManager.isAppWarning()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isAppWarning();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsAppWarningFalse() {
    boolean expected = false;
    expect(appStateManager.isAppWarning()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isAppWarning();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsAppFaultyTrue() {
    boolean expected = true;
    expect(appStateManager.isAppFaulty()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isAppFaulty();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsAppFaultyFalse() {
    boolean expected = false;
    expect(appStateManager.isAppFaulty()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isAppFaulty();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsAppUsableTrue() {
    boolean expected = true;
    expect(appStateManager.isAppUsable()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isAppUsable();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsAppUsableFalse() {
    boolean expected = false;
    expect(appStateManager.isAppUsable()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isAppUsable();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testGetAppUsableNumberTrue() {
    boolean expected = true;
    expect(appStateManager.isAppUsable()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.getAppUsable();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testGetAppUsableNumberFalse() {
    boolean expected = false;
    expect(appStateManager.isAppUsable()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.getAppUsable();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testIsAppUnusableTrue() {
    boolean expected = true;
    expect(appStateManager.isAppUnusable()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isAppUnusable();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testIsAppUnusableFalse() {
    boolean expected = false;
    expect(appStateManager.isAppUnusable()).andReturn(expected).once();

    replayAll();

    boolean actual = stateManager.isAppUnusable();

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testStartPeriodicTasks() {
    runner.start();
    expectLastCall().once();

    replayAll();

    stateManager.startPeriodicTasks();

    verifyAll();
  }

  @Test
  public void testStopPeriodicTasks() {
    runner.stop();
    expectLastCall().once();

    replayAll();

    stateManager.stopPeriodicTasks();

    verifyAll();
  }

  @Test
  public void testCreateRegisteredAppStatePushFacetPeriodicTasks() {
    AppStatePushFacet facet = createMock(AppStatePushFacet.class);
    expect(appStateManager.createRegisteredAppStatePushFacet("foo")).andReturn(facet);
    expectLastCall().once();

    replayAll();

    AppStatePushFacet actual = stateManager.createRegisteredAppStatePushFacet("foo");

    verifyAll();

    assertThat(actual).isSameAs(facet);
  }

  @Test
  public void testStopPeriodicTasksOk() {
    AppStateFacet facet = createMock(AppStateFacet.class);
    expect(appStateManager.registerAppStateFacet("foo", facet)).andReturn(true);

    replayAll();

    boolean result = stateManager.registerAppStateFacet("foo", facet);

    verifyAll();

    assertThat(result).isTrue();
  }

  @Test
  public void testStopPeriodicTasksFail() {
    AppStateFacet facet = createMock(AppStateFacet.class);
    expect(appStateManager.registerAppStateFacet("foo", facet)).andReturn(false);

    replayAll();

    boolean result = stateManager.registerAppStateFacet("foo", facet);

    verifyAll();

    assertThat(result).isFalse();
  }

  @Test
  public void testGetAppStateNumberReady() {
    expect(appStateManager.getAppState()).andReturn(AppState.READY).once();

    replayAll();

    int actual = stateManager.getAppStateNumber();

    verifyAll();

    assertThat(actual).isSameAs(1);
  }

  @Test
  public void testGetAppStateNumberFaulty() {
    expect(appStateManager.getAppState()).andReturn(AppState.FAULTY).once();

    replayAll();

    int actual = stateManager.getAppStateNumber();

    verifyAll();

    assertThat(actual).isSameAs(4);
  }

}
