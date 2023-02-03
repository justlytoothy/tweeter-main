package edu.byu.cs.tweeter.client.presenter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.main.following.FollowingFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowersPresenter {
    public interface View {
        void displayMessage(String message);
        void setLoadingFooter(boolean set);
        void addMoreItems(List<User> followers);
        void userReceived(User user);
    }
    private View view;
    private static final int PAGE_SIZE = 10;

    public void setLastFollower(User lastFollower) {
        this.lastFollower = lastFollower;
    }

    private User lastFollower;

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    private boolean hasMorePages;

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    private boolean isLoading = false;
    private FollowService followService;
    private UserService userService;


    public GetFollowersPresenter(View view) {
        this.view = view;
        followService = new FollowService();
        userService = new UserService();
    }





    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingFooter(true);
            followService.loadMoreItemsFollowers(user,PAGE_SIZE,lastFollower, new GetFollowersObserver());
        }
    }
    public void getUser(AuthToken authToken, String username) {
        userService.getUser(authToken,username, new GetUserObserver());
    }

    public class GetFollowersObserver implements FollowService.Observer{

        @Override
        public void displayError(String message) {
            setLoading(false);
            view.setLoadingFooter(isLoading);
            view.displayMessage(message);
        }
        @Override
        public void displayException(Exception ex) {
            setLoading(false);
            view.setLoadingFooter(isLoading);
            view.displayMessage("Failed to get followers because of exception: " + ex.getMessage());
        }
        @Override
        public void onSuccess(List<User> followers, boolean morePages) {
            setLoading(false);
            view.setLoadingFooter(isLoading);
            setHasMorePages(morePages);
            setLastFollower((followers.size() > 0) ? followers.get(followers.size() - 1) : null);
            view.addMoreItems(followers);
        }


    }
    public class GetUserObserver implements UserService.GetUserObserver {

        @Override
        public void handleSuccess(User user) {
            view.userReceived(user);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage(message);
        }

    }
}
