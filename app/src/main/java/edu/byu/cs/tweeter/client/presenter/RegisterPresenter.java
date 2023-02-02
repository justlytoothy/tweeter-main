package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter implements UserService.RegisterObserver {
    public interface View {

    }
    @Override
    public void handleSuccess(User user, AuthToken authToken) {

    }

    @Override
    public void handleFailure(String message) {

    }

    @Override
    public void handleException(Exception exception) {

    }
}
