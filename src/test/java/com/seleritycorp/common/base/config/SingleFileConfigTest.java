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

import java.nio.file.Path;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.seleritycorp.common.base.test.FileTestCase;
import java.io.IOException;

public class SingleFileConfigTest extends FileTestCase {
  @Test
  public void testPropertiesFile() throws IOException {
    Path path = createTempFile();
    writeFile(path, "foo=bar");

    Config config = new SingleFileConfig(path, Config.newEmptyConfig());
    assertThat(config).isNotNull();
    assertThat(config.get("foo")).isEqualTo("bar");
    assertThat(config.get("paths.conf")).isEqualTo(path.getParent().toString());
  }

  @Test
  public void testJSONFile() throws IOException {
    Path path = createTempFile();
    writeFile(path, "{\"foo\":\"bar\"}");

    Config config = new SingleFileConfig(path, Config.newEmptyConfig());
    assertThat(config).isNotNull();
    assertThat(config.get("foo")).isEqualTo("bar");
    assertThat(config.get("paths.conf")).isEqualTo(path.getParent().toString());
  }

  @Test
  public void testApplicationDefaultsAndOverrides() throws IOException {
    Path path = createTempFile();
    writeFile(path, "foo=bar");

    Config appDefaults = new ConfigImpl() {
      {
        set("foo", "default");
        set("pippo", "pluto");
      }
    };

    Config config = new SingleFileConfig(path, appDefaults);
    assertThat(config).isNotNull();
    assertThat(config.get("foo")).isNotEqualTo("default");
    assertThat(config.get("foo")).isEqualTo("bar");
    assertThat(config.get("pippo")).isEqualTo("pluto");
  }
}
