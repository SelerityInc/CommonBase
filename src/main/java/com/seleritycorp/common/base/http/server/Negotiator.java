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

package com.seleritycorp.common.base.http.server;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generic negotiator between client and server preferences for Http headers.
 * 
 * @param <T> The type to negotiate on.
 */
public abstract class Negotiator<T> {
  /*
   * The ability to parse strings is needed, as we lazily evaluate the client preferences.
   * <p>
   * static methods came to Java only with Java 8, but CommonBase is only at Java 7, so we push
   * down the issue to a method on the class itself.
   */
  /**
   * Parses an object from a string.
   * 
   * @param raw the raw string to parse
   * @return The constructed instance of T
   */
  protected abstract T parse(String raw);

  /**
   * Checks if a candidate meets the given specification.
   * 
   * @param candidate The candidate to check against the specification.
   * @param specification The specification to check against.
   * @return True, if the candidate meets the specification. False otherwise.
   */
  protected abstract boolean meets(T candidate, T specification);

  /**
   * Turn a preference string into parts, sorted descendingly by their q values.
   *
   * @param preferenceSpecification raw specification of preferences.
   * @return List of specifications that have their q parameter parsed away. This list is sorted
   *     descendingly by the item's q value.
   */
  private List<SortableUnparsedT> sortByQValue(String preferenceSpecification) {
    List<SortableUnparsedT> ret = new ArrayList<SortableUnparsedT>();
    if (preferenceSpecification != null) {
      String[] rawPreferences = preferenceSpecification.split(",");
      for (String rawPreference : rawPreferences) {
        String[] preferenceParts = rawPreference.split(" *; *q *= *", 2);
        String rawT = preferenceParts[0].trim();
        if (!rawT.isEmpty()) {
          float quality = 1.0f;
          if (preferenceParts.length > 1) {
            String numberString = preferenceParts[1].split(";")[0].trim();
            try {
              quality = Float.parseFloat(numberString);
              quality = Math.min(Math.max(quality, 0), 1); // force into [0, 1]
            } catch (Exception e) {
              // Parsing went wrong, so we pick the default weight.
              quality = 1.0f;
            }
          }
          ret.add(new SortableUnparsedT(rawT, quality));
        }
      }
    }
    Collections.sort(ret);
    return ret;
  }

  /**
   * Find the best match between preferences and candidates
   * 
   * @param preferences List of preference parts, sorted descendingly by their q values.
   * @param candidates candidates to negotiate for, sorted by descending preference.
   * @return The best match between the preferences and candidates. Null, if there is no match at
   *     all.
   */
  private T findBestMatch(List<SortableUnparsedT> preferences, T[] candidates) {
    T ret = null;
    for (SortableUnparsedT sortableUnparsedT : preferences) {
      if (ret == null) {
        T preference = parse(sortableUnparsedT.getRaw());
        for (T candidate : candidates) {
          if (ret == null) {
            if (meets(candidate, preference)) {
              ret = candidate;
            }
          }
        }
      }
    }
    return ret;
  }

  /**
   * Negotiates between expressed client preferences and server offerings.
   *
   * @param preferenceSpecification The preference from the client.
   * @param fallback The fallback, if there is no match between client prefences and server
   *     offerings.
   * @param candidates The offerings from the server.
   * @return The client preferred item that the server can offer. If there is no match, the
   *     fallback will be returned.
   */
  @SuppressWarnings("unchecked")
  public T negotiate(String preferenceSpecification, T fallback, T ... candidates) {
    List<SortableUnparsedT> preferences = sortByQValue(preferenceSpecification);
    
    T ret = findBestMatch(preferences, candidates);
    
    if (ret == null) {
      ret = fallback;
    }
    return ret;
  }
  
  /**
   * Wrapper for half parsed client preferences.
   * 
   * <p>Instances are raw, unparsed strings that have their q parameter (and following parameters)
   * chopped off. Items sort by their q value descendingly.
   */
  private class SortableUnparsedT implements Comparable<SortableUnparsedT> {
    private final String raw;
    private final float quality;

    SortableUnparsedT(String raw, float quality) {
      this.raw = raw;
      this.quality = quality;
    }
    
    public String getRaw() {
      return raw;
    }

    @SuppressFBWarnings(value = "CO_COMPARETO_INCORRECT_FLOATING",
        justification = "The strict equality checks on floats are ok. If there are tiny"
            + "differences between them, we want them to tip the scale.")
    @Override
    public int compareTo(Negotiator<T>.SortableUnparsedT other) {
      float diff = quality - other.quality;
      if (diff == 0) {
        diff = raw.length() - other.raw.length(); 
      }
      return (diff < 0) ? 1 : (diff == 0) ? 0 : -1;
    }

    @Override
    public String toString() {
      return raw + "; q=" + quality;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Float.floatToIntBits(quality);
      result = prime * result + ((raw == null) ? 0 : raw.hashCode());
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
      @SuppressWarnings("unchecked")
      SortableUnparsedT other = (SortableUnparsedT) obj;
      if (Float.floatToIntBits(quality) != Float.floatToIntBits(other.quality)) {
        return false;
      }
      if (raw == null) {
        if (other.raw != null) {
          return false;
        }
      } else if (!raw.equals(other.raw)) {
        return false;
      }
      return true;
    }
  }
}
