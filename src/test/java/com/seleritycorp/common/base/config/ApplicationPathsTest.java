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

package com.seleritycorp.common.base.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.test.FileTestCase;

public class ApplicationPathsTest extends FileTestCase {
  private Path base;

  @Before
  public void setUp() throws IOException {
    base = createTempDirectory();
  }

  @Test
  public void testGetBasePath() throws IOException {
    ApplicationPaths applicationPaths = createApplicationPaths();

    Path actual = applicationPaths.getBasePath();
    assertThat(actual).isEqualTo(base);
  }

  @Test
  public void testGetConfPath() throws IOException {
    ApplicationPaths applicationPaths = createApplicationPaths();

    Path actual = applicationPaths.getConfPath();
    assertThat(actual).isEqualTo(base.resolve("pathConf"));
    assertThat(actual).isDirectory();
  }

  @Test
  public void testGetConfAnsiblizedPath() throws IOException {
    ApplicationPaths applicationPaths = createApplicationPaths();

    Path actual = applicationPaths.getConfAnsiblizedPath();
    assertThat(actual).isEqualTo(base.resolve("pathConfAnsiblized"));
    assertThat(actual).isDirectory();
  }

  @Test
  public void testGetDataPath() throws IOException {
    ApplicationPaths applicationPaths = createApplicationPaths();

    Path actual = applicationPaths.getDataPath();
    assertThat(actual).isEqualTo(base.resolve("pathData"));
    assertThat(actual).isDirectory();
  }

  @Test
  public void testGetDataStatePath() throws IOException {
    ApplicationPaths applicationPaths = createApplicationPaths();

    Path actual = applicationPaths.getDataStatePath();
    assertThat(actual).isEqualTo(base.resolve("pathData").resolve("pathState"));
    assertThat(actual).isDirectory();
  }

  @Test
  public void testConstructorWithExistingDataDirectory() throws IOException {
    Files.createDirectories(base.resolve("pathData"));
    ApplicationPaths applicationPaths = createApplicationPaths();

    Path actual = applicationPaths.getDataStatePath();
    assertThat(actual).isEqualTo(base.resolve("pathData").resolve("pathState"));
    assertThat(actual).isDirectory();
  }

  @Test
  public void testConstructorWithDataSymlink() throws IOException {
    Path source = base.resolve("pathData");
    Path target = base.resolve("pathDataTarget");
    Files.createDirectories(target);
    Files.createSymbolicLink(base.resolve("pathData"), target);

    ApplicationPaths applicationPaths = createApplicationPaths();

    assertThat(source).isDirectory();
    assertThat(target.resolve("pathState")).isDirectory();

    Path actual = applicationPaths.getDataStatePath();
    assertThat(actual).isEqualTo(base.resolve("pathData").resolve("pathState"));
    assertThat(actual).isDirectory();
  }

  private ApplicationPaths createApplicationPaths() throws IOException {
    ConfigImpl config = new ConfigImpl();
    config.set("paths.conf", "pathConf");
    config.set("paths.confAnsiblized", "pathConfAnsiblized");
    config.set("paths.data", "pathData");
    config.set("paths.dataState", "pathState");

    return new ApplicationPaths(base, config);
  }
}
