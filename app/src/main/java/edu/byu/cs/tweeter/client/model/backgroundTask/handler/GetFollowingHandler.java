package edu.byu.cs.tweeter.client.model.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;

import edu.byu.cs.tweeter.client.model.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Message handler (i.e., observer) for GetFollowingTask.
 */
public class GetFollowingHandler extends Handler {
    private FollowService.Observer observer;

    public GetFollowingHandler(FollowService.Observer observe) {
        super(Looper.getMainLooper());
        observer = observe;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(GetFollowingTask.SUCCESS_KEY);
        if (success) {
            List<User> followees = (List<User>) msg.getData().getSerializable(GetFollowingTask.ITEMS_KEY);
            boolean hasMorePages = msg.getData().getBoolean(GetFollowingTask.MORE_PAGES_KEY);
            observer.handleSuccess(followees, hasMorePages);
        } else if (msg.getData().containsKey(GetFollowingTask.MESSAGE_KEY)) {
            String message = msg.getData().getString(GetFollowingTask.MESSAGE_KEY);
            observer.displayError("Failed to get following: " + message);
        } else if (msg.getData().containsKey(GetFollowingTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(GetFollowingTask.EXCEPTION_KEY);
            observer.displayException(ex);
        }
    }
}
