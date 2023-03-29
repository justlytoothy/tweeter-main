package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;

public interface IDAOFactory {
    IFollowDAO getFollowDAO();
    IStatusDAO getStatusDAO();
    IUserDAO getUserDAO();
}
