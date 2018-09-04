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

import com.seleritycorp.common.base.inject.InjectorFactory;
import com.seleritycorp.common.base.test.FileTestCase;

import com.google.inject.Inject;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;


public class ConfigModuleIntegrationTest  extends FileTestCase {

  @Before
  public void setUp() {
    InjectorFactory.forceInjector(null);
  }

  @Test
  public void testProvideApplicationConfigUsingUserProvidedConfigFile() throws IOException {
    Path confFile = createTempFile();
    writeFile(confFile, "foo=bar");

    InjectorFactory.register(new CustomConfigModule(confFile));

    ApplicationConfigHolder ach = InjectorFactory.getInjector().getInstance(ApplicationConfigHolder.class);

    Config config = ach.value;
    assertThat(config).isNotNull();
    assertThat(config.get("foo")).isEqualTo("bar");
    assertThat(config.get("paths.conf")).isEqualTo(confFile.getParent().toString());
  }

  @Test
  public void testProvideApplicationConfigFromEnforcedDir() throws IOException {
    ApplicationConfigHolder ach = InjectorFactory.getInjector().getInstance(ApplicationConfigHolder.class);

    Config config = ach.value;
    assertThat(config).isNotNull();
    assertThat(config.get("foo")).isEqualTo("bar");
    assertThat(config.get("paths.conf")).isEqualTo(Paths.get("conf").toString());
  }


  static class ApplicationConfigHolder {
    final Config value;

    @Inject
    public ApplicationConfigHolder(@ApplicationConfig Config value) {
      this.value = value;
    }
  }
}
