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

package com.seleritycorp.common.base.inject;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.seleritycorp.common.base.inject.FactoryModule;
import com.seleritycorp.common.base.inject.InjectorFactory;
import com.seleritycorp.common.base.test.InjectingTestCase;

public class FactoryModuleTest extends InjectingTestCase {
  @Test
  public void testCreatingInstance() {
    FactoryModule module = new FactoryModule() {
      @Override
      protected void configure() {
        installFactory(Foo.Factory.class);
      }
    };

    InjectorFactory.register(module);
    Injector injector = InjectorFactory.getInjector();
    Foo.Factory factory = injector.getInstance(Foo.Factory.class);
    Foo foo = factory.create(42);
    assertThat(foo.getValue()).isEqualTo(42);
  }

  public static class Foo {
    public interface Factory {
      Foo create(Integer value);
    }

    private int value;

    @Inject
    Foo(@Assisted Integer value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }
}
