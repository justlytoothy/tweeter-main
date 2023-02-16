package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.AuthHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.AuthObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.UserObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UserService {
    public void login(String username, String password, AuthObserver observer) {
        LoginTask loginTask = new LoginTask(username, password, new AuthHandler(observer));
        ServiceExecutor.execute(loginTask);
    }
    public void register(String fName, String lName,String username, String password, String image, AuthObserver observer) {
        RegisterTask registerTask = new RegisterTask(fName,lName,username, password,image, new AuthHandler(observer));
        ServiceExecutor.execute(registerTask);
    }
    public void getUser(AuthToken authToken, String username, UserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(authToken,
                username, new GetUserHandler(observer));
        ServiceExecutor.execute(getUserTask);
    }
    public void logout(AuthToken authToken, SimpleNotificationObserver observer) {
        LogoutTask logoutTask = new LogoutTask(authToken, new SimpleNotificationHandler(observer));
        ServiceExecutor.execute(logoutTask);

    }

}
