//package edu.byu.cs.tweeter.client.model.service;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//
//import edu.byu.cs.tweeter.client.model.backgroundTask.observer.PagedNotificationObserver;
//import edu.byu.cs.tweeter.client.model.net.ServerFacade;
//import edu.byu.cs.tweeter.client.model.service.StatusService;
//import edu.byu.cs.tweeter.model.domain.AuthToken;
//import edu.byu.cs.tweeter.model.domain.Status;
//import edu.byu.cs.tweeter.model.domain.User;
//import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
//import edu.byu.cs.tweeter.model.net.request.CountRequest;
//import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
//import edu.byu.cs.tweeter.model.net.request.SignupRequest;
//import edu.byu.cs.tweeter.model.net.response.AuthResponse;
//import edu.byu.cs.tweeter.model.net.response.CountResponse;
//import edu.byu.cs.tweeter.model.net.response.UserListResponse;
//import edu.byu.cs.tweeter.util.FakeData;
//
//public class StatusServiceTest {
//    private StatusService statusServiceSpy;
//    private User user;
//    private CountDownLatch countDownLatch;
//    private AuthToken authToken;
//    private StatusServiceObserver observer;
//
//
//    @BeforeEach
//    public void setUp() {
//        statusServiceSpy = Mockito.spy(new StatusService());
//        authToken = new AuthToken();
//        user = new User("first", "last",
//                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");
//        observer = new StatusServiceObserver();
//        resetCountDownLatch();
//
//    }
//
//    private void resetCountDownLatch() {
//        countDownLatch = new CountDownLatch(1);
//    }
//
//    private void awaitCountDownLatch() throws InterruptedException {
//        countDownLatch.await();
//        resetCountDownLatch();
//    }
//
//
//    private class StatusServiceObserver implements PagedNotificationObserver<Status> {
//        private boolean success;
//        private String message;
//        private List<Status> statuses;
//        private boolean hasMorePages;
//        private Exception exception;
//
//        @Override
//        public void handleSuccess(List<Status> list, boolean morePages) {
//            this.success = true;
//            this.message = null;
//            this.statuses = list;
//            this.hasMorePages = morePages;
//            this.exception = null;
//
//            countDownLatch.countDown();
//        }
//
//        @Override
//        public void handleFailure(String message) {
//            this.success = false;
//            this.message = message;
//            this.statuses = null;
//            this.hasMorePages = false;
//            this.exception = null;
//
//            countDownLatch.countDown();
//        }
//
//        @Override
//        public void handleException(Exception exception) {
//            this.success = false;
//            this.message = null;
//            this.statuses = null;
//            this.hasMorePages = false;
//            this.exception = exception;
//
//            countDownLatch.countDown();
//        }
//
//        public boolean isSuccess() {
//            return success;
//        }
//
//        public String getMessage() {
//            return message;
//        }
//
//        public List<Status> getStatuses() {
//            return statuses;
//        }
//
//        public boolean isHasMorePages() {
//            return hasMorePages;
//        }
//
//        public Exception getException() {
//            return exception;
//        }
//    }
//
//
//
//    @Test
//    public void testRetrieveStorySuccessful() throws InterruptedException {
//        statusServiceSpy.loadMoreItems(user,3,null,observer);
//        awaitCountDownLatch();
//
//        List<Status> expectedStatuses = FakeData.getInstance().getPageOfStatus(null,3).getFirst();
//        Assertions.assertTrue(observer.isSuccess());
//        Assertions.assertNull(observer.getMessage());
//        for (int i = 0; i < 3; i++) {
//            Assertions.assertEquals(expectedStatuses.get(i).getPost(),observer.getStatuses().get(i).getPost());
//            Assertions.assertEquals(expectedStatuses.get(i).getMentions(),observer.getStatuses().get(i).getMentions());
//            Assertions.assertEquals(expectedStatuses.get(i).getUrls(),observer.getStatuses().get(i).getUrls());
//            Assertions.assertEquals(expectedStatuses.get(i).getUser(),observer.getStatuses().get(i).getUser());
//        }
//        Assertions.assertTrue(observer.isHasMorePages());
//        Assertions.assertNull(observer.getException());
//
//    }
//
//}
