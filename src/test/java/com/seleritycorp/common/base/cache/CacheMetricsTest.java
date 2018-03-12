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

import static org.easymock.EasyMock.expect;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.seleritycorp.common.base.test.InjectingTestCase;
import com.seleritycorp.common.base.test.SettableStaticClock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CacheStats.class})
public class CacheMetricsTest extends InjectingTestCase {
  Cache<Integer, String> cache;
  SettableStaticClock clock;
  CacheStats stats;
  
  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    cache = createMock(Cache.class);
    clock = getClock();
    stats = PowerMock.createMock(CacheStats.class);
  }
  
  @Test
  public void testStatsCaching() {
    CacheStats stats2 = PowerMock.createMock(CacheStats.class);

    expect(cache.stats()).andReturn(stats);
    expect(cache.stats()).andReturn(stats2);

    expect(stats.hitCount()).andReturn(42L).anyTimes();

    expect(stats2.hitCount()).andReturn(4711L);

    replayAll();
    PowerMock.replay(stats, stats2);
    
    CacheMetrics metrics = createCacheMetrics();
    long actual1 = metrics.getHitCount();

    clock.advanceMillis(9500);
    
    long actual2 = metrics.getHitCount();

    clock.advanceMillis(501);
    
    long actual3 = metrics.getHitCount();
    
    verifyAll();
    PowerMock.verify(stats, stats2);
    
    assertThat(actual1).isEqualTo(42L);
    assertThat(actual2).isEqualTo(42L);
    assertThat(actual3).isEqualTo(4711L);
  }
  
  @Test
  public void testGetHitCount() {
    expect(cache.stats()).andReturn(stats);
    expect(stats.hitCount()).andReturn(42L);

    replayAll();
    PowerMock.replay(stats);
    
    CacheMetrics metrics = createCacheMetrics();
    long actual = metrics.getHitCount();

    verifyAll();
    PowerMock.verify(stats);
    
    assertThat(actual).isEqualTo(42L);
  }
  
  @Test
  public void testGetMissCount() {
    expect(cache.stats()).andReturn(stats);
    expect(stats.missCount()).andReturn(42L);

    replayAll();
    PowerMock.replay(stats);
    
    CacheMetrics metrics = createCacheMetrics();
    long actual = metrics.getMissCount();

    verifyAll();
    PowerMock.verify(stats);
    
    assertThat(actual).isEqualTo(42L);
  }

  @Test
  public void testGetLoadSuccessCount() {
    expect(cache.stats()).andReturn(stats);
    expect(stats.loadSuccessCount()).andReturn(42L);

    replayAll();
    PowerMock.replay(stats);
    
    CacheMetrics metrics = createCacheMetrics();
    long actual = metrics.getLoadSuccessCount();

    verifyAll();
    PowerMock.verify(stats);
    
    assertThat(actual).isEqualTo(42L);
  }
  
  @Test
  public void testGetLoadExceptionCount() {
    expect(cache.stats()).andReturn(stats);
    expect(stats.loadExceptionCount()).andReturn(42L);

    replayAll();
    PowerMock.replay(stats);
    
    CacheMetrics metrics = createCacheMetrics();
    long actual = metrics.getLoadExceptionCount();

    verifyAll();
    PowerMock.verify(stats);
    
    assertThat(actual).isEqualTo(42L);
  }
  
  @Test
  public void testGetTotalLoadTimeNanos() {
    expect(cache.stats()).andReturn(stats);
    expect(stats.totalLoadTime()).andReturn(42L);

    replayAll();
    PowerMock.replay(stats);
    
    CacheMetrics metrics = createCacheMetrics();
    long actual = metrics.getTotalLoadTimeNanos();

    verifyAll();
    PowerMock.verify(stats);
    
    assertThat(actual).isEqualTo(42L);
  }
  
  @Test
  public void testGetEvictionCount() {
    expect(cache.stats()).andReturn(stats);
    expect(stats.evictionCount()).andReturn(42L);

    replayAll();
    PowerMock.replay(stats);
    
    CacheMetrics metrics = createCacheMetrics();
    long actual = metrics.getEvictionCount();

    verifyAll();
    PowerMock.verify(stats);
    
    assertThat(actual).isEqualTo(42L);
  }
  
  private CacheMetrics createCacheMetrics() {
    return new CacheMetrics(cache, clock);
  }
}
