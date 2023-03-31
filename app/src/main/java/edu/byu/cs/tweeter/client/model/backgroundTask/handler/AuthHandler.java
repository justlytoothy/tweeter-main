package edu.byu.cs.tweeter.client.model.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.backgroundTask.AuthenticateTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.AuthObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthHandler extends BackgroundTaskHandler<AuthObserver>{
    public AuthHandler(AuthObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, AuthObserver observer) {
        User user = (User) data.getSerializable(AuthenticateTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(AuthenticateTask.AUTH_TOKEN_KEY);
        Cache.getInstance().setCurrUser(user);
        Cache.getInstance().setCurrUserAuthToken(authToken);
        System.out.println(Cache.getInstance().getCurrUserAuthToken().getToken());
        observer.handleSuccess(user,authToken,"Hello " + Cache.getInstance().getCurrUser().getName());
    }
}
