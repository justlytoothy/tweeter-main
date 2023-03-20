package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class IsFollowingRequest {
    private String targetUserAlias;
    private String currentUser;
    private AuthToken authToken;

    private IsFollowingRequest() {}

    public IsFollowingRequest(String targetUserAlias, String currentUser, AuthToken authToken) {
        this.targetUserAlias = targetUserAlias;
        this.currentUser = currentUser;
        this.authToken = authToken;
    }


    public String getTargetUserAlias() {
        return targetUserAlias;
    }

    public void setTargetUserAlias(String targetUserAlias) {
        this.targetUserAlias = targetUserAlias;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }
    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
