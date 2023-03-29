package edu.byu.cs.tweeter.server.dao;


import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.SendStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.response.SendStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StatusesResponse;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class StatusDAO extends BaseDAO implements IStatusDAO {


    public StatusDAO() {
        this.initializeDatabase();
        this.switchTable("status");
    }

    @Override
    public SendStatusResponse post(SendStatusRequest request) {
        return new SendStatusResponse();
    }

    @Override
    public StatusesResponse getStory(StatusesRequest request) {
        Pair<List<Status>,Boolean> res = getFakeData().getPageOfStatus(request.getLastStatus(), request.getLimit());
        return new StatusesResponse(res.getFirst(), res.getSecond());

    }

    @Override
    public StatusesResponse getFeed(StatusesRequest request) {
        Pair<List<Status>,Boolean> res = getFakeData().getPageOfStatus(request.getLastStatus(), request.getLimit());
        return new StatusesResponse(res.getFirst(), res.getSecond());
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
