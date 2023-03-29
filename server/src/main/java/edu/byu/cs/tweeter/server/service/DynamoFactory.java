package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class DynamoFactory implements IDAOFactory{
    @Override
    public FollowDAO getFollowDAO() {
        return new FollowDAO();
    }

    @Override
    public StatusDAO getStatusDAO() {
        return new StatusDAO();
    }

    @Override
    public UserDAO getUserDAO() {
        return new UserDAO();
    }
}
