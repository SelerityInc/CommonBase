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

package com.seleritycorp.common.base.logging;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.log4j.rolling.RolloverDescription;
import org.apache.log4j.rolling.helper.Action;
import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.logging.RoverLikeRollingPolicy;
import com.seleritycorp.common.base.test.InjectingTestCase;

public class RoverLikeRollingPolicyTest extends InjectingTestCase {
  private RoverLikeRollingPolicy policy;
  private Path tempDir;

  @Before
  public void setUp() throws IOException {
    tempDir = createTempDirectory();
  }

  @Test
  public void testInitializeNullNoAppendNonExisting() {
    policy = createRoverLikeRollingPolicy();
    RolloverDescription desc = policy.initialize(null, false);

    assertThat(desc.getActiveFileName()).isEqualTo("application.log");
    assertThat(desc.getAppend()).isFalse();
    assertThat(desc.getSynchronous()).isNull();
    assertThat(desc.getAsynchronous()).isNull();
  }

  @Test
  public void testInitializeNullAppendNonExisting() {
    policy = createRoverLikeRollingPolicy();
    RolloverDescription desc = policy.initialize(null, true);

    assertThat(desc.getActiveFileName()).isEqualTo("application.log");
    assertThat(desc.getAppend()).isTrue();
    assertThat(desc.getSynchronous()).isNull();
    assertThat(desc.getAsynchronous()).isNull();
  }

  @Test
  public void testInititializeProperNoAppendNonExisting() {
    policy = createRoverLikeRollingPolicy();
    Path path = tempDir.resolve(tempDir).resolve("foo");
    RolloverDescription desc = policy.initialize(path.toString(), false);

    assertThat(desc.getActiveFileName()).isEqualTo(path.toString());
    assertThat(desc.getAppend()).isFalse();
    assertThat(desc.getSynchronous()).isNull();
    assertThat(desc.getAsynchronous()).isNull();
  }

  @Test
  public void testInitializeProperAppendNonExisting() {
    policy = createRoverLikeRollingPolicy();
    Path path = tempDir.resolve(tempDir).resolve("foo");
    RolloverDescription desc = policy.initialize(path.toString(), true);

    assertThat(desc.getActiveFileName()).isEqualTo(path.toString());
    assertThat(desc.getAppend()).isTrue();
    assertThat(desc.getSynchronous()).isNull();
    assertThat(desc.getAsynchronous()).isNull();
  }

  @Test
  public void testInititializeProperNoAppendExistingEmpty() throws IOException {
    policy = createRoverLikeRollingPolicy();
    Path path = tempDir.resolve(tempDir).resolve("foo");
    writeFile(path, "");

    RolloverDescription desc = policy.initialize(path.toString(), false);

    assertThat(desc.getActiveFileName()).isEqualTo(path.toString());
    assertThat(desc.getAppend()).isFalse();
    assertThat(desc.getSynchronous()).isNull();
    assertThat(desc.getAsynchronous()).isNull();

    assertThat(path).hasContent("");
    assertThat(tempDir.resolve("rotated.2004-11-09T11:33:20.log")).doesNotExist();
  }

  @Test
  public void testInititializeProperNoAppendExistingNonEmpty() throws IOException {
    policy = createRoverLikeRollingPolicy();
    Path path = tempDir.resolve(tempDir).resolve("foo");
    writeFile(path, "bar");

    RolloverDescription desc = policy.initialize(path.toString(), false);

    assertThat(desc.getActiveFileName()).isEqualTo(path.toString());
    assertThat(desc.getAppend()).isFalse();
    Action action = desc.getSynchronous();
    assertThat(action).isNotNull();
    assertThat(desc.getAsynchronous()).isNull();

    assertThat(path).exists();

    assertThat(action.execute()).isTrue();

    assertThat(path).doesNotExist();

    Path rotatedPath = tempDir.resolve("rotated.2004-11-09T11:33:20.log");
    assertThat(rotatedPath).hasContent("bar");
  }

  @Test
  public void testInititializeProperNoAppendExistingClobber() throws IOException {
    policy = createRoverLikeRollingPolicy();

    Path path = tempDir.resolve(tempDir).resolve("rotated.2016-02-03T12:34:56.log");
    writeFile(path, "bar");

    path = tempDir.resolve(tempDir).resolve("foo");
    writeFile(path, "baz");

    writeFile(tempDir.resolve("rotated.2004-11-09T11:33:20.log"), "prefilled");

    RolloverDescription desc = policy.initialize(path.toString(), false);

    assertThat(desc.getActiveFileName()).isEqualTo(path.toString());
    assertThat(desc.getAppend()).isFalse();
    Action action = desc.getSynchronous();
    assertThat(action).isNotNull();
    assertThat(desc.getAsynchronous()).isNull();

    assertThat(path).exists();

    assertThat(action.execute()).isTrue();

    assertThat(path).doesNotExist();

    assertThat(tempDir.resolve("rotated.2004-11-09T11:33:20.log")).hasContent("prefilled");
    assertThat(tempDir.resolve("rotated.2004-11-09T11:33:20-2.log")).hasContent("baz");
  }

  private RoverLikeRollingPolicy createRoverLikeRollingPolicy() {
    policy = new RoverLikeRollingPolicy();
    policy.setRotateFormat(tempDir.resolve("rotated.%1$tFT%1$tT%2$s.log").toString());
    policy.activateOptions();
    return policy;
  }
}
