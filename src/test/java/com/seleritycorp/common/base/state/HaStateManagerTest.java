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
import static org.easymock.EasyMock.expect;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.config.ApplicationPaths;
import com.seleritycorp.common.base.state.HaState;
import com.seleritycorp.common.base.state.HaStateManager;
import com.seleritycorp.common.base.test.FileTestCase;
import com.seleritycorp.common.base.test.SettableConfig;

public class HaStateManagerTest extends FileTestCase {
  Path statePath;
  ApplicationPaths paths;
  SettableConfig config;


  @Before
  public void setUp() throws IOException {
    Path dir = createTempDirectory();

    paths = createMock(ApplicationPaths.class);
    expect(paths.getDataStatePath()).andReturn(dir);

    statePath = dir.resolve("ha-state");

    config = new SettableConfig();
  }

  @Test
  public void testInitialStateStatic() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();

    assertThat(stateManager.getHaState()).isSameAs(HaState.MASTER);

    verifyAll();
  }

  @Test
  public void testInitialStateDynamic() {
    replayAll();

    HaStateManager stateManager = createHaStateManager(true);

    assertThat(stateManager.getHaState()).isSameAs(HaState.FAULT);

    verifyAll();
  }

  @Test
  public void testSetStateMaster() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();

    stateManager.setHaState(HaState.MASTER);
    assertThat(stateManager.getHaState()).isSameAs(HaState.MASTER);

    verifyAll();
  }

  @Test
  public void testSetStateBackup() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();

    stateManager.setHaState(HaState.BACKUP);
    assertThat(stateManager.getHaState()).isSameAs(HaState.BACKUP);

    verifyAll();
  }

  @Test
  public void testSetStateFault() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();

    stateManager.setHaState(HaState.FAULT);
    assertThat(stateManager.getHaState()).isSameAs(HaState.FAULT);

    verifyAll();
  }

  @Test
  public void testSetStateNull() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();

    assertThat(stateManager.getHaState()).isSameAs(HaState.MASTER);
    assertThat(stateManager.getHaState()).isSameAs(HaState.MASTER);

    stateManager.setHaState(null);
    assertThat(stateManager.getHaState()).isSameAs(HaState.FAULT);

    verifyAll();
  }

  @Test
  public void testIsMasterMaster() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    stateManager.setHaState(HaState.MASTER);
    assertThat(stateManager.isHaMaster()).isTrue();

    verifyAll();
  }

  @Test
  public void testIsMasterBackup() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    stateManager.setHaState(HaState.BACKUP);
    assertThat(stateManager.isHaMaster()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsMasterFault() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    stateManager.setHaState(HaState.FAULT);
    assertThat(stateManager.isHaMaster()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsBackupMaster() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    stateManager.setHaState(HaState.MASTER);
    assertThat(stateManager.isHaBackup()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsBackupBackup() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    stateManager.setHaState(HaState.BACKUP);
    assertThat(stateManager.isHaBackup()).isTrue();

    verifyAll();
  }

  @Test
  public void testIsBackupFault() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    stateManager.setHaState(HaState.FAULT);
    assertThat(stateManager.isHaBackup()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsFaultMaster() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    stateManager.setHaState(HaState.MASTER);
    assertThat(stateManager.isHaFault()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsFaultBackup() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    stateManager.setHaState(HaState.BACKUP);
    assertThat(stateManager.isHaFault()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsFaultFault() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    stateManager.setHaState(HaState.FAULT);
    assertThat(stateManager.isHaFault()).isTrue();

    verifyAll();
  }

  @Test
  public void testIsHealthyMaster() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    stateManager.setHaState(HaState.MASTER);
    assertThat(stateManager.isHaHealthy()).isTrue();

    verifyAll();
  }

  @Test
  public void testIsHealthyBackup() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    stateManager.setHaState(HaState.BACKUP);
    assertThat(stateManager.isHaHealthy()).isTrue();

    verifyAll();
  }

  @Test
  public void testIsHealthyFault() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    stateManager.setHaState(HaState.FAULT);
    assertThat(stateManager.isHaHealthy()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsUnhealthyMaster() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    stateManager.setHaState(HaState.MASTER);
    assertThat(stateManager.isHaUnhealthy()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsUnhealthyBackup() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    stateManager.setHaState(HaState.BACKUP);
    assertThat(stateManager.isHaUnhealthy()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsUnhealthyFault() {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    stateManager.setHaState(HaState.FAULT);
    assertThat(stateManager.isHaUnhealthy()).isTrue();

    verifyAll();
  }

  @Test
  public void testReadStateForcedMaster() throws IOException {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    writeStateFile("MASTER");
    stateManager.readStateForced();
    assertThat(stateManager.getHaState()).isSameAs(HaState.MASTER);

    verifyAll();
  }

  @Test
  public void testReadStateForcedBackup() throws IOException {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    writeStateFile("BACKUP");
    stateManager.readStateForced();
    assertThat(stateManager.getHaState()).isSameAs(HaState.BACKUP);

    verifyAll();
  }

  @Test
  public void testReadStateForcedFault() throws IOException {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    writeStateFile("FAULT");
    stateManager.readStateForced();
    assertThat(stateManager.getHaState()).isSameAs(HaState.FAULT);

    verifyAll();
  }

  @Test
  public void testReadStateForcedFirstLine() throws IOException {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    writeStateFile("MASTER\nBACKUP");
    stateManager.readStateForced();
    assertThat(stateManager.getHaState()).isSameAs(HaState.MASTER);

    verifyAll();
  }

  @Test
  public void testReadStateForcedFoo() throws IOException {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    writeStateFile("Foo");
    stateManager.readStateForced();
    assertThat(stateManager.getHaState()).isSameAs(HaState.FAULT);

    verifyAll();
  }

  @Test
  public void testReadStateForcedEmpty() throws IOException {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    writeStateFile("");
    stateManager.readStateForced();
    assertThat(stateManager.getHaState()).isSameAs(HaState.FAULT);

    verifyAll();
  }

  @Test
  public void testReadStateForcedNonExisting() throws IOException {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    File stateFile = new File(statePath.toString());
    assertThat(stateFile).doesNotExist();
    stateManager.setHaState(HaState.MASTER);

    stateManager.readStateForced();

    assertThat(stateManager.getHaState()).isSameAs(HaState.FAULT);

    verifyAll();
  }

  @Test
  public void testReadDynamic() throws IOException {
    replayAll();

    HaStateManager stateManager = createHaStateManager(true);
    writeStateFile("MASTER");
    stateManager.setHaState(HaState.BACKUP);

    stateManager.readState();

    assertThat(stateManager.getHaState()).isSameAs(HaState.MASTER);

    verifyAll();
  }

  @Test
  public void testReadStatic() throws IOException {
    replayAll();

    HaStateManager stateManager = createHaStateManager();
    writeStateFile("MASTER");
    stateManager.setHaState(HaState.BACKUP);

    stateManager.readState();

    // Not "MASTER", as manager is not dynamic. So un-forced reads
    // are ignored.
    assertThat(stateManager.getHaState()).isSameAs(HaState.BACKUP);

    verifyAll();
  }

  private HaStateManager createHaStateManager() {
    return createHaStateManager(false);
  }

  private HaStateManager createHaStateManager(boolean dynamic) {
    config.setBoolean("HaStateManager.enabled", dynamic);
    return new HaStateManager(config, paths);
  }

  private void writeStateFile(String contents) throws IOException {
    Files.write(statePath, contents.getBytes());
  }
}
