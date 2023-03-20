package edu.byu.cs.tweeter.client.model.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask {

    /**
     * The user that is being followed.
     */
    private final User followee;
    private final User currentUser;


    public UnfollowTask(AuthToken authToken, User currentUser, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
        this.currentUser = currentUser;
    }

    @Override
    protected void runTask() {
        // We could do this from the presenter, without a task and handler, but we will
        // eventually access the database from here when we aren't using dummy data.
        try {
            FollowResponse res = serverFacade.unfollow(new FollowRequest(followee.getAlias(), currentUser.getAlias(),getAuthToken()),"unfollow");
            if (res.isSuccess()) {
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(res.getMessage());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            sendExceptionMessage(e);
        }
    }


}
