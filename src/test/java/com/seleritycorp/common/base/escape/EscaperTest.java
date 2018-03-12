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

package com.seleritycorp.common.base.escape;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class EscaperTest {
  @Test
  public void testHtmlEscapeEmpty() {
    Escaper escaper = createEscaper();
    assertThat(escaper.html("")).isEqualTo("");
  }
  
  @Test
  public void testHtmlEscapeNull() {
    Escaper escaper = createEscaper();
    assertThat(escaper.html(null)).isEqualTo("");
  }
  
  @Test
  public void testHtmlEscapeLtSingle() {
    Escaper escaper = createEscaper();
    assertThat(escaper.html("<")).isEqualTo("&lt;");
  }
  
  @Test
  public void testHtmlEscapeLtMultiple() {
    Escaper escaper = createEscaper();
    assertThat(escaper.html("<foo <bar")).isEqualTo("&lt;foo &lt;bar");
  }
  
  @Test
  public void testHtmlEscapeAmpersAndSingle() {
    Escaper escaper = createEscaper();
    assertThat(escaper.html("&")).isEqualTo("&amp;");
  }
  
  @Test
  public void testHtmlEscapeAmpersAndMultiple() {
    Escaper escaper = createEscaper();
    assertThat(escaper.html("foo & bar & baz")).isEqualTo("foo &amp; bar &amp; baz");
  }

  @Test
  public void testHtmlEscapeAmpersDouble() {
    Escaper escaper = createEscaper();
    assertThat(escaper.html("&amp;")).isEqualTo("&amp;amp;");
  }
  
  @Test
  public void testHtmlEscapeQuotSingle() {
    Escaper escaper = createEscaper();
    assertThat(escaper.html("\"")).isEqualTo("&quot;");
  }
  
  @Test
  public void testHtmlEscapeQuotMultiple() {
    Escaper escaper = createEscaper();
    assertThat(escaper.html("\"foo\"")).isEqualTo("&quot;foo&quot;");
  }
  
  @Test
  public void testHtmlEscapeMix() {
    Escaper escaper = createEscaper();
    assertThat(escaper.html("<b>\"foo & bar\"</b>")).isEqualTo("&lt;b&gt;&quot;foo &amp; bar&quot;&lt;/b&gt;");
  }
  
  private Escaper createEscaper() {
    return new Escaper();
  }
}
