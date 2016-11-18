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


import com.seleritycorp.common.base.inject.InjectorFactory;
import com.seleritycorp.common.base.time.Clock;

import org.apache.log4j.rolling.RollingPolicy;
import org.apache.log4j.rolling.RolloverDescription;
import org.apache.log4j.rolling.RolloverDescriptionImpl;
import org.apache.log4j.rolling.helper.Action;
import org.apache.log4j.rolling.helper.FileRenameAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.inject.Inject;

/**
 * Policy for rolling log files by time and size.
 */
public class RoverLikeRollingPolicy implements RollingPolicy {
  /**
   * The clock used for timing.
   */
  private final Clock clock;

  /**
   * The file name pattern for rotated files.
   */
  private String rotateFormat;

  public RoverLikeRollingPolicy() {
    this(InjectorFactory.getInjector().getInstance(Clock.class));
  }

  @Inject
  public RoverLikeRollingPolicy(Clock clock) {
    this.clock = clock;
    setRotateFormat("application.%1$tFT%1$tT%2$s.log");
  }

  @Override
  public void activateOptions() {}

  private long getTimestampForFile(Path path) {
    Long ret = null;
    try {
      ret = Files.getLastModifiedTime(path).toMillis();
    } catch (IOException e) {
      // Parsing failed. No problem, we have a fallback later on
      e.printStackTrace();
    }

    if (ret == null) {
      ret = clock.getSecondsEpoch();
    }
    return ret;
  }

  private Action rotateFileAction(String src, Long timestamp) {
    Action ret = null;
    File rotatedFile = null;
    Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
    cal.setTimeInMillis(timestamp);
    for (int i = 1; rotatedFile == null || (rotatedFile.exists() && i < 1024); i++) {
      String suffix = (i < 2) ? "" : ("-" + i);
      String rotatedName = String.format(rotateFormat, cal, suffix);
      rotatedFile = new File(rotatedName);
    }
    if (!rotatedFile.exists()) {
      ret = new FileRenameAction(new File(src), rotatedFile, true);
    }
    return ret;
  }

  @Override
  public RolloverDescription initialize(String file, boolean append) throws SecurityException {
    return rollover(file, append);
  }

  @Override
  public RolloverDescription rollover(final String activeFile) throws SecurityException {
    return rollover(activeFile, false);
  }

  /**
   * Composes a roll-over description
   * 
   * @param file The file to roll-over
   * @param append Whether or not an eventually existing file should be
   *        appended to instead of truncated.
   * @return description of the roll-over process
   * @throws SecurityException when security violations occur
   */
  public RolloverDescription rollover(String file, boolean append) throws SecurityException {

    if (file == null) {
      file = "application.log";
    }

    Path path = Paths.get(file);
    Action activeFileAction = null;
    try {
      if (Files.exists(path) && Files.size(path) > 0 && !append) {
        long timestamp = getTimestampForFile(path);
        activeFileAction = rotateFileAction(file, timestamp);
      }
    } catch (IOException e) {
      // If an IO error, something must be in flux.
      // Enforce appending to avoid truncating files that shold have
      // been rotated away, but failed.
      append = true;
    }

    Action archivingAction = null;

    return new RolloverDescriptionImpl(file, append, activeFileAction, archivingAction);
  }

  /**
   * Sets the format used for rotated files.
   * 
   * @return the rotateFormat.
   */
  public String getRotateFormat() {
    return rotateFormat;
  }

  /**
   * Sets the format to use for rotated files.
   * 
   * @param rotateFormat the rotateFormat to set
   */
  public void setRotateFormat(String rotateFormat) {
    this.rotateFormat = rotateFormat;
  }
}
