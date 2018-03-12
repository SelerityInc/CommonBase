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
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.newCapture;

import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.logging.CommonsLog;
import com.seleritycorp.common.base.logging.Formatter;
import com.seleritycorp.common.base.logging.Log;

public class CommonsLogLog4jIntegrationTest extends EasyMockSupport {
  /**
   * The offset into the stack trace for getLineNumberHelper to determine the line number.
   * 
   * <p>If all tests fail all of a sudden, Java's stack structure may have changed.
   * Try decreasing/increasing this number to re-adjust line number getting.
   */
  private static int STACK_TRACE_INDEX_FOR_LINE_NUMBER = 2;

  private Formatter formatter;

  @Before
  public void setUp() {
    formatter = createMock(Formatter.class);
  }

  @After
  public void tearDown() {
    // We messed with logging, so better reset it.
    org.apache.commons.logging.LogFactory.releaseAll();
  }

  @Test
  public void testLineNumberHelper() {
    // We explicitly test this class' line number helper, so we get a canary to detect if all
    // tests are failing simply because the stack structure changed.
    //
    // See {@code #STACK_TRACE_INDEX_FOR_LINE_NUMBER} above.

    assertThat(getLineNumber()).isEqualTo("67");
  }

  @Test
  public void testTrace1Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("Trace1Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    log.trace("foo"); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.TRACE);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testTrace2Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("Trace2Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    Exception e = new Exception("catch me");
    log.trace("foo", e); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.TRACE);
    assertThat(event.getThrowableInformation().getThrowable()).isEqualTo(e);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testDebug1Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("Debug1Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    log.debug("foo"); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.DEBUG);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testDebug2Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("Debug2Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    Exception e = new Exception("catch me");
    log.debug("foo", e); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getThrowableInformation().getThrowable()).isEqualTo(e);
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.DEBUG);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testInfo1Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("Info1Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    log.info("foo"); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.INFO);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testInfo2Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("Info2Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    Exception e = new Exception("catch me");
    log.info("foo", e); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getThrowableInformation().getThrowable()).isEqualTo(e);
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.INFO);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testWarn1Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("Warn1Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    log.warn("foo"); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.WARN);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testWarn2Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("Warn2Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    Exception e = new Exception("catch me");
    log.warn("foo", e); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getThrowableInformation().getThrowable()).isEqualTo(e);
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.WARN);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testError1Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("Error1Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    log.error("foo"); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.ERROR);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testError2Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("Error2Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    Exception e = new Exception("catch me");
    log.error("foo", e); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getThrowableInformation().getThrowable()).isEqualTo(e);
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.ERROR);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testFatal1Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("Fatal1Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    log.fatal("foo"); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.FATAL);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testFatal2Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("Fatal2Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    Exception e = new Exception("catch me");
    log.fatal("foo", e); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getThrowableInformation().getThrowable()).isEqualTo(e);
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.FATAL);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }

  @Test
  public void testLogFatal1Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("LogFatal1Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    log.log(Level.FATAL, "foo"); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.FATAL);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testLogFatal2Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("LogFatal2Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    Exception e = new Exception("catch me");
    log.log(Level.FATAL, "foo", e); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.FATAL);
    assertThat(event.getThrowableInformation().getThrowable()).isEqualTo(e);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }

  @Test
  public void testLogWarn1Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("LogWarn1Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    log.log(Level.WARN, "foo"); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.WARN);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testLogWarn2Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("LogWarn2Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    Exception e = new Exception("catch me");
    log.log(Level.WARN, "foo", e); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.WARN);
    assertThat(event.getThrowableInformation().getThrowable()).isEqualTo(e);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }

  @Test
  public void testLogError1Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("LogError1Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    log.log(Level.ERROR, "foo"); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.ERROR);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testLogError2Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("LogError2Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    Exception e = new Exception("catch me");
    log.log(Level.ERROR, "foo", e); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.ERROR);
    assertThat(event.getThrowableInformation().getThrowable()).isEqualTo(e);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }

  @Test
  public void testLogInfo1Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("LogInfo1Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    log.log(Level.INFO, "foo"); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.INFO);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testLogInfo2Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("LogInfo2Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    Exception e = new Exception("catch me");
    log.log(Level.INFO, "foo", e); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.INFO);
    assertThat(event.getThrowableInformation().getThrowable()).isEqualTo(e);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }

  @Test
  public void testLogDebug1Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("LogDebug1Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    log.log(Level.DEBUG, "foo"); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.DEBUG);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testLogDebug2Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("LogDebug2Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    Exception e = new Exception("catch me");
    log.log(Level.DEBUG, "foo", e); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.DEBUG);
    assertThat(event.getThrowableInformation().getThrowable()).isEqualTo(e);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }

  @Test
  public void testLogTrace1Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("LogTrace1Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    log.log(Level.TRACE, "foo"); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.TRACE);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testLogTrace2Param() {
    Appender appender = createMock(Appender.class);

    CommonsLog log = createCommonsLog("LogTrace2Param", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    Exception e = new Exception("catch me");
    log.log(Level.TRACE, "foo", e); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("foo");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.TRACE);
    assertThat(event.getThrowableInformation().getThrowable()).isEqualTo(e);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }

  @Test
  public void testStructuredInfo() {
    Appender appender = createMock(Appender.class);
    expect(formatter.formatStructuredLine("tagFoo", 42, "bar", "baz")).andReturn("quux");

    CommonsLog log = createCommonsLog("StructuredInfo", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    log.structuredInfo("tagFoo", 42, "bar", "baz"); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("quux");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.INFO);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  @Test
  public void testStructuredDebug() {
    Appender appender = createMock(Appender.class);
    expect(formatter.formatStructuredLine("tagFoo", 42, "bar", "baz")).andReturn("quux");

    CommonsLog log = createCommonsLog("StructuredDebug", appender);

    Capture<LoggingEvent> loggingEventCapture = newCapture();
    appender.doAppend(capture(loggingEventCapture));
    
    replayAll();

    log.structuredDebug("tagFoo", 42, "bar", "baz"); String lineNumber = getLineNumber();

    verifyAll();

    LoggingEvent event = loggingEventCapture.getValue();
    assertThat(event.getMessage()).isEqualTo("quux");
    assertThat(event.getLevel()).isEqualTo(org.apache.log4j.Level.DEBUG);
    assertThat(event.getLocationInformation().getLineNumber()).isEqualTo(lineNumber);
  }
  
  private CommonsLog createCommonsLog(String testName, Appender appender) {
    // Reset Commons Logging. Otherwise the setting won't be effective
    org.apache.commons.logging.LogFactory.releaseAll();

    // Force using Log4J
    System.setProperty(org.apache.commons.logging.impl.LogFactoryImpl.LOG_PROPERTY,
        "org.apache.commons.logging.impl.Log4JLogger");    

    // Get the commons logging wrapper 
    org.apache.commons.logging.Log wrappedLogCommons =
        org.apache.commons.logging.LogFactory.getLog(
            CommonsLogLog4jIntegrationTest.class.getName() + ":" + testName);

    // Install the appender
    assertThat(wrappedLogCommons).isInstanceOf(Log4JLogger.class);
    Log4JLogger log4jLogger = (Log4JLogger) wrappedLogCommons;
    Logger logger = log4jLogger.getLogger();
    logger.addAppender(appender);
    logger.setLevel(org.apache.log4j.Level.TRACE);

    // created the CommonsLog
    Log wrappedLog = LogFactory.getLog(CommonsLogLog4jIntegrationTest.class.getName() + ":" + testName);
    return new CommonsLog(wrappedLog, formatter);
  }
  
  /**
   * Gets the line number of the caller
   * 
   * @return Caller's line number. The number is returned as String to make testing against log
   *     events simpler.
   */
  private String getLineNumber() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace(); 
    return new Integer(stackTrace[STACK_TRACE_INDEX_FOR_LINE_NUMBER].getLineNumber()).toString();
  }
}
