package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowingRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowingResponse;
import edu.byu.cs.tweeter.model.net.response.UserListResponse;

/**
 * A DAO for accessing 'following' data from the database.
 */
public interface IFollowDAO {

    UserListResponse getFollowees(FollowingRequest request);
    UserListResponse getFollowers(FollowerRequest request);
    CountResponse getFollowerCount(CountRequest request);
    CountResponse getFollowingCount(CountRequest request);
    FollowResponse follow(FollowRequest request);
    FollowResponse unfollow(FollowRequest request);
    IsFollowingResponse isFollowing(IsFollowingRequest request);

}
