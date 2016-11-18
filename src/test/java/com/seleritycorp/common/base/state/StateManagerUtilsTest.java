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

package com.seleritycorp.common.base.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.expect;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.seleritycorp.common.base.inject.InjectorFactory;
import com.seleritycorp.common.base.state.AppStateFacet;
import com.seleritycorp.common.base.state.AppStatePushFacet;
import com.seleritycorp.common.base.state.StateManager;
import com.seleritycorp.common.base.state.StateManagerUtils;
import com.seleritycorp.common.base.test.InjectingTestCase;

public class StateManagerUtilsTest extends InjectingTestCase {
  StateManager stateManager;

  @Before
  public void setUp() throws IOException {
    stateManager = createMock(StateManager.class);

    InjectorFactory.register(new AbstractModule() {
      @Override
      protected void configure() {
        bind(StateManager.class).toInstance(stateManager);;
      }
    });
  }

  @Test
  public void testCreateRegisteredAppStatePushFacet() {
    AppStatePushFacet expected = createMock(AppStatePushFacet.class);
    expect(stateManager.createRegisteredAppStatePushFacet("foo")).andReturn(expected);

    replayAll();

    AppStatePushFacet actual = StateManagerUtils.createRegisteredAppStatePushFacet("foo");

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  @Test
  public void testRegisterAppStateFacetUninitialized() {
    AppStateFacet facet = createMock(AppStateFacet.class);
    expect(stateManager.registerAppStateFacet("foo", facet)).andReturn(true);

    replayAll();

    boolean result = StateManagerUtils.registerAppStateFacet("foo", facet);

    verifyAll();

    assertThat(result).isTrue();
  }
}
