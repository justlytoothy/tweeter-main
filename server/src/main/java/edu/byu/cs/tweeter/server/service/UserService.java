package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.SignupRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.IUserDAO;

public class UserService {
    private final IDAOFactory factory;
    public UserService() {
        factory = new DynamoFactory();
    }

    IUserDAO getUserDAO() {
        return factory.getUserDAO();
    }



    public AuthResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        return getUserDAO().login(request);
    }


    public AuthResponse signup(SignupRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        }
        else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }
        else if(request.getImage() == null) {
            throw new RuntimeException("[Bad Request] Missing an image");
        }
        else if(request.getFirstName() == null) {
            throw new RuntimeException("[Bad Request] Missing first name");
        }
        else if(request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing last name");
        }

        // TODO: Generates dummy data. Replace with a real implementation.
        return getUserDAO().signup(request);
    }

    public LogoutResponse logout(LogoutRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Missing an auth token");
        }
        return getUserDAO().logout(request);
    }

    public GetUserResponse getUser(GetUserRequest request) {
        return getUserDAO().getUser(request);
    }
}
