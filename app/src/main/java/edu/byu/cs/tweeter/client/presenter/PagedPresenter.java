package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.backgroundTask.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.UserObserver;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> {
    public PagedView<T> view;
    public boolean isLoading = false;
    public FollowService followService;
    public UserService userService;
    public StatusService statusService;
    public boolean hasMorePages;
    public static final int PAGE_SIZE = 10;
    public User targetUser;
    public AuthToken authToken;
    public boolean isGettingUser;

    public PagedPresenter(PagedView<T> view) {
        this.view = view;
        followService = getFollowService();
        userService = getUserService();
        statusService = getStatusService();
    }
    protected StatusService getStatusService() {
        if (statusService == null) {
            statusService = new StatusService();
        }
        return statusService;
    }
    protected UserService getUserService() {
        if (userService == null) {
            userService = new UserService();
        }
        return userService;
    }
    protected FollowService getFollowService() {
        if (followService == null) {
            followService = new FollowService();
        }
        return followService;
    }
    public T getLastItem() {
        return lastItem;
    }

    public void setLastItem(T lastItem) {
        this.lastItem = lastItem;
    }

    public T lastItem;
    public boolean hasMorePages() {
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

    public void loadMoreItems(User user) {
        this.targetUser = user;
        if (!isLoading) {
            isLoading = true;
            view.setLoadingFooter(true);
            getItems(targetUser, lastItem);
        }
    }

    public void getUser(AuthToken authToken,String user) {

        getUserService().getUser(authToken,user, new GetUserObserver());
    }

    public abstract void getItems(User targetUser, T lastItem);
    public abstract String getDescription();
    public class GetUserObserver implements UserObserver {
        @Override
        public void handleSuccess(User user) {
            view.userReceived(user);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage(message);
        }
    }

    public class PageObserver implements PagedNotificationObserver<T> {
        @Override
        public void handleFailure(String message) {
            setLoading(false);
            view.setLoadingFooter(isLoading);
            view.displayMessage(getDescription() + message);
        }
        @Override
        public void handleException(Exception ex) {
            setLoading(false);
            view.setLoadingFooter(isLoading);
            view.displayMessage(getDescription() + ex.getMessage());
        }
        @Override
        public void handleSuccess(List<T> list, boolean morePages) {
            setLoading(false);
            view.setLoadingFooter(isLoading);
            setHasMorePages(morePages);
            setLastItem((list.size() > 0) ? list.get(list.size() - 1) : null);
            view.addMoreItems(list);
        }
    }

}
