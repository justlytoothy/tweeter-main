package edu.byu.cs.tweeter.client.model.backgroundTask.observer;

import android.os.Bundle;

import java.util.List;

public interface PagedNotificationObserver<T> extends ServiceObserver {
    void handleSuccess(List<T> list, boolean morePages);
}
