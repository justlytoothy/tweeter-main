package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.DataPage;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.beans.FollowBean;
import edu.byu.cs.tweeter.server.dao.beans.StoryBean;
import edu.byu.cs.tweeter.server.dao.beans.UpdateFeedUtil;
import edu.byu.cs.tweeter.server.service.DynamoFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

/**
 * An AWS lambda function that returns the users a user is following.
 */
public class PostUpdateFeedMessagesHandler implements RequestHandler<SQSEvent, Void> {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request.
     *
     * @param context the lambda context.
     * @return the followees.
     */
    @Override
    public Void handleRequest(SQSEvent msg, Context context) {
        FollowService followService = new FollowService(new DynamoFactory());
        Gson gson = new Gson();
        boolean morePages = true;
        String lastUser = null;
        context.getLogger().log(msg.getRecords().get(0).getBody());
        StoryBean request = (StoryBean)gson.fromJson(msg.getRecords().get(0).getBody(), StoryBean.class);
        context.getLogger().log("This is post feed \n" + request.toString());

        while (morePages) {
            DataPage<FollowBean> page = new FollowDAO().getPageOfFollowers(gson.fromJson(request.getUser(), User.class).getAlias(),25,lastUser);
            List<String> users = new ArrayList<String>();
            for (FollowBean f : page.getValues()) {
                users.add(f.getFollower_handle());
                lastUser = f.getFollower_handle();
            }
            UpdateFeedUtil updateFeedUtil = new UpdateFeedUtil(users,request.getAlias(),request.getTimestamp(), request.getPost(), request.getUrls(),request.getMentions(), request.getUser());
            String messageBody = gson.toJson(updateFeedUtil);
            String queueUrl = "https://sqs.us-east-1.amazonaws.com/978529209683/TweeterUpdateFeed";

            SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(queueUrl).withMessageBody(messageBody);
            AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
            SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
            String msgId = send_msg_result.getMessageId();
            morePages = page.isHasMorePages();
        }
        return null;
    }
}
