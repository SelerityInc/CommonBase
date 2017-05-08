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

package com.seleritycorp.common.base.http.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class HeaderParameterTest {
  @Test
  public void testConstructorDecomposed() {
    HeaderParameter parameter = new HeaderParameter("foo", "bar");
    
    assertThat(parameter.getKey()).isEqualTo("foo");
    assertThat(parameter.getValue()).isEqualTo("bar");
  }

  @Test
  public void testConstructorDecomposedLowerCaseKey() {
    HeaderParameter parameter = new HeaderParameter("FOO", "bar");
    
    assertThat(parameter.getKey()).isEqualTo("foo");
    assertThat(parameter.getValue()).isEqualTo("bar");
  }

  @Test
  public void testConstructorDecomposedNull() {
    HeaderParameter parameter = new HeaderParameter(null, null);
    
    assertThat(parameter.getKey()).isEqualTo("");
    assertThat(parameter.getValue()).isEqualTo("");
  }

  @Test
  public void testConstructorRaw() {
    HeaderParameter parameter = new HeaderParameter("foo=bar");
    
    assertThat(parameter.getKey()).isEqualTo("foo");
    assertThat(parameter.getValue()).isEqualTo("bar");
  }

  @Test
  public void testConstructorRawLowerCaseKey() {
    HeaderParameter parameter = new HeaderParameter("FOO=bar");
    
    assertThat(parameter.getKey()).isEqualTo("foo");
    assertThat(parameter.getValue()).isEqualTo("bar");
  }

  @Test
  public void testConstructorRawNoEquals() {
    HeaderParameter parameter = new HeaderParameter("foo");
    
    assertThat(parameter.getKey()).isEqualTo("foo");
    assertThat(parameter.getValue()).isEqualTo("");
  }

  @Test
  public void testConstructorRawNoValue() {
    HeaderParameter parameter = new HeaderParameter("foo=");
    
    assertThat(parameter.getKey()).isEqualTo("foo");
    assertThat(parameter.getValue()).isEqualTo("");
  }

  @Test
  public void testConstructorRawUntrimmed() {
    HeaderParameter parameter = new HeaderParameter(" foo  =   bar    ");
    
    assertThat(parameter.getKey()).isEqualTo("foo");
    assertThat(parameter.getValue()).isEqualTo("bar");
  }

  @Test
  public void testGetKey() {
    HeaderParameter parameter = new HeaderParameter("foo", "bar");
    
    assertThat(parameter.getKey()).isEqualTo("foo");
  }

  @Test
  public void testGetValue() {
    HeaderParameter parameter = new HeaderParameter("foo", "bar");
    
    assertThat(parameter.getValue()).isEqualTo("bar");
  }

  @Test
  public void testEqualsEqual() {
    HeaderParameter left = new HeaderParameter("foo", "bar");
    HeaderParameter right = new HeaderParameter("foo=bar");
    
    assertThat(left).isEqualTo(right);
  }

  @Test
  public void testEqualsDifferentKey() {
    HeaderParameter left = new HeaderParameter("foo", "bar");
    HeaderParameter right = new HeaderParameter("baz", "bar");
    
    assertThat(left).isNotEqualTo(right);
  }

  @Test
  public void testEqualsDifferentValue() {
    HeaderParameter left = new HeaderParameter("foo", "bar");
    HeaderParameter right = new HeaderParameter("foo", "baz");
    
    assertThat(left).isNotEqualTo(right);
  }

  @Test
  public void testHashCodeEqual() {
    HeaderParameter left = new HeaderParameter("foo", "bar");
    HeaderParameter right = new HeaderParameter("foo", "bar");
    
    assertThat(left.hashCode()).isEqualTo(right.hashCode());
  }

  @Test
  public void testHashCodeNotEqual() {
    HeaderParameter left = new HeaderParameter("foo", "baz");
    HeaderParameter right = new HeaderParameter("foo", "bar");
    
    assertThat(left.hashCode()).isNotEqualTo(right.hashCode());
  }

  @Test
  public void testToString() {
    HeaderParameter parameter = new HeaderParameter("foo", "bar");
    
    assertThat(parameter.toString()).isEqualTo("foo=bar");
  }

  @Test
  public void testCompareEqual() {
    HeaderParameter left = new HeaderParameter("foo", "bar");
    HeaderParameter right = new HeaderParameter("foo", "bar");
    
    assertThat(left).isEqualByComparingTo(right);
  }

  @Test
  public void testCompareLessKey() {
    HeaderParameter left = new HeaderParameter("bar", "baz");
    HeaderParameter right = new HeaderParameter("baz", "bar");
    
    assertThat(left).isLessThan(right);
  }

  @Test
  public void testCompareLessValue() {
    HeaderParameter left = new HeaderParameter("foo", "bar");
    HeaderParameter right = new HeaderParameter("foo", "baz");
    
    assertThat(left).isLessThan(right);
  }

  @Test
  public void testCompareGreaterKey() {
    HeaderParameter left = new HeaderParameter("baz", "bar");
    HeaderParameter right = new HeaderParameter("bar", "baz");
    
    assertThat(left).isGreaterThan(right);
  }

  @Test
  public void testCompareGreaterValue() {
    HeaderParameter left = new HeaderParameter("foo", "baz");
    HeaderParameter right = new HeaderParameter("foo", "bar");
    
    assertThat(left).isGreaterThan(right);
  }
}
