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

package com.seleritycorp.common.base.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.easymock.EasyMockSupport;
import org.easymock.IMocksControl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.seleritycorp.common.base.inject.InjectorFactory;
import com.seleritycorp.common.base.state.AppState;
import com.seleritycorp.common.base.state.StateManager;

public class ApplicationRunnerTest extends EasyMockSupport {
  @Rule
  public final ExpectedSystemExit exit = ExpectedSystemExit.none();

  @Test
  public void testRun() throws Exception {
    IMocksControl control = createStrictControl();
    Application application = control.createMock(Application.class);
    StateManager stateManager = control.createMock(StateManager.class);

    application.init();

    stateManager.setMainAppState(AppState.READY);
    
    application.run();
    
    stateManager.setMainAppState(AppState.FAULTY);
    
    application.shutdown();
        
    replayAll();
    
    ApplicationRunner runner = new ApplicationRunner(application, stateManager);
    runner.run();
    
    verifyAll();
  }
  
  @Test
  public void testStaticRun() throws Exception {
    Application application = createMock(Application.class);
    application.init();
    application.run();
    application.shutdown();
    
    final String[] args = new String[] { "foo" };    
    final PlainShimModule module1 = new PlainShimModule();
    final PlainShimModule module2 = new ApplicationShimModule(application);

    replayAll();

    exit.expectSystemExit();
    exit.checkAssertionAfterwards(new Assertion() {
      @Override
      public void checkAssertion() throws Exception {
        verifyAll();

        assertThat(module1.isConfigured()).isTrue();
        assertThat(module2.isConfigured()).isTrue();

        Key<String[]> key = Key.get(String[].class, CommandLineArguments.class);
        String[] actualArgs = InjectorFactory.getInjector().getInstance(key);
        assertThat(actualArgs).isSameAs(args);
      }      
    });
    InjectorFactory.forceInjector(null);
    ApplicationRunner.run(args, module1, module2);
  }

  private static class PlainShimModule extends AbstractModule {
    private boolean configured;
    
    public PlainShimModule() {
      configured = false;
    }

    @Override
    protected void configure() {
      configured = true;
    }

    public boolean isConfigured() {
      return configured;
    }
  }

  private static class ApplicationShimModule extends PlainShimModule {
    private final Application application;
    
    public ApplicationShimModule(Application application) {
      this.application = application;
    }
    
    @Override
    protected void configure() {
      super.configure();
      
      bind(Application.class).toInstance(application);
    }    
  }
}
