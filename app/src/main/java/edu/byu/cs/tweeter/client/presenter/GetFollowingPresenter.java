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

public class GetFollowingPresenter {

    private View view;
    private static final int PAGE_SIZE = 10;
    private boolean isLoading = false;
    private FollowService followService;
    private UserService userService;
    private User lastFollowee;

    public interface View {
        void displayMessage(String message);
        void setLoadingFooter(boolean set);
        void addMoreItems(List<User> followees);
        void userReceived(User user);
    }

    public GetFollowingPresenter(View view) {
        this.view = view;
        followService = new FollowService();
        userService = new UserService();
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingFooter(true);
            followService.loadMoreItems(user,PAGE_SIZE,lastFollowee, new GetFollowingObserver());
        }
    }

    public void getUser(AuthToken authToken, String username) {
        userService.getUser(authToken,username, new GetUserObserver());
    }

    public class GetFollowingObserver implements FollowService.Observer{

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
            view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
        }
        @Override
        public void onSuccess(List<User> followees, boolean morePages) {
            setLoading(false);
            view.setLoadingFooter(isLoading);
            setHasMorePages(morePages);
            setLastFollowee((followees.size() > 0) ? followees.get(followees.size() - 1) : null);
            view.addMoreItems(followees);
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

    public void setLastFollowee(User lastFollowee) {
        this.lastFollowee = lastFollowee;
    }

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
}
