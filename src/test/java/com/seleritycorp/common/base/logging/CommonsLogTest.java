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

package com.seleritycorp.common.base.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.expect;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.logging.CommonsLog;
import com.seleritycorp.common.base.logging.Formatter;
import com.seleritycorp.common.base.logging.Log;

public class CommonsLogTest extends EasyMockSupport {
  private Log wrappedLog;
  private Formatter formatter;
  private Log log;

  @Before
  public void setUp() {
    wrappedLog = createMock(Log.class);
    formatter = createMock(Formatter.class);
    log = new CommonsLog(wrappedLog, formatter);
  }

  @Test
  public void testIsFatalEnabledTrue() {
    expect(wrappedLog.isFatalEnabled()).andReturn(true);

    replayAll();

    boolean actual = log.isFatalEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsFatalEnabledFalse() {
    expect(wrappedLog.isFatalEnabled()).andReturn(false);

    replayAll();

    boolean actual = log.isFatalEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testFatal1Param() {
    wrappedLog.fatal("foo, bar");

    replayAll();

    log.fatal("foo, bar");

    verifyAll();
  }

  @Test
  public void testFatal2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.fatal("foo, bar", t);

    replayAll();

    log.fatal("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testIsErrorEnabledTrue() {
    expect(wrappedLog.isErrorEnabled()).andReturn(true);

    replayAll();

    boolean actual = log.isErrorEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsErrorEnabledFalse() {
    expect(wrappedLog.isErrorEnabled()).andReturn(false);

    replayAll();

    boolean actual = log.isErrorEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testError1Param() {
    wrappedLog.error("foo, bar");

    replayAll();

    log.error("foo, bar");

    verifyAll();
  }

  @Test
  public void testError2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.error("foo, bar", t);

    replayAll();

    log.error("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testIsWarnEnabledTrue() {
    expect(wrappedLog.isWarnEnabled()).andReturn(true);

    replayAll();

    boolean actual = log.isWarnEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsWarnEnabledFalse() {
    expect(wrappedLog.isWarnEnabled()).andReturn(false);

    replayAll();

    boolean actual = log.isWarnEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testWarn1Param() {
    wrappedLog.warn("foo, bar");

    replayAll();

    log.warn("foo, bar");

    verifyAll();
  }

  @Test
  public void testWarn2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.warn("foo, bar", t);

    replayAll();

    log.warn("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testIsInfoEnabledTrue() {
    expect(wrappedLog.isInfoEnabled()).andReturn(true);

    replayAll();

    boolean actual = log.isInfoEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsInfoEnabledFalse() {
    expect(wrappedLog.isInfoEnabled()).andReturn(false);

    replayAll();

    boolean actual = log.isInfoEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testInfo1Param() {
    wrappedLog.info("foo, bar");

    replayAll();

    log.info("foo, bar");

    verifyAll();
  }

  @Test
  public void testInfo2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.info("foo, bar", t);

    replayAll();

    log.info("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testIsDebugEnabledTrue() {
    expect(wrappedLog.isDebugEnabled()).andReturn(true);

    replayAll();

    boolean actual = log.isDebugEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsDebugEnabledFalse() {
    expect(wrappedLog.isDebugEnabled()).andReturn(false);

    replayAll();

    boolean actual = log.isDebugEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testDebug1Param() {
    wrappedLog.debug("foo, bar");

    replayAll();

    log.debug("foo, bar");

    verifyAll();
  }

  @Test
  public void testDebug2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.debug("foo, bar", t);

    replayAll();

    log.debug("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testIsTraceEnabledTrue() {
    expect(wrappedLog.isTraceEnabled()).andReturn(true);

    replayAll();

    boolean actual = log.isTraceEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsTraceEnabledFalse() {
    expect(wrappedLog.isTraceEnabled()).andReturn(false);

    replayAll();

    boolean actual = log.isTraceEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testTrace1Param() {
    wrappedLog.trace("foo, bar");

    replayAll();

    log.trace("foo, bar");

    verifyAll();
  }

  @Test
  public void testTrace2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.trace("foo, bar", t);

    replayAll();

    log.trace("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testStructuredInfo() {
    wrappedLog.info("baz");
    expect(formatter.formatStructuredLine("foo", 42, "bar")).andReturn("baz");

    replayAll();

    log.structuredInfo("foo", 42, "bar");

    verifyAll();
  }
}
