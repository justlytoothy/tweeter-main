package edu.byu.cs.tweeter.client.model.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.GetFollowersCountHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.GetFollowersHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.GetFollowingCountHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.GetFollowingHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {
    public interface Observer {
        void displayError(String message);
        void displayException(Exception ex);
        void handleSuccess(List<User> followees, boolean morePages);
    }
    public interface FollowingCountObserver {
        void displayError(String message);
        void displayException(Exception ex);
        void handleSuccess(int count);
    }
    public interface FollowersCountObserver {
        void displayError(String message);
        void displayException(Exception ex);
        void handleSuccess(int count);
    }
    public interface IsFollowerObserver {
        void displayError(String message);
        void displayException(Exception ex);
        void handleSuccess(boolean isFollower);
    }


    public void loadMoreItems(User user, int pageSize, User lastFollowee, Observer observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollowee, new GetFollowingHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowingTask);
    }
    public void loadMoreItemsFollowers(User user, int pageSize, User lastFollower, Observer observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollower, new GetFollowersHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);
    }
    public void getFollowingCount(AuthToken authToken, User user, FollowingCountObserver followingCountObserver, FollowersCountObserver followersCountObserver) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new GetFollowersCountHandler(followersCountObserver));
        executor.execute(followersCountTask);
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(authToken,
                user, new GetFollowingCountHandler(followingCountObserver));
        executor.execute(followingCountTask);
    }
    public void getIsFollowing(AuthToken authToken, User currUser, User selectedUser, IsFollowerObserver isFollowerObserver) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(authToken,
                currUser, selectedUser, new IsFollowerHandler(isFollowerObserver));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(isFollowerTask);
    }

    public void follow(AuthToken currUserAuthToken, User selectedUser, SimpleNotificationObserver followObserver) {
        FollowTask followTask = new FollowTask(currUserAuthToken,
                selectedUser, new SimpleNotificationHandler(followObserver));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followTask);
    }

    public void unfollow(AuthToken currUserAuthToken, User selectedUser, SimpleNotificationObserver unfollowObserver) {
        UnfollowTask unfollowTask = new UnfollowTask(currUserAuthToken,
                selectedUser, new SimpleNotificationHandler(unfollowObserver));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(unfollowTask);
    }

}
