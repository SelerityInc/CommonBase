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

public class ConfigImplTest {
  @Test
  public void testGetNotExisting() {
    ConfigImpl config = new ConfigImpl();

    String actual = config.get("foo");

    assertThat(actual).isNull();
  }

  @Test
  public void testSet() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "bar");

    String actual = config.get("foo");

    assertThat(actual).isEqualTo("bar");
  }

  @Test
  public void testSetOverwrite() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "bar");
    config.set("foo", "baz");

    String actual = config.get("foo");

    assertThat(actual).isEqualTo("baz");
  }

  @Test
  public void testSetNull() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "bar");
    assertThat(config.get("foo")).isEqualTo("bar");

    config.set("foo", null);
    assertThat(config.get("foo")).isEqualTo(null);

    config.set("foo", "baz");
    assertThat(config.get("foo")).isEqualTo("baz");
  }

  @Test
  public void testGetDefaultValue() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "bar");

    String actual = config.get("foo", "baz");

    assertThat(actual).isEqualTo("bar");
  }

  @Test
  public void testGetDefaultDefault() {
    ConfigImpl config = new ConfigImpl();

    String actual = config.get("foo", "baz");

    assertThat(actual).isEqualTo("baz");
  }

  @Test
  public void testGetAsIntOk() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42");

    int actual = config.getInt("foo");

    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetAsIntFloat() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.6");

    int actual = config.getInt("foo");

    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetAsIntFloatPostfixed() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.6f");

    int actual = config.getInt("foo");

    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetAsIntDoublePostfixed() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.6d");

    int actual = config.getInt("foo");

    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetAsIntMissing() {
    ConfigImpl config = new ConfigImpl();

    int actual = config.getInt("foo");

    assertThat(actual).isEqualTo(0);
  }

  @Test
  public void testGetAsIntMisformated() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "bar");

    int actual = config.getInt("foo");

    assertThat(actual).isEqualTo(0);
  }

  @Test
  public void testGetAsIntDefaultOk() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42");

    int actual = config.getInt("foo", 1);

    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetAsIntDefaultFloat() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.6");

    int actual = config.getInt("foo", 1);

    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetAsIntDefaultFloatPostfixed() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.6f");

    int actual = config.getInt("foo", 1);

    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetAsIntDefaultDoublePostfixed() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.6d");

    int actual = config.getInt("foo", 1);

    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetAsIntDefaultMissing() {
    ConfigImpl config = new ConfigImpl();

    int actual = config.getInt("foo", 1);

    assertThat(actual).isEqualTo(1);
  }

  @Test
  public void testGetAsIntDefaultMisformated() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "bar");

    int actual = config.getInt("foo", 1);

    assertThat(actual).isEqualTo(1);
  }

  @Test
  public void testGetAsLongOk() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42");

    long actual = config.getLong("foo");

    assertThat(actual).isEqualTo(42L);
  }

  @Test
  public void testGetAsLongFloat() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.6");

    long actual = config.getLong("foo");

    assertThat(actual).isEqualTo(42L);
  }

  @Test
  public void testGetAsLongFloatPostfixed() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.6f");

    long actual = config.getLong("foo");

    assertThat(actual).isEqualTo(42L);
  }

  @Test
  public void testGetAsLongDoublePostfixed() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.6d");

    long actual = config.getLong("foo");

    assertThat(actual).isEqualTo(42L);
  }

  @Test
  public void testGetAsLongMissing() {
    ConfigImpl config = new ConfigImpl();

    long actual = config.getLong("foo");

    assertThat(actual).isEqualTo(0L);
  }

  @Test
  public void testGetAsLongMisformated() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "bar");

    long actual = config.getLong("foo");

    assertThat(actual).isEqualTo(0L);
  }

  @Test
  public void testGetAsLongDefaultOk() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42");

    long actual = config.getLong("foo", 1L);

    assertThat(actual).isEqualTo(42L);
  }

  @Test
  public void testGetAsLongDefaultFloat() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.6");

    long actual = config.getLong("foo", 1L);

    assertThat(actual).isEqualTo(42L);
  }

  @Test
  public void testGetAsLongDefaultFloatPostfixed() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.6f");

    long actual = config.getLong("foo", 1L);

    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetAsLongDefaultDoublePostfixed() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.6d");

    long actual = config.getLong("foo", 1L);

    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetAsLongDefaultMissing() {
    ConfigImpl config = new ConfigImpl();

    long actual = config.getLong("foo", 1L);

    assertThat(actual).isEqualTo(1L);
  }

  @Test
  public void testGetAsLongDefaultMisformated() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "bar");

    long actual = config.getLong("foo", 1L);

    assertThat(actual).isEqualTo(1L);
  }

  @Test
  public void testGetAsFloatOk() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42");

    float actual = config.getFloat("foo");

    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetAsFloatPostfixed() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.3f");

    float actual = config.getFloat("foo");

    assertThat(actual).isEqualTo(42.3f);
  }

  @Test
  public void testGetAsFloatDoublePostfixed() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.3d");

    float actual = config.getFloat("foo");

    assertThat(actual).isEqualTo(42.3f);
  }

  @Test
  public void testGetAsFloatFloat() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.6");

    float actual = config.getFloat("foo");

    assertThat(actual).isEqualTo(42.6f);
  }

  @Test
  public void testGetAsFloatMissing() {
    ConfigImpl config = new ConfigImpl();

    float actual = config.getFloat("foo");

    assertThat(actual).isEqualTo(Float.NaN);
  }

  @Test
  public void testGetAsFloatMisformated() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "bar");

    float actual = config.getFloat("foo");

    assertThat(actual).isEqualTo(Float.NaN);
  }

  @Test
  public void testGetAsFloatDefaultOk() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42");

    float actual = config.getFloat("foo", 1);

    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetAsFloatDefaultPostfixed() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.3f");

    float actual = config.getFloat("foo", 1);

    assertThat(actual).isEqualTo(42.3f);
  }

  @Test
  public void testGetAsFloatDefaultDoublePostfixed() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.3d");

    float actual = config.getFloat("foo", 1);

    assertThat(actual).isEqualTo(42.3f);
  }

  @Test
  public void testGetAsFloatDefaultMissing() {
    ConfigImpl config = new ConfigImpl();

    float actual = config.getFloat("foo", 1);

    assertThat(actual).isEqualTo(1);
  }

  @Test
  public void testGetAsFloatDefaultMisformated() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "bar");

    float actual = config.getFloat("foo", 1);

    assertThat(actual).isEqualTo(1);
  }

  @Test
  public void testGetAsDoubleOk() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42");

    double actual = config.getDouble("foo");

    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetAsDoubleFloatPostfixed() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.5f");

    double actual = config.getDouble("foo");

    assertThat(actual).isEqualTo(42.5f);
  }

  @Test
  public void testGetAsDoubleDoublePostfixed() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.3d");

    double actual = config.getDouble("foo");

    assertThat(actual).isEqualTo(42.3d);
  }

  @Test
  public void testGetAsDoubleFloat() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.125f");

    double actual = config.getDouble("foo");

    assertThat(actual).isEqualTo(42.125d);
  }

  @Test
  public void testGetAsDoubleMissing() {
    ConfigImpl config = new ConfigImpl();

    double actual = config.getDouble("foo");

    assertThat(actual).isEqualTo(Double.NaN);
  }

  @Test
  public void testGetAsDoubleMisformated() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "bar");

    double actual = config.getDouble("foo");

    assertThat(actual).isEqualTo(Double.NaN);
  }

  @Test
  public void testGetAsDoubleDefaultOk() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42");

    double actual = config.getDouble("foo", 1);

    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetAsDoubleDefaultFloatPostfixed() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.25f");

    double actual = config.getDouble("foo", 1);

    assertThat(actual).isEqualTo(42.25f);
  }

  @Test
  public void testGetAsDoubleDefaultDoublePostfixed() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "42.3d");

    double actual = config.getDouble("foo", 1);

    assertThat(actual).isEqualTo(42.3d);
  }

  @Test
  public void testGetAsDoubleDefaultMissing() {
    ConfigImpl config = new ConfigImpl();

    double actual = config.getDouble("foo", 1);

    assertThat(actual).isEqualTo(1);
  }

  @Test
  public void testGetAsDoubleDefaultMisformated() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "bar");

    double actual = config.getDouble("foo", 1);

    assertThat(actual).isEqualTo(1);
  }

  @Test
  public void testGetAsBooleanOk() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "true");

    boolean actual = config.getBoolean("foo");

    assertThat(actual).isTrue();
  }

  @Test
  public void testGetAsBooleanCaseAndPadding() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", " tRuE  ");

    boolean actual = config.getBoolean("foo");

    assertThat(actual).isTrue();
  }

  @Test
  public void testGetAsBooleanFalse() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "false");

    boolean actual = config.getBoolean("foo");

    assertThat(actual).isFalse();
  }

  @Test
  public void testGetAsBooleanNumber0() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "0");

    boolean actual = config.getBoolean("foo");

    assertThat(actual).isFalse();
  }

  @Test
  public void testGetAsBooleanNumber1() {
    ConfigImpl config = new ConfigImpl();

    config.set("foo", "1");

    boolean actual = config.getBoolean("foo");

    assertThat(actual).isTrue();
  }

  @Test
  public void testGetAsBooleanMissing() {
    ConfigImpl config = new ConfigImpl();

    boolean actual = config.getBoolean("foo");

    assertThat(actual).isFalse();
  }

  @Test
  public void testGetAsIntWParentOkInstance() {
    ConfigImpl config = new ConfigImpl();
    config.set("foo", "bar");

    ConfigImpl parent = new ConfigImpl();
    parent.set("foo", "baz");
    config.setParent(parent);

    String actual = config.get("foo");

    assertThat(actual).isEqualTo("bar");
  }

  @Test
  public void testGetAsIntWParentOkParent() {
    ConfigImpl config = new ConfigImpl();

    ConfigImpl parent = new ConfigImpl();
    parent.set("foo", "baz");
    config.setParent(parent);

    String actual = config.get("foo");

    assertThat(actual).isEqualTo("baz");
  }

  @Test
  public void testGetAsIntWParentMissing() {
    ConfigImpl config = new ConfigImpl();

    ConfigImpl parent = new ConfigImpl();
    config.setParent(parent);

    String actual = config.get("foo");

    assertThat(actual).isNull();
  }

  @Test
  public void testGetAsIntWParentDefaultOkInstance() {
    ConfigImpl config = new ConfigImpl();
    config.set("foo", "bar");

    ConfigImpl parent = new ConfigImpl();
    parent.set("foo", "baz");
    config.setParent(parent);

    String actual = config.get("foo", "quux");

    assertThat(actual).isEqualTo("bar");
  }

  @Test
  public void testGetAsIntWParentDefaultOkParent() {
    ConfigImpl config = new ConfigImpl();

    ConfigImpl parent = new ConfigImpl();
    parent.set("foo", "baz");
    config.setParent(parent);

    String actual = config.get("foo", "quux");

    assertThat(actual).isEqualTo("baz");
  }

  @Test
  public void testGetAsIntWParentDefaultMissing() {
    ConfigImpl config = new ConfigImpl();

    ConfigImpl parent = new ConfigImpl();
    config.setParent(parent);

    String actual = config.get("foo", "quux");

    assertThat(actual).isEqualTo("quux");
  }
}
