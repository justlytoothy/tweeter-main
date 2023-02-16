package edu.byu.cs.tweeter.client.model.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.PagedNotificationObserver;

public class PagedNotificationHandler<T> extends BackgroundTaskHandler<PagedNotificationObserver> {
    public PagedNotificationHandler(PagedNotificationObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, PagedNotificationObserver observer) {
        List<T> list = (List<T>) data.getSerializable(GetFollowersTask.ITEMS_KEY);
        boolean morePages = data.getBoolean(GetFollowersTask.MORE_PAGES_KEY);
        observer.handleSuccess(list,morePages);
    }
}
