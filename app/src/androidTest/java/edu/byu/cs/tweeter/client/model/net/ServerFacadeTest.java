package edu.byu.cs.tweeter.client.model.net;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.StatusServiceTest;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.SignupRequest;
import edu.byu.cs.tweeter.model.net.response.AuthResponse;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.UserListResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class ServerFacadeTest {
    private StatusService statusServiceSpy;
    private User user;
    private AuthToken authToken;


    @BeforeEach
    public void setUp() {
        statusServiceSpy = Mockito.spy(new StatusService());
        authToken = new AuthToken();
        user = new User("first", "last",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");


    }
    @Test
    public void testGetFollowers() throws IOException, TweeterRemoteException {
        UserListResponse response = new ServerFacade().getFollowers(new FollowerRequest(authToken,user.getAlias(),3,null),"getfollowers");
        List<User> expectedUsers = FakeData.getInstance().getPageOfUsers(user, 3, null).getFirst();
        Assertions.assertTrue(response.isSuccess());
        for (int i = 0; i < response.getItems().size(); i++) {
            Assertions.assertEquals(response.getItems().get(i),expectedUsers.get(i));
        }
    }
    @Test
    public void testGetFollowerCount() throws IOException, TweeterRemoteException {
        CountResponse response = new ServerFacade().getFollowersCount(new CountRequest(authToken, user.getAlias()),"count/followers");
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(response.getCount(),FakeData.getInstance().getFakeUsers().size());
    }
    @Test
    public void testRegister() throws IOException, TweeterRemoteException {
        AuthResponse res = new ServerFacade().signup(new SignupRequest("username", "password","FirstName1", "LastName1",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png"),"signup");
        Assertions.assertTrue(res.isSuccess());
        Assertions.assertEquals(res.getAuthToken().getToken(),FakeData.getInstance().getAuthToken().getToken());
        Assertions.assertEquals(res.getAuthToken().getDatetime(),FakeData.getInstance().getAuthToken().getDatetime());
        Assertions.assertEquals(res.getUser(),FakeData.getInstance().getFirstUser());
    }
}
