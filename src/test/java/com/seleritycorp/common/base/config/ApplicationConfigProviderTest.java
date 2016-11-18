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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import com.seleritycorp.common.base.test.FileTestCase;

public class ApplicationConfigProviderTest extends FileTestCase {
  @Test
  public void testGetConfigLayering() throws IOException {
    Path dir = createTempDirectory();

    Path confPath = Files.createDirectory(dir.resolve("conf"));
    Path confAnsiblizedPath = Files.createDirectory(dir.resolve("conf-ansiblized"));

    String jsonName = "application.json";
    String propertiesName = "application.properties";

    Path confJson = confPath.resolve(jsonName);
    writeFile(confJson,
        "{\"all\": \"confJson\"," + "\"json\": \"confJson\"," + "\"confJson\": \"confJson\"" + "}");

    Path confProperties = confPath.resolve(propertiesName);
    writeFile(confProperties, "all = confProperties\n" + "properties = confProperties\n"
        + "confProperties = confProperties");

    Path confAnsiblizedJson = confAnsiblizedPath.resolve(jsonName);
    writeFile(confAnsiblizedJson, "{\"all\": \"ansiblizedJson\"," + "\"json\": \"ansiblizedJson\","
        + "\"confAnsiblizedJson\": \"confAnsiblizedJson\"" + "}");

    Path confAnsiblizedProperties = confAnsiblizedPath.resolve(propertiesName);
    writeFile(confAnsiblizedProperties,
        "all = ansiblizedProperties\n" + "properties = ansiblizedProperties\n"
            + "confAnsiblizedProperties = confAnsiblizedProperties");

    ApplicationConfigProvider provider = new ApplicationConfigProvider(dir);

    Config config = provider.get();

    // Test key that is used by all 4 configs
    assertThat(config.get("all")).isEqualTo("confProperties");

    // Test layering for different formats
    assertThat(config.get("json")).isEqualTo("confJson");
    assertThat(config.get("properties")).isEqualTo("confProperties");

    // Make sure all 4 configs get loaded.
    assertThat(config.get("confJson")).isEqualTo("confJson");
    assertThat(config.get("confAnsiblizedJson")).isEqualTo("confAnsiblizedJson");
    assertThat(config.get("confProperties")).isEqualTo("confProperties");
    assertThat(config.get("confAnsiblizedProperties")).isEqualTo("confAnsiblizedProperties");
  }
}
