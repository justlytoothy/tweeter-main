package edu.byu.cs.tweeter.client.model.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthenticatedTask {

    public LogoutTask(AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
    }

    @Override
    protected void runTask() {
        // We could do this from the presenter, without a task and handler, but we will
        // eventually remove the auth token from  the DB and will need this then.
        try {
            LogoutResponse res = serverFacade.logout(new LogoutRequest(getAuthToken()),"logout");
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
