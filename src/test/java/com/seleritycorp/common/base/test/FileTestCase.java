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

package com.seleritycorp.common.base.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.assertj.core.util.Lists;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;

import com.seleritycorp.common.base.io.FileUtils;

public class FileTestCase extends EasyMockSupport {
  private List<Path> tmpDirectories;

  @Before
  public void setUpTmpDirectories() {
    tmpDirectories = Lists.newArrayList();
  }

  @After
  public void tearDownTmpDirectories() {
    for (Path dir : tmpDirectories) {
      FileUtils.deleteFilesRecursivelySilentlyNoFollow(dir);
    }
    tmpDirectories.clear();
  }

  /**
   * Creates a temporary file that gets cleaned up automatically
   * 
   * @return The Path to the file
   * @throws IOException
   */
  protected Path createTempFile() throws IOException {
    File file = File.createTempFile("CommonUtils-Test-", ".tmp");
    file.deleteOnExit();
    return file.toPath();
  }

  /**
   * Creates a temporary directory that gets cleaned up automatically
   * 
   * @return The Path to the directory
   * @throws IOException
   */
  protected Path createTempDirectory() throws IOException {
    Path dir = Files.createTempDirectory("CommonUtils-Test-");
    return dir;
  }

  /**
   * Write a string to a file
   * <p>
   * The file is replaced completely by the string's content
   * 
   * @param path The file to overwrite with the content
   * @param contents The content to write
   * @throws IOException
   */
  protected void writeFile(Path path, String contents) throws IOException {
    Files.write(path, contents.getBytes());
  }
}
