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

package com.seleritycorp.common.base.meta;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class BuildFormatterTest {
  @Test
  public void testGetArtifactId() {
    BuildProperties props = createBuildProperties();

    assertThat(props.getArtifactId()).isEqualTo("CommonBase");
  }

  @Test
  public void testGetBuildTimeId() {
    BuildProperties props = createBuildProperties();

    assertThat(props.getBuildTime()).startsWith("20");
  }

  @Test
  public void testGetGitBranchId() {
    BuildProperties props = createBuildProperties();

    assertThat(props.getGitBranch()).isNotEmpty();
  }

  @Test
  public void testGetGitDescription() {
    BuildProperties props = createBuildProperties();

    assertThat(props.getGitDescription()).matches("(.*-g)?[0-9a-f]{7}(-dirty)?");
  }

  @Test
  public void testGetGitHashAbbreviated() {
    BuildProperties props = createBuildProperties();

    assertThat(props.getGitHashAbbreviated()).matches("[0-9a-f]{7}");
  }

  @Test
  public void testGetGroupId() {
    BuildProperties props = createBuildProperties();

    assertThat(props.getGroupId()).isEqualTo("com.seleritycorp.common.base");
  }

  @Test
  public void testGetName() {
    BuildProperties props = createBuildProperties();

    assertThat(props.getName()).startsWith("Common");
  }

  @Test
  public void testGetVersion() {
    BuildProperties props = createBuildProperties();

    assertThat(props.getVersion()).matches("[0-9]+\\.[0-9]+\\.[0-9]+.*");
  }

  private BuildProperties createBuildProperties() {
    return new BuildProperties("com.seleritycorp.common.base", "CommonBase");
  }
}
