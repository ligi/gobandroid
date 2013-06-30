/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * Warning! This file is generated. Modify at your own risk.
 */

package com.google.api.services.cloudgoban.model;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.DateTime;

/**
 * Model definition for User.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the . For a detailed explanation see:
 * <a href="http://code.google.com/p/google-api-java-client/wiki/Json">http://code.google.com/p/google-api-java-client/wiki/Json</a>
 * </p>
 *
 * <p>
 * Upgrade warning: starting with version 1.12 {@code getResponseHeaders()} is removed, instead use
 * {@link com.google.api.client.http.json.JsonHttpRequest#getLastResponseHeaders()}
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class User extends GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("IP")
  private String iP;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String contact;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private DateTime created;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String encodedKey;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String rank;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String secret;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private DateTime updated;

  /**

   * The value returned may be {@code null}.
   */
  public String getIP() {
    return iP;
  }

  /**

   * The value set may be {@code null}.
   */
  public User setIP(String iP) {
    this.iP = iP;
    return this;
  }

  /**

   * The value returned may be {@code null}.
   */
  public String getContact() {
    return contact;
  }

  /**

   * The value set may be {@code null}.
   */
  public User setContact(String contact) {
    this.contact = contact;
    return this;
  }

  /**

   * The value returned may be {@code null}.
   */
  public DateTime getCreated() {
    return created;
  }

  /**

   * The value set may be {@code null}.
   */
  public User setCreated(DateTime created) {
    this.created = created;
    return this;
  }

  /**

   * The value returned may be {@code null}.
   */
  public String getEncodedKey() {
    return encodedKey;
  }

  /**

   * The value set may be {@code null}.
   */
  public User setEncodedKey(String encodedKey) {
    this.encodedKey = encodedKey;
    return this;
  }

  /**

   * The value returned may be {@code null}.
   */
  public String getName() {
    return name;
  }

  /**

   * The value set may be {@code null}.
   */
  public User setName(String name) {
    this.name = name;
    return this;
  }

  /**

   * The value returned may be {@code null}.
   */
  public String getRank() {
    return rank;
  }

  /**

   * The value set may be {@code null}.
   */
  public User setRank(String rank) {
    this.rank = rank;
    return this;
  }

  /**

   * The value returned may be {@code null}.
   */
  public String getSecret() {
    return secret;
  }

  /**

   * The value set may be {@code null}.
   */
  public User setSecret(String secret) {
    this.secret = secret;
    return this;
  }

  /**

   * The value returned may be {@code null}.
   */
  public DateTime getUpdated() {
    return updated;
  }

  /**

   * The value set may be {@code null}.
   */
  public User setUpdated(DateTime updated) {
    this.updated = updated;
    return this;
  }

}
