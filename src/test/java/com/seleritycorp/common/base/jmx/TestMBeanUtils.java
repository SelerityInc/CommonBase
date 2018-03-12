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

package com.seleritycorp.common.base.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.Test;

public class TestMBeanUtils {
  @Test
  public void testRegister2ArgOk() {
    Foo foo = new Foo();

    ObjectName ret = MBeanUtils.register("foo:bar=baz", foo);

    assertThat(ret.getCanonicalName()).isEqualTo("foo:bar=baz");
  }

  @Test
  public void testRegister2ArgNameIssues() {
    Foo foo = new Foo();

    ObjectName ret = MBeanUtils.register("foo", foo);

    assertThat(ret).isNull();
  }

  @Test
  public void testRegister2ArgNonStandardMBean() {
    Bar bar = new Bar();

    ObjectName ret = MBeanUtils.register("foo", bar);

    assertThat(ret).isNull();
  }

  @Test
  public void testRegister3ArgOkStandardMBean() {
    Foo foo = new Foo();

    ObjectName ret = MBeanUtils.register("foo:bar=baz", foo, FooMBean.class);

    assertThat(ret.getCanonicalName()).isEqualTo("foo:bar=baz");
  }

  @Test
  public void testRegister3ArgOkNonStandardMBean() {
    Bar bar = new Bar();

    ObjectName ret = MBeanUtils.register("foo:bar=baz", bar, FooMBean.class);

    assertThat(ret.getCanonicalName()).isEqualTo("foo:bar=baz");
  }

  @Test
  public void testRegister3ArgNameIssues() {
    Foo foo = new Foo();

    ObjectName ret = MBeanUtils.register("foo", foo, FooMBean.class);

    assertThat(ret).isNull();
  }

  @Test
  public void testUnregisterIfRegisteredRegistered() {
    Foo foo = new Foo();

    ObjectName ret = MBeanUtils.register("foo:bar=baz", foo);
    assertThat(ret.getCanonicalName()).isEqualTo("foo:bar=baz");

    MBeanUtils.unregisterIfRegistered(ret);
  }

  @Test
  public void testUnregisterIfRegisteredUnregistered() throws MalformedObjectNameException {
    ObjectName objectName = new ObjectName("foo:bar=baz");
    MBeanUtils.unregisterIfRegistered(objectName);
  }

  @Test
  public void testUnregisterIfRegisteredNull() {
    MBeanUtils.unregisterIfRegistered(null);
  }

  public class Foo implements FooMBean {
  }

  public interface FooMBean {
  }

  public class Bar implements FooMBean {
  }
}
