package edu.byu.cs.tweeter.client.model.backgroundTask.observer;


public interface GetCountObserver extends ServiceObserver {
    void handleSuccess(int data);
}
