package edu.byu.cs.tweeter.server.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.UserListResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;

public class FollowServiceTest {

    private FollowingRequest request;
    private FollowerRequest requestTwo;
    private UserListResponse expectedResponse;
    private FollowDAO mockFollowDAO;
    private FollowService followServiceSpy;
    private User currentUser;
    private AuthToken authToken;

    @BeforeEach
    public void setup() {
        authToken = new AuthToken();

        currentUser = new User("FirstName", "LastName", null);

        User resultUser1 = new User("FirstName1", "LastName1",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        User resultUser2 = new User("FirstName2", "LastName2",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");
        User resultUser3 = new User("FirstName3", "LastName3",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");

        // Setup a request object to use in the tests
        request = new FollowingRequest(authToken, currentUser.getAlias(), 3, null);
        requestTwo = new FollowerRequest(authToken, currentUser.getAlias(), 3, null);


        // Setup a mock FollowDAO that will return known responses
        expectedResponse = new UserListResponse(Arrays.asList(resultUser1, resultUser2, resultUser3), false);
        mockFollowDAO = Mockito.mock(FollowDAO.class);
        Mockito.when(mockFollowDAO.getFollowees(request)).thenReturn(expectedResponse);
        Mockito.when(mockFollowDAO.getFollowers(requestTwo)).thenReturn(expectedResponse);


        followServiceSpy = Mockito.spy(FollowService.class);
        Mockito.when(followServiceSpy.getFollowDAO()).thenReturn(mockFollowDAO);
    }

    /**
     * Verify that the {@link FollowService#getFollowees(FollowingRequest)}
     * method returns the same result as the {@link FollowDAO} class.
     */
    @Test
    public void testGetFollowees_validRequest_correctResponse() {
        UserListResponse response = followServiceSpy.getFollowees(request);
        Assertions.assertEquals(expectedResponse, response);
    }

}
