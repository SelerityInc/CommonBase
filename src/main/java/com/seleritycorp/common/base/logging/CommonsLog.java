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

import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Wrapper for Apache Common Loggging logs to become Selerity Log.
 */
public class CommonsLog implements Log {
  /**
   * Fully qualified class name.
   * 
   * <p>This value is used as logger name for Log4j
   */
  private static final String fqcn = CommonsLog.class.getName();

  /**
   * The wrapped Log instance.
   */
  private final org.apache.commons.logging.Log wrappedLog;

  /**
   * wrappedLog as Log4j Logger, if it is an instance of it.
   * 
   * <p>This variable is a shortcut to again and again doing the instanceof checking. 
   * 
   */
  private final Logger wrappedLog4j;

  /**
   * The Formatter for log entries.
   */
  private final Formatter formatter;
  
  /**
   * Wraps a Commons Logging log to becomen a Selerity Log
   * 
   * @param wrappedLog The Commons Logging Log instance to wrap.
   * @param formatter Instance of Formating helper 
   */
  CommonsLog(org.apache.commons.logging.Log wrappedLog, Formatter formatter) {
    this.wrappedLog = wrappedLog;
    this.formatter = formatter;
    if (wrappedLog instanceof Log4JLogger) {
      this.wrappedLog4j = ((Log4JLogger) wrappedLog).getLogger();
    } else if (wrappedLog instanceof Log) {
      this.wrappedLog4j = ((Log) wrappedLog).getLog4jLogger();
    } else {
      this.wrappedLog4j = null;
    }
  }

  /**
   * Process the message before doing the actual logging
   * 
   * <p>Child classes can use this method to modify the logged messages in a single place.
   * 
   * <p>If child classes override {@link CommonsLog#processEvent(Event)}, there is no
   * guarantee that this method is called.
   * 
   * <p>Structured data does not pass through this method, but is processed in
   * {@link #processStructuredData(String, int, Object...)}.
   * 
   * @param message The message to process
   * @return The message to log
   */
  protected String processMessage(String message) {
    return (message == null) ? null : message.toString();
  }

  /**
   * Process the event capsule before doing the actual logging
   * 
   * <p>Child classes can use this method to modify the logged event in a single place.
   * 
   * <p>Structured data does not pass through this method, but is processed in
   * {@link #processStructuredData(String, int, Object...)}.
   * 
   * @param event The event to log. This event will get mangled. 
   * @return The mangled event that should get logged.
   */
  protected Event processEvent(Event event) {
    event.setMessage(processMessage(event.getMessage()));
    return event;
  }

  /**
   * Process structured data for logging
   * 
   * <p>Child classes can use this method to modify the logged event in a single place.
   * 
   * <p>The following replacements are expected by the returned string:
   * <ol>
   * <li>Backslashes get replaced by double backslashes.</li>
   * <li>Carriage-Returns get replaced by backslash, followed by r.</li>
   * <li>Linebreaks get replaced by backslash, followed by n.</li>
   * <li>Slashes get replaced by backslash, followed by a pipe.</li>
   * </ol>
   * 
   * @param tag The tag to store the line at
   * @param version The version of the tag
   * @param objs The name and objects to log. objs is expected to hold an
   *        even number of elements, with objs[2*n] holding the name for the
   *        object at objs[2*n+1]. So an example of a call would be
   *        {@code structuredInfo("foo", 42, "bar", bar, "bar-size", bar.size());}
   * @return string representation of the parameters.
   */
  protected String processStructuredData(String tag, int version, Object... objs) {
    return formatter.formatStructuredLine(tag, version, objs); 
  }

  @Override
  public boolean isDebugEnabled() {
    return wrappedLog.isDebugEnabled();
  }

  @Override
  public boolean isErrorEnabled() {
    return wrappedLog.isErrorEnabled();
  }

  @Override
  public boolean isFatalEnabled() {
    return wrappedLog.isFatalEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return wrappedLog.isInfoEnabled();
  }

  @Override
  public boolean isTraceEnabled() {
    return wrappedLog.isTraceEnabled();
  }

  @Override
  public boolean isWarnEnabled() {
    return wrappedLog.isWarnEnabled();
  }

  @Override
  public void trace(Object message) {
    Event event = new Event(message);
    event = processEvent(event);
    if (wrappedLog4j != null) {
      // More specific wrappers get the line numbers wrong, so we resort to the most generic one :-/
      wrappedLog4j.log(fqcn, Level.TRACE, event.getMessage(), event.getThrowable());
    } else {
      if (event.hasThrowable()) {
        wrappedLog.trace(event.getMessage(), event.getThrowable());
      } else {
        wrappedLog.trace(event.getMessage());
      }
    }
  }

  @Override
  public void trace(Object message, Throwable throwable) {
    Event event = new Event(message, throwable);
    event = processEvent(event);
    if (wrappedLog4j != null) {
      // More specific wrappers get the line numbers wrong, so we resort to the most generic one :-/
      wrappedLog4j.log(fqcn, Level.TRACE, event.getMessage(), event.getThrowable());
    } else {
      if (event.hasThrowable()) {
        wrappedLog.trace(event.getMessage(), event.getThrowable());
      } else {
        wrappedLog.trace(event.getMessage());
      }
    }
  }

  @Override
  public void debug(Object message) {
    Event event = new Event(message);
    event = processEvent(event);
    if (wrappedLog4j != null) {
      // More specific wrappers get the line numbers wrong, so we resort to the most generic one :-/
      wrappedLog4j.log(fqcn, Level.DEBUG, event.getMessage(), event.getThrowable());
    } else {
      if (event.hasThrowable()) {
        wrappedLog.debug(event.getMessage(), event.getThrowable());
      } else {
        wrappedLog.debug(event.getMessage());
      }
    }
  }

  @Override
  public void debug(Object message, Throwable throwable) {
    Event event = new Event(message, throwable);
    event = processEvent(event);
    if (wrappedLog4j != null) {
      // More specific wrappers get the line numbers wrong, so we resort to the most generic one :-/
      wrappedLog4j.log(fqcn, Level.DEBUG, event.getMessage(), event.getThrowable());
    } else {
      if (event.hasThrowable()) {
        wrappedLog.debug(event.getMessage(), event.getThrowable());
      } else {
        wrappedLog.debug(event.getMessage());
      }
    }
  }

  @Override
  public void info(Object message) {
    Event event = new Event(message);
    event = processEvent(event);
    if (wrappedLog4j != null) {
      // More specific wrappers get the line numbers wrong, so we resort to the most generic one :-/
      wrappedLog4j.log(fqcn, Level.INFO, event.getMessage(), event.getThrowable());
    } else {
      if (event.hasThrowable()) {
        wrappedLog.info(event.getMessage(), event.getThrowable());
      } else {
        wrappedLog.info(event.getMessage());
      }
    }
  }

  @Override
  public void info(Object message, Throwable throwable) {
    Event event = new Event(message, throwable);
    event = processEvent(event);
    if (wrappedLog4j != null) {
      // More specific wrappers get the line numbers wrong, so we resort to the most generic one :-/
      wrappedLog4j.log(fqcn, Level.INFO, event.getMessage(), event.getThrowable());
    } else {
      if (event.hasThrowable()) {
        wrappedLog.info(event.getMessage(), event.getThrowable());
      } else {
        wrappedLog.info(event.getMessage());
      }
    }
  }

  @Override
  public void warn(Object message) {
    Event event = new Event(message);
    event = processEvent(event);
    if (wrappedLog4j != null) {
      // More specific wrappers get the line numbers wrong, so we resort to the most generic one :-/
      wrappedLog4j.log(fqcn, Level.WARN, event.getMessage(), event.getThrowable());
    } else {
      if (event.hasThrowable()) {
        wrappedLog.warn(event.getMessage(), event.getThrowable());
      } else {
        wrappedLog.warn(event.getMessage());
      }
    }
  }

  @Override
  public void warn(Object message, Throwable throwable) {
    Event event = new Event(message, throwable);
    event = processEvent(event);
    if (wrappedLog4j != null) {
      // More specific wrappers get the line numbers wrong, so we resort to the most generic one :-/
      wrappedLog4j.log(fqcn, Level.WARN, event.getMessage(), event.getThrowable());
    } else {
      if (event.hasThrowable()) {
        wrappedLog.warn(event.getMessage(), event.getThrowable());
      } else {
        wrappedLog.warn(event.getMessage());
      }
    }
  }

  @Override
  public void error(Object message) {
    Event event = new Event(message);
    event = processEvent(event);
    if (wrappedLog4j != null) {
      // More specific wrappers get the line numbers wrong, so we resort to the most generic one :-/
      wrappedLog4j.log(fqcn, Level.ERROR, event.getMessage(), event.getThrowable());
    } else {
      if (event.hasThrowable()) {
        wrappedLog.error(event.getMessage(), event.getThrowable());
      } else {
        wrappedLog.error(event.getMessage());
      }
    }
  }

  @Override
  public void error(Object message, Throwable throwable) {
    Event event = new Event(message, throwable);
    event = processEvent(event);
    if (wrappedLog4j != null) {
      // More specific wrappers get the line numbers wrong, so we resort to the most generic one :-/
      wrappedLog4j.log(fqcn, Level.ERROR, event.getMessage(), event.getThrowable());
    } else {
      if (event.hasThrowable()) {
        wrappedLog.error(event.getMessage(), event.getThrowable());
      } else {
        wrappedLog.error(event.getMessage());
      }
    }
  }

  @Override
  public void fatal(Object message) {
    Event event = new Event(message);
    event = processEvent(event);
    if (wrappedLog4j != null) {
      // More specific wrappers get the line numbers wrong, so we resort to the most generic one :-/
      wrappedLog4j.log(fqcn, Level.FATAL, event.getMessage(), event.getThrowable());
    } else {
      if (event.hasThrowable()) {
        wrappedLog.fatal(event.getMessage(), event.getThrowable());
      } else {
        wrappedLog.fatal(event.getMessage());
      }
    }
  }

  @Override
  public void fatal(Object message, Throwable throwable) {
    Event event = new Event(message, throwable);
    event = processEvent(event);
    if (wrappedLog4j != null) {
      // More specific wrappers get the line numbers wrong, so we resort to the most generic one :-/
      wrappedLog4j.log(fqcn, Level.FATAL, event.getMessage(), event.getThrowable());
    } else {
      if (event.hasThrowable()) {
        wrappedLog.fatal(event.getMessage(), event.getThrowable());
      } else {
        wrappedLog.fatal(event.getMessage());
      }
    }
  }

  @Override
  public void structuredInfo(String tag, int version, Object... objs) {
    String message = processStructuredData(tag, version, objs);
    if (wrappedLog4j != null) {
      // More specific wrappers get the line numbers wrong, so we resort to the most generic one :-/
      wrappedLog4j.log(fqcn, Level.INFO, message, null);
    } else {
      wrappedLog.info(message);
    }
  }

  @Override
  public Logger getLog4jLogger() {
    return wrappedLog4j;
  }

  public static class Event {
    private String message;
    private boolean hasThrowable;
    private Throwable throwable;

    /**
     * Creates an Event for a given message
     * 
     * <p>The Event's throwable is unset by this contructor.
     *
     * @param message The message to set for the event
     */
    public Event(Object message) {
      setMessage(message);
      unsetThrowable();
    }

    /**
     * Creates an Event for a given message and throwable.
     * 
     * @param message The message to set for the event
     * @param throwable The throwable to set for the event.
     */
    public Event(Object message, Throwable throwable) {
      setMessage(message);
      setThrowable(throwable);
    }

    /**
     * Gets the Event's message.
     * 
     * @return the Event's message
     */
    public String getMessage() {
      return message;
    }

    /**
     * Sets the Event's message.
     *
     * @param message The message to set for the Event
     */
    public void setMessage(Object message) {
      if (message == null) {
        this.message = null;
      } else {
        this.message = message.toString();        
      }
    }

    /**
     * Gets whether or not the event has a usable Throwable.
     * 
     * @return if true, the Event's throwbale is usable. False otherwise.
     */
    public boolean hasThrowable() {
      return hasThrowable;
    }

    /**
     * Clears the event's throwable.
     */
    public void unsetThrowable() {
      hasThrowable = false;
      throwable = null;
    }
    /**
     * Gets the Event's throwable.
     * 
     * @return The Event's throwable. This value should only be used, if
     *     {@link #hasThrowable()} is true.
     */
    public Throwable getThrowable() {
      return throwable;
    }
    /**
     * Sets the Event's throwable.
     *
     * <p>Even if the passed throwable is null, it is considered a proper throwable. So
     * {@link #hasThrowable} will return true in this case. To unset the throwable, use
     * {@link #unsetThrowable()} instead.
     * 
     * @param throwable the throwable to set
     */
    public void setThrowable(Throwable throwable) {
      this.hasThrowable = true;
      this.throwable = throwable;
    }
  }
}
