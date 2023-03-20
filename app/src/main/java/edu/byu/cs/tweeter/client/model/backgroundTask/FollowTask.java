package edu.byu.cs.tweeter.client.model.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthenticatedTask {
    /**
     * The user that is being followed.
     */
    private final User followee;
    private final User currentUser;

    public FollowTask(AuthToken authToken, User currentUser, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
        this.currentUser = currentUser;
    }

    @Override
    protected void runTask() {
        try {
            FollowResponse res = serverFacade.follow(new FollowRequest(followee.getAlias(), currentUser.getAlias(), getAuthToken()),"follow");
            if (res.isSuccess()) {
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(res.getMessage());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            sendExceptionMessage(e);
        }

    }
}
