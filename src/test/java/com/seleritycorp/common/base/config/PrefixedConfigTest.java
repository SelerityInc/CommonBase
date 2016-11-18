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
import static org.easymock.EasyMock.expect;

import org.easymock.EasyMockSupport;
import org.junit.Test;

public class PrefixedConfigTest extends EasyMockSupport {
  @Test
  public void testGet() {
    Config config = createMock(Config.class);
    Config subconfig = new PrefixedConfig(config, "foo");

    expect(config.get("foo.bar")).andReturn("baz").once();

    replayAll();

    String actual = subconfig.get("bar");

    verifyAll();

    assertThat(actual).isEqualTo("baz");
  }

  @Test
  public void testGetDefault() {
    Config config = createMock(Config.class);
    Config subconfig = new PrefixedConfig(config, "foo");

    expect(config.get("foo.bar", "default")).andReturn("baz").once();

    replayAll();

    String actual = subconfig.get("bar", "default");

    verifyAll();

    assertThat(actual).isEqualTo("baz");
  }

  @Test
  public void testGetInt() {
    Config config = createMock(Config.class);
    Config subconfig = new PrefixedConfig(config, "foo");

    expect(config.getInt("foo.bar")).andReturn(42).once();

    replayAll();

    int actual = subconfig.getInt("bar");

    verifyAll();

    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetIntDefault() {
    Config config = createMock(Config.class);
    Config subconfig = new PrefixedConfig(config, "foo");

    expect(config.getInt("foo.bar", 23)).andReturn(42).once();

    replayAll();

    int actual = subconfig.getInt("bar", 23);

    verifyAll();

    assertThat(actual).isEqualTo(42);
  }

  @Test
  public void testGetLong() {
    Config config = createMock(Config.class);
    Config subconfig = new PrefixedConfig(config, "foo");

    expect(config.getLong("foo.bar")).andReturn(42L).once();

    replayAll();

    long actual = subconfig.getLong("bar");

    verifyAll();

    assertThat(actual).isEqualTo(42L);
  }

  @Test
  public void testGetLongDefault() {
    Config config = createMock(Config.class);
    Config subconfig = new PrefixedConfig(config, "foo");

    expect(config.getLong("foo.bar", 23L)).andReturn(42L).once();

    replayAll();

    long actual = subconfig.getLong("bar", 23L);

    verifyAll();

    assertThat(actual).isEqualTo(42L);
  }

  @Test
  public void testGetFloat() {
    Config config = createMock(Config.class);
    Config subconfig = new PrefixedConfig(config, "foo");

    expect(config.getFloat("foo.bar")).andReturn(42f).once();

    replayAll();

    float actual = subconfig.getFloat("bar");

    verifyAll();

    assertThat(actual).isEqualTo(42f);
  }

  @Test
  public void testGetFloatDefault() {
    Config config = createMock(Config.class);
    Config subconfig = new PrefixedConfig(config, "foo");

    expect(config.getFloat("foo.bar", 23f)).andReturn(42f).once();

    replayAll();

    float actual = subconfig.getFloat("bar", 23f);

    verifyAll();

    assertThat(actual).isEqualTo(42f);
  }

  @Test
  public void testGetDouble() {
    Config config = createMock(Config.class);
    Config subconfig = new PrefixedConfig(config, "foo");

    expect(config.getDouble("foo.bar")).andReturn(42d).once();

    replayAll();

    double actual = subconfig.getDouble("bar");

    verifyAll();

    assertThat(actual).isEqualTo(42d);
  }

  @Test
  public void testGetDoubleDefault() {
    Config config = createMock(Config.class);
    Config subconfig = new PrefixedConfig(config, "foo");

    expect(config.getDouble("foo.bar", 23d)).andReturn(42d).once();

    replayAll();

    double actual = subconfig.getDouble("bar", 23d);

    verifyAll();

    assertThat(actual).isEqualTo(42d);
  }

  @Test
  public void testGetBoolean() {
    Config config = createMock(Config.class);
    Config subconfig = new PrefixedConfig(config, "foo");

    expect(config.getBoolean("foo.bar")).andReturn(true).once();

    replayAll();

    boolean actual = subconfig.getBoolean("bar");

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testGetBooleanDefault() {
    Config config = createMock(Config.class);
    Config subconfig = new PrefixedConfig(config, "foo");

    expect(config.getBoolean("foo.bar", true)).andReturn(false).once();

    replayAll();

    boolean actual = subconfig.getBoolean("bar", true);

    verifyAll();

    assertThat(actual).isFalse();
  }
}
