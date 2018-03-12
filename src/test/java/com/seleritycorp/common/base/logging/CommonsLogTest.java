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

import org.apache.log4j.Logger;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.logging.CommonsLog;
import com.seleritycorp.common.base.logging.Formatter;
import com.seleritycorp.common.base.logging.Log;

public class CommonsLogTest extends EasyMockSupport {
  private Log wrappedLog;
  private Formatter formatter;
  private Throwable addedThrowable;

  @Before
  public void setUp() {
    wrappedLog = createMock(Log.class);
    expect(wrappedLog.getLog4jLogger()).andReturn(null);
    
    formatter = createMock(Formatter.class);
    
    addedThrowable = createMock(Throwable.class);
  }

  @Test
  public void testIsFatalEnabledTrue() {
    expect(wrappedLog.isFatalEnabled()).andReturn(true);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    boolean actual = log.isFatalEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsFatalEnabledFalse() {
    expect(wrappedLog.isFatalEnabled()).andReturn(false);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    boolean actual = log.isFatalEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testFatal1Param() {
    wrappedLog.fatal("foo, bar");

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.fatal("foo, bar");

    verifyAll();
  }

  @Test
  public void testFatal1ParamAddedThrowable() {
    wrappedLog.fatal("fooAdder", addedThrowable);

    replayAll();

    Log log = new CommonsLogThrowableAdder(wrappedLog, formatter);
    log.fatal("foo, bar");

    verifyAll();
  }

  @Test
  public void testFatal1ParamRemovedThrowable() {
    wrappedLog.fatal("fooRemover");

    replayAll();

    Log log = new CommonsLogThrowableRemover(wrappedLog, formatter);
    log.fatal("foo, bar");

    verifyAll();
  }

  @Test
  public void testFatal2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.fatal("foo, bar", t);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.fatal("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testFatal2ParamAddedThrowable() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.fatal("fooAdder", addedThrowable);

    replayAll();

    Log log = new CommonsLogThrowableAdder(wrappedLog, formatter);
    log.fatal("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testFatal2ParamRemovedThrowable() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.fatal("fooRemover");

    replayAll();

    Log log = new CommonsLogThrowableRemover(wrappedLog, formatter);
    log.fatal("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testIsErrorEnabledTrue() {
    expect(wrappedLog.isErrorEnabled()).andReturn(true);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    boolean actual = log.isErrorEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsErrorEnabledFalse() {
    expect(wrappedLog.isErrorEnabled()).andReturn(false);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    boolean actual = log.isErrorEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testError1Param() {
    wrappedLog.error("foo, bar");

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.error("foo, bar");

    verifyAll();
  }

  @Test
  public void testError1ParamAddedThrowable() {
    wrappedLog.error("fooAdder", addedThrowable);

    replayAll();

    Log log = new CommonsLogThrowableAdder(wrappedLog, formatter);
    log.error("foo, bar");

    verifyAll();
  }

  @Test
  public void testError1ParamRemovedThrowable() {
    wrappedLog.error("fooRemover");

    replayAll();

    Log log = new CommonsLogThrowableRemover(wrappedLog, formatter);
    log.error("foo, bar");

    verifyAll();
  }

  @Test
  public void testError2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.error("foo, bar", t);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.error("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testError2ParamAddedThrowable() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.error("fooAdder", addedThrowable);

    replayAll();

    Log log = new CommonsLogThrowableAdder(wrappedLog, formatter);
    log.error("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testError2ParamRemovedThrowable() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.error("fooRemover");

    replayAll();

    Log log = new CommonsLogThrowableRemover(wrappedLog, formatter);
    log.error("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testIsWarnEnabledTrue() {
    expect(wrappedLog.isWarnEnabled()).andReturn(true);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    boolean actual = log.isWarnEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsWarnEnabledFalse() {
    expect(wrappedLog.isWarnEnabled()).andReturn(false);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    boolean actual = log.isWarnEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testWarn1Param() {
    wrappedLog.warn("foo, bar");

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.warn("foo, bar");

    verifyAll();
  }

  @Test
  public void testWarn1ParamAddedThrowable() {
    wrappedLog.warn("fooAdder", addedThrowable);

    replayAll();

    Log log = new CommonsLogThrowableAdder(wrappedLog, formatter);
    log.warn("foo, bar");

    verifyAll();
  }

  @Test
  public void testWarn1ParamRemovedThrowable() {
    wrappedLog.warn("fooRemover");

    replayAll();

    Log log = new CommonsLogThrowableRemover(wrappedLog, formatter);
    log.warn("foo, bar");

    verifyAll();
  }

  @Test
  public void testWarn2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.warn("foo, bar", t);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.warn("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testWarn2ParamAddedThrowable() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.warn("fooAdder", addedThrowable);

    replayAll();

    Log log = new CommonsLogThrowableAdder(wrappedLog, formatter);
    log.warn("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testWarn2ParamRemovedThrowable() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.warn("fooRemover");

    replayAll();

    Log log = new CommonsLogThrowableRemover(wrappedLog, formatter);
    log.warn("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testIsInfoEnabledTrue() {
    expect(wrappedLog.isInfoEnabled()).andReturn(true);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    boolean actual = log.isInfoEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsInfoEnabledFalse() {
    expect(wrappedLog.isInfoEnabled()).andReturn(false);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    boolean actual = log.isInfoEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testInfo1Param() {
    wrappedLog.info("foo, bar");

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.info("foo, bar");

    verifyAll();
  }

  @Test
  public void testInfo1ParamAddedThrowable() {
    wrappedLog.info("fooAdder", addedThrowable);

    replayAll();

    Log log = new CommonsLogThrowableAdder(wrappedLog, formatter);
    log.info("foo, bar");

    verifyAll();
  }

  @Test
  public void testInfo1ParamRemovedThrowable() {
    wrappedLog.info("fooRemover");

    replayAll();

    Log log = new CommonsLogThrowableRemover(wrappedLog, formatter);
    log.info("foo, bar");

    verifyAll();
  }

  @Test
  public void testInfo2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.info("foo, bar", t);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.info("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testInfo2ParamAddedThrowable() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.info("fooAdder", addedThrowable);

    replayAll();

    Log log = new CommonsLogThrowableAdder(wrappedLog, formatter);
    log.info("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testInfo2ParamRemovedThrowable() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.info("fooRemover");

    replayAll();

    Log log = new CommonsLogThrowableRemover(wrappedLog, formatter);
    log.info("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testIsDebugEnabledTrue() {
    expect(wrappedLog.isDebugEnabled()).andReturn(true);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    boolean actual = log.isDebugEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsDebugEnabledFalse() {
    expect(wrappedLog.isDebugEnabled()).andReturn(false);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    boolean actual = log.isDebugEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testDebug1Param() {
    wrappedLog.debug("foo, bar");

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.debug("foo, bar");

    verifyAll();
  }

  @Test
  public void testDebug1ParamAddedThrowable() {
    wrappedLog.debug("fooAdder", addedThrowable);

    replayAll();

    Log log = new CommonsLogThrowableAdder(wrappedLog, formatter);
    log.debug("foo, bar");

    verifyAll();
  }

  @Test
  public void testDebug1ParamRemovedThrowable() {
    wrappedLog.debug("fooRemover");

    replayAll();

    Log log = new CommonsLogThrowableRemover(wrappedLog, formatter);
    log.debug("foo, bar");

    verifyAll();
  }

  @Test
  public void testDebug2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.debug("foo, bar", t);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.debug("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testDebug2ParamAddedThrowable() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.debug("fooAdder", addedThrowable);

    replayAll();

    Log log = new CommonsLogThrowableAdder(wrappedLog, formatter);
    log.debug("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testDebug2ParamRemovedThrowable() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.debug("fooRemover");

    replayAll();

    Log log = new CommonsLogThrowableRemover(wrappedLog, formatter);
    log.debug("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testIsTraceEnabledTrue() {
    expect(wrappedLog.isTraceEnabled()).andReturn(true);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    boolean actual = log.isTraceEnabled();

    verifyAll();

    assertThat(actual).isTrue();
  }

  @Test
  public void testIsTraceEnabledFalse() {
    expect(wrappedLog.isTraceEnabled()).andReturn(false);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    boolean actual = log.isTraceEnabled();

    verifyAll();

    assertThat(actual).isFalse();
  }

  @Test
  public void testTraceNull() {
    wrappedLog.trace(null);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.trace(null);

    verifyAll();
  }

  @Test
  public void testTrace1Param() {
    wrappedLog.trace("foo, bar");

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.trace("foo, bar");

    verifyAll();
  }

  @Test
  public void testTrace1ParamAddedThrowable() {
    wrappedLog.trace("fooAdder", addedThrowable);

    replayAll();

    Log log = new CommonsLogThrowableAdder(wrappedLog, formatter);
    log.trace("foo, bar");

    verifyAll();
  }

  @Test
  public void testTrace1ParamRemovedThrowable() {
    wrappedLog.trace("fooRemover");

    replayAll();

    Log log = new CommonsLogThrowableRemover(wrappedLog, formatter);
    log.trace("foo, bar");

    verifyAll();
  }

  @Test
  public void testTrace2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.trace("foo, bar", t);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.trace("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testTrace2ParamAddedThrowable() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.trace("fooAdder", addedThrowable);

    replayAll();

    Log log = new CommonsLogThrowableAdder(wrappedLog, formatter);
    log.trace("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testTrace2ParamRemovedThrowable() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.trace("fooRemover");

    replayAll();

    Log log = new CommonsLogThrowableRemover(wrappedLog, formatter);
    log.trace("foo, bar", t);

    verifyAll();
  }

  @Test
  public void testLogFatal1Param() {
    wrappedLog.fatal("foo, bar");

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.log(Level.FATAL, "foo, bar");

    verifyAll();
  }

  @Test
  public void testLogFatal2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.fatal("foo, bar", t);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.log(Level.FATAL, "foo, bar", t);

    verifyAll();
  }

  @Test
  public void testLogError1Param() {
    wrappedLog.error("foo, bar");

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.log(Level.ERROR, "foo, bar");

    verifyAll();
  }

  @Test
  public void testLogError2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.error("foo, bar", t);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.log(Level.ERROR, "foo, bar", t);

    verifyAll();
  }

  @Test
  public void testLogWarn1Param() {
    wrappedLog.warn("foo, bar");

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.log(Level.WARN, "foo, bar");

    verifyAll();
  }

  @Test
  public void testLogWarn2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.warn("foo, bar", t);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.log(Level.WARN, "foo, bar", t);

    verifyAll();
  }

  @Test
  public void testLogInfo1Param() {
    wrappedLog.info("foo, bar");

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.log(Level.INFO, "foo, bar");

    verifyAll();
  }

  @Test
  public void testLogInfo2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.info("foo, bar", t);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.log(Level.INFO, "foo, bar", t);

    verifyAll();
  }

  @Test
  public void testLogDebug1Param() {
    wrappedLog.debug("foo, bar");

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.log(Level.DEBUG, "foo, bar");

    verifyAll();
  }

  @Test
  public void testLogDebug2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.debug("foo, bar", t);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.log(Level.DEBUG, "foo, bar", t);

    verifyAll();
  }

  @Test
  public void testLogTrace1Param() {
    wrappedLog.trace("foo, bar");

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.log(Level.TRACE, "foo, bar");

    verifyAll();
  }

  @Test
  public void testLogTrace2Param() {
    Throwable t = createMock(Throwable.class);
    wrappedLog.trace("foo, bar", t);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.log(Level.TRACE, "foo, bar", t);

    verifyAll();
  }

  @Test
  public void testLogOff1Param() {
    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.log(Level.OFF, "foo, bar");

    verifyAll();
  }

  @Test
  public void testLogOff2Param() {
    Throwable t = createMock(Throwable.class);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.log(Level.OFF, "foo, bar", t);

    verifyAll();
  }


  @Test
  public void testStructuredInfo() {
    wrappedLog.info("baz");
    expect(formatter.formatStructuredLine("foo", 42, "bar")).andReturn("baz");

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    log.structuredInfo("foo", 42, "bar");

    verifyAll();
  }

  @Test
  public void testStructuredInfoChanger() {
    wrappedLog.info("baz");

    replayAll();

    Log log = new CommonsLogStructuredChanger(wrappedLog, formatter);
    log.structuredInfo("foo", 42, "bar");

    verifyAll();
  }

  @Test
  public void testGetLog4jLoggerWrappedNull() {
    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    assertThat(log.getLog4jLogger()).isNull();

    verifyAll();
  }

  @Test
  public void testGetLog4jLoggerCommons() {
    resetAll();

    org.apache.commons.logging.Log commonsLog = createMock(org.apache.commons.logging.Log.class);

    replayAll();

    Log log = new CommonsLog(commonsLog, formatter);
    assertThat(log.getLog4jLogger()).isNull();

    verifyAll();
  }

  @Test
  public void testGetLog4jLoggerWrappedNonNull() {
    Logger logger = createMock(Logger.class);

    resetAll();
    
    expect(wrappedLog.getLog4jLogger()).andReturn(logger);

    replayAll();

    Log log = new CommonsLog(wrappedLog, formatter);
    assertThat(log.getLog4jLogger()).isSameAs(logger);

    verifyAll();
  }
  
  class CommonsLogThrowableRemover extends CommonsLog {
    CommonsLogThrowableRemover(org.apache.commons.logging.Log wrappedLog, Formatter formatter) {
      super(wrappedLog, formatter);
    }
    
    @Override
    public Event processEvent(Event event) {
      event.setMessage("fooRemover");
      event.unsetThrowable();
      return event;
    }
  }

  class CommonsLogThrowableAdder extends CommonsLog {
    CommonsLogThrowableAdder(org.apache.commons.logging.Log wrappedLog, Formatter formatter) {
      super(wrappedLog, formatter);
    }
    
    @Override
    public Event processEvent(Event event) {
      event.setMessage("fooAdder");
      event.setThrowable(addedThrowable);
      return event;
    }
  }

  class CommonsLogStructuredChanger extends CommonsLog {
    CommonsLogStructuredChanger(org.apache.commons.logging.Log wrappedLog, Formatter formatter) {
      super(wrappedLog, formatter);
    }
    
    @Override
    protected String processStructuredData(String tag, int version, Object... objs) {
      return "baz"; 
    }
  }
}
