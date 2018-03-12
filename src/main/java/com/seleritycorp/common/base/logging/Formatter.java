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

import javax.inject.Singleton;

/**
 * Helper class to format log lines.
 *
 * <p>This class is thread-safe.
 */
@Singleton
public class Formatter {
  /**
   * The separator used between two fields in the sliceable format.
   */
  private static final char SEPARATOR = '/';

  /**
   * Encodes a structured column to allow easy grepping, and cut-ing.
   * 
   * @param name the name of the value
   * @param value the value to encode
   * @return the encoded string
   */
  private String formatStructuredColumn(String name, Object value) {
    String ret = (name == null) ? "<null>" : name;
    ret += ":";
    ret += (value == null) ? "<null>" : value;

    ret = ret.replaceAll("\\\\", "\\\\\\\\");
    ret = ret.replaceAll("\r", "\\\\r");
    ret = ret.replaceAll("\n", "\\\\n");
    ret = ret.replaceAll("/", "\\\\|");
    return ret;
  }

  /**
   * Formats a log line in a format that to eases use grep and cut.
   *
   * <p>The format meets the requirements of
   * {@link Log#structuredInfo(String, int, Object...)}
   * 
   * @param tag The tag to store the objects at
   * @param version The version of the tag
   * @param objs The name and objects to embed. objs is expected to hold an
   *        even number of elements, with objs[2*n] holding the name for the
   *        object at objs[2*n+1].
   * @return the encoded string
   */
  public String formatStructuredLine(String tag, int version, Object... objs) {
    StringBuilder sb = new StringBuilder();
    sb.append(SEPARATOR);
    sb.append(formatStructuredColumn("log-tag", tag));
    sb.append(SEPARATOR);
    sb.append(formatStructuredColumn("log-tag-version", version));

    if (objs != null) {
      Object prev = null;
      boolean hasPrev = false;
      for (Object o : objs) {
        if (hasPrev) {
          sb.append(SEPARATOR);
          String prevStr = null;
          if (prev != null) {
            prevStr = prev.toString();
          }
          sb.append(formatStructuredColumn(prevStr, o));
          hasPrev = false;
        } else {
          prev = o;
          hasPrev = true;
        }
      }
      if (hasPrev) {
        // objs was not even, so there is an element present that has
        // not been logged yet :-(
        sb.append(SEPARATOR);
        sb.append(formatStructuredColumn(null, prev));
      }
    }
    sb.append(SEPARATOR);
    return sb.toString();
  }
}
