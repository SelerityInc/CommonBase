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

package com.seleritycorp.common.base.http.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.expect;

import org.apache.http.entity.ContentType;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.seleritycorp.common.base.meta.MetaDataFormatter;

public class HttpRequestFactoryTest extends EasyMockSupport {
  private HttpRequest.Factory requestFactory;
  private MetaDataFormatter metaDataFormatter;

  @Before
  public void setUp() {
    requestFactory = createMock(HttpRequest.Factory.class);

    metaDataFormatter = createMock(MetaDataFormatter.class);
    expect(metaDataFormatter.getUserAgent()).andReturn("userAgentBar").anyTimes();
  }

  @Test
  public void testCreate() {
    HttpRequest expected = createMock(HttpRequest.class);
    expect(expected.setUserAgent("userAgentBar")).andReturn(expected);

    expect(requestFactory.create("foo")).andReturn(expected);
    
    replayAll();

    HttpRequestFactory factory = createHttpRequestFactory();
    HttpRequest request = factory.create("foo");
    
    verifyAll();
    
    assertThat(request).isEqualTo(expected);
  }
  
  @Test
  public void testCreatePostJson() {
    HttpRequest expected = createMock(HttpRequest.class);
    expect(expected.setUserAgent("userAgentBar")).andReturn(expected);
    expect(expected.setMethodPost()).andReturn(expected);
    expect(expected.setContentType(ContentType.APPLICATION_JSON)).andReturn(expected);
    expect(expected.addData("{\"bar\":4711}")).andReturn(expected);

    expect(requestFactory.create("foo")).andReturn(expected);
    
    replayAll();

    JsonObject json = new JsonObject();
    json.addProperty("bar", 4711);
    HttpRequestFactory factory = createHttpRequestFactory();
    HttpRequest request = factory.createPostJson("foo", json);
    
    verifyAll();
    
    assertThat(request).isEqualTo(expected);
  }
  
  private HttpRequestFactory createHttpRequestFactory() {
    return new HttpRequestFactory(requestFactory, metaDataFormatter);
  }
}
