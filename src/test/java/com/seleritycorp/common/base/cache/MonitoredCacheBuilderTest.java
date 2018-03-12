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

package com.seleritycorp.common.base.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.seleritycorp.common.base.test.InjectingTestCase;
import com.seleritycorp.common.base.test.SettableStaticClock;

public class MonitoredCacheBuilderTest extends InjectingTestCase {
  private SettableStaticClock clock;
  private CacheMetrics.Factory cacheMetricsFactory;

  @Before
  public void setUp() {
    clock = getClock();
    cacheMetricsFactory = createMock(CacheMetrics.Factory.class);
  }

  @Test
  public void testBuildPlain() {
    CacheMetrics cacheMetrics = createMock(CacheMetrics.class);
    Capture<Cache<Integer, String>> monitoredCacheCapture = newCapture();
    expect(cacheMetricsFactory.create(capture(monitoredCacheCapture))).andReturn(cacheMetrics);
    
    replayAll();
    
    Cache<Integer, String> cache = createMonitoredCacheBuilder().build();

    cache.put(42, "bar");

    String value1 = cache.getIfPresent(42);
    String value2 = cache.getIfPresent(4711);

    verifyAll();

    assertThat(value1).isEqualTo("bar");
    assertThat(value2).isNull();
    
    Cache<Integer, String> monitoredCache = monitoredCacheCapture.getValue();
    assertThat(cache).isSameAs(monitoredCache);
  }

  @Test
  public void testBuildLoader() throws Exception {
    CacheMetrics cacheMetrics = createMock(CacheMetrics.class);
    Capture<Cache<Integer, String>> monitoredCacheCapture = newCapture();
    expect(cacheMetricsFactory.create(capture(monitoredCacheCapture))).andReturn(cacheMetrics);
    
    Exception expectedException = new Exception("catch me");

    @SuppressWarnings("unchecked")
    CacheLoader<Integer, String> loader = createMock(CacheLoader.class);
    expect(loader.load(4711)).andReturn("quux");
    expect(loader.load(1083)).andThrow(expectedException);

    replayAll();
    
    LoadingCache<Integer, String> cache = createMonitoredCacheBuilder().build(loader);

    cache.put(42, "bar");

    String value1 = cache.get(42);
    String value2 = cache.get(4711);
    String value3 = cache.get(4711);
    try {
      cache.get(1083);
      failBecauseExceptionWasNotThrown(ExecutionException.class);
    } catch (Exception e) {
      assertThat(e.getCause()).isSameAs(expectedException);
    }
 
    verifyAll();

    
    assertThat(value1).isEqualTo("bar");
    assertThat(value2).isEqualTo("quux");
    assertThat(value3).isEqualTo("quux");
    
    Cache<Integer, String> monitoredCache = monitoredCacheCapture.getValue();
    assertThat(cache).isSameAs(monitoredCache);
  }

  @Test
  public void testMaximumSize() {
    CacheMetrics cacheMetrics = createMock(CacheMetrics.class);
    Capture<Cache<Integer, String>> monitoredCacheCapture = newCapture();
    expect(cacheMetricsFactory.create(capture(monitoredCacheCapture))).andReturn(cacheMetrics);
    
    replayAll();
    
    Cache<Integer, String> cache = createMonitoredCacheBuilder().maximumSize(1).build();

    cache.put(42, "bar");
    cache.put(4711, "quux");

    String value1 = cache.getIfPresent(42);
    String value2 = cache.getIfPresent(4711);

    verifyAll();

    assertThat(value1).isNull();
    assertThat(value2).isEqualTo("quux");
    
    Cache<Integer, String> monitoredCache = monitoredCacheCapture.getValue();
    assertThat(cache).isSameAs(monitoredCache);
  }

  @Test
  public void testExpireAfterAccess() {
    CacheMetrics cacheMetrics = createMock(CacheMetrics.class);
    Capture<Cache<Integer, String>> monitoredCacheCapture = newCapture();
    expect(cacheMetricsFactory.create(capture(monitoredCacheCapture))).andReturn(cacheMetrics);
    
    replayAll();
    
    Cache<Integer, String> cache = createMonitoredCacheBuilder()
        .expireAfterAccess(2, TimeUnit.SECONDS).build();

    cache.put(42, "bar");
    String value1 = cache.getIfPresent(42);

    clock.advanceMillis(1001);
    
    String value2 = cache.getIfPresent(42);

    clock.advanceMillis(1001);
    
    String value3 = cache.getIfPresent(42);

    clock.advanceMillis(2001);
    
    String value4 = cache.getIfPresent(42);
    
    verifyAll();

    assertThat(value1).isEqualTo("bar");
    assertThat(value2).isEqualTo("bar");
    assertThat(value3).isEqualTo("bar");
    assertThat(value4).isNull();
    
    Cache<Integer, String> monitoredCache = monitoredCacheCapture.getValue();
    assertThat(cache).isSameAs(monitoredCache);
  }

  @Test
  public void testExpireAfterWrite() {
    CacheMetrics cacheMetrics = createMock(CacheMetrics.class);
    Capture<Cache<Integer, String>> monitoredCacheCapture = newCapture();
    expect(cacheMetricsFactory.create(capture(monitoredCacheCapture))).andReturn(cacheMetrics);
    
    replayAll();
    
    Cache<Integer, String> cache = createMonitoredCacheBuilder()
        .expireAfterWrite(2, TimeUnit.SECONDS).build();

    cache.put(42, "bar");
    String value1 = cache.getIfPresent(42);

    clock.advanceMillis(1001);
    
    cache.put(42, "bar");
    String value2 = cache.getIfPresent(42);

    clock.advanceMillis(1001);
    
    String value3 = cache.getIfPresent(42);

    clock.advanceMillis(1001);
    
    String value4 = cache.getIfPresent(42);
    
    verifyAll();

    assertThat(value1).isEqualTo("bar");
    assertThat(value2).isEqualTo("bar");
    assertThat(value3).isEqualTo("bar");
    assertThat(value4).isNull();
    
    Cache<Integer, String> monitoredCache = monitoredCacheCapture.getValue();
    assertThat(cache).isSameAs(monitoredCache);
  }

  @Test
  public void testRemovalListener() {
    CacheMetrics cacheMetrics = createMock(CacheMetrics.class);
    Capture<Cache<Integer, String>> monitoredCacheCapture = newCapture();
    expect(cacheMetricsFactory.create(capture(monitoredCacheCapture))).andReturn(cacheMetrics);
    
    Capture<RemovalNotification<Integer, String>> notificationCapture = newCapture();

    @SuppressWarnings("unchecked")
    RemovalListener<Integer, String> listener = createMock(RemovalListener.class);
    listener.onRemoval(capture(notificationCapture));
    
    replayAll();
    
    Cache<Integer, String> cache = createMonitoredCacheBuilder()
        .removalListener(listener).maximumSize(1).build();

    cache.put(42, "bar");
    cache.put(4711, "foo");
    
    verifyAll();

    RemovalNotification<Integer, String> notification = notificationCapture.getValue();
    assertThat(notification.getKey()).isEqualTo(42);
    assertThat(notification.getValue()).isEqualTo("bar");
    assertThat(notification.getCause()).isEqualTo(RemovalCause.SIZE);
    
    Cache<Integer, String> monitoredCache = monitoredCacheCapture.getValue();
    assertThat(cache).isSameAs(monitoredCache);
  }

  @Test
  public void testBuildCacheMetricsFactoryNull() {
    cacheMetricsFactory = null;

    replayAll();
    
    createMonitoredCacheBuilder().build();

    verifyAll();
  }

  private MonitoredCacheBuilder<Object, Object> createMonitoredCacheBuilder() {
    return new MonitoredCacheBuilder<>("foo", clock, cacheMetricsFactory);
  }
}
