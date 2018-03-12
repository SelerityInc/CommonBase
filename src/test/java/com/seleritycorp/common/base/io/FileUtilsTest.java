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

package com.seleritycorp.common.base.io;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import com.seleritycorp.common.base.test.FileTestCase;

public class FileUtilsTest extends FileTestCase {
  Path base;

  @Before
  public void setUp() throws IOException {
    base = createTempDirectory();
  }

  @Test
  public void testDeleteFilesRecursivelySilentlyNoFollowNonExisting() throws IOException {
    Path path = base.resolve("foo");

    FileUtils.deleteFilesRecursivelySilentlyNoFollow(path);

    assertThat(base).isDirectory();
    assertThat(path).doesNotExist();
  }

  @Test
  public void testDeleteFilesRecursivelySilentlyNoFollowPlainFile() throws IOException {
    Path path = base.resolve("foo");

    Files.createFile(path);

    FileUtils.deleteFilesRecursivelySilentlyNoFollow(path);

    assertThat(base).isDirectory();
    assertThat(path).doesNotExist();
  }

  @Test
  public void testDeleteFilesRecursivelySilentlyNoFollowSymLinkNonExisting() throws IOException {
    Path path = base.resolve("foo");
    Path bar = base.resolve("bar");

    Files.createFile(bar);
    Files.createLink(path, bar);

    FileUtils.deleteFilesRecursivelySilentlyNoFollow(path);

    assertThat(base).isDirectory();
    assertThat(path).doesNotExist();
    assertThat(bar).exists();
  }

  @Test
  public void testDeleteFilesRecursivelySilentlyNoFollowSymLinkExisting() throws IOException {
    Path path = base.resolve("foo");
    Path bar = base.resolve("bar");

    Files.createFile(bar);
    Files.createLink(path, bar);
    Files.delete(bar);
    assertThat(bar).doesNotExist();

    FileUtils.deleteFilesRecursivelySilentlyNoFollow(path);

    assertThat(base).isDirectory();
    assertThat(path).doesNotExist();
    assertThat(bar).doesNotExist();
  }

  @Test
  public void testDeleteFilesRecursivelySilentlyNoFollowDirectoryEmpty() throws IOException {
    Path path = base.resolve("foo");

    Files.createDirectory(path);

    FileUtils.deleteFilesRecursivelySilentlyNoFollow(path);

    assertThat(base).isDirectory();
    assertThat(path).doesNotExist();
  }

  @Test
  public void testDeleteFilesRecursivelySilentlyNoFollowDirectoryNested() throws IOException {
    Path path = base.resolve("foo");
    Files.createDirectory(path);

    Path bar = path.resolve("bar");
    Files.createDirectory(bar);

    Files.createFile(bar.resolve("baz"));
    Files.createDirectory(bar.resolve("quux"));

    FileUtils.deleteFilesRecursivelySilentlyNoFollow(path);

    assertThat(base).isDirectory();
    assertThat(path).doesNotExist();
  }
}
