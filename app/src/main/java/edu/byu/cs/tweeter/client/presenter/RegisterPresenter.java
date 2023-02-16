package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.model.service.UserService;

public class RegisterPresenter extends AuthPresenter {
    private UserService userService;

    public RegisterPresenter(View view) {
        super(view);
        userService = new UserService();
    }


    public void initiateRegister( String fName, String lName, String username, String password, Drawable image) {
        this.fName = fName;
        this.lName = lName;
        this.username = username;
        this.password = password;
        this.image = image;
        String validationMessage = validate();
        if (validationMessage == null) {
            view.displayMessage("Registering ....");
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
            view.displayMessage("Error: " + validationMessage);
        }

    }
    @Override
    public String validate() {
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
