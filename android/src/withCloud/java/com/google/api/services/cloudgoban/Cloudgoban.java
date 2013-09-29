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

import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.common.base.Preconditions;

/**
 * Service definition for Cloudgoban (v4).
 * <p/>
 * <p>
 * This is an API
 * </p>
 * <p/>
 * <p>
 * For more information about this service, see the
 * <a href="" target="_blank">API Documentation</a>
 * </p>
 * <p/>
 * <p>
 * This service uses {@link CloudgobanRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 * <p/>
 * <p>
 * Upgrade warning: this class now extends {@link AbstractGoogleJsonClient}, whereas in prior
 * version 1.8 it extended {@link com.google.api.client.googleapis.services.GoogleClient}.
 * </p>
 *
 * @author Google, Inc.
 * @since 1.3
 */
@SuppressWarnings("javadoc")
public class Cloudgoban extends AbstractGoogleJsonClient {

    // Note: Leave this static initializer at the top of the file.
    static {
        Preconditions.checkState(GoogleUtils.VERSION.equals("1.13.2-beta"),
                "You are currently running with version %s of google-api-client. " +
                        "You need version 1.13.2-beta of google-api-client to run version " +
                        "1.13.2-beta of the  library.", GoogleUtils.VERSION);
    }

    /**
     * The default encoded root URL of the service. This is determined when the library is generated
     * and normally should not be changed.
     *
     * @since 1.7
     */
    public static final String DEFAULT_ROOT_URL = "https://cloud-goban.appspot.com/_ah/api/";

    /**
     * The default encoded service path of the service. This is determined when the library is
     * generated and normally should not be changed.
     *
     * @since 1.7
     */
    public static final String DEFAULT_SERVICE_PATH = "cloudgoban/v4/";

    /**
     * The default encoded base URL of the service. This is determined when the library is generated
     * and normally should not be changed.
     *
     * @deprecated (scheduled to be removed in 1.13)
     */
    @Deprecated
    public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

    /**
     * Constructor.
     * <p/>
     * <p>
     * Use {@link Builder} if you need to specify any of the optional parameters.
     * </p>
     *
     * @param transport              HTTP transport
     * @param jsonFactory            JSON factory
     * @param httpRequestInitializer HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Cloudgoban(HttpTransport transport, JsonFactory jsonFactory,
                      HttpRequestInitializer httpRequestInitializer) {
        super(transport,
                jsonFactory,
                DEFAULT_ROOT_URL,
                DEFAULT_SERVICE_PATH,
                httpRequestInitializer,
                false);
    }

    /**
     * @param transport                      HTTP transport
     * @param httpRequestInitializer         HTTP request initializer or {@code null} for none
     * @param rootUrl                        root URL of the service
     * @param servicePath                    service path
     * @param jsonObjectParser               JSON object parser
     * @param googleClientRequestInitializer Google request initializer or {@code null} for none
     * @param applicationName                application name to be sent in the User-Agent header of requests or
     *                                       {@code null} for none
     * @param suppressPatternChecks          whether discovery pattern checks should be suppressed on required
     *                                       parameters
     */
    Cloudgoban(HttpTransport transport,
               HttpRequestInitializer httpRequestInitializer,
               String rootUrl,
               String servicePath,
               JsonObjectParser jsonObjectParser,
               GoogleClientRequestInitializer googleClientRequestInitializer,
               String applicationName,
               boolean suppressPatternChecks) {
        super(transport,
                httpRequestInitializer,
                rootUrl,
                servicePath,
                jsonObjectParser,
                googleClientRequestInitializer,
                applicationName,
                suppressPatternChecks);
    }

    @Override
    protected void initialize(AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
        super.initialize(httpClientRequest);
    }

    /**
     * An accessor for creating requests from the Games collection.
     * <p/>
     * <p>The typical use is:</p>
     * <pre>
     *   {@code Cloudgoban cloudgoban = new Cloudgoban(...);}
     *   {@code Cloudgoban.Games.List request = cloudgoban.games().list(parameters ...)}
     * </pre>
     *
     * @return the resource collection
     */
    public Games games() {
        return new Games();
    }

    /**
     * The "games" collection of methods.
     */
    public class Games {

        /**
         * Create a request for the method "games.get".
         * <p/>
         * This request holds the parameters needed by the the cloudgoban server.  After setting any
         * optional parameters, call the {@link Get#execute()} method to invoke the remote operation.
         *
         * @param gameKey
         * @return the request
         */
        public Get get(String gameKey) throws java.io.IOException {
            Get result = new Get(gameKey);
            initialize(result);
            return result;
        }

        public class Get extends CloudgobanRequest<com.google.api.services.cloudgoban.model.Game> {

            private static final String REST_PATH = "game/{game_key}";

            /**
             * Create a request for the method "games.get".
             * <p/>
             * This request holds the parameters needed by the the cloudgoban server.  After setting any
             * optional parameters, call the {@link Get#execute()} method to invoke the remote operation. <p>
             * {@link Get#initialize(AbstractGoogleClientRequest)} must be called to initialize this instance
             * immediately after invoking the constructor. </p>
             *
             * @param gameKey
             * @since 1.13
             */
            protected Get(String gameKey) {
                super(Cloudgoban.this, "GET", REST_PATH, null, com.google.api.services.cloudgoban.model.Game.class);
                this.gameKey = Preconditions.checkNotNull(gameKey, "Required parameter gameKey must be specified.");
            }

            @Override
            public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
                return super.executeUsingHead();
            }

            @Override
            public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
                return super.buildHttpRequestUsingHead();
            }

            @Override
            public Get setAlt(String alt) {
                return (Get) super.setAlt(alt);
            }

            @Override
            public Get setFields(String fields) {
                return (Get) super.setFields(fields);
            }

            @Override
            public Get setKey(String key) {
                return (Get) super.setKey(key);
            }

            @Override
            public Get setOauthToken(String oauthToken) {
                return (Get) super.setOauthToken(oauthToken);
            }

            @Override
            public Get setPrettyPrint(Boolean prettyPrint) {
                return (Get) super.setPrettyPrint(prettyPrint);
            }

            @Override
            public Get setQuotaUser(String quotaUser) {
                return (Get) super.setQuotaUser(quotaUser);
            }

            @Override
            public Get setUserIp(String userIp) {
                return (Get) super.setUserIp(userIp);
            }

            @com.google.api.client.util.Key("game_key")
            private String gameKey;

            /**

             */
            public String getGameKey() {
                return gameKey;
            }

            public Get setGameKey(String gameKey) {
                this.gameKey = gameKey;
                return this;
            }

        }

        /**
         * Create a request for the method "games.insert".
         * <p/>
         * This request holds the parameters needed by the the cloudgoban server.  After setting any
         * optional parameters, call the {@link Insert#execute()} method to invoke the remote operation.
         *
         * @param content the {@link com.google.api.services.cloudgoban.model.Game}
         * @return the request
         */
        public Insert insert(com.google.api.services.cloudgoban.model.Game content) throws java.io.IOException {
            Insert result = new Insert(content);
            initialize(result);
            return result;
        }

        public class Insert extends CloudgobanRequest<com.google.api.services.cloudgoban.model.Game> {

            private static final String REST_PATH = "game";

            /**
             * Create a request for the method "games.insert".
             * <p/>
             * This request holds the parameters needed by the the cloudgoban server.  After setting any
             * optional parameters, call the {@link Insert#execute()} method to invoke the remote operation.
             * <p> {@link Insert#initialize(AbstractGoogleClientRequest)} must be called to initialize this
             * instance immediately after invoking the constructor. </p>
             *
             * @param content the {@link com.google.api.services.cloudgoban.model.Game}
             * @since 1.13
             */
            protected Insert(com.google.api.services.cloudgoban.model.Game content) {
                super(Cloudgoban.this, "POST", REST_PATH, content, com.google.api.services.cloudgoban.model.Game.class);
            }

            @Override
            public Insert setAlt(String alt) {
                return (Insert) super.setAlt(alt);
            }

            @Override
            public Insert setFields(String fields) {
                return (Insert) super.setFields(fields);
            }

            @Override
            public Insert setKey(String key) {
                return (Insert) super.setKey(key);
            }

            @Override
            public Insert setOauthToken(String oauthToken) {
                return (Insert) super.setOauthToken(oauthToken);
            }

            @Override
            public Insert setPrettyPrint(Boolean prettyPrint) {
                return (Insert) super.setPrettyPrint(prettyPrint);
            }

            @Override
            public Insert setQuotaUser(String quotaUser) {
                return (Insert) super.setQuotaUser(quotaUser);
            }

            @Override
            public Insert setUserIp(String userIp) {
                return (Insert) super.setUserIp(userIp);
            }

        }

        /**
         * Create a request for the method "games.list".
         * <p/>
         * This request holds the parameters needed by the the cloudgoban server.  After setting any
         * optional parameters, call the {@link List#execute()} method to invoke the remote operation.
         *
         * @return the request
         */
        public List list() throws java.io.IOException {
            List result = new List();
            initialize(result);
            return result;
        }

        public class List extends CloudgobanRequest<com.google.api.services.cloudgoban.model.GameCollection> {

            private static final String REST_PATH = "game";

            /**
             * Create a request for the method "games.list".
             * <p/>
             * This request holds the parameters needed by the the cloudgoban server.  After setting any
             * optional parameters, call the {@link List#execute()} method to invoke the remote operation. <p>
             * {@link List#initialize(AbstractGoogleClientRequest)} must be called to initialize this instance
             * immediately after invoking the constructor. </p>
             *
             * @since 1.13
             */
            protected List() {
                super(Cloudgoban.this, "GET", REST_PATH, null, com.google.api.services.cloudgoban.model.GameCollection.class);
            }

            @Override
            public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
                return super.executeUsingHead();
            }

            @Override
            public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
                return super.buildHttpRequestUsingHead();
            }

            @Override
            public List setAlt(String alt) {
                return (List) super.setAlt(alt);
            }

            @Override
            public List setFields(String fields) {
                return (List) super.setFields(fields);
            }

            @Override
            public List setKey(String key) {
                return (List) super.setKey(key);
            }

            @Override
            public List setOauthToken(String oauthToken) {
                return (List) super.setOauthToken(oauthToken);
            }

            @Override
            public List setPrettyPrint(Boolean prettyPrint) {
                return (List) super.setPrettyPrint(prettyPrint);
            }

            @Override
            public List setQuotaUser(String quotaUser) {
                return (List) super.setQuotaUser(quotaUser);
            }

            @Override
            public List setUserIp(String userIp) {
                return (List) super.setUserIp(userIp);
            }

            @com.google.api.client.util.Key
            private String cursor;

            /**

             */
            public String getCursor() {
                return cursor;
            }

            public List setCursor(String cursor) {
                this.cursor = cursor;
                return this;
            }

            @com.google.api.client.util.Key
            private String type;

            /**

             */
            public String getType() {
                return type;
            }

            public List setType(String type) {
                this.type = type;
                return this;
            }

            @com.google.api.client.util.Key
            private Integer limit;

            /**

             */
            public Integer getLimit() {
                return limit;
            }

            public List setLimit(Integer limit) {
                this.limit = limit;
                return this;
            }

        }

        /**
         * Create a request for the method "games.patch".
         * <p/>
         * This request holds the parameters needed by the the cloudgoban server.  After setting any
         * optional parameters, call the {@link Patch#execute()} method to invoke the remote operation.
         *
         * @param userKey
         * @param gameKey
         * @param content the {@link com.google.api.services.cloudgoban.model.Game}
         * @return the request
         */
        public Patch patch(String userKey, String gameKey, com.google.api.services.cloudgoban.model.Game content) throws java.io.IOException {
            Patch result = new Patch(userKey, gameKey, content);
            initialize(result);
            return result;
        }

        public class Patch extends CloudgobanRequest<com.google.api.services.cloudgoban.model.Game> {

            private static final String REST_PATH = "game/{user_key}";

            /**
             * Create a request for the method "games.patch".
             * <p/>
             * This request holds the parameters needed by the the cloudgoban server.  After setting any
             * optional parameters, call the {@link Patch#execute()} method to invoke the remote operation.
             * <p> {@link Patch#initialize(AbstractGoogleClientRequest)} must be called to initialize this
             * instance immediately after invoking the constructor. </p>
             *
             * @param userKey
             * @param gameKey
             * @param content the {@link com.google.api.services.cloudgoban.model.Game}
             * @since 1.13
             */
            protected Patch(String userKey, String gameKey, com.google.api.services.cloudgoban.model.Game content) {
                super(Cloudgoban.this, "PATCH", REST_PATH, content, com.google.api.services.cloudgoban.model.Game.class);
                this.userKey = Preconditions.checkNotNull(userKey, "Required parameter userKey must be specified.");
                this.gameKey = Preconditions.checkNotNull(gameKey, "Required parameter gameKey must be specified.");
            }

            @Override
            public Patch setAlt(String alt) {
                return (Patch) super.setAlt(alt);
            }

            @Override
            public Patch setFields(String fields) {
                return (Patch) super.setFields(fields);
            }

            @Override
            public Patch setKey(String key) {
                return (Patch) super.setKey(key);
            }

            @Override
            public Patch setOauthToken(String oauthToken) {
                return (Patch) super.setOauthToken(oauthToken);
            }

            @Override
            public Patch setPrettyPrint(Boolean prettyPrint) {
                return (Patch) super.setPrettyPrint(prettyPrint);
            }

            @Override
            public Patch setQuotaUser(String quotaUser) {
                return (Patch) super.setQuotaUser(quotaUser);
            }

            @Override
            public Patch setUserIp(String userIp) {
                return (Patch) super.setUserIp(userIp);
            }

            @com.google.api.client.util.Key("user_key")
            private String userKey;

            /**

             */
            public String getUserKey() {
                return userKey;
            }

            public Patch setUserKey(String userKey) {
                this.userKey = userKey;
                return this;
            }

            @com.google.api.client.util.Key("game_key")
            private String gameKey;

            /**

             */
            public String getGameKey() {
                return gameKey;
            }

            public Patch setGameKey(String gameKey) {
                this.gameKey = gameKey;
                return this;
            }

        }

        /**
         * Create a request for the method "games.update".
         * <p/>
         * This request holds the parameters needed by the the cloudgoban server.  After setting any
         * optional parameters, call the {@link Update#execute()} method to invoke the remote operation.
         *
         * @param userKey
         * @param content the {@link com.google.api.services.cloudgoban.model.Game}
         * @return the request
         */
        public Update update(String userKey, com.google.api.services.cloudgoban.model.Game content) throws java.io.IOException {
            Update result = new Update(userKey, content);
            initialize(result);
            return result;
        }

        public class Update extends CloudgobanRequest<com.google.api.services.cloudgoban.model.Game> {

            private static final String REST_PATH = "game/{user_key}";

            /**
             * Create a request for the method "games.update".
             * <p/>
             * This request holds the parameters needed by the the cloudgoban server.  After setting any
             * optional parameters, call the {@link Update#execute()} method to invoke the remote operation.
             * <p> {@link Update#initialize(AbstractGoogleClientRequest)} must be called to initialize this
             * instance immediately after invoking the constructor. </p>
             *
             * @param userKey
             * @param content the {@link com.google.api.services.cloudgoban.model.Game}
             * @since 1.13
             */
            protected Update(String userKey, com.google.api.services.cloudgoban.model.Game content) {
                super(Cloudgoban.this, "PUT", REST_PATH, content, com.google.api.services.cloudgoban.model.Game.class);
                this.userKey = Preconditions.checkNotNull(userKey, "Required parameter userKey must be specified.");
            }

            @Override
            public Update setAlt(String alt) {
                return (Update) super.setAlt(alt);
            }

            @Override
            public Update setFields(String fields) {
                return (Update) super.setFields(fields);
            }

            @Override
            public Update setKey(String key) {
                return (Update) super.setKey(key);
            }

            @Override
            public Update setOauthToken(String oauthToken) {
                return (Update) super.setOauthToken(oauthToken);
            }

            @Override
            public Update setPrettyPrint(Boolean prettyPrint) {
                return (Update) super.setPrettyPrint(prettyPrint);
            }

            @Override
            public Update setQuotaUser(String quotaUser) {
                return (Update) super.setQuotaUser(quotaUser);
            }

            @Override
            public Update setUserIp(String userIp) {
                return (Update) super.setUserIp(userIp);
            }

            @com.google.api.client.util.Key("user_key")
            private String userKey;

            /**

             */
            public String getUserKey() {
                return userKey;
            }

            public Update setUserKey(String userKey) {
                this.userKey = userKey;
                return this;
            }

        }

    }

    /**
     * An accessor for creating requests from the Participation collection.
     * <p/>
     * <p>The typical use is:</p>
     * <pre>
     *   {@code Cloudgoban cloudgoban = new Cloudgoban(...);}
     *   {@code Cloudgoban.Participation.List request = cloudgoban.participation().list(parameters ...)}
     * </pre>
     *
     * @return the resource collection
     */
    public Participation participation() {
        return new Participation();
    }

    /**
     * The "participation" collection of methods.
     */
    public class Participation {

        /**
         * Create a request for the method "participation.delete".
         * <p/>
         * This request holds the parameters needed by the the cloudgoban server.  After setting any
         * optional parameters, call the {@link Delete#execute()} method to invoke the remote operation.
         *
         * @param contact
         * @param gameKey
         * @return the request
         */
        public Delete delete(String contact, String gameKey) throws java.io.IOException {
            Delete result = new Delete(contact, gameKey);
            initialize(result);
            return result;
        }

        public class Delete extends CloudgobanRequest<com.google.api.services.cloudgoban.model.CloudgobanVoid> {

            private static final String REST_PATH = "delete/{contact}/{game_key}";

            /**
             * Create a request for the method "participation.delete".
             * <p/>
             * This request holds the parameters needed by the the cloudgoban server.  After setting any
             * optional parameters, call the {@link Delete#execute()} method to invoke the remote operation.
             * <p> {@link Delete#initialize(AbstractGoogleClientRequest)} must be called to initialize this
             * instance immediately after invoking the constructor. </p>
             *
             * @param contact
             * @param gameKey
             * @since 1.13
             */
            protected Delete(String contact, String gameKey) {
                super(Cloudgoban.this, "DELETE", REST_PATH, null, com.google.api.services.cloudgoban.model.CloudgobanVoid.class);
                this.contact = Preconditions.checkNotNull(contact, "Required parameter contact must be specified.");
                this.gameKey = Preconditions.checkNotNull(gameKey, "Required parameter gameKey must be specified.");
            }

            @Override
            public Delete setAlt(String alt) {
                return (Delete) super.setAlt(alt);
            }

            @Override
            public Delete setFields(String fields) {
                return (Delete) super.setFields(fields);
            }

            @Override
            public Delete setKey(String key) {
                return (Delete) super.setKey(key);
            }

            @Override
            public Delete setOauthToken(String oauthToken) {
                return (Delete) super.setOauthToken(oauthToken);
            }

            @Override
            public Delete setPrettyPrint(Boolean prettyPrint) {
                return (Delete) super.setPrettyPrint(prettyPrint);
            }

            @Override
            public Delete setQuotaUser(String quotaUser) {
                return (Delete) super.setQuotaUser(quotaUser);
            }

            @Override
            public Delete setUserIp(String userIp) {
                return (Delete) super.setUserIp(userIp);
            }

            @com.google.api.client.util.Key
            private String contact;

            /**

             */
            public String getContact() {
                return contact;
            }

            public Delete setContact(String contact) {
                this.contact = contact;
                return this;
            }

            @com.google.api.client.util.Key("game_key")
            private String gameKey;

            /**

             */
            public String getGameKey() {
                return gameKey;
            }

            public Delete setGameKey(String gameKey) {
                this.gameKey = gameKey;
                return this;
            }

        }

        /**
         * Create a request for the method "participation.insert".
         * <p/>
         * This request holds the parameters needed by the the cloudgoban server.  After setting any
         * optional parameters, call the {@link Insert#execute()} method to invoke the remote operation.
         *
         * @param content the {@link com.google.api.services.cloudgoban.model.GoGameParticipation}
         * @return the request
         */
        public Insert insert(com.google.api.services.cloudgoban.model.GoGameParticipation content) throws java.io.IOException {
            Insert result = new Insert(content);
            initialize(result);
            return result;
        }

        public class Insert extends CloudgobanRequest<com.google.api.services.cloudgoban.model.GoGameParticipation> {

            private static final String REST_PATH = "gogameparticipation";

            /**
             * Create a request for the method "participation.insert".
             * <p/>
             * This request holds the parameters needed by the the cloudgoban server.  After setting any
             * optional parameters, call the {@link Insert#execute()} method to invoke the remote operation.
             * <p> {@link Insert#initialize(AbstractGoogleClientRequest)} must be called to initialize this
             * instance immediately after invoking the constructor. </p>
             *
             * @param content the {@link com.google.api.services.cloudgoban.model.GoGameParticipation}
             * @since 1.13
             */
            protected Insert(com.google.api.services.cloudgoban.model.GoGameParticipation content) {
                super(Cloudgoban.this, "POST", REST_PATH, content, com.google.api.services.cloudgoban.model.GoGameParticipation.class);
            }

            @Override
            public Insert setAlt(String alt) {
                return (Insert) super.setAlt(alt);
            }

            @Override
            public Insert setFields(String fields) {
                return (Insert) super.setFields(fields);
            }

            @Override
            public Insert setKey(String key) {
                return (Insert) super.setKey(key);
            }

            @Override
            public Insert setOauthToken(String oauthToken) {
                return (Insert) super.setOauthToken(oauthToken);
            }

            @Override
            public Insert setPrettyPrint(Boolean prettyPrint) {
                return (Insert) super.setPrettyPrint(prettyPrint);
            }

            @Override
            public Insert setQuotaUser(String quotaUser) {
                return (Insert) super.setQuotaUser(quotaUser);
            }

            @Override
            public Insert setUserIp(String userIp) {
                return (Insert) super.setUserIp(userIp);
            }

        }

    }

    /**
     * An accessor for creating requests from the Serverstatus collection.
     * <p/>
     * <p>The typical use is:</p>
     * <pre>
     *   {@code Cloudgoban cloudgoban = new Cloudgoban(...);}
     *   {@code Cloudgoban.Serverstatus.List request = cloudgoban.serverstatus().list(parameters ...)}
     * </pre>
     *
     * @return the resource collection
     */
    public Serverstatus serverstatus() {
        return new Serverstatus();
    }

    /**
     * The "serverstatus" collection of methods.
     */
    public class Serverstatus {

        /**
         * Create a request for the method "serverstatus.get".
         * <p/>
         * This request holds the parameters needed by the the cloudgoban server.  After setting any
         * optional parameters, call the {@link Get#execute()} method to invoke the remote operation.
         *
         * @return the request
         */
        public Get get() throws java.io.IOException {
            Get result = new Get();
            initialize(result);
            return result;
        }

        public class Get extends CloudgobanRequest<com.google.api.services.cloudgoban.model.ServerStatus> {

            private static final String REST_PATH = "serverstatus";

            /**
             * Create a request for the method "serverstatus.get".
             * <p/>
             * This request holds the parameters needed by the the cloudgoban server.  After setting any
             * optional parameters, call the {@link Get#execute()} method to invoke the remote operation. <p>
             * {@link Get#initialize(AbstractGoogleClientRequest)} must be called to initialize this instance
             * immediately after invoking the constructor. </p>
             *
             * @since 1.13
             */
            protected Get() {
                super(Cloudgoban.this, "GET", REST_PATH, null, com.google.api.services.cloudgoban.model.ServerStatus.class);
            }

            @Override
            public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
                return super.executeUsingHead();
            }

            @Override
            public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
                return super.buildHttpRequestUsingHead();
            }

            @Override
            public Get setAlt(String alt) {
                return (Get) super.setAlt(alt);
            }

            @Override
            public Get setFields(String fields) {
                return (Get) super.setFields(fields);
            }

            @Override
            public Get setKey(String key) {
                return (Get) super.setKey(key);
            }

            @Override
            public Get setOauthToken(String oauthToken) {
                return (Get) super.setOauthToken(oauthToken);
            }

            @Override
            public Get setPrettyPrint(Boolean prettyPrint) {
                return (Get) super.setPrettyPrint(prettyPrint);
            }

            @Override
            public Get setQuotaUser(String quotaUser) {
                return (Get) super.setQuotaUser(quotaUser);
            }

            @Override
            public Get setUserIp(String userIp) {
                return (Get) super.setUserIp(userIp);
            }

        }

    }

    /**
     * An accessor for creating requests from the Test collection.
     * <p/>
     * <p>The typical use is:</p>
     * <pre>
     *   {@code Cloudgoban cloudgoban = new Cloudgoban(...);}
     *   {@code Cloudgoban.Test.List request = cloudgoban.test().list(parameters ...)}
     * </pre>
     *
     * @return the resource collection
     */
    public Test test() {
        return new Test();
    }

    /**
     * The "test" collection of methods.
     */
    public class Test {

        /**
         * Create a request for the method "test.test".
         * <p/>
         * This request holds the parameters needed by the the cloudgoban server.  After setting any
         * optional parameters, call the {@link TestRequest#execute()} method to invoke the remote
         * operation.
         *
         * @param content the {@link com.google.api.services.cloudgoban.model.User}
         * @return the request
         */
        public TestRequest test(com.google.api.services.cloudgoban.model.User content) throws java.io.IOException {
            TestRequest result = new TestRequest(content);
            initialize(result);
            return result;
        }

        public class TestRequest extends CloudgobanRequest<com.google.api.services.cloudgoban.model.Text> {

            private static final String REST_PATH = "test";

            /**
             * Create a request for the method "test.test".
             * <p/>
             * This request holds the parameters needed by the the cloudgoban server.  After setting any
             * optional parameters, call the {@link TestRequest#execute()} method to invoke the remote
             * operation. <p> {@link TestRequest#initialize(AbstractGoogleClientRequest)} must be called to
             * initialize this instance immediately after invoking the constructor. </p>
             *
             * @param content the {@link com.google.api.services.cloudgoban.model.User}
             * @since 1.13
             */
            protected TestRequest(com.google.api.services.cloudgoban.model.User content) {
                super(Cloudgoban.this, "POST", REST_PATH, content, com.google.api.services.cloudgoban.model.Text.class);
            }

            @Override
            public TestRequest setAlt(String alt) {
                return (TestRequest) super.setAlt(alt);
            }

            @Override
            public TestRequest setFields(String fields) {
                return (TestRequest) super.setFields(fields);
            }

            @Override
            public TestRequest setKey(String key) {
                return (TestRequest) super.setKey(key);
            }

            @Override
            public TestRequest setOauthToken(String oauthToken) {
                return (TestRequest) super.setOauthToken(oauthToken);
            }

            @Override
            public TestRequest setPrettyPrint(Boolean prettyPrint) {
                return (TestRequest) super.setPrettyPrint(prettyPrint);
            }

            @Override
            public TestRequest setQuotaUser(String quotaUser) {
                return (TestRequest) super.setQuotaUser(quotaUser);
            }

            @Override
            public TestRequest setUserIp(String userIp) {
                return (TestRequest) super.setUserIp(userIp);
            }

        }

    }

    /**
     * An accessor for creating requests from the Users collection.
     * <p/>
     * <p>The typical use is:</p>
     * <pre>
     *   {@code Cloudgoban cloudgoban = new Cloudgoban(...);}
     *   {@code Cloudgoban.Users.List request = cloudgoban.users().list(parameters ...)}
     * </pre>
     *
     * @return the resource collection
     */
    public Users users() {
        return new Users();
    }

    /**
     * The "users" collection of methods.
     */
    public class Users {

        /**
         * Create a request for the method "users.list".
         * <p/>
         * This request holds the parameters needed by the the cloudgoban server.  After setting any
         * optional parameters, call the {@link List#execute()} method to invoke the remote operation.
         *
         * @return the request
         */
        public List list() throws java.io.IOException {
            List result = new List();
            initialize(result);
            return result;
        }

        public class List extends CloudgobanRequest<com.google.api.services.cloudgoban.model.UserCollection> {

            private static final String REST_PATH = "user";

            /**
             * Create a request for the method "users.list".
             * <p/>
             * This request holds the parameters needed by the the cloudgoban server.  After setting any
             * optional parameters, call the {@link List#execute()} method to invoke the remote operation. <p>
             * {@link List#initialize(AbstractGoogleClientRequest)} must be called to initialize this instance
             * immediately after invoking the constructor. </p>
             *
             * @since 1.13
             */
            protected List() {
                super(Cloudgoban.this, "GET", REST_PATH, null, com.google.api.services.cloudgoban.model.UserCollection.class);
            }

            @Override
            public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
                return super.executeUsingHead();
            }

            @Override
            public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
                return super.buildHttpRequestUsingHead();
            }

            @Override
            public List setAlt(String alt) {
                return (List) super.setAlt(alt);
            }

            @Override
            public List setFields(String fields) {
                return (List) super.setFields(fields);
            }

            @Override
            public List setKey(String key) {
                return (List) super.setKey(key);
            }

            @Override
            public List setOauthToken(String oauthToken) {
                return (List) super.setOauthToken(oauthToken);
            }

            @Override
            public List setPrettyPrint(Boolean prettyPrint) {
                return (List) super.setPrettyPrint(prettyPrint);
            }

            @Override
            public List setQuotaUser(String quotaUser) {
                return (List) super.setQuotaUser(quotaUser);
            }

            @Override
            public List setUserIp(String userIp) {
                return (List) super.setUserIp(userIp);
            }

            @com.google.api.client.util.Key
            private String cursor;

            /**

             */
            public String getCursor() {
                return cursor;
            }

            public List setCursor(String cursor) {
                this.cursor = cursor;
                return this;
            }

            @com.google.api.client.util.Key
            private String filter;

            /**

             */
            public String getFilter() {
                return filter;
            }

            public List setFilter(String filter) {
                this.filter = filter;
                return this;
            }

            @com.google.api.client.util.Key
            private Integer limit;

            /**

             */
            public Integer getLimit() {
                return limit;
            }

            public List setLimit(Integer limit) {
                this.limit = limit;
                return this;
            }

        }

        /**
         * Create a request for the method "users.put".
         * <p/>
         * This request holds the parameters needed by the the cloudgoban server.  After setting any
         * optional parameters, call the {@link Put#execute()} method to invoke the remote operation.
         *
         * @param content the {@link com.google.api.services.cloudgoban.model.User}
         * @return the request
         */
        public Put put(com.google.api.services.cloudgoban.model.User content) throws java.io.IOException {
            Put result = new Put(content);
            initialize(result);
            return result;
        }

        public class Put extends CloudgobanRequest<com.google.api.services.cloudgoban.model.User> {

            private static final String REST_PATH = "put";

            /**
             * Create a request for the method "users.put".
             * <p/>
             * This request holds the parameters needed by the the cloudgoban server.  After setting any
             * optional parameters, call the {@link Put#execute()} method to invoke the remote operation. <p>
             * {@link Put#initialize(AbstractGoogleClientRequest)} must be called to initialize this instance
             * immediately after invoking the constructor. </p>
             *
             * @param content the {@link com.google.api.services.cloudgoban.model.User}
             * @since 1.13
             */
            protected Put(com.google.api.services.cloudgoban.model.User content) {
                super(Cloudgoban.this, "POST", REST_PATH, content, com.google.api.services.cloudgoban.model.User.class);
            }

            @Override
            public Put setAlt(String alt) {
                return (Put) super.setAlt(alt);
            }

            @Override
            public Put setFields(String fields) {
                return (Put) super.setFields(fields);
            }

            @Override
            public Put setKey(String key) {
                return (Put) super.setKey(key);
            }

            @Override
            public Put setOauthToken(String oauthToken) {
                return (Put) super.setOauthToken(oauthToken);
            }

            @Override
            public Put setPrettyPrint(Boolean prettyPrint) {
                return (Put) super.setPrettyPrint(prettyPrint);
            }

            @Override
            public Put setQuotaUser(String quotaUser) {
                return (Put) super.setQuotaUser(quotaUser);
            }

            @Override
            public Put setUserIp(String userIp) {
                return (Put) super.setUserIp(userIp);
            }

        }

    }

    /**
     * Builder for {@link Cloudgoban}.
     * <p/>
     * <p>
     * Implementation is not thread-safe.
     * </p>
     *
     * @since 1.3.0
     */
    public static final class Builder extends AbstractGoogleJsonClient.Builder {

        /**
         * Returns an instance of a new builder.
         *
         * @param transport              HTTP transport
         * @param jsonFactory            JSON factory
         * @param httpRequestInitializer HTTP request initializer or {@code null} for none
         * @since 1.7
         */
        public Builder(HttpTransport transport, JsonFactory jsonFactory,
                       HttpRequestInitializer httpRequestInitializer) {
            super(
                    transport,
                    jsonFactory,
                    DEFAULT_ROOT_URL,
                    DEFAULT_SERVICE_PATH,
                    httpRequestInitializer,
                    false);
        }

        /**
         * Builds a new instance of {@link Cloudgoban}.
         */
        @Override
        public Cloudgoban build() {
            return new Cloudgoban(getTransport(),
                    getHttpRequestInitializer(),
                    getRootUrl(),
                    getServicePath(),
                    getObjectParser(),
                    getGoogleClientRequestInitializer(),
                    getApplicationName(),
                    getSuppressPatternChecks());
        }

        @Override
        public Builder setRootUrl(String rootUrl) {
            return (Builder) super.setRootUrl(rootUrl);
        }

        @Override
        public Builder setServicePath(String servicePath) {
            return (Builder) super.setServicePath(servicePath);
        }

        @Override
        public Builder setHttpRequestInitializer(HttpRequestInitializer httpRequestInitializer) {
            return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
        }

        @Override
        public Builder setApplicationName(String applicationName) {
            return (Builder) super.setApplicationName(applicationName);
        }

        @Override
        public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
            return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
        }

        /**
         * Set the {@link CloudgobanRequestInitializer}.
         *
         * @since 1.12
         */
        public Builder setCloudgobanRequestInitializer(
                CloudgobanRequestInitializer cloudgobanRequestInitializer) {
            return (Builder) super.setGoogleClientRequestInitializer(cloudgobanRequestInitializer);
        }

        @Override
        public Builder setGoogleClientRequestInitializer(
                GoogleClientRequestInitializer googleClientRequestInitializer) {
            return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
        }
    }
}
