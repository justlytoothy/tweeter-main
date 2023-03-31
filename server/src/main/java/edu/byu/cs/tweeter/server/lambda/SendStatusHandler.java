package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.SendStatusRequest;
import edu.byu.cs.tweeter.model.net.response.SendStatusResponse;
import edu.byu.cs.tweeter.server.service.DynamoFactory;
import edu.byu.cs.tweeter.server.service.StatusService;

/**
 * An AWS lambda function that logs a user in and returns the user object and an auth code for
 * a successful login.
 */
public class SendStatusHandler implements RequestHandler<SendStatusRequest, SendStatusResponse> {
    @Override
    public SendStatusResponse handleRequest(SendStatusRequest request, Context context) {
        StatusService statusService = new StatusService(new DynamoFactory());
        return statusService.post(request);
    }
}
