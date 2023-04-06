package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.beans.StoryBean;
import edu.byu.cs.tweeter.server.dao.beans.UpdateFeedUtil;
import edu.byu.cs.tweeter.server.service.DynamoFactory;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.StatusService;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

/**
 * An AWS lambda function that returns the users a user is following.
 */
public class UpdateFeedHandler implements RequestHandler<SQSEvent, Void> {

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
        context.getLogger().log("This is update feed \n" + msg.getRecords().get(0).getBody());
        UpdateFeedUtil updateFeedUtil = new Gson().fromJson(msg.getRecords().get(0).getBody(),UpdateFeedUtil.class);
        StatusService statusService = new StatusService(new DynamoFactory());
        Status status = new Status(updateFeedUtil.getPost(), new Gson().fromJson(updateFeedUtil.getUser(), User.class), updateFeedUtil.getTimestamp(), updateFeedUtil.getUrls(),updateFeedUtil.getMentions());
        statusService.postFeed(updateFeedUtil.getUsers(),status);
        return null;
    }
}
