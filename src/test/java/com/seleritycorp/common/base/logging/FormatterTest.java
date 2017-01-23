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

import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.logging.Formatter;

public class FormatterTest {
  private Formatter formatter;

  @Before
  public void setUp() {
    formatter = new Formatter();
  }

  @Test
  public void testFormatStructuredCsvLinePlain() {
    String actual = formatter.formatStructuredLine("foo", 42, "bar", "baz");
    assertThat(actual).isEqualTo("/log-tag:foo/log-tag-version:42/bar:baz/");
  }

  @Test
  public void testFormatStructuredCsvLineTagNull() {
    String actual = formatter.formatStructuredLine(null, 42, "bar", "baz");
    assertThat(actual).isEqualTo("/log-tag:<null>/log-tag-version:42/bar:baz/");
  }

  @Test
  public void testFormatStructuredCsvLineOddObjs() {
    String actual = formatter.formatStructuredLine("foo", 42, "bar", "baz", "quux");
    assertThat(actual).isEqualTo("/log-tag:foo/log-tag-version:42/bar:baz/<null>:quux/");
  }

  @Test
  public void testFormatStructuredCsvLineNonStringName() {
    String actual = formatter.formatStructuredLine("foo", 42, 7, "bar");
    assertThat(actual).isEqualTo("/log-tag:foo/log-tag-version:42/7:bar/");
  }

  @Test
  public void testFormatStructuredCsvLineNameNull() {
    String actual = formatter.formatStructuredLine("foo", 42, null, "bar");
    assertThat(actual).isEqualTo("/log-tag:foo/log-tag-version:42/<null>:bar/");
  }

  @Test
  public void testFormatStructuredCsvLineValueNull() {
    String actual = formatter.formatStructuredLine("foo", 42, "bar", null);
    assertThat(actual).isEqualTo("/log-tag:foo/log-tag-version:42/bar:<null>/");
  }

  @Test
  public void testFormatStructuredCsvLineLinebreakSingle() {
    String actual = formatter.formatStructuredLine("foo", 42, "bar", "baz\nquux");
    assertThat(actual).isEqualTo("/log-tag:foo/log-tag-version:42/bar:baz\\nquux/");
  }

  @Test
  public void testFormatStructuredCsvLineLinebreakMultiple() {
    String actual = formatter.formatStructuredLine("foo", 42, "bar", "baz\nquux\nquuux");
    assertThat(actual).isEqualTo("/log-tag:foo/log-tag-version:42/bar:baz\\nquux\\nquuux/");
  }

  @Test
  public void testFormatStructuredCsvLineCarriageReturnSingle() {
    String actual = formatter.formatStructuredLine("foo", 42, "bar", "baz\rquux");
    assertThat(actual).isEqualTo("/log-tag:foo/log-tag-version:42/bar:baz\\rquux/");
  }

  @Test
  public void testFormatStructuredCsvLineCarriageReturnMultiple() {
    String actual = formatter.formatStructuredLine("foo", 42, "bar", "baz\rquux\rquuux");
    assertThat(actual).isEqualTo("/log-tag:foo/log-tag-version:42/bar:baz\\rquux\\rquuux/");
  }

  @Test
  public void testFormatStructuredCsvLineSemicolonSingle() {
    String actual = formatter.formatStructuredLine("foo", 42, "bar", "baz/quux");
    assertThat(actual).isEqualTo("/log-tag:foo/log-tag-version:42/bar:baz\\|quux/");
  }

  @Test
  public void testFormatStructuredCsvLineSlashMultiple() {
    String actual = formatter.formatStructuredLine("foo", 42, "bar", "baz/quux/quuux");
    assertThat(actual).isEqualTo("/log-tag:foo/log-tag-version:42/bar:baz\\|quux\\|quuux/");
  }

  @Test
  public void testFormatStructuredCsvLineSlashSingle() {
    String actual = formatter.formatStructuredLine("foo", 42, "bar", "baz\\quux");
    assertThat(actual).isEqualTo("/log-tag:foo/log-tag-version:42/bar:baz\\\\quux/");
  }

  @Test
  public void testFormatStructuredCsvLineBackslashMultiple() {
    String actual = formatter.formatStructuredLine("foo", 42, "bar", "baz\\quux\\quuux");
    assertThat(actual).isEqualTo("/log-tag:foo/log-tag-version:42/bar:baz\\\\quux\\\\quuux/");
  }

  @Test
  public void testFormatStructuredCsvLineMixed() {
    String actual =
        formatter.formatStructuredLine("foo", 42, "foo\nbar", "bar/baz\\quux/\rquuux\nfoo");
    assertThat(actual).isEqualTo(
        "/log-tag:foo/log-tag-version:42/foo\\nbar:bar\\|baz\\\\" + "quux\\|\\rquuux\\nfoo/");
  }
}
