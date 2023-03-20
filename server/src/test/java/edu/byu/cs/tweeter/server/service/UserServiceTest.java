package edu.byu.cs.tweeter.server.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.SignupRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.UserListResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.util.FakeData;

public class UserServiceTest {


    private UserService userService;
    private User currentUser;
    private SignupRequest request;

    @BeforeEach
    public void setup() {
        userService = new UserService();

        currentUser = new User("FirstName", "LastName", null);

        User newUser = new User("FirstName1", "LastName1",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");

        // Setup a request object to use in the tests
        request = new SignupRequest("username", "password","FirstName1", "LastName1",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");

    }


    @Test
    public void testRegister() {
        AuthResponse res = userService.signup(request);
        Assertions.assertTrue(res.isSuccess());
        Assertions.assertEquals(res.getAuthToken().getToken(),FakeData.getInstance().getAuthToken().getToken());
        Assertions.assertEquals(res.getAuthToken().getDatetime(),FakeData.getInstance().getAuthToken().getDatetime());
        Assertions.assertEquals(res.getUser(),FakeData.getInstance().getFirstUser());
    }

}
