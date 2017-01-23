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

package com.seleritycorp.common.base.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import com.seleritycorp.common.base.test.FileTestCase;

public class ConfigUtilsTest extends FileTestCase {
  @Test
  public void testLoadNonExistingFile() throws IOException {
    Path path = createTempFile();
    Files.delete(path);

    Config config = ConfigUtils.load(path);
    assertThat(config).isNotNull();
    assertThat(config.get("foo")).isNull();
  }

  @Test
  public void testLoadEmptyFile() throws IOException {
    Path path = createTempFile();

    Config config = ConfigUtils.load(path);
    assertThat(config).isNotNull();
    assertThat(config.get("foo")).isNull();
  }

  @Test
  public void testLoadPropertySingle() throws IOException {
    Path path = createTempFile();
    writeFile(path, "foo=bar");

    Config config = ConfigUtils.load(path);
    assertThat(config).isNotNull();
    assertThat(config.get("foo")).isEqualTo("bar");
  }

  @Test
  public void testLoadPropertyMultiple() throws IOException {
    Path path = createTempFile();
    writeFile(path, "foo=bar\nbaz=quux");

    Config config = ConfigUtils.load(path);
    assertThat(config).isNotNull();
    assertThat(config.get("foo")).isEqualTo("bar");
    assertThat(config.get("baz")).isEqualTo("quux");
  }

  @Test
  public void testLoadPropertyMultiplePaddedWComments() throws IOException {
    Path path = createTempFile();
    writeFile(path, " \n  foo = bar\n baz= quux\n#bar=42");

    Config config = ConfigUtils.load(path);
    assertThat(config).isNotNull();
    assertThat(config.get("foo")).isEqualTo("bar");
    assertThat(config.get("baz")).isEqualTo("quux");
    assertThat(config.get("bar")).isNull();
  }

  @Test
  public void testLoadJsonEmpty() throws IOException {
    Path path = createTempFile();
    writeFile(path, "{}");

    Config config = ConfigUtils.load(path);
    assertThat(config).isNotNull();
  }

  @Test
  public void testLoadJsonSingle() throws IOException {
    Path path = createTempFile();
    writeFile(path, "{\"foo\":\"bar\"}");

    Config config = ConfigUtils.load(path);
    assertThat(config).isNotNull();
    assertThat(config.get("foo")).isEqualTo("bar");
  }

  @Test
  public void testLoadJsonArrayRoot() throws IOException {
    Path path = createTempFile();
    writeFile(path, "[{\"foo\":\"bar\"},{\"foo\":\"baz\"}]");

    Config config = ConfigUtils.load(path);
    assertThat(config).isNotNull();
    assertThat(config.get("0.foo")).isEqualTo("bar");
    assertThat(config.get("1.foo")).isEqualTo("baz");
  }

  @Test
  public void testLoadJsonObjectRoot() throws IOException {
    Path path = createTempFile();
    writeFile(path, "{\"foo\":1, \"bar\":[{\"foo\":\"baz\"}, 2.17, \"quux\"]}");

    Config config = ConfigUtils.load(path);
    assertThat(config).isNotNull();
    assertThat(config.get("foo")).isEqualTo("1.0");
    assertThat(config.get("bar.0.foo")).isEqualTo("baz");
    assertThat(config.get("bar.1")).isEqualTo("2.17");
    assertThat(config.get("bar.2")).isEqualTo("quux");
  }

  @Test
  public void testSubconfigSimple() {
    ConfigImpl config = new ConfigImpl();
    config.set("foo", "fooValue");
    config.set("foo.bar", "fooBarValue");
    config.set("foo.bar.baz", "fooBarBazValue");

    Config subconfig = ConfigUtils.subconfig(config, "foo");

    assertThat(subconfig.get("foo")).isNull();
    assertThat(subconfig.get("foo.bar")).isNull();
    assertThat(subconfig.get("foo.bar.baz")).isNull();
    assertThat(subconfig.get("bar")).isEqualTo("fooBarValue");
    assertThat(subconfig.get("baz")).isNull();
    assertThat(subconfig.get("bar.baz")).isEqualTo("fooBarBazValue");
  }

  @Test
  public void testSubconfigEmpty() {
    ConfigImpl config = new ConfigImpl();
    config.set("bar", "barValue");

    Config subconfig = ConfigUtils.subconfig(config, "foo");

    assertThat(subconfig.get("bar")).isNull();
    assertThat(subconfig.get(".")).isNull();
  }

  @Test
  public void testSubconfigDirectNode() {
    ConfigImpl config = new ConfigImpl();
    config.set("foo", "fooValue");

    Config subconfig = ConfigUtils.subconfig(config, "foo");

    assertThat(subconfig.get("foo")).isNull();
    assertThat(subconfig.get(".")).isNull();
  }

  @Test
  public void testSubconfigDotlessSibling() {
    ConfigImpl config = new ConfigImpl();
    config.set("foo.bar", "fooBarValue");
    config.set("foobaz", "fooBarBazValue");

    Config subconfig = ConfigUtils.subconfig(config, "foo");

    assertThat(subconfig.get("bar")).isEqualTo("fooBarValue");
    assertThat(subconfig.get("baz")).isNull();
  }

  @Test
  public void testSubsubconfigSimple() {
    ConfigImpl config = new ConfigImpl();
    config.set("foo", "fooValue");
    config.set("foo.bar", "fooBarValue");
    config.set("foo.bar.baz", "fooBarBazValue");

    Config subconfig = ConfigUtils.subconfig(config, "foo");

    assertThat(subconfig.get("foo")).isNull();
    assertThat(subconfig.get("foo.bar")).isNull();
    assertThat(subconfig.get("foo.bar.baz")).isNull();
    assertThat(subconfig.get("bar")).isEqualTo("fooBarValue");
    assertThat(subconfig.get("baz")).isNull();
    assertThat(subconfig.get("bar.baz")).isEqualTo("fooBarBazValue");
  }
}
