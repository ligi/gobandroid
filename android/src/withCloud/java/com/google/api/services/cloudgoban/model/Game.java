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
 * Model definition for Game.
 * <p/>
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the . For a detailed explanation see:
 * <a href="http://code.google.com/p/google-api-java-client/wiki/Json">http://code.google.com/p/google-api-java-client/wiki/Json</a>
 * </p>
 * <p/>
 * <p>
 * Upgrade warning: starting with version 1.12 {@code getResponseHeaders()} is removed, instead use
 * {@link com.google.api.client.http.json.JsonHttpRequest#getLastResponseHeaders()}
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Game extends GenericJson {

    /**
     * The value may be {@code null}.
     */
    @com.google.api.client.util.Key
    private DateTime created;

    /**
     * The value may be {@code null}.
     */
    @com.google.api.client.util.Key
    private String editKey;

    /**
     * The value may be {@code null}.
     */
    @com.google.api.client.util.Key
    private String editkey;

    /**
     * The value may be {@code null}.
     */
    @com.google.api.client.util.Key
    private String encodedKey;

    /**
     * The value may be {@code null}.
     */
    @com.google.api.client.util.Key
    private Text sgf;

    /**
     * The value may be {@code null}.
     */
    @com.google.api.client.util.Key
    private String type;

    /**
     * The value may be {@code null}.
     */
    @com.google.api.client.util.Key
    private DateTime updated;

    /**
     * The value returned may be {@code null}.
     */
    public DateTime getCreated() {
        return created;
    }

    /**
     * The value set may be {@code null}.
     */
    public Game setCreated(DateTime created) {
        this.created = created;
        return this;
    }

    /**
     * The value returned may be {@code null}.
     */
    public String getEditKey() {
        return editKey;
    }

    /**
     * The value set may be {@code null}.
     */
    public Game setEditKey(String editKey) {
        this.editKey = editKey;
        return this;
    }

    /**
     * The value returned may be {@code null}.
     */
    public String getEditkey() {
        return editkey;
    }

    /**
     * The value set may be {@code null}.
     */
    public Game setEditkey(String editkey) {
        this.editkey = editkey;
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
    public Game setEncodedKey(String encodedKey) {
        this.encodedKey = encodedKey;
        return this;
    }

    /**
     * The value returned may be {@code null}.
     */
    public Text getSgf() {
        return sgf;
    }

    /**
     * The value set may be {@code null}.
     */
    public Game setSgf(Text sgf) {
        this.sgf = sgf;
        return this;
    }

    /**
     * The value returned may be {@code null}.
     */
    public String getType() {
        return type;
    }

    /**
     * The value set may be {@code null}.
     */
    public Game setType(String type) {
        this.type = type;
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
    public Game setUpdated(DateTime updated) {
        this.updated = updated;
        return this;
    }

}
