package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.SignupRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

public interface IUserDAO {
    AuthResponse login(LoginRequest request);
    AuthResponse signup(SignupRequest request);
    LogoutResponse logout(LogoutRequest request);
    GetUserResponse getUser(GetUserRequest request);


}
