package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

/**
 * Contains all the information needed to make a request to have the server return the next page of
 * stories for a specified story.
 */
public class StatusesRequest {

    private AuthToken authToken;
    private String userAlias;
    private int limit;
    private Status lastStatus;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private StatusesRequest() {}

    /**
     * Creates an instance.
     *
     * @param userAlias the alias of the user whose stories are to be returned.
     * @param limit the maximum number of stories to return.
     * @param lastStatus the alias of the last story that was returned in the previous request (null if
     *                     there was no previous request or if no stories were returned in the
     *                     previous request).
     */
    public StatusesRequest(AuthToken authToken, String userAlias, int limit, Status lastStatus) {
        this.authToken = authToken;
        this.userAlias = userAlias;
        this.limit = limit;
        this.lastStatus = lastStatus;
    }

    /**
     * Returns the auth token of the user who is making the request.
     *
     * @return the auth token.
     */
    public AuthToken getAuthToken() {
        return authToken;
    }

    /**
     * Sets the auth token.
     *
     * @param authToken the auth token.
     */
    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    /**
     * Returns the user whose stories are to be returned by this request.
     *
     * @return the story.
     */
    public String getUserAlias() {
        return userAlias;
    }

    /**
     * Sets the story.
     *
     * @param userAlias the story.
     */
    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    /**
     * Returns the number representing the maximum number of stories to be returned by this request.
     *
     * @return the limit.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Sets the limit.
     *
     * @param limit the limit.
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Returns the last story that was returned in the previous request or null if there was no
     * previous request or if no stories were returned in the previous request.
     *
     * @return the last story.
     */
    public Status getLastStatus() {
        return lastStatus;
    }

    /**
     * Sets the last story.
     *
     * @param lastStatus the last story.
     */
    public void setLastStatus(Status lastStatus) {
        this.lastStatus = lastStatus;
    }
}
