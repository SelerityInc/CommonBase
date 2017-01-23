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
import static org.assertj.core.api.Assertions.fail;
import static org.easymock.EasyMock.expect;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.config.ApplicationPaths;
import com.seleritycorp.common.base.inject.InjectorFactory;
import com.seleritycorp.common.base.state.AnnotatedAppStateFacet;
import com.seleritycorp.common.base.state.AppState;
import com.seleritycorp.common.base.state.AppStateFacet;
import com.seleritycorp.common.base.state.AppStateManager;
import com.seleritycorp.common.base.state.AppStatePushFacet;
import com.seleritycorp.common.base.test.InjectingTestCase;
import com.seleritycorp.common.base.test.TimeUtilsSettableClock;
import com.seleritycorp.common.base.time.Clock;

public class AppStateManagerTest extends InjectingTestCase {
  private ApplicationPaths paths;
  private TimeUtilsSettableClock timeUtils;

  private Path statePath;
  private Path usablePath;
  private Path drainPath;
  private Path overridePath;

  @Before
  public void setUp() throws IOException {
    Path dir = createTempDirectory();

    paths = createMock(ApplicationPaths.class);
    expect(paths.getDataStatePath()).andReturn(dir);

    statePath = dir.resolve("app-state");
    usablePath = Paths.get(statePath.toString() + ".usable");
    drainPath = Paths.get(statePath.toString() + ".drain");
    overridePath = Paths.get(statePath.toString() + ".override");

    timeUtils = InjectorFactory.getInjector().getInstance(TimeUtilsSettableClock.class);
  }

  @Test
  public void testStateSettingInitializing() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.INITIALIZING);
    assertThat(stateManager.getAppState()).isSameAs(AppState.INITIALIZING);

    verifyAll();
  }

  @Test
  public void testStateSettingReady() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);
    assertThat(stateManager.getAppState()).isSameAs(AppState.READY);

    verifyAll();
  }

  @Test
  public void testStateSettingWarning() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.WARNING);
    assertThat(stateManager.getAppState()).isSameAs(AppState.WARNING);

    verifyAll();
  }

  @Test
  public void testStateSettingFaulty() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.FAULTY);
    assertThat(stateManager.getAppState()).isSameAs(AppState.FAULTY);

    verifyAll();
  }

  @Test
  public void testStateSettingNullToFaulty() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(null);
    assertThat(stateManager.getAppState()).isSameAs(AppState.FAULTY);

    verifyAll();
  }

  @Test
  public void testIsInitializingReady() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);
    assertThat(stateManager.isAppInitializing()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsInitializingInitializing() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.INITIALIZING);
    assertThat(stateManager.isAppInitializing()).isTrue();

    verifyAll();
  }

  @Test
  public void testIsReadyReady() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);
    assertThat(stateManager.isAppReady()).isTrue();

    verifyAll();
  }

  @Test
  public void testIsReadyInitializing() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.INITIALIZING);
    assertThat(stateManager.isAppReady()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsWarningReady() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);
    assertThat(stateManager.isAppWarning()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsWarningWarning() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.WARNING);
    assertThat(stateManager.isAppWarning()).isTrue();

    verifyAll();
  }

  @Test
  public void testIsFaultyReady() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);
    assertThat(stateManager.isAppFaulty()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsFaultyFaulty() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.FAULTY);
    assertThat(stateManager.isAppFaulty()).isTrue();

    verifyAll();
  }

  @Test
  public void testIsUsableInitializing() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.INITIALIZING);
    assertThat(stateManager.isAppUsable()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsUsableReady() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);
    assertThat(stateManager.isAppUsable()).isTrue();

    verifyAll();
  }

  @Test
  public void testIsUsableWarning() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.WARNING);
    assertThat(stateManager.isAppUsable()).isTrue();

    verifyAll();
  }

  @Test
  public void testIsUsableFaulty() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.FAULTY);
    assertThat(stateManager.isAppUsable()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsUnusableInitializing() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.INITIALIZING);
    assertThat(stateManager.isAppUnusable()).isTrue();

    verifyAll();
  }

  @Test
  public void testIsUnusableReady() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);
    assertThat(stateManager.isAppUnusable()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsUnusableWarning() {
    replayAll();


    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.WARNING);
    assertThat(stateManager.isAppUnusable()).isFalse();

    verifyAll();
  }

  @Test
  public void testIsUnusableFaulty() {
    replayAll();


    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.FAULTY);
    assertThat(stateManager.isAppUnusable()).isTrue();

    verifyAll();
  }

  @Test
  public void testStateWritingReady() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);

    stateManager.persistState();

    verifyAll();

    assertWrittenStateContentsEquals("READY");
    assertUsable();
  }

  @Test
  public void testStateWritingFaulty() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.FAULTY);

    stateManager.persistState();

    verifyAll();

    assertWrittenStateContentsEquals("FAULTY");
    assertUnusable();
  }

  @Test
  public void testStateWritingTransitionUsableToUsable() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);
    stateManager.persistState();
    assertWrittenStateContentsEquals("READY");
    assertUsable();

    // Pausing 3 seconds, to make sure that the mtime of the above first
    // persisting would not could as good mtime for the second persisting
    // (below). That way, we test that if the "usable" file exists before
    // persisting, it has it's mtime updated.
    timeUtils.advanceClockSettled(3000);

    stateManager.setMainAppState(AppState.WARNING);
    stateManager.persistState();

    verifyAll();

    assertWrittenStateContentsEquals("WARNING");
    assertUsable();
  }

  @Test
  public void testStateWritingTransitionUsableToUnusable() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);
    stateManager.persistState();
    assertWrittenStateContentsEquals("READY");
    assertUsable();

    stateManager.setMainAppState(AppState.FAULTY);
    stateManager.persistState();

    verifyAll();

    assertWrittenStateContentsEquals("FAULTY");
    assertUnusable();
  }

  @Test
  public void testStateWritingTransitionUnusableToUsable() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.INITIALIZING);
    stateManager.persistState();
    assertWrittenStateContentsEquals("INITIALIZING");
    assertUnusable();

    stateManager.setMainAppState(AppState.READY);
    stateManager.persistState();

    verifyAll();

    assertWrittenStateContentsEquals("READY");
    assertUsable();
  }

  @Test
  public void testStateWritingTransitionUnusableToUnusable() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.INITIALIZING);
    stateManager.persistState();
    assertWrittenStateContentsEquals("INITIALIZING");
    assertUnusable();

    stateManager.setMainAppState(AppState.FAULTY);
    stateManager.persistState();

    verifyAll();

    assertWrittenStateContentsEquals("FAULTY");
    assertUnusable();
  }

  @Test
  public void testRegisterAppStateFacetSingle() {
    AppStateFacet facet = createMock(AppStateFacet.class);
    expect(facet.getAppState()).andReturn(AppState.INITIALIZING).times(0, 1);

    replayAll();

    AppStateManager stateManager = createAppStateManager();

    assertThat(stateManager.registerAppStateFacet("foo", facet)).isTrue();

    verifyAll();
  }

  @Test
  public void testRegisterAppStateFacetDoubleSameNamesSameFacets() {
    AppStateFacet facet = createMock(AppStateFacet.class);
    expect(facet.getAppState()).andReturn(AppState.INITIALIZING).times(0, 2);

    replayAll();

    AppStateManager stateManager = createAppStateManager();

    assertThat(stateManager.registerAppStateFacet("foo", facet)).isTrue();
    assertThat(stateManager.registerAppStateFacet("foo", facet)).isTrue();

    verifyAll();
  }

  @Test
  public void testRegisterAppStateFacetDoubleSameNamesDifferentFacets() {
    AppStateFacet facet1 = createMock(AppStateFacet.class);
    expect(facet1.getAppState()).andReturn(AppState.INITIALIZING).times(0, 1);
    AppStateFacet facet2 = createMock(AppStateFacet.class);
    expect(facet2.getAppState()).andReturn(AppState.INITIALIZING).times(0, 1);

    replayAll();

    AppStateManager stateManager = createAppStateManager();

    assertThat(stateManager.registerAppStateFacet("foo", facet1)).isTrue();
    assertThat(stateManager.registerAppStateFacet("foo", facet2)).isFalse();

    verifyAll();
  }

  @Test
  public void testRegisterAppStateFacetDoubleDifferentNamesDifferentFacets() {
    AppStateFacet facet1 = createMock(AppStateFacet.class);
    expect(facet1.getAppState()).andReturn(AppState.INITIALIZING).times(0, 1);
    AppStateFacet facet2 = createMock(AppStateFacet.class);
    expect(facet2.getAppState()).andReturn(AppState.INITIALIZING).times(0, 1);

    replayAll();

    AppStateManager stateManager = createAppStateManager();

    assertThat(stateManager.registerAppStateFacet("foo", facet1)).isTrue();
    assertThat(stateManager.registerAppStateFacet("bar", facet2)).isTrue();

    verifyAll();
  }

  @Test
  public void testPushFacetRegistration() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    AppStatePushFacet facetFoo = stateManager.createRegisteredAppStatePushFacet("foo");
    assertThat(facetFoo).isNotNull();

    assertThat(stateManager.getAppState()).isSameAs(AppState.INITIALIZING);

    stateManager.setMainAppState(AppState.READY);

    // main facet is READY
    // facetFoo is still INITIALIZING.
    // -> Expecting INITIALIZING.
    assertThat(stateManager.getAppState()).isSameAs(AppState.INITIALIZING);

    facetFoo.setAppState(AppState.WARNING);
    // main facet is READY
    // facetFoo is still WARNING.
    // -> Expecting WARNING.
    assertThat(stateManager.getAppState()).isSameAs(AppState.WARNING);

    stateManager.setMainAppState(AppState.FAULTY);
    // main facet is FAULTY
    // facetFoo is still WARNING.
    // -> Expecting FAULTY.
    assertThat(stateManager.getAppState()).isSameAs(AppState.FAULTY);

    verifyAll();
  }

  @Test
  public void testGetStatusReportPlain() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.WARNING);

    String report = stateManager.getStatusReport();

    verifyAll();

    assertThat(report).isNotNull();

    assertThat(report).startsWith("WARNING\n");
    assertThat(report).contains("Application state: WARNING");
    assertThat(report).matches("(?s).*WARNING *main.*");
  }

  @Test
  public void testGetStatusReportMixedFacets() {
    AppStateFacet facetBar = createMock(AppStateFacet.class);
    expect(facetBar.getAppState()).andReturn(AppState.FAULTY).times(1, 3);

    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);

    AppStatePushFacet facetFoo = stateManager.createRegisteredAppStatePushFacet("foo");
    assertThat(facetFoo).isNotNull();
    facetFoo.setAppState(AppState.WARNING, "Foo-Annotation");

    assertThat(stateManager.registerAppStateFacet("bar", facetBar)).isTrue();
    String report = stateManager.getStatusReport();

    verifyAll();

    assertThat(report).isNotNull();
    assertThat(report).startsWith("FAULTY\n");
    assertThat(report).contains("Application state: FAULTY");
    assertThat(report).matches("(?s).*READY *main.*");
    assertThat(report).matches("(?s).*WARNING *foo *Foo-Annotation.*");
    assertThat(report).matches("(?s).*FAULTY *bar.*");
  }

  @Test
  public void testGetAppStateFailingFacet() {
    AppStateFacet facet = createMock(AppStateFacet.class);
    Throwable exception = new RuntimeException("foo");
    expect(facet.getAppState()).andReturn(AppState.READY);
    expect(facet.getAppState()).andThrow(exception);

    replayAll();

    AppStateManager stateManager = createAppStateManager();

    assertThat(stateManager.registerAppStateFacet("bar", facet)).isTrue();
    assertThat(stateManager.getAppState()).isSameAs(AppState.FAULTY);

    verifyAll();
  }

  @Test
  public void testGetAppStateFacetNullStateAlways() {
    AppStateFacet facet = createMock(AppStateFacet.class);
    expect(facet.getAppState()).andReturn(null).anyTimes();

    replayAll();

    AppStateManager stateManager = createAppStateManager();

    assertThat(stateManager.registerAppStateFacet("bar", facet)).isTrue();
    assertThat(stateManager.getAppState()).isSameAs(AppState.FAULTY);

    verifyAll();
  }

  @Test
  public void testGetAppStateFacetNullStateAfterReady() {
    AppStateFacet facet = createMock(AppStateFacet.class);
    expect(facet.getAppState()).andReturn(AppState.READY);
    expect(facet.getAppState()).andReturn(null);

    replayAll();

    AppStateManager stateManager = createAppStateManager();

    assertThat(stateManager.registerAppStateFacet("bar", facet)).isTrue();
    assertThat(stateManager.getAppState()).isSameAs(AppState.FAULTY);

    verifyAll();
  }

  @Test
  public void testGetStatusReportFaultyFacetAppState() {
    AppStateFacet facet = createMock(AppStateFacet.class);
    Throwable exception = new RuntimeException("foo");
    expect(facet.getAppState()).andReturn(AppState.READY);
    expect(facet.getAppState()).andThrow(exception).anyTimes();

    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);

    assertThat(stateManager.registerAppStateFacet("bar", facet)).isTrue();
    String report = stateManager.getStatusReport();

    verifyAll();

    assertThat(report).isNotNull();
    assertThat(report).startsWith("FAULTY\n");
    assertThat(report).contains("Application state: FAULTY");
    assertThat(report).matches("(?s).*READY *main.*");
    assertThat(report).matches("(?s).*FAULTY *bar .*foo.*");
  }

  @Test
  public void testGetStatusReportFaultyFacetAppAnnotation() {
    AnnotatedAppStateFacet facet = createMock(AnnotatedAppStateFacet.class);
    Throwable exception = new RuntimeException("foo");
    expect(facet.getAppState()).andReturn(AppState.READY).anyTimes();
    expect(facet.getAppStateAnnotation()).andThrow(exception).anyTimes();

    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);

    assertThat(stateManager.registerAppStateFacet("bar", facet)).isTrue();
    String report = stateManager.getStatusReport();

    verifyAll();

    assertThat(report).isNotNull();
    assertThat(report).startsWith("READY\n");
    assertThat(report).contains("Application state: READY");
    assertThat(report).matches("(?s).*READY *main.*");
    assertThat(report).matches("(?s).*READY *bar .*foo.*");
  }

  @Test
  public void testReadStatePathsInitializing() {
    replayAll();

    AppStateManager stateManager = createAppStateManager(false);
    stateManager.setMainAppState(AppState.READY);

    assertThat(stateManager.getAppState()).isSameAs(AppState.INITIALIZING);

    stateManager.readStatePaths();

    assertThat(stateManager.getAppState()).isSameAs(AppState.READY);

    verifyAll();
  }

  @Test
  public void testReadStatePathsDraining() throws IOException {
    replayAll();

    AppStateManager stateManager = createAppStateManager(false);
    stateManager.setMainAppState(AppState.READY);

    Files.createFile(drainPath);
    stateManager.readStatePaths();

    assertThat(stateManager.getAppState()).isSameAs(AppState.FAULTY);

    verifyAll();
  }

  @Test
  public void testReadStatePathsOverride() {
    replayAll();

    AppStateManager stateManager = createAppStateManager(false);
    stateManager.setMainAppState(AppState.READY);

    writeStateOverrideFile("WARNING");

    stateManager.readStatePaths();

    assertThat(stateManager.getAppState()).isSameAs(AppState.WARNING);

    verifyAll();
  }

  @Test
  public void testReadDrainStateNonExisting() {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);

    assertThat(stateManager.getAppState()).isSameAs(AppState.READY);

    stateManager.readDrainState();

    assertThat(stateManager.getAppState()).isSameAs(AppState.READY);

    verifyAll();
  }

  @Test
  public void testReadDrainStateExisting() throws IOException {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);

    assertThat(stateManager.getAppState()).isSameAs(AppState.READY);

    Files.createFile(drainPath);

    stateManager.readDrainState();

    assertThat(stateManager.getAppState()).isSameAs(AppState.FAULTY);

    verifyAll();
  }

  @Test
  public void testReadOverrideStateWarning() throws IOException {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);

    assertThat(stateManager.getAppState()).isSameAs(AppState.READY);

    writeStateOverrideFile("WARNING");

    stateManager.readOverrideState();

    assertThat(stateManager.getAppState()).isSameAs(AppState.WARNING);

    verifyAll();
  }

  @Test
  public void testReadOverrideStateReady() throws IOException {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.WARNING);

    assertThat(stateManager.getAppState()).isSameAs(AppState.WARNING);

    writeStateOverrideFile("READY");

    stateManager.readOverrideState();

    assertThat(stateManager.getAppState()).isSameAs(AppState.READY);

    verifyAll();
  }

  @Test
  public void testReadOverrideStateReadyWhitespacePadded() throws IOException {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.FAULTY);

    assertThat(stateManager.getAppState()).isSameAs(AppState.FAULTY);

    writeStateOverrideFile("  READY      ");

    stateManager.readOverrideState();

    assertThat(stateManager.getAppState()).isSameAs(AppState.READY);

    verifyAll();
  }

  @Test
  public void testReadOverrideStateFoo() throws IOException {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.READY);

    assertThat(stateManager.getAppState()).isSameAs(AppState.READY);

    writeStateOverrideFile("foo");

    stateManager.readOverrideState();

    assertThat(stateManager.getAppState()).isSameAs(AppState.FAULTY);

    verifyAll();
  }

  @Test
  public void testReadOverrideStateEmpty() throws IOException {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.WARNING);

    assertThat(stateManager.getAppState()).isSameAs(AppState.WARNING);

    Files.createFile(overridePath);

    stateManager.readOverrideState();

    assertThat(stateManager.getAppState()).isSameAs(AppState.WARNING);

    verifyAll();
  }

  @Test
  public void testReadOverrideStateNonExistent() throws IOException {
    replayAll();

    AppStateManager stateManager = createAppStateManager();
    stateManager.setMainAppState(AppState.WARNING);

    assertThat(stateManager.getAppState()).isSameAs(AppState.WARNING);

    stateManager.readOverrideState();

    assertThat(stateManager.getAppState()).isSameAs(AppState.WARNING);

    verifyAll();
  }

  private AppStateManager createAppStateManager() {
    return createAppStateManager(true);
  }

  private AppStateManager createAppStateManager(boolean readStatePaths) {
    Clock clock = InjectorFactory.getInjector().getInstance(Clock.class);
    AppStateManager stateManager = new AppStateManager(paths, timeUtils, clock);
    if (readStatePaths) {
      stateManager.readStatePaths();
    }
    return stateManager;

  }

  private void writeStateOverrideFile(String stateStr) {
    try {
      Files.write(overridePath, stateStr.getBytes());
    } catch (Exception e) {
      fail("Failed to write override state to " + overridePath, e);
    }
  }

  private void assertWrittenStateContentsEquals(String expected) {
    assertThat(statePath).exists();
    List<String> actual = null;
    try {
      actual = Files.readAllLines(statePath, Charset.defaultCharset());
    } catch (IOException e) {
      fail("Failed to read contents of " + statePath, e);
    }
    assertThat(actual.size()).describedAs("Number of lines in " + statePath)
        .isGreaterThanOrEqualTo(1);
    assertThat(actual.get(0)).describedAs("First line of " + statePath).isEqualTo(expected);
  }

  private void assertUsable() {
    assertThat(usablePath).exists();
  }

  private void assertUnusable() {
    assertThat(usablePath).doesNotExist();
  }
}
