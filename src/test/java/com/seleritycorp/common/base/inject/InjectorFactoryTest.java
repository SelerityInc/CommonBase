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
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.After;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.seleritycorp.common.base.config.ApplicationConfig;
import com.seleritycorp.common.base.config.ApplicationPath;
import com.seleritycorp.common.base.config.Config;
import com.seleritycorp.common.base.inject.InjectorFactory;
import com.seleritycorp.common.base.test.InjectingTestCase;

public class InjectorFactoryTest extends InjectingTestCase {
  @After
  public void tearDown() {
    InjectorFactory.forceInjector(null);
  }

  @Test
  public void testGetInjectorSingle() {
    Injector injector = InjectorFactory.getInjector();
    assertThat(injector).isNotNull();
  }

  @Test
  public void testGetInjectorSingleton() {
    Injector injectorA = InjectorFactory.getInjector();
    Injector injectorB = InjectorFactory.getInjector();
    assertThat(injectorA).isSameAs(injectorB);
  }

  @Test
  public void testGetInjectorFunctional() {
    Injector injector = InjectorFactory.getInjector();
    Foo foo = injector.getInstance(Foo.class);
    assertThat(foo).isNotNull();
  }

  @Test
  public void testGetInjectorEnvironmentInitialized() {
    InjectorFactory.forceInjector(null);
    Injector injector = InjectorFactory.getInjector();
    BasicEnvironmentAccessor env1 = injector.getInstance(BasicEnvironmentAccessor.class);

    assertThat(env1.getApplicationPath()).isNotNull();
    assertThat(env1.getApplicationConfig()).isNotNull();

    BasicEnvironmentAccessor env2 = injector.getInstance(BasicEnvironmentAccessor.class);

    assertThat(env1).isNotSameAs(env2);

    assertThat(env2.getApplicationPath()).isSameAs(env2.getApplicationPath());
    assertThat(env2.getApplicationConfig()).isSameAs(env2.getApplicationConfig());
  }

  @Test
  public void testGetInjectorClean() {
    InjectorFactory.forceInjector(Guice.createInjector());
    Injector injector = InjectorFactory.getInjector();
    try {
      injector.getInstance(BasicEnvironmentAccessor.class);
      failBecauseExceptionWasNotThrown(ConfigurationException.class);
    } catch (ConfigurationException e) {
    }
  }

  @Test
  public void testForcingNull() {
    Injector injectorA = InjectorFactory.getInjector();
    InjectorFactory.forceInjector(null);
    Injector injectorB = InjectorFactory.getInjector();
    assertThat(injectorA).isNotSameAs(injectorB);
  }

  @Test
  public void testForcingEmpty() {
    Injector injectorA = InjectorFactory.getInjector();
    Injector forced = Guice.createInjector();

    InjectorFactory.forceInjector(forced);

    Injector injectorB = InjectorFactory.getInjector();
    assertThat(injectorA).isNotSameAs(injectorB);
    assertThat(injectorB).isSameAs(forced);
  }

  @Test
  public void testRegister() {
    final Baz baz = new Baz();

    Injector injectorA = InjectorFactory.getInjector();
    Foo fooA = injectorA.getInstance(Foo.class);
    Bar barA = injectorA.getInstance(Bar.class);

    InjectorFactory.register(new AbstractModule() {
      @Override
      protected void configure() {
        this.bind(Baz.class).toInstance(baz);
      }
    });
    Injector injectorB = InjectorFactory.getInjector();

    assertThat(injectorA).isNotSameAs(injectorB);

    Foo fooB = injectorB.getInstance(Foo.class);
    Bar barB = injectorB.getInstance(Bar.class);
    Baz bazB = injectorB.getInstance(Baz.class);

    assertThat(fooB).isNotSameAs(fooA);
    assertThat(barB).isSameAs(barA);
    assertThat(bazB).isSameAs(baz);
  }

  @Test
  public void testLazyInjectorCreation() {
    InjectorFactory.forceInjector(null);

    ModuleShim module = new ModuleShim();
    InjectorFactory.register(module);

    assertThat(module.hasBeenConfigured()).isFalse();

    InjectorFactory.getInjector();

    assertThat(module.hasBeenConfigured()).isTrue();
  }

  @Test
  public void testDependentModules() {
    InjectorFactory.forceInjector(null);

    final Foo foo = new Foo();
    InjectorFactory.register(new AbstractModule() {
      @Override
      protected void configure() {
        bind(Foo.class).toInstance(foo);
      }
    });

    InjectorFactory.register(new AbstractModule() {
      @Override
      protected void configure() {
        InjectorFactory.getInjector().getInstance(Quux.class);
      }
    });
    
    assertThat(InjectorFactory.getInjector()).isNotNull();
  }

  static class Foo {
  }

  @Singleton
  static class Bar {
  }

  static class Baz {
  }

  static class Quux {
    @Inject
    public Quux() {
      InjectorFactory.getInjector().getInstance(Foo.class);
    }
  }

  static class BasicEnvironmentAccessor {
    private Path applicationPath;
    private Config applicationConfig;

    @Inject
    BasicEnvironmentAccessor(@ApplicationPath Path applicationPath,
        @ApplicationConfig Config applicationConfig) {
      this.applicationPath = applicationPath;
      this.applicationConfig = applicationConfig;
    }

    public Path getApplicationPath() {
      return applicationPath;
    }

    public Config getApplicationConfig() {
      return applicationConfig;
    }
  }
  
  static class ModuleShim extends AbstractModule {
    private boolean configured;
    
    public ModuleShim() {
      configured = false;
    }

    @Override
    protected void configure() {
      configured = true;
    }

    public boolean hasBeenConfigured() {
      return configured;
    }
  }
}
