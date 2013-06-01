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
 * This file was generated.
 *  with google-apis-code-generator 1.2.0 (build: 2013-01-22 20:40:36 UTC)
 *  on 2013-02-01 at 18:54:40 UTC 
 */

package com.google.api.services.cloudgoban;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.UriTemplate;

/**
 * Cloudgoban request.
 *
 * <p>
 * Upgrade warning: this class now extends {@link AbstractGoogleJsonClientRequest}, whereas in prior
 * version 1.8 it extended {@link com.google.api.client.http.json.JsonHttpRequest}.
 * </p>
 *
 * @since 1.3
 */
@SuppressWarnings("javadoc")
public abstract class CloudgobanRequest<T> extends AbstractGoogleJsonClientRequest<T> {

  /**
   * @param client Google client
   * @param method HTTP Method
   * @param uriTemplate URI template for the path relative to the base URL. If it starts with a "/"
   *        the base path from the base URL will be stripped out. The URI template can also be a
   *        full URL. URI template expansion is done using
   *        {@link UriTemplate#expand(String, String, Object, boolean)}
   * @param content A POJO that can be serialized into JSON or {@code null} for none
   * @param responseClass response class to parse into
   */
  public CloudgobanRequest(
      Cloudgoban client, String method, String uriTemplate, Object content, Class<T> responseClass) {
    super(
        client,
        method,
        uriTemplate,
        content,
        responseClass);
  }

  /** Data format for the response. */
  @com.google.api.client.util.Key
  private String alt;

  /**
   * Data format for the response.    [default: json]
   */
  public String getAlt() {
    return alt;
  }

  /** Data format for the response. */
  public CloudgobanRequest<T> setAlt(String alt) {
    this.alt = alt;
    return this;
  }

  /** Selector specifying which fields to include in a partial response. */
  @com.google.api.client.util.Key
  private String fields;

  /**
   * Selector specifying which fields to include in a partial response.
   */
  public String getFields() {
    return fields;
  }

  /** Selector specifying which fields to include in a partial response. */
  public CloudgobanRequest<T> setFields(String fields) {
    this.fields = fields;
    return this;
  }

  /**
 * API key. Your API key identifies your project and provides you with API access, quota, and
 * reports. Required unless you provide an OAuth 2.0 token.
 */
  @com.google.api.client.util.Key
  private String key;

  /**
   * API key. Your API key identifies your project and provides you with API access, quota, and
   * reports. Required unless you provide an OAuth 2.0 token.
   */
  public String getKey() {
    return key;
  }

  /**
 * API key. Your API key identifies your project and provides you with API access, quota, and
 * reports. Required unless you provide an OAuth 2.0 token.
 */
  public CloudgobanRequest<T> setKey(String key) {
    this.key = key;
    return this;
  }

  /** OAuth 2.0 token for the current user. */
  @com.google.api.client.util.Key("oauth_token")
  private String oauthToken;

  /**
   * OAuth 2.0 token for the current user.
   */
  public String getOauthToken() {
    return oauthToken;
  }

  /** OAuth 2.0 token for the current user. */
  public CloudgobanRequest<T> setOauthToken(String oauthToken) {
    this.oauthToken = oauthToken;
    return this;
  }

  /** Returns response with indentations and line breaks. */
  @com.google.api.client.util.Key
  private Boolean prettyPrint;

  /**
   * Returns response with indentations and line breaks.    [default: true]
   */
  public Boolean getPrettyPrint() {
    return prettyPrint;
  }

  /** Returns response with indentations and line breaks. */
  public CloudgobanRequest<T> setPrettyPrint(Boolean prettyPrint) {
    this.prettyPrint = prettyPrint;
    return this;
  }

  /**
 * Available to use for quota purposes for server-side applications. Can be any arbitrary string
 * assigned to a user, but should not exceed 40 characters. Overrides userIp if both are provided.
 */
  @com.google.api.client.util.Key
  private String quotaUser;

  /**
   * Available to use for quota purposes for server-side applications. Can be any arbitrary string
   * assigned to a user, but should not exceed 40 characters. Overrides userIp if both are provided.
   */
  public String getQuotaUser() {
    return quotaUser;
  }

  /**
 * Available to use for quota purposes for server-side applications. Can be any arbitrary string
 * assigned to a user, but should not exceed 40 characters. Overrides userIp if both are provided.
 */
  public CloudgobanRequest<T> setQuotaUser(String quotaUser) {
    this.quotaUser = quotaUser;
    return this;
  }

  /**
 * IP address of the site where the request originates. Use this if you want to enforce per-user
 * limits.
 */
  @com.google.api.client.util.Key
  private String userIp;

  /**
   * IP address of the site where the request originates. Use this if you want to enforce per-user
   * limits.
   */
  public String getUserIp() {
    return userIp;
  }

  /**
 * IP address of the site where the request originates. Use this if you want to enforce per-user
 * limits.
 */
  public CloudgobanRequest<T> setUserIp(String userIp) {
    this.userIp = userIp;
    return this;
  }

  @Override
  public final Cloudgoban getAbstractGoogleClient() {
    return (Cloudgoban) super.getAbstractGoogleClient();
  }

  @Override
  public CloudgobanRequest<T> setDisableGZipContent(boolean disableGZipContent) {
    return (CloudgobanRequest<T>) super.setDisableGZipContent(disableGZipContent);
  }

  @Override
  public CloudgobanRequest<T> setRequestHeaders(HttpHeaders headers) {
    return (CloudgobanRequest<T>) super.setRequestHeaders(headers);
  }
}
