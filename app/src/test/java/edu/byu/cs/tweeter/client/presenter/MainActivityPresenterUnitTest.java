package edu.byu.cs.tweeter.client.presenter;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenterUnitTest {
    private MainActivityPresenter.View mockView;
    private StatusService mockService;
    private MainActivityPresenter mainPresenterSpy;

    @BeforeEach
    public void setUp() {
        mockView = Mockito.mock(MainActivityPresenter.View.class);
        mockService = Mockito.mock(StatusService.class);
        mainPresenterSpy = Mockito.spy(new MainActivityPresenter(mockView));
        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockService);

    }
    @Test
    public void testPostStatus_postStatusSuccessful() {
        Answer<Void> answer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainActivityPresenter.PostStatusObserver observer = invocation.getArgument(2, MainActivityPresenter.PostStatusObserver.class);
                observer.handleSuccess();
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockService).postStatus(Mockito.any(),Mockito.any(),Mockito.any());
        mainPresenterSpy.postStatus(new AuthToken(),"Example",new User(false));
        Mockito.verify(mockView).displayMessage("Posting Status...");
        Mockito.verify(mockService).postStatus(Mockito.any(),Mockito.any(),Mockito.any());
        Mockito.verify(mockView).statusPosted();
    }
    @Test
    public void testPostStatus_postStatusFailedWithMessage() {
        Answer<Void> answer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainActivityPresenter.PostStatusObserver observer = invocation.getArgument(2, MainActivityPresenter.PostStatusObserver.class);
                observer.handleFailure("Uh oh... an error occurred");
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockService).postStatus(Mockito.any(),Mockito.any(),Mockito.any());
        mainPresenterSpy.postStatus(new AuthToken(),"Example",new User(false));
        Mockito.verify(mockView).displayMessage("Posting Status...");
        Mockito.verify(mockService).postStatus(Mockito.any(),Mockito.any(),Mockito.any());
        Mockito.verify(mockView).displayMessage("Failed to post the status: Uh oh... an error occurred");
    }
    @Test
    public void testPostStatus_postStatusFailedWithException() {
        Answer<Void> answer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainActivityPresenter.PostStatusObserver observer = invocation.getArgument(2, MainActivityPresenter.PostStatusObserver.class);
                observer.handleException(new ParseException("Uh oh... an exception occurred",1));
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockService).postStatus(Mockito.any(),Mockito.any(),Mockito.any());
        mainPresenterSpy.postStatus(new AuthToken(),"Example",new User(false));
        Mockito.verify(mockView).displayMessage("Posting Status...");
        Mockito.verify(mockService).postStatus(Mockito.any(),Mockito.any(),Mockito.any());
        Mockito.verify(mockView).displayMessage("Failed to post the status because of exception: Uh oh... an exception occurred");
    }
    @Test
    public void testPostStatus_postStatusCorrectParameters() {
        Answer<Void> answer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainActivityPresenter.PostStatusObserver observer = invocation.getArgument(2, MainActivityPresenter.PostStatusObserver.class);
                assertEquals(AuthToken.class,invocation.getArgument(0).getClass());
                assertEquals(Status.class,invocation.getArgument(1).getClass());
                assertEquals(MainActivityPresenter.PostStatusObserver.class,invocation.getArgument(2).getClass());
                observer.handleSuccess();
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockService).postStatus(Mockito.any(),Mockito.any(),Mockito.any());
        mainPresenterSpy.postStatus(new AuthToken(),"Example",new User(false));
    }
}
