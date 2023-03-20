package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.SignupRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.server.service.UserService;

/**
 * An AWS lambda function that logs a user in and returns the user object and an auth code for
 * a successful login.
 */
public class SignupHandler implements RequestHandler<SignupRequest, AuthResponse> {
    @Override
    public AuthResponse handleRequest(SignupRequest signupRequest, Context context) {
        UserService userService = new UserService();
        return userService.signup(signupRequest);
    }
}
