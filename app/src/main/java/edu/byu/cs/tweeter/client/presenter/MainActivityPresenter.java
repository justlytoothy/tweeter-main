package edu.byu.cs.tweeter.client.presenter;

import java.text.ParseException;

import edu.byu.cs.tweeter.client.model.backgroundTask.observer.FollowerObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.GetCountObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.SimpleNotificationObserver;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter {
    private View view;
    private UserService userService;
    private FollowService followService;
    private StatusService statusService;

    public interface View {
        void displayMessage(String message);
        void logoutSuccess();
        void followingCountReceived(int count);
        void followerCountReceived(int count);
        void isFollowerReceived(boolean isFollower);
        void updateFollowingOrNot(boolean followingStatus);
        void statusPosted();
    }

    public MainActivityPresenter(View view) {
        this.view = view;
        userService = new UserService();
        followService = new FollowService();
        statusService = new StatusService();
    }

    public void logout(AuthToken currUserAuthToken) {
        userService.logout(currUserAuthToken, new LogoutObserver());
    }
    public void getFollowingCount(AuthToken authToken, User user) {
        followService.getFollowingCount(authToken,user, new FollowingCountObserver(),new FollowersCountObserver());
    }
    public void getIsFollower(AuthToken authToken, User currUser, User selectedUser) {
        followService.getIsFollowing(authToken,currUser,selectedUser, new IsFollowerObserver());
    }

    public void unfollow(AuthToken currUserAuthToken, User selectedUser) {
        followService.unfollow(currUserAuthToken,selectedUser, new UnfollowObserver());
    }

    public void follow(AuthToken currUserAuthToken, User selectedUser) {
        followService.follow(currUserAuthToken,selectedUser, new FollowObserver());

    }

    public void postStatus(AuthToken currUserAuthToken, String post, User currUser) throws ParseException {
        statusService.postStatus(currUserAuthToken,post,currUser, new PostStatusObserver());
    }

    public class LogoutObserver implements SimpleNotificationObserver {
        @Override
        public void handleSuccess() {
            view.logoutSuccess();
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to log out: " + message);
        }
        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to log out because of exception: " + ex.getMessage());
        }
    }
    public class FollowingCountObserver implements GetCountObserver {

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get following count: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to get following count because of exception: " + ex.getMessage());
        }

        @Override
        public void handleSuccess(int count) {
            view.followingCountReceived(count);
        }
    }
    public class FollowersCountObserver implements GetCountObserver {

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get followers count: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to get follower count because of exception: " + ex.getMessage());
        }

        @Override
        public void handleSuccess(int count) {
            view.followerCountReceived(count);
        }
    }
    public class IsFollowerObserver implements FollowerObserver {

        @Override
        public void handleFailure(String message) {
            view.displayMessage(message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to determine following relationship because of exception: " + ex.getMessage());
        }

        @Override
        public void handleSuccess(boolean isFollower) {
            view.isFollowerReceived(isFollower);
        }
    }
    public class UnfollowObserver implements SimpleNotificationObserver {

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to unfollow: " + message);
        }
        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to unfollow because of exception: " + ex.getMessage());

        }
        @Override
        public void handleSuccess() {
            view.updateFollowingOrNot(true);
        }
    }
    public class FollowObserver implements SimpleNotificationObserver {

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to follow: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to follow because of exception: " + ex.getMessage());
        }

        @Override
        public void handleSuccess() {
            view.updateFollowingOrNot(false);
        }
    }
    public class PostStatusObserver implements SimpleNotificationObserver {

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to post the status: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to post the status because of exception: " + ex.getMessage());
        }

        @Override
        public void handleSuccess() {
            view.statusPosted();
        }
    }

}
