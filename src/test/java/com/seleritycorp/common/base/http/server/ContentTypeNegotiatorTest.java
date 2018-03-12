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

package com.seleritycorp.common.base.http.server;

import static com.seleritycorp.common.base.http.common.ContentType.APPLICATION_JSON;
import static com.seleritycorp.common.base.http.common.ContentType.TEXT_HTML;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.seleritycorp.common.base.http.common.ContentType;

public class ContentTypeNegotiatorTest {
  @Test
  public void testNegotiateFallback() {
    ContentTypeNegotiator negotiator = createContentTypeNegotiator();

    ContentType negotiated = negotiator.negotiate("image/*", APPLICATION_JSON, TEXT_HTML);
    
    assertThat(negotiated).isEqualTo(APPLICATION_JSON);
  }
  
  @Test
  public void testNegotiateFixed() {
    ContentTypeNegotiator negotiator = createContentTypeNegotiator();

    ContentType negotiated = negotiator.negotiate("text/html", APPLICATION_JSON, TEXT_HTML);
    
    assertThat(negotiated).isEqualTo(TEXT_HTML);
  }
  
  @Test
  public void testNegotiateFixedWithParameters() {
    ContentTypeNegotiator negotiator = createContentTypeNegotiator();

    ContentType negotiated = negotiator.negotiate("text/html; charset=UTF-8", APPLICATION_JSON, TEXT_HTML);
    
    assertThat(negotiated).isEqualTo(TEXT_HTML);
  }
  
  @Test
  public void testNegotiateWildcardSubtype() {
    ContentTypeNegotiator negotiator = createContentTypeNegotiator();

    ContentType negotiated = negotiator.negotiate("text/*", APPLICATION_JSON, TEXT_HTML);
    
    assertThat(negotiated).isEqualTo(TEXT_HTML);
  }
  
  @Test
  public void testNegotiateWildcard() {
    ContentTypeNegotiator negotiator = createContentTypeNegotiator();

    ContentType negotiated = negotiator.negotiate("*/*", APPLICATION_JSON, TEXT_HTML);
    
    assertThat(negotiated).isEqualTo(TEXT_HTML);
  }
  
  @Test
  public void testNegotiateNull() {
    ContentTypeNegotiator negotiator = createContentTypeNegotiator();

    ContentType negotiated = negotiator.negotiate(null, APPLICATION_JSON, TEXT_HTML);
    
    assertThat(negotiated).isEqualTo(APPLICATION_JSON);
  }
  
  @Test
  public void testNegotiateEmpty() {
    ContentTypeNegotiator negotiator = createContentTypeNegotiator();

    ContentType negotiated = negotiator.negotiate("", APPLICATION_JSON, TEXT_HTML);
    
    assertThat(negotiated).isEqualTo(APPLICATION_JSON);
  }
  
  @Test
  public void testNegotiateOnlyWhitespace() {
    ContentTypeNegotiator negotiator = createContentTypeNegotiator();

    ContentType negotiated = negotiator.negotiate("   ", APPLICATION_JSON, TEXT_HTML);
    
    assertThat(negotiated).isEqualTo(APPLICATION_JSON);
  }
  
  private ContentTypeNegotiator createContentTypeNegotiator() {
    return new ContentTypeNegotiator();
  }
}
