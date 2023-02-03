package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter implements UserService.RegisterObserver {
    public View view;
    private UserService userService;
    public interface View {
        public void displayInfoMessage(String message);
        public void displayErrorMessage(String message);
        public void registerSuccessful(User user, AuthToken authToken,String message);
    }
    public RegisterPresenter(RegisterPresenter.View view) {
        this.view = view;
        userService = new UserService();
    }

    @Override
    public void handleSuccess(User user, AuthToken authToken,String message) {
        view.registerSuccessful(user,authToken,message);
    }

    @Override
    public void handleFailure(String message) {
        view.displayInfoMessage(message);
    }

    @Override
    public void handleException(Exception exception) {
        view.displayErrorMessage("Failed to register because of exception: " + exception.getMessage());
    }

    public void initiateRegister( String fName, String lName, String username, String password, Drawable image) {
        String validationMessage = validateRegistration(fName,lName,username,password,image);
        if (validationMessage == null) {
            view.displayInfoMessage("Registering ....");
            // Convert image to byte array.
            Bitmap imageFinal = ((BitmapDrawable) image).getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            imageFinal.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] imageBytes = bos.toByteArray();
            // Intentionally, Use the java Base64 encoder so it is compatible with M4.
            String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);
            userService.register(fName,lName,username,password,imageBytesBase64,this);
        }
        else {
            view.displayErrorMessage(validationMessage);
        }

    }
    public String validateRegistration(String fName, String lName, String username, String password, Drawable image) {
        if (fName.length() == 0) {
            return "First Name cannot be empty.";
        }
        if (lName.length() == 0) {
            return "Last Name cannot be empty.";
        }
        if (username.length() == 0) {
            return "Alias cannot be empty.";
        }
        if (username.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (username.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }

        if (image == null) {
            return "Profile image must be uploaded.";
        }
        return null;
    }

}
