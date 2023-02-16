package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;

public class LoginPresenter extends AuthPresenter {
    private UserService userService;

    public LoginPresenter(View view) {
        super(view);
        userService = new UserService();
    }

    public void initiateLogin(String username, String password) {
        this.username = username;
        this.password = password;
        String validationMessage = validate();
        if (validationMessage == null) {
            view.displayMessage("Logging in ....");
            userService.login(username,password, this);
        }
        else {
            view.displayMessage("Error: " + validationMessage);
        }

    }
    @Override
    public String validate() {
        if (username.length() > 0 && username.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (username.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }

        return null;
    }
}
