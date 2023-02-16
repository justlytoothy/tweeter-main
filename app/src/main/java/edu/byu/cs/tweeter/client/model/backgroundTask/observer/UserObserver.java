package edu.byu.cs.tweeter.client.model.backgroundTask.observer;

import edu.byu.cs.tweeter.model.domain.User;

public interface UserObserver {
    void handleSuccess(User user);
    void handleFailure(String message);
}