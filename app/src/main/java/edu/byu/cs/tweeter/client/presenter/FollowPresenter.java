package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowPresenter extends PagedPresenter<User> {
    String type;
    @Override
    public void getItems(AuthToken authToken, User targetUser, int pageSize, User lastItem) {
        if (type.equals("follower")) {
            followService.loadMoreItemsFollowers(targetUser,PAGE_SIZE,lastItem, new PageObserver());

        }
        else {
            followService.loadMoreItems(targetUser,PAGE_SIZE,lastItem, new PageObserver());
        }
    }

    @Override
    public String getDescription() {
        return null;
    }

    public FollowPresenter(PagedView<User> view,String type) {
        super(view);
        followService = new FollowService();
        userService = new UserService();
        this.type = type;
    }



}
