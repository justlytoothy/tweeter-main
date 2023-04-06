package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.SendStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.response.SendStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StatusesResponse;
import edu.byu.cs.tweeter.server.dao.beans.StoryBean;

public interface IStatusDAO {
    SendStatusResponse post(SendStatusRequest request);
    StatusesResponse getStory(StatusesRequest request);
    StatusesResponse getFeed(StatusesRequest request);
    void postFeed(List<String> users, Status status);
}
