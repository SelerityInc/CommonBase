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
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.logging.FlatLog;
import com.seleritycorp.common.base.logging.Log;

public class FlatLogTest extends EasyMockSupport {
  private Log backendLog;
  private Formatter formatter;

  @Before
  public void setUp() {
    backendLog = createMock(Log.class);
    expect(backendLog.getLog4jLogger()).andReturn(null);

    formatter = createMock(Formatter.class);
  }

  @Test
  public void testIsFatalEnabledTrue() {
    expect(backendLog.isFatalEnabled()).andReturn(true);

    replayAll();

    Log log = createFlatLogger();
    boolean actual = log.isFatalEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsFatalEnabledFalse() {
    expect(backendLog.isFatalEnabled()).andReturn(false);

    replayAll();

    Log log = createFlatLogger();
    boolean actual = log.isFatalEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testFatal1ParamSingleLine() {
    backendLog.fatal("foo");

    replayAll();

    Log log = createFlatLogger();
    log.fatal("foo");

    verifyAll();
  }

  @Test
  public void testFatal1ParamMultiLine() {
    backendLog.fatal("foo\\nbar\\nbaz");

    replayAll();

    Log log = createFlatLogger();
    log.fatal("foo\nbar\nbaz");

    verifyAll();
  }

  @Test
  public void testFatal2ParamSingleLine() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.fatal(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.fatal("foo", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("2ParamSingleLine");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testFatal2ParamMultiLine() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.fatal(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.fatal("foo\nbar\nbaz", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\nbar\\nbaz\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("2ParamMultiLine");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testFatalFormatEnabledThrowable() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.fatal(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.fatal("foo\n%s", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\n%s\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("FormatEnabledThrowable");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testIsErrorEnabledTrue() {
    expect(backendLog.isErrorEnabled()).andReturn(true);

    replayAll();

    Log log = createFlatLogger();
    boolean actual = log.isErrorEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsErrorEnabledFalse() {
    expect(backendLog.isErrorEnabled()).andReturn(false);

    replayAll();

    Log log = createFlatLogger();
    boolean actual = log.isErrorEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testError1ParamSingleLine() {
    backendLog.error("foo");

    replayAll();

    Log log = createFlatLogger();
    log.error("foo");

    verifyAll();
  }

  @Test
  public void testError1ParamMultiLine() {
    backendLog.error("foo\\nbar\\nbaz");

    replayAll();

    Log log = createFlatLogger();
    log.error("foo\nbar\nbaz");

    verifyAll();
  }

  @Test
  public void testError2ParamSingleLine() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.error(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.error("foo", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("2ParamSingleLine");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testError2ParamMultiLine() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.error(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.error("foo\nbar\nbaz", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\nbar\\nbaz\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("2ParamMultiLine");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testErrorFormatEnabledThrowable() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.error(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.error("foo\n%s", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\n%s\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("FormatEnabledThrowable");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testIsWarnEnabledTrue() {
    expect(backendLog.isWarnEnabled()).andReturn(true);

    replayAll();

    Log log = createFlatLogger();
    boolean actual = log.isWarnEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsWarnEnabledFalse() {
    expect(backendLog.isWarnEnabled()).andReturn(false);

    replayAll();

    Log log = createFlatLogger();
    boolean actual = log.isWarnEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testWarn1ParamSingleLine() {
    backendLog.warn("foo");

    replayAll();

    Log log = createFlatLogger();
    log.warn("foo");

    verifyAll();
  }

  @Test
  public void testWarn1ParamMultiLine() {
    backendLog.warn("foo\\nbar\\nbaz");

    replayAll();

    Log log = createFlatLogger();
    log.warn("foo\nbar\nbaz");

    verifyAll();
  }

  @Test
  public void testWarn2ParamSingleLine() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.warn(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.warn("foo", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("2ParamSingleLine");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testWarn2ParamMultiLine() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.warn(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.warn("foo\nbar\nbaz", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\nbar\\nbaz\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("2ParamMultiLine");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testWarnFormatEnabledThrowable() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.warn(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.warn("foo\n%s", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\n%s\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("FormatEnabledThrowable");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testIsInfoEnabledTrue() {
    expect(backendLog.isInfoEnabled()).andReturn(true);

    replayAll();

    Log log = createFlatLogger();
    boolean actual = log.isInfoEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsInfoEnabledFalse() {
    expect(backendLog.isInfoEnabled()).andReturn(false);

    replayAll();

    Log log = createFlatLogger();
    boolean actual = log.isInfoEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testInfo1ParamSingleLine() {
    backendLog.info("foo");

    replayAll();

    Log log = createFlatLogger();
    log.info("foo");

    verifyAll();
  }

  @Test
  public void testInfo1ParamMultiLine() {
    backendLog.info("foo\\nbar\\nbaz");

    replayAll();

    Log log = createFlatLogger();
    log.info("foo\nbar\nbaz");

    verifyAll();
  }

  @Test
  public void testInfo2ParamSingleLine() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.info(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.info("foo", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("2ParamSingleLine");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testInfo2ParamMultiLine() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.info(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.info("foo\nbar\nbaz", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\nbar\\nbaz\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("2ParamMultiLine");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testInfoFormatEnabledThrowable() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.info(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.info("foo\n%s", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\n%s\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("FormatEnabledThrowable");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testIsDebugEnabledTrue() {
    expect(backendLog.isDebugEnabled()).andReturn(true);

    replayAll();

    Log log = createFlatLogger();
    boolean actual = log.isDebugEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsDebugEnabledFalse() {
    expect(backendLog.isDebugEnabled()).andReturn(false);

    replayAll();

    Log log = createFlatLogger();
    boolean actual = log.isDebugEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testDebug1ParamSingleLine() {
    backendLog.debug("foo");

    replayAll();

    Log log = createFlatLogger();
    log.debug("foo");

    verifyAll();
  }

  @Test
  public void testDebug1ParamMultiLine() {
    backendLog.debug("foo\\nbar\\nbaz");

    replayAll();

    Log log = createFlatLogger();
    log.debug("foo\nbar\nbaz");

    verifyAll();
  }

  @Test
  public void testDebug2ParamSingleLine() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.debug(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.debug("foo", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("2ParamSingleLine");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testDebug2ParamMultiLine() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.debug(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.debug("foo\nbar\nbaz", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\nbar\\nbaz\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("2ParamMultiLine");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testDebugFormatEnabledThrowable() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.debug(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.debug("foo\n%s", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\n%s\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("FormatEnabledThrowable");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testIsTraceEnabledTrue() {
    expect(backendLog.isTraceEnabled()).andReturn(true);

    replayAll();

    Log log = createFlatLogger();
    boolean actual = log.isTraceEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsTraceEnabledFalse() {
    expect(backendLog.isTraceEnabled()).andReturn(false);

    replayAll();

    Log log = createFlatLogger();
    boolean actual = log.isTraceEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testTrace1ParamSingleLine() {
    backendLog.trace("foo");

    replayAll();

    Log log = createFlatLogger();
    log.trace("foo");

    verifyAll();
  }

  @Test
  public void testTrace1ParamMultiLine() {
    backendLog.trace("foo\\nbar\\nbaz");

    replayAll();

    Log log = createFlatLogger();
    log.trace("foo\nbar\nbaz");

    verifyAll();
  }

  @Test
  public void testTrace2ParamSingleLine() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.trace(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.trace("foo", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("2ParamSingleLine");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testTrace2ParamMultiLine() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.trace(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.trace("foo\nbar\nbaz", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\nbar\\nbaz\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("2ParamMultiLine");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testTraceFormatEnabledThrowable() {
    Throwable t = new RuntimeException();
    Capture<String> msgCap = newCapture();
    backendLog.trace(capture(msgCap));

    replayAll();

    Log log = createFlatLogger();
    log.trace("foo\n%s", t);

    verifyAll();

    String actual = msgCap.getValue();
    assertThat(actual).startsWith("foo\\n%s\\n");
    assertThat(actual).contains("RuntimeException");
    assertThat(actual).contains("FormatEnabledThrowable");
    assertThat(actual).doesNotContain("\n");
  }

  @Test
  public void testStructuredInfo() {
    expect(formatter.formatStructuredLine("foo", 42, "bar")).andReturn("quux");
    backendLog.info("quux");

    replayAll();

    Log log = createFlatLogger();
    log.structuredInfo("foo", 42, "bar");

    verifyAll();
  }

  private FlatLog createFlatLogger() {
    return new FlatLog(backendLog, formatter);
  }
}
