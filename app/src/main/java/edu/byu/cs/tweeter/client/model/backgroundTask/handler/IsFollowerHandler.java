package edu.byu.cs.tweeter.client.model.backgroundTask.handler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.model.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.FollowerObserver;

public class IsFollowerHandler extends Handler {
    private FollowerObserver observer;

    public IsFollowerHandler(FollowerObserver observer) {
        super(Looper.getMainLooper());
        this.observer = observer;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(IsFollowerTask.SUCCESS_KEY);
        if (success) {
            boolean isFollower = msg.getData().getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            observer.handleSuccess(isFollower);
        } else if (msg.getData().containsKey(IsFollowerTask.MESSAGE_KEY)) {
            String message = msg.getData().getString(IsFollowerTask.MESSAGE_KEY);
            observer.handleFailure("Failed to determine following relationship: " + message);
        } else if (msg.getData().containsKey(IsFollowerTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(IsFollowerTask.EXCEPTION_KEY);
            observer.handleException(ex);
        }
    }
}
