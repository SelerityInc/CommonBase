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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class NegotiatorTest {
  @Test
  public void testNegotiateNull() {
    Negotiator<String> negotiator = createNegotiator();
    
    String actual = negotiator.negotiate(null, "foo");
    
    assertThat(actual).isEqualTo("foo");
  }

  @Test
  public void testNegotiateFallbackNoCandidates() {
    Negotiator<String> negotiator = createNegotiator();
    
    String actual = negotiator.negotiate("bar", "foo");
    
    assertThat(actual).isEqualTo("foo");
  }

  @Test
  public void testNegotiateFallbackCandidates() {
    Negotiator<String> negotiator = createNegotiator();
    
    String actual = negotiator.negotiate("bar", "foo", "baz");
    
    assertThat(actual).isEqualTo("foo");
  }

  @Test
  public void testNegotiateCandidateFirst() {
    Negotiator<String> negotiator = createNegotiator();
    
    String actual = negotiator.negotiate("foo", "quux", "foo", "bar", "baz");
    
    assertThat(actual).isEqualTo("foo");
  }

  @Test
  public void testNegotiateCandidateMiddle() {
    Negotiator<String> negotiator = createNegotiator();
    
    String actual = negotiator.negotiate("bar", "quux", "foo", "bar", "baz");
    
    assertThat(actual).isEqualTo("bar");
  }

  @Test
  public void testNegotiateCandidateLast() {
    Negotiator<String> negotiator = createNegotiator();
    
    String actual = negotiator.negotiate("baz", "quux", "foo", "bar", "baz");
    
    assertThat(actual).isEqualTo("baz");
  }

  @Test
  public void testNegotiatorfc2616With5Candidates() {
    Negotiator<String> negotiator = createNegotiator();
    
    String actual = negotiator.negotiate("text/plain;q=0.5,text/html,text/x-dvi;q=0.8,text/x-c",
        "fallback", "text/plain", "text/html", "text/x-dvi", "text/x-c", "foo");
    
    assertThat(actual).isIn("text/html", "text/x-c");
  }

  @Test
  public void testNegotiatorfc2616With4CandidatesXc() {
    Negotiator<String> negotiator = createNegotiator();
    
    String actual = negotiator.negotiate("text/plain;q=0.5,text/html,text/x-dvi;q=0.8,text/x-c",
        "fallback", "text/plain", "text/x-dvi", "text/x-c", "foo");
    
    assertThat(actual).isEqualTo("text/x-c");
  }

  @Test
  public void testNegotiatorfc2616With4CandidatesHtml() {
    Negotiator<String> negotiator = createNegotiator();
    
    String actual = negotiator.negotiate("text/plain;q=0.5,text/html,text/x-dvi;q=0.8,text/x-c",
        "fallback", "text/plain", "text/html", "text/x-dvi", "foo");
    
    assertThat(actual).isEqualTo("text/html");
  }

  @Test
  public void testNegotiatorfc2616With3Candidates() {
    Negotiator<String> negotiator = createNegotiator();
    
    String actual = negotiator.negotiate("text/plain;q=0.5,text/html,text/x-dvi;q=0.8,text/x-c",
        "fallback", "text/plain", "text/x-dvi", "foo");
    
    assertThat(actual).isEqualTo("text/x-dvi");
  }

  @Test
  public void testNegotiatorfc2616With2Candidate() {
    Negotiator<String> negotiator = createNegotiator();
    
    String actual = negotiator.negotiate("text/plain;q=0.5,text/html,text/x-dvi;q=0.8,text/x-c",
        "fallback", "text/plain", "foo");
    
    assertThat(actual).isEqualTo("text/plain");
  }

  @Test
  public void testNegotiatorfc2616With1Candidate() {
    Negotiator<String> negotiator = createNegotiator();
    
    String actual = negotiator.negotiate("text/plain;q=0.5,text/html,text/x-dvi;q=0.8,text/x-c",
        "fallback", "foo");
    
    assertThat(actual).isEqualTo("fallback");
  }

  @Test
  public void testNegotiateUseFirstQ() {
    Negotiator<String> negotiator = createNegotiator();
    
    String actual = negotiator.negotiate("foo;q=0.5;q=0.8,bar;q=0.7",
        "fallback", "bar");
    
    assertThat(actual).isEqualTo("bar");
  }

  @Test
  public void testNegotiateFindQ() {
    Negotiator<String> negotiator = createNegotiator();
    
    String actual = negotiator.negotiate("quuux ; q=0.4   ,  "
        + "foo ; param1=bar ; q =  0.5 ; param2= baz , quux;q=0.3",
        "fallback", "foo ; param1=bar", "quux", "quuux");
    
    assertThat(actual).isEqualTo("foo ; param1=bar");
  }

  @Test
  public void testNegotiateMostSpecificWins() {
    Negotiator<String> negotiator = createNegotiator();
    
    String actual = negotiator.negotiate("foo,foo;bar=baz", "fallback", "foo", "foo;bar=baz");
    
    assertThat(actual).isEqualTo("foo;bar=baz");
  }

  @Test
  public void testNegotiateNonEqualSpec() {
    Negotiator<String> negotiator = createNegotiator();
    
    String actual = negotiator.negotiate("bar;q=0.8,foo;q=0.9", "fallback", "bar", "foo/bar");
    
    assertThat(actual).isEqualTo("foo/bar");
  }

  private Negotiator<String> createNegotiator() {
    return new StringNegotiator();
  }
  
  private class StringNegotiator extends Negotiator<String> {
    @Override
    public String parse(String raw) {
      return raw;
    }

    @Override
    protected boolean meets(String candidate, String specification) {
      return candidate.startsWith(specification);
    }
  }
}
