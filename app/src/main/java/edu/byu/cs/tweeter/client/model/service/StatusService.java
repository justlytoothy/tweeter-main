package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.PagedNotificationHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {

    public void loadMoreItems(User user, int pageSize, Status lastStatus, PagedNotificationObserver<Status> getStoryObserver) {
        GetStoryTask getStoryTask = new GetStoryTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastStatus, new PagedNotificationHandler<Status>(getStoryObserver));
        ServiceExecutor.execute(getStoryTask);
    }

    public void loadMoreItemsFeed(User user, int pageSize, Status lastStatus, PagedNotificationObserver<Status> getFeedObserver) {
        GetFeedTask getFeedTask = new GetFeedTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastStatus, new PagedNotificationHandler<Status>(getFeedObserver));
        ServiceExecutor.execute(getFeedTask);

    }

    public void postStatus(AuthToken currUserAuthToken, Status newStatus, SimpleNotificationObserver postStatusObserver) {
        PostStatusTask statusTask = new PostStatusTask(currUserAuthToken,
                newStatus, new SimpleNotificationHandler(postStatusObserver));
        ServiceExecutor.execute(statusTask);
    }


}
