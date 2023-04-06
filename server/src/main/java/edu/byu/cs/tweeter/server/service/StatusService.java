package edu.byu.cs.tweeter.server.service;

import com.google.gson.Gson;

import edu.byu.cs.tweeter.model.net.request.SendStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.response.SendStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StatusesResponse;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.beans.StoryBean;

public class StatusService {
    private final IDAOFactory factory;
    public StatusService(IDAOFactory factory) {
        this.factory = factory;
    }
    IStatusDAO getStatusDAO() {
        return factory.getStatusDAO();
    }
    public SendStatusResponse post(SendStatusRequest request) {
        return getStatusDAO().post(request);
    }

    public StatusesResponse getStory(StatusesRequest request) {
        return getStatusDAO().getStory(request);
    }

    public StatusesResponse getFeed(StatusesRequest request) {
        return getStatusDAO().getFeed(request);
    }
    public void postFeed(StoryBean bean) {
        getStatusDAO().postFeed(new Gson().toJson(bean));
    }

}
