package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusesPresenter extends PagedPresenter<Status> {
    private String type;
    public StatusesPresenter(PagedView<Status> view,String type) {
        super(view);
        userService = new UserService();
        statusService = new StatusService();
        this.type = type;
    }


    @Override
    public void getItems(User targetUser, Status lastItem) {
        if (this.type.equals("feed")) {
            getStatusService().loadMoreItemsFeed(targetUser,PAGE_SIZE,lastItem, new PageObserver());
        }
        else {
            getStatusService().loadMoreItems(targetUser,PAGE_SIZE,lastItem, new PageObserver());
        }
    }

    @Override
    public String getDescription() {
        return this.type.equals("feed") ? "Failed to get feed: " : "Failed to get story: ";
    }
}
