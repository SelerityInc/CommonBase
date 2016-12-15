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

package com.seleritycorp.common.base.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class EnvironmentConfigBaseTest {
  @Test
  public void testGetExisting() {
    EnvironmentConfigBase config = createEnvironmentConfigBase();

    String actual = config.get("PATH"); // PATH is available on Linux, Windows, and Macs

    assertThat(actual).isNotEmpty();
  }

  @Test
  public void testGetNonExisting() {
    EnvironmentConfigBase config = createEnvironmentConfigBase();

    String actual = config.get("LUDICROUS_AND_NON_EXISTING_NAME");

    assertThat(actual).isNull();
  }

  private EnvironmentConfigBase createEnvironmentConfigBase() {
    return new EnvironmentConfigBase();
  }
}
