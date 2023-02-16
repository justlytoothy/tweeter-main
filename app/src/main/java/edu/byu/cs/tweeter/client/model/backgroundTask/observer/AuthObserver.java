package edu.byu.cs.tweeter.client.model.backgroundTask.observer;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public interface AuthObserver extends ServiceObserver{
    void handleSuccess(User registeredUser, AuthToken authToken, String s);
}
