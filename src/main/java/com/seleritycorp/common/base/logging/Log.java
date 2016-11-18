/*
 * Copyright (C) 2016 Selerity, Inc. (support@seleritycorp.com)
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Selerity specific Log extension.
 */
@SuppressFBWarnings(value = "NM_SAME_SIMPLE_NAME_AS_INTERFACE",
    justification = "This Log should act as drop-in replacement for commons' Log")
public interface Log extends org.apache.commons.logging.Log {
  /**
   * Logs objects in structured format in a single INFO line
   *
   * <p>This method is mostly useful for logs that are meant to be parsed by
   * machines. The format is friendly to `cut` and `grep`.
   *
   * <p>The following replacements take place:
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
   */
  public void structuredInfo(String tag, int version, Object... objs);
}
