package edu.byu.cs.tweeter.client.model.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.SendStatusRequest;
import edu.byu.cs.tweeter.model.net.response.SendStatusResponse;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends AuthenticatedTask {

    /**
     * The new status being sent. Contains all properties of the status,
     * including the identity of the user sending the status.
     */
    private final Status status;

    public PostStatusTask(AuthToken authToken, Status status, Handler messageHandler) {
        super(authToken, messageHandler);
        this.status = status;
    }

    @Override
    protected void runTask() {
        // We could do this from the presenter, without a task and handler, but we will
        try {
            SendStatusResponse res = serverFacade.sendStatus(new SendStatusRequest(status,getAuthToken()),"status");
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
