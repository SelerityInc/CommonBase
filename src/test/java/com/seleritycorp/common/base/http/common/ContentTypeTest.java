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

package com.seleritycorp.common.base.http.common;

import static org.assertj.core.api.Assertions.assertThat;

import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_8;

import org.junit.Test;

public class ContentTypeTest {
  @Test
  public void testEqualsMatch() {
    ContentType left = new ContentType("foo", "bar", UTF_8);
    ContentType right = new ContentType("foo", "bar", UTF_8);
    
    assertThat(left).isEqualTo(right);
  }

  @Test
  public void testEqualsMatchNoCharset() {
    ContentType left = new ContentType("foo", "bar");
    ContentType right = new ContentType("foo", "bar");
    
    assertThat(left).isEqualTo(right);
  }

  @Test
  public void testEqualsMatchNullNone() {
    ContentType left = new ContentType("foo", "bar", null);
    ContentType right = new ContentType("foo", "bar");
    
    assertThat(left).isEqualTo(right);
  }

  @Test
  public void testEqualsNoMatchCharsetUtf8None() {
    ContentType left = new ContentType("foo", "bar", UTF_8);
    ContentType right = new ContentType("foo", "bar");
    
    assertThat(left).isNotEqualTo(right);
  }

  @Test
  public void testEqualsNoMatchCharsetUtf8Utf16() {
    ContentType left = new ContentType("foo", "bar", UTF_8);
    ContentType right = new ContentType("foo", "bar", UTF_16);
    
    assertThat(left).isNotEqualTo(right);
  }
  
  @Test
  public void testToStringNoCharset() {
    ContentType contentType = new ContentType("foo", "bar");

    String actual = contentType.toString();
    
    assertThat(actual).isEqualTo("foo/bar");
  }

  @Test
  public void testToStringCharsetNull() {
    ContentType contentType = new ContentType("foo", "bar", null);

    String actual = contentType.toString();
    
    assertThat(actual).isEqualTo("foo/bar");
  }

  @Test
  public void testToStringUtf8() {
    ContentType contentType = new ContentType("foo", "bar", UTF_8);

    String actual = contentType.toString();
    
    assertThat(actual).isEqualTo("foo/bar; charset=UTF-8");
  }

  @Test
  public void testEqualsMatchingExtraParameters() {
    ContentType left = new ContentType("foo/bar; baz=quux; quuux=42");
    ContentType right = new ContentType("foo/bar; baz=quux; quuux=42");
    
    assertThat(left).isEqualTo(right);
  }

  @Test
  public void testEqualsDifferentExtraParameters() {
    ContentType left = new ContentType("foo/bar; baz=quux; quuux=42");
    ContentType right = new ContentType("foo/bar; baz=4711; quuux=42");
    
    assertThat(left).isNotEqualTo(right);
  }

  @Test
  public void testEqualsUnsortedParameters() {
    ContentType left = new ContentType("foo/bar; baz=quux; foo=quuux");
    ContentType right = new ContentType("foo/bar; foo=quuux; baz=quux");
    
    assertThat(left).isEqualTo(right);
  }

  @Test
  public void testMeetsWithParamsFullMatch() {
    ContentType candidate = new ContentType("text/html; foo=bar; bar=baz");
    ContentType specification = new ContentType("text/html; foo=bar; bar=baz");
    
    assertThat(candidate.meets(specification)).isTrue();
  }

  @Test
  public void testMeetsWithParamsDifferentValue() {
    ContentType candidate = new ContentType("text/html; foo=bar; bar=quux");
    ContentType specification = new ContentType("text/html; foo=bar; bar=baz");
    
    assertThat(candidate.meets(specification)).isFalse();
  }

  @Test
  public void testMeetsWithParamsDifferentKey() {
    ContentType candidate = new ContentType("text/html; foo=bar; bar=quux");
    ContentType specification = new ContentType("text/html; foo=bar; baz=quux");
    
    assertThat(candidate.meets(specification)).isFalse();
  }

  @Test
  public void testMeetsWithParamsCandidateMoreParams() {
    ContentType candidate = new ContentType("text/html; foo=bar; bar=quux");
    ContentType specification = new ContentType("text/html; foo=bar");
    
    assertThat(candidate.meets(specification)).isTrue();
  }

  @Test
  public void testMeetsDifferentSubtype() {
    ContentType candidate = new ContentType("text/html");
    ContentType specification = new ContentType("text/plain");
    
    assertThat(candidate.meets(specification)).isFalse();
  }

  @Test
  public void testMeetsDifferentType() {
    ContentType candidate = new ContentType("text/html");
    ContentType specification = new ContentType("foo/html");
    
    assertThat(candidate.meets(specification)).isFalse();
  }

  @Test
  public void testMeetsWildcardSubtype() {
    ContentType candidate = new ContentType("text/html");
    ContentType specification = new ContentType("text/*");
    
    assertThat(candidate.meets(specification)).isTrue();
  }

  @Test
  public void testMeetsWildcardSubtypeDifferentType() {
    ContentType candidate = new ContentType("text/html");
    ContentType specification = new ContentType("image/*");
    
    assertThat(candidate.meets(specification)).isFalse();
  }

  @Test
  public void testMeetsWildcard() {
    ContentType candidate = new ContentType("text/html");
    ContentType specification = new ContentType("*/*");
    
    assertThat(candidate.meets(specification)).isTrue();
  }

  @Test
  public void testMeetsWildcardWithParameters() {
    ContentType candidate = new ContentType("text/html; foo=bar");
    ContentType specification = new ContentType("*/*");
    
    assertThat(candidate.meets(specification)).isTrue();
  }

  @Test
  public void testMeetsWildcardWithParametersBoth() {
    ContentType candidate = new ContentType("text/html; foo=bar");
    ContentType specification = new ContentType("*/*; foo=bar");
    
    assertThat(candidate.meets(specification)).isTrue();
  }

  @Test
  public void testMeetsWildcardWithParametersBothDifferent() {
    ContentType candidate = new ContentType("text/html; foo=bar");
    ContentType specification = new ContentType("*/*; bar=foo");
    
    assertThat(candidate.meets(specification)).isFalse();
  }
}
