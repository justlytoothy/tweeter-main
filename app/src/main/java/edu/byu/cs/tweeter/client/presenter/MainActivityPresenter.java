package edu.byu.cs.tweeter.client.presenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
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




    public class LogoutObserver implements UserService.LogoutObserver {
        @Override
        public void handleSuccess() {
            view.logoutSuccess();
        }
        @Override
        public void handleFailure(String message) {
            view.displayMessage(message);
        }
    }
    public class FollowingCountObserver implements FollowService.FollowingCountObserver {

        @Override
        public void displayError(String message) {
            view.displayMessage(message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to get following count because of exception: " + ex.getMessage());
        }

        @Override
        public void onSuccess(int count) {
            view.followingCountReceived(count);
        }
    }
    public class FollowersCountObserver implements FollowService.FollowersCountObserver {

        @Override
        public void displayError(String message) {
            view.displayMessage(message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to get follower count because of exception: " + ex.getMessage());
        }

        @Override
        public void onSuccess(int count) {
            view.followerCountReceived(count);
        }
    }
    public class IsFollowerObserver implements FollowService.IsFollowerObserver {

        @Override
        public void displayError(String message) {
            view.displayMessage(message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to determine following relationship because of exception: " + ex.getMessage());
        }

        @Override
        public void onSuccess(boolean isFollower) {
            view.isFollowerReceived(isFollower);
        }
    }
    public class UnfollowObserver implements FollowService.UnfollowObserver {

        @Override
        public void displayError(String message) {
            view.displayMessage(message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to unfollow because of exception: " + ex.getMessage());

        }

        @Override
        public void onSuccess() {
            view.updateFollowingOrNot(true);
        }
    }
    public class FollowObserver implements FollowService.FollowObserver {

        @Override
        public void displayError(String message) {
            view.displayMessage(message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to follow because of exception: " + ex.getMessage());
        }

        @Override
        public void onSuccess() {
            view.updateFollowingOrNot(false);
        }
    }
    public class PostStatusObserver implements StatusService.PostStatusObserver {

        @Override
        public void displayError(String message) {
            view.displayMessage(message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to post the status because of exception: " + ex.getMessage());
        }

        @Override
        public void onSuccess() {
            view.statusPosted();
        }
    }

}
