package edu.byu.cs.tweeter.client;

import static net.bytebuddy.agent.Installer.getInstrumentation;

import static java.util.UUID.randomUUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.backgroundTask.observer.AuthObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.MainActivityPresenter;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class IntegrationTest {
    private CountDownLatch countDownLatch;
    private UserService userService;
    private LoginObserver loginObserver;
    private AuthToken authToken;
    private User user;
    private MainActivityPresenter mainActivityPresenter;
    private MainActivityPresenter.View mockView;
    private PostStatusObserver mockPostStatusObserver;
    private String currentPost;
    private StatusService statusService;
    private StatusServiceObserver serviceObserver;

    @BeforeEach
    void setUp() {
        userService = Mockito.spy(new UserService());
        loginObserver = new LoginObserver();
//        mainView = Mockito.spy(new MainActivity());
        mockView = Mockito.mock(MainActivityPresenter.View.class);
        mainActivityPresenter = Mockito.spy(new MainActivityPresenter(mockView));
        mockPostStatusObserver = new PostStatusObserver();
        currentPost = randomUUID().toString();
        statusService = Mockito.spy(new StatusService());
        serviceObserver = new StatusServiceObserver();
        Mockito.when(mainActivityPresenter.getPostStatusObserver()).thenReturn(mockPostStatusObserver);
//        Mockito.when(mainActivityPresenter.getStatusService()).thenReturn()
        resetCountDownLatch();
    }
    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    @Test
    void integrationTest() throws InterruptedException {
        userService.login("@bigboi","bigboi", loginObserver);
        awaitCountDownLatch();
        Assertions.assertTrue(loginObserver.isSuccess());
        authToken = loginObserver.getAuthToken();
        user = loginObserver.getUser();
        mainActivityPresenter.postStatus(authToken,currentPost,user);
        awaitCountDownLatch();
        Mockito.verify(mockView).statusPosted();
        statusService.loadMoreItems(user,25,null,serviceObserver);
        awaitCountDownLatch();
        Status receivedStatus = serviceObserver.getStatuses().get(0);
        Assertions.assertEquals(currentPost,receivedStatus.getPost());
        Assertions.assertEquals(user,receivedStatus.getUser());
        Assertions.assertEquals(new ArrayList<String>(),receivedStatus.getMentions());
        Assertions.assertEquals(new ArrayList<String>(),receivedStatus.getUrls());




    }

    private class StatusServiceObserver implements PagedNotificationObserver<Status> {
        private boolean success;
        private String message;
        private List<Status> statuses;
        private boolean hasMorePages;
        private Exception exception;

        @Override
        public void handleSuccess(List<Status> list, boolean morePages) {
            this.success = true;
            this.message = null;
            this.statuses = list;
            this.hasMorePages = morePages;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void handleFailure(String message) {
            this.success = false;
            this.message = message;
            this.statuses = null;
            this.hasMorePages = false;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception exception) {
            this.success = false;
            this.message = null;
            this.statuses = null;
            this.hasMorePages = false;
            this.exception = exception;

            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<Status> getStatuses() {
            return statuses;
        }

        public boolean isHasMorePages() {
            return hasMorePages;
        }

        public Exception getException() {
            return exception;
        }
    }







    private class LoginObserver implements AuthObserver {
        private boolean success;
        private String message;
        private Exception exception;
        private User user;
        private AuthToken authToken;

        @Override
        public void handleSuccess(User registeredUser, AuthToken authToken, String s) {
            user = registeredUser;
            this.authToken = authToken;
            message = s;
            success = true;
            exception = null;
            countDownLatch.countDown();
        }

        @Override
        public void handleFailure(String message) {
            user = null;
            this.authToken = null;
            this.message = message;
            success = false;
            exception = null;
            countDownLatch.countDown();

        }

        @Override
        public void handleException(Exception exception) {
            user = null;
            this.authToken = null;
            this.message = exception.getMessage();
            success = false;
            exception = exception;
            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Exception getException() {
            return exception;
        }

        public User getUser() {
            return user;
        }

        public AuthToken getAuthToken() {
            return authToken;
        }
    }
    public class PostStatusObserver implements SimpleNotificationObserver {

        @Override
        public void handleFailure(String message) {
            mockView.displayMessage("Failed to post the status: " + message);
            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception ex) {
            mockView.displayMessage("Failed to post the status because of exception: " + ex.getMessage());
            countDownLatch.countDown();
        }

        @Override
        public void handleSuccess() {
            mockView.statusPosted();
            countDownLatch.countDown();
        }
    }
}
