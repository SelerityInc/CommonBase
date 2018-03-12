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

package com.seleritycorp.common.base.http.common;

/**
 * Key-Value parameter as used in Http headers and Mime types.
 * 
 * <p>Key and value may be empty, but they are guaranteed to be not-null.
 */
public class HeaderParameter implements Comparable<HeaderParameter> {
  private final String key;
  private final String value;
  
  /**
   * Creates a HeaderParameter from key and value.
   *
   * @param key key for the HeaderParameter.
   * @param value value for the HeaderParameter.
   */
  public HeaderParameter(String key, String value) {
    this.key = key != null ? key.toLowerCase() : "";
    this.value = value != null ? value : "";
  }

  /**
   * Creates a HeaderParameter by parsing a string.
   * 
   * <p>The String should have the forwat &quot;key=value&quot;.
   *
   * @param raw The string to parse.
   */
  public HeaderParameter(String raw) {
    String[] parts = raw.split("=");
    key = parts[0].trim().toLowerCase();
    value = parts.length > 1 ? parts[1].trim() : "";
  }

  /**
   * Gets the key.
   *
   * @return the key
   */
  public String getKey() {
    return key;
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + key.hashCode();
    result = prime * result + value.hashCode();
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    HeaderParameter other = (HeaderParameter) obj;
    if (!key.equals(other.key)) {
      return false;
    }
    if (!value.equals(other.value)) {
      return false;
    }

    return true;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return key + "=" + value;
  }

  @Override
  public int compareTo(HeaderParameter other) {
    int ret = key.compareTo(other.key);
    if (ret == 0 ) {
      ret = value.compareTo(other.value);
    }
    return ret;
  }
}
