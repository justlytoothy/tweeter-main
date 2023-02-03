package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter {
    private View view;
    private UserService userService;
    private StatusService statusService;
    private Status lastStatus;
    private boolean hasMorePages;
    private boolean isLoading = false;
    private static final int PAGE_SIZE = 10;

    public interface View {
        void displayMessage(String message);
        void userReceived(User user);
        void setLoadingFooter(boolean isLoading);
        void addMoreItems(List<Status> statuses);
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingFooter(true);
            statusService.loadMoreItems(user,PAGE_SIZE,lastStatus, new StoryPresenter.GetStoryObserver());
        }
    }

    public StoryPresenter(StoryPresenter.View view) {
        this.view = view;
        userService = new UserService();
        statusService = new StatusService();
    }

    public void getUser(AuthToken authToken, String username) {
        userService.getUser(authToken,username, new StoryPresenter.GetUserObserver());
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
    public class GetStoryObserver implements StatusService.GetStoryObserver {

        @Override
        public void displayError(String message) {
            view.displayMessage(message);
        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to get story because of exception: " + ex.getMessage());
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
}
