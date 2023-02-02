package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter implements UserService.RegisterObserver {
    public View view;
    public interface View {
        public void displayInfoMessage(String message);
        public void displayErrorMessage(String message);
        public void registerSuccessful(User user, AuthToken authToken);
    }
    public RegisterPresenter(RegisterPresenter.View view) {
        this.view = view;
    }

    @Override
    public void handleSuccess(User user, AuthToken authToken) {
        view.registerSuccessful(user,authToken);
    }

    @Override
    public void handleFailure(String message) {
        view.displayInfoMessage(message);
    }

    @Override
    public void handleException(Exception exception) {
        view.displayInfoMessage("Failed to register because of exception: " + exception.getMessage());
    }
    public void initiateRegister( String fName, String lName, String username, String password, String image) {
        String validationMessage = validateRegistration(fName,lName,username,password,image);
        if (validationMessage == null) {
            view.displayInfoMessage("Registering ....");
            UserService userService = new UserService();
            userService.register(fName,lName,username,password,image,this);
        }
        else {
            view.displayErrorMessage(validationMessage);
        }

    }
    public String validateRegistration(String fName, String lName, String username, String password, String image) {
        if (fName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (username.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (username.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (username.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (image.length() == 0) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
        return null;
    }

}
