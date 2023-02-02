package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.LoginHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.RegisterHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService {
    public interface LoginObserver {
        void handleSuccess(User user, AuthToken authToken,String message);
        void handleFailure(String message);
        void handleException(Exception exception);
    }
    public interface RegisterObserver {
        void handleSuccess(User user, AuthToken authToken,String message);
        void handleFailure(String message);
        void handleException(Exception exception);
    }

    public void login(String username, String password, LoginObserver observer) {
        LoginTask loginTask = new LoginTask(username, password, new LoginHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(loginTask);
    }
    public void register(String fName, String lName,String username, String password, String image, RegisterObserver observer) {
        RegisterTask registerTask = new RegisterTask(fName,lName,username, password,image, new RegisterHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(registerTask);
    }
}
