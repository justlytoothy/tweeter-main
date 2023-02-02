package edu.byu.cs.tweeter.client.model.backgroundTask.handler;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterHandler extends Handler {
    UserService.RegisterObserver observer;
    public RegisterHandler(UserService.RegisterObserver registerObserver) {
        super(Looper.getMainLooper());
        this.observer = registerObserver;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(RegisterTask.SUCCESS_KEY);
        if (success) {
            User registeredUser = (User) msg.getData().getSerializable(RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) msg.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);

//            Intent intent = new Intent(getContext(), MainActivity.class);

            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);

//            intent.putExtra(MainActivity.CURRENT_USER_KEY, registeredUser);
//
//            registeringToast.cancel();

//            Toast.makeText(getContext(), "Hello " + Cache.getInstance().getCurrUser().getName(), Toast.LENGTH_LONG).show();
            observer.handleSuccess(registeredUser,authToken);
//            try {
//                startActivity(intent);
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
        } else if (msg.getData().containsKey(RegisterTask.MESSAGE_KEY)) {
            String message = msg.getData().getString(RegisterTask.MESSAGE_KEY);
            observer.handleFailure("Failed to register: " + message);
//            Toast.makeText(getContext(), "Failed to register: " + message, Toast.LENGTH_LONG).show();
        } else if (msg.getData().containsKey(RegisterTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(RegisterTask.EXCEPTION_KEY);
            observer.handleException(ex);
//            Toast.makeText(getContext(), "Failed to register because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}