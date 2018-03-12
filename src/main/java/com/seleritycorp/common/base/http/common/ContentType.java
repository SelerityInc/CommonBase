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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * ContentType with parameters.
 */
public class ContentType implements Negotiable<ContentType> {
  public static final ContentType APPLICATION_JSON = new ContentType("application", "json",
      UTF_8);
  public static final ContentType TEXT_PLAIN = new ContentType("text", "plain", UTF_8);
  public static final ContentType TEXT_HTML = new ContentType("text", "html", UTF_8);
  public static final ContentType TEXT_WILDCARD = new ContentType("text", "*");
  public static final ContentType IMAGE_PNG = new ContentType("image", "png");
  public static final ContentType WILDCARD = new ContentType("*", "*");

  private final String type; 
  private final String subtype; 
  private final HeaderParameter[] parameters;

  /**
   * Creates a ContentType with given type and subtype.
   * 
   * @param type type for the ContentType.
   * @param subtype subtype for the ContentType.
   */
  public ContentType(String type, String subtype) {
    this(type, subtype, new HeaderParameter[0]);
  }

  /**
   * Creates a ContentType with given type, subtype, and character set.
   * 
   * @param type type for the ContentType.
   * @param subtype subtype for the ContentType.
   * @param charset character set for the ContentType.
   */
  public ContentType(String type, String subtype, Charset charset) {
    this(type, subtype,
        charset != null ? new HeaderParameter[] {new HeaderParameter("charset", charset.name())}
          : new HeaderParameter[0]);
  }

  /**
   * Creates a ContentType from a plain string.
   * 
   * @param raw The string to parse.
   */
  public ContentType(String raw) {
    String[] parts = raw.split(";");

    String[] mimeParts = parts[0].trim().split("/", 2);
    this.type = mimeParts[0].trim().toLowerCase();
    this.subtype = mimeParts.length > 1 ? mimeParts[1].trim().toLowerCase() : "";

    if (parts.length > 1) {
      String trimmedParams = parts[1].trim();
      if (!trimmedParams.isEmpty()) {
        this.parameters = new HeaderParameter[parts.length - 1];
        for (int idx = 1; idx < parts.length; idx++) {
          parameters[idx - 1] = new HeaderParameter(parts[idx]);
        }
        Arrays.sort(parameters);
      } else {
        this.parameters = new HeaderParameter[0];
      }
    } else {
      this.parameters = new HeaderParameter[0];
    }
  }

  /**
   * Creates a ContentType with given type, subtype, and parameters.
   * 
   * @param type type for the ContentType.
   * @param subtype subtype for the ContentType.
   * @param parameters parameters for the ContentType.
   */
  private ContentType(String type, String subtype, HeaderParameter[] parameters) {
    this.type = type != null ? type.trim().toLowerCase() : "";
    this.subtype = subtype != null ? subtype.trim().toLowerCase() : "";
    if (parameters != null) {
      this.parameters = parameters;
    } else {
      this.parameters = new HeaderParameter[0];
    }
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(parameters);
    result = prime * result + subtype.hashCode();
    result = prime * result + type.hashCode();
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

    ContentType other = (ContentType) obj;
    if (!type.equals(other.type)) {
      return false;
    }
    if (!subtype.equals(other.subtype)) {
      return false;
    }
    if (!Arrays.equals(parameters, other.parameters)) {
      return false;
    }
    return true;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(type);
    sb.append("/");
    sb.append(subtype);
    if (parameters.length > 0) {
      for (HeaderParameter parameter: parameters) {
        sb.append("; ");
        sb.append(parameter.toString());
      }
    }
    return sb.toString();
  }

  @Override
  public boolean meets(ContentType specification) {
    boolean ret = false;
    if ("*".equals(specification.type) || type.equals(specification.type)) {
      if ("*".equals(specification.subtype) || subtype.equals(specification.subtype)) {
        ret = true;
        for (HeaderParameter specParam : specification.parameters) {
          boolean foundParameterMatch = false;
          for (int idx = 0; !foundParameterMatch && idx < parameters.length; idx++) {
            if (specParam.getKey().equals(parameters[idx].getKey())
                && specParam.getValue().equals(parameters[idx].getValue())) {
              foundParameterMatch = true;
            }
          }
          ret &= foundParameterMatch;
        }
      }
    }
    return ret;
  }
}
