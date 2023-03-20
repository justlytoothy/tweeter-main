package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowingRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.UserListResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowingResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public UserListResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getFollowingDAO().getFollowees(request);
    }
    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public UserListResponse getFollowers(FollowerRequest request) {
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getFollowingDAO().getFollowers(request);
    }

    public CountResponse getFollowerCount(CountRequest request) {
        if (request.getAuthToken() == null) {
            //not sure if need to check this TODO
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }
        else if (request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        }
        return getFollowingDAO().getFollowerCount(request);
    }
    public CountResponse getFollowingCount(CountRequest request) {
        if (request.getAuthToken() == null) {
            //not sure if need to check this TODO
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }
        else if (request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        }
        return getFollowingDAO().getFollowingCount(request);
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowingDAO() {
        return new FollowDAO();
    }

    public FollowResponse follow(FollowRequest request, boolean follow) {
        if (request.getAuthToken() == null) {
            //not sure if need to check this TODO
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }
        else if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        }
        else if (request.getCurrentUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a current user alias");
        }
        return follow ? getFollowingDAO().follow(request) : getFollowingDAO().unfollow(request);
    }

    public IsFollowingResponse isFollowing(IsFollowingRequest request) {
        if (request.getAuthToken() == null) {
            //not sure if need to check this TODO
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }
        else if (request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a target user alias");
        }
        else if (request.getCurrentUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a current user alias");
        }
        return getFollowingDAO().isFollowing(request);
    }


}
