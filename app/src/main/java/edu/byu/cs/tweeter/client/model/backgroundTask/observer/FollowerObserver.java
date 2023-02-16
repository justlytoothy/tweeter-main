package edu.byu.cs.tweeter.client.model.backgroundTask.observer;

public interface FollowerObserver {
    void handleFailure(String message);
    void handleException(Exception ex);
    void handleSuccess(boolean isFollower);
}
