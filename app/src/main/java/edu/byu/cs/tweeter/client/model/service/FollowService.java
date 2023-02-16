package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.GetCountHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.PagedNotificationHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.FollowerObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.GetCountObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {


    public void loadMoreItems(User user, int pageSize, User lastFollowee, PagedNotificationObserver<User> observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollowee, new PagedNotificationHandler<User>(observer));
        ServiceExecutor.execute(getFollowingTask);
    }
    public void loadMoreItemsFollowers(User user, int pageSize, User lastFollower, PagedNotificationObserver<User> observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollower, new PagedNotificationHandler<User>(observer));
        ServiceExecutor.execute(getFollowersTask);

    }
    public void getFollowingCount(AuthToken authToken, User user, GetCountObserver followingCountObserver, GetCountObserver followersCountObserver) {
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new GetCountHandler(followersCountObserver));
        ServiceExecutor.execute(followersCountTask);
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(authToken,
                user, new GetCountHandler(followingCountObserver));
        ServiceExecutor.execute(followingCountTask);
    }
    public void getIsFollowing(AuthToken authToken, User currUser, User selectedUser, FollowerObserver isFollowerObserver) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(authToken,
                currUser, selectedUser, new IsFollowerHandler(isFollowerObserver));

        ServiceExecutor.execute(isFollowerTask);

    }

    public void follow(AuthToken currUserAuthToken, User selectedUser, SimpleNotificationObserver followObserver) {
        FollowTask followTask = new FollowTask(currUserAuthToken,
                selectedUser, new SimpleNotificationHandler(followObserver));
        ServiceExecutor.execute(followTask);

    }

    public void unfollow(AuthToken currUserAuthToken, User selectedUser, SimpleNotificationObserver unfollowObserver) {
        UnfollowTask unfollowTask = new UnfollowTask(currUserAuthToken,
                selectedUser, new SimpleNotificationHandler(unfollowObserver));
        ServiceExecutor.execute(unfollowTask);

    }

}
