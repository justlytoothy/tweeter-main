package edu.byu.cs.tweeter.client.presenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.FollowerObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.GetCountObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.SimpleNotificationObserver;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter {
    private final View view;
    private UserService userService;
    private final FollowService followService;
    private StatusService statusService;

    public interface View extends BaseView {
        void logoutSuccess();
        void followingCountReceived(int count);
        void followerCountReceived(int count);
        void isFollowerReceived(boolean isFollower);
        void updateFollowingOrNot(boolean followingStatus);
        void statusPosted();
    }

    public MainActivityPresenter(View view) {
        this.view = view;
        followService = new FollowService();
    }

    protected UserService getUserService() {
        if (userService == null) {
            userService = new UserService();
        }
        return userService;
    }
    protected StatusService getStatusService() {
        if (statusService == null) {
            statusService = new StatusService();
        }
        return statusService;
    }

    public void logout(AuthToken currUserAuthToken) {
        view.displayMessage("Logging Out...");
        userService = this.getUserService();
        getUserService().logout(currUserAuthToken, new LogoutObserver());
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

    public void postStatus(AuthToken currUserAuthToken, String post, User currUser) {
        view.displayMessage("Posting Status...");
//        try {
            Status newStatus = new Status(post,currUser,System.currentTimeMillis(),parseURLs(post),parseMentions(post));
            getStatusService().postStatus(currUserAuthToken,newStatus, new PostStatusObserver());
//        }
//        catch(ParseException e) {
//            new PostStatusObserver().handleException(e);
//        }

    }

    public class LogoutObserver implements SimpleNotificationObserver {
        @Override
        public void handleSuccess() {
            Cache.getInstance().clearCache();
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


    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }
    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }
    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }
    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }
}
