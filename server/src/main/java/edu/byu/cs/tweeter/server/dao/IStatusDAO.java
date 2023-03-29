package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.SendStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.response.SendStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StatusesResponse;

public interface IStatusDAO {
    SendStatusResponse post(SendStatusRequest request);
    StatusesResponse getStory(StatusesRequest request);
    StatusesResponse getFeed(StatusesRequest request);
}
