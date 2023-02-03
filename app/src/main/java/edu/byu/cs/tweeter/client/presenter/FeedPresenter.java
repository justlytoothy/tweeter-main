package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.main.feed.FeedFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter {
    private View view;
    private UserService userService;
    private StatusService statusService;
    private Status lastStatus;
    public FeedPresenter(View view) {
        this.view = view;
        userService = new UserService();
        statusService = new StatusService();
    }
    public Status getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(Status lastStatus) {
        this.lastStatus = lastStatus;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    private boolean hasMorePages;
    private boolean isLoading = false;
    private static final int PAGE_SIZE = 10;


    public interface View {
        void displayMessage(String message);
        void setLoadingFooter(boolean set);
        void addMoreItems(List<Status> statuses);
        void userReceived(User user);
    }
    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingFooter(true);
            statusService.loadMoreItemsFeed(user,PAGE_SIZE,lastStatus, new FeedPresenter.GetFeedObserver());
        }
    }

    public void getUser(AuthToken authToken, String username) {
        userService.getUser(authToken,username, new FeedPresenter.GetUserObserver());
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
    public class GetFeedObserver implements StatusService.GetFeedObserver {

        @Override
        public void displayError(String message) {
            view.displayMessage(message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to get feed because of exception: " + ex.getMessage());
        }

        @Override
        public void onSuccess(List<Status> statuses, boolean morePages) {
            setLoading(false);
            view.setLoadingFooter(isLoading);
            setHasMorePages(morePages);
            setLastStatus((statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null);
            view.addMoreItems(statuses);
        }
    }
}
