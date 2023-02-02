package edu.byu.cs.tweeter.client.model.backgroundTask.handler;
//
//import android.content.Intent;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//
//import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
//import edu.byu.cs.tweeter.client.view.main.MainActivity;
//import edu.byu.cs.tweeter.model.domain.User;
//
///**
// * Message handler (i.e., observer) for GetUserTask.
// */
//public class GetUserHandler extends Handler {
//
//    public GetUserHandler() {
//        super(Looper.getMainLooper());
//    }
//
//    @Override
//    public void handleMessage(@NonNull Message msg) {
//        boolean success = msg.getData().getBoolean(GetUserTask.SUCCESS_KEY);
//        if (success) {
//            User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
//
//            Intent intent = new Intent(getContext(), MainActivity.class);
//            intent.putExtra(MainActivity.CURRENT_USER_KEY, user);
//            startActivity(intent);
//        } else if (msg.getData().containsKey(GetUserTask.MESSAGE_KEY)) {
//            String message = msg.getData().getString(GetUserTask.MESSAGE_KEY);
//            Toast.makeText(getContext(), "Failed to get user's profile: " + message, Toast.LENGTH_LONG).show();
//        } else if (msg.getData().containsKey(GetUserTask.EXCEPTION_KEY)) {
//            Exception ex = (Exception) msg.getData().getSerializable(GetUserTask.EXCEPTION_KEY);
//            Toast.makeText(getContext(), "Failed to get user's profile because of exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }
//}