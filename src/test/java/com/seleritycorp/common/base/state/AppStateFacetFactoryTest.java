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

package com.seleritycorp.common.base.state;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.expect;

import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.state.AppStateFacetFactory;
import com.seleritycorp.common.base.state.AppStateManager;
import com.seleritycorp.common.base.state.AppStatePushFacet;
import com.seleritycorp.common.base.test.InjectingTestCase;

public class AppStateFacetFactoryTest extends InjectingTestCase {
  private AppStateManager appStateManager;
  private AppStatePushFacet.Factory appStatePushFacetFactory;
  private AppStateFacetFactory factory;

  @Before
  public void setUp() {
    appStateManager = createMock(AppStateManager.class);
    appStatePushFacetFactory = null;
    factory = null;
  }

  @Test
  public void testCreateAppStatePushFacet() {
    appStatePushFacetFactory = createMock(AppStatePushFacet.Factory.class);

    AppStatePushFacet expected = createMock(AppStatePushFacet.class);
    expect(appStatePushFacetFactory.create()).andReturn(expected);

    expect(appStateManager.registerAppStateFacet("foo", expected)).andReturn(true);

    replayAll();

    factory = createFactory();
    AppStatePushFacet actual = factory.createAppStatePushFacet("foo");

    verifyAll();

    assertThat(actual).isSameAs(expected);
  }

  private AppStateFacetFactory createFactory() {
    return new AppStateFacetFactory(appStateManager, appStatePushFacetFactory);
  }
}
