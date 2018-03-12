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

import com.seleritycorp.common.base.http.server.ForwardedForResolver;

public class ForwardedForResolverTest {
  @Test
  public void testResolveUntrustedRemoteEmptyHeader() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    String actual = resolver.resolve("1.2.3.4", "");
    
    assertThat(actual).isEqualTo("1.2.3.4");
  }

  @Test
  public void testResolveTrustedRemoteEmptyHeader() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    String actual = resolver.resolve("10.2.3.4", "");
    
    assertThat(actual).isEqualTo("10.2.3.4");
  }

  @Test
  public void testResolveUntrustedRemoteNullHeader() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    String actual = resolver.resolve("1.2.3.4", "");
    
    assertThat(actual).isEqualTo("1.2.3.4");
  }

  @Test
  public void testResolveTrustedRemoteNullHeader() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    String actual = resolver.resolve("10.2.3.4", "");
    
    assertThat(actual).isEqualTo("10.2.3.4");
  }

  @Test
  public void testResolveUntrustedRemoteSingleIpHeader() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    String actual = resolver.resolve("1.2.3.4", "2.3.4.5");
    
    assertThat(actual).isEqualTo("1.2.3.4");
  }

  @Test
  public void testResolveUntrustedRemoteMultipleIpsHeader() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    String actual = resolver.resolve("1.2.3.4", "3.4.5.6,2.3.4.5");
    
    assertThat(actual).isEqualTo("1.2.3.4");
  }

  @Test
  public void testResolveTrustedRemoteSingleIpHeader() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    String actual = resolver.resolve("10.2.3.4", "2.3.4.5");
    
    assertThat(actual).isEqualTo("2.3.4.5");
  }

  @Test
  public void testResolveTrustedRemoteSingleIpHeaderTrimming() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    String actual = resolver.resolve("10.2.3.4", " 2.3.4.5  ");
    
    assertThat(actual).isEqualTo("2.3.4.5");
  }

  @Test
  public void testResolveTrustedRemoteMultipleIpHeaderExpectFirstItem() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    String actual = resolver.resolve("10.2.3.4", "2.3.4.5,192.168.4.5,10.2.3.5");
    
    assertThat(actual).isEqualTo("2.3.4.5");
  }

  @Test
  public void testResolveTrustedRemodeMultipleIpHeaderExectSecondItem() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    String actual = resolver.resolve("10.2.3.4", "3.4.5.6,2.3.4.5,192.168.4.5,10.2.3.5");
    
    assertThat(actual).isEqualTo("2.3.4.5");
  }

  @Test
  public void testResolveTrustedRemoteMultipleIpHeaderExpectLastItem() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    String actual = resolver.resolve("10.2.3.4", "2.3.4.5,192.168.4.5,10.2.3.5,3.4.5.6");
    
    assertThat(actual).isEqualTo("3.4.5.6");
  }

  @Test
  public void testResolveTrustedRemoteMultipleIpHeaderExpectFirstItemTrimming() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    String actual = resolver.resolve("10.2.3.4", "  192.168.4.5   ,    10.2.3.5  ,     2.3.4.5 ");
    
    assertThat(actual).isEqualTo("2.3.4.5");
  }

  @Test
  public void testResolveTrustedRemoteMultipleIpHeaderExpectSecondItemTrimming() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    String actual = resolver.resolve("10.2.3.4", "3.4.5.6  ,   2.3.4.5   ,  192.168.4.5    ,  10.2.3.5");
    
    assertThat(actual).isEqualTo("2.3.4.5");
  }

  @Test
  public void testResolveTrustedRemoteMultipleIpHeaderExpectLastItemTrimming() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    String actual = resolver.resolve("10.2.3.4", "  2.3.4.5 ,  192.168.4.5 ,  10.2.3.5  ,  3.4.5.6    ");
    
    assertThat(actual).isEqualTo("3.4.5.6");
  }

  @Test
  public void testResolveTrustedRemoteMultipleIpHeaderAllTrusted() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    String actual = resolver.resolve("10.2.3.4", "10.3.4.5,192.168.1.2");
    
    assertThat(actual).isEqualTo("10.3.4.5");
  }

  @Test
  public void testResolveTrustedRemoteMultipleIpHeaderTrustedUntrustworthy() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    String actual = resolver.resolve("10.2.3.4", "2.3.4.5,foo,192.168.1.2");
    
    assertThat(actual).isEqualTo("192.168.1.2");
  }

  @Test
  public void testIsTrustedIpInternal10() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    assertThat(resolver.isTrustedIp("10.1.2.3")).isTrue();
  }

  @Test
  public void testIsTrustedIpInternal127() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    assertThat(resolver.isTrustedIp("127.1.2.3")).isTrue();
  }

  @Test
  public void testIsTrustedIpInternal192_168() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    assertThat(resolver.isTrustedIp("192.168.1.2")).isTrue();
  }

  @Test
  public void testIsTrustedIpInternal192_169() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    assertThat(resolver.isTrustedIp("192.169.1.2")).isFalse();
  }

  @Test
  public void testIsTrustedIpInternal1_2_3_4() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    assertThat(resolver.isTrustedIp("1.2.3.4")).isFalse();
  }

  @Test
  public void testIsIp() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    assertThat(resolver.isIp("1.2.3.4")).isTrue();
  }

  @Test
  public void testIsIp192_168_1_234() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    assertThat(resolver.isIp("192.168.1.234")).isTrue();
  }

  @Test
  public void testIsIpFoo() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    assertThat(resolver.isIp("foo")).isFalse();
  }

  @Test
  public void testIsIpFooEmptyOctet() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    assertThat(resolver.isIp("1..2.3")).isFalse();
  }

  @Test
  public void testIsIpTooBigOctet() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    assertThat(resolver.isIp("192.168.1.256")).isFalse();
  }

  @Test
  public void testIsIpTooSmallOctet() {
    ForwardedForResolver resolver = createForwardedForResolver();
        
    assertThat(resolver.isIp("192.168.-1.2")).isFalse();
  }

  private ForwardedForResolver createForwardedForResolver() {
    return new ForwardedForResolver();
  }
}
