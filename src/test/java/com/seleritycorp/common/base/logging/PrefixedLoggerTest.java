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

package com.seleritycorp.common.base.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.expect;

import org.apache.commons.logging.Log;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.logging.PrefixedLogger;

public class PrefixedLoggerTest extends EasyMockSupport {
  Log parentLog;
  Formatter formatter;

  @Before
  public void setUp() {
    parentLog = createMock(Log.class);
    formatter = createMock(Formatter.class);
  }

  @Test
  public void testIsDebugEnabledTrue() {
    expect(parentLog.isDebugEnabled()).andReturn(true);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    boolean actual = log.isDebugEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsDebugEnabledFalse() {
    expect(parentLog.isDebugEnabled()).andReturn(false);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    boolean actual = log.isDebugEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testIsErrorEnabledTrue() {
    expect(parentLog.isErrorEnabled()).andReturn(true);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    boolean actual = log.isErrorEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsErrorEnabledFalse() {
    expect(parentLog.isErrorEnabled()).andReturn(false);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    boolean actual = log.isErrorEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testIsFatalEnabledTrue() {
    expect(parentLog.isFatalEnabled()).andReturn(true);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    boolean actual = log.isFatalEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsFatalEnabledFalse() {
    expect(parentLog.isFatalEnabled()).andReturn(false);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    boolean actual = log.isFatalEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testIsInfoEnabledTrue() {
    expect(parentLog.isInfoEnabled()).andReturn(true);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    boolean actual = log.isInfoEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsInfoEnabledFalse() {
    expect(parentLog.isInfoEnabled()).andReturn(false);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    boolean actual = log.isInfoEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testIsTraceEnabledTrue() {
    expect(parentLog.isTraceEnabled()).andReturn(true);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    boolean actual = log.isTraceEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsTraceEnabledFalse() {
    expect(parentLog.isTraceEnabled()).andReturn(false);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    boolean actual = log.isTraceEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }


  @Test
  public void testIsWarnEnabledTrue() {
    expect(parentLog.isWarnEnabled()).andReturn(true);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    boolean actual = log.isWarnEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsWarnEnabledFalse() {
    expect(parentLog.isWarnEnabled()).andReturn(false);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    boolean actual = log.isWarnEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testDebug() {
    parentLog.debug("(foo) bar");

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    log.debug("bar");

    verifyAll();
  }

  @Test
  public void testDebugThrowable() {
    Throwable t = createMock(Throwable.class);
    parentLog.debug("(foo) bar", t);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    log.debug("bar", t);

    verifyAll();
  }

  @Test
  public void testError() {
    parentLog.error("(foo) bar");

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    log.error("bar");

    verifyAll();
  }

  @Test
  public void testErrorThrowable() {
    Throwable t = createMock(Throwable.class);
    parentLog.error("(foo) bar", t);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    log.error("bar", t);

    verifyAll();
  }

  @Test
  public void testFatal() {
    parentLog.fatal("(foo) bar");

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    log.fatal("bar");

    verifyAll();
  }

  @Test
  public void testFatalThrowable() {
    Throwable t = createMock(Throwable.class);
    parentLog.fatal("(foo) bar", t);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    log.fatal("bar", t);

    verifyAll();
  }

  @Test
  public void testInfo() {
    parentLog.info("(foo) bar");

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    log.info("bar");

    verifyAll();
  }

  @Test
  public void testInfoThrowable() {
    Throwable t = createMock(Throwable.class);
    parentLog.info("(foo) bar", t);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    log.info("bar", t);

    verifyAll();
  }

  @Test
  public void testTrace() {
    parentLog.trace("(foo) bar");

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    log.trace("bar");

    verifyAll();
  }

  @Test
  public void testTraceThrowable() {
    Throwable t = createMock(Throwable.class);
    parentLog.trace("(foo) bar", t);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    log.trace("bar", t);

    verifyAll();
  }

  @Test
  public void testWarn() {
    parentLog.warn("(foo) bar");

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    log.warn("bar");

    verifyAll();
  }

  @Test
  public void testWarnThrowable() {
    Throwable t = createMock(Throwable.class);
    parentLog.warn("(foo) bar", t);

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    log.warn("bar", t);

    verifyAll();
  }

  @Test
  public void testStructuredInfo() {
    parentLog.info("quux");
    expect(formatter.formatStructuredLine("bar", 42, "prefix", "foo", "baz")).andReturn("quux");

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    log.structuredInfo("bar", 42, "baz");

    verifyAll();
  }

  @Test
  public void testStructuredInfoNull() {
    parentLog.info("baz");
    expect(formatter.formatStructuredLine("bar", 42, "prefix", "foo")).andReturn("baz");

    replayAll();

    PrefixedLogger log = createPrefixedLogger();
    log.structuredInfo("bar", 42);

    verifyAll();
  }

  private PrefixedLogger createPrefixedLogger() {
    return new PrefixedLogger("foo", parentLog, formatter);
  }
}
