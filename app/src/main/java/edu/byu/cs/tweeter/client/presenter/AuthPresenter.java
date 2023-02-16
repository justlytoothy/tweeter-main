package edu.byu.cs.tweeter.client.presenter;

import android.graphics.drawable.Drawable;

import edu.byu.cs.tweeter.client.model.backgroundTask.observer.AuthObserver;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthPresenter implements AuthObserver {
    public View view;
    private UserService userService;
    public String fName;
    public String lName;
    public String username;
    public String password;
    public Drawable image;

    public AuthPresenter(View view) {
        this.view = view;
    }

    public interface View {
        void displayMessage(String message);
        void handleSuccess(User user, AuthToken authToken,String message);
    }

    @Override
    public void handleSuccess(User user, AuthToken authToken,String message) {
        view.handleSuccess(user,authToken, message);
    }

    @Override
    public void handleFailure(String message) {
        view.displayMessage(message);
    }

    @Override
    public void handleException(Exception exception) {
        view.displayMessage("Error: " + exception.getMessage());
    }
    public abstract String validate();

}
