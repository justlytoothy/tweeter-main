package edu.byu.cs.tweeter.server.dao;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.SendStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.response.SendStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StatusesResponse;
import edu.byu.cs.tweeter.server.dao.beans.FollowBean;
import edu.byu.cs.tweeter.server.dao.beans.StoryBean;
import edu.byu.cs.tweeter.server.dao.beans.UserBean;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class StatusDAO extends BaseDAO<StoryBean> implements IStatusDAO {


    @Override
    public SendStatusResponse post(SendStatusRequest request) {
        try {
            if (UserDAO.checkAuthToken(request.getAuthToken())) {
                table = enhancedClient.table("story", TableSchema.fromBean(StoryBean.class));
                StoryBean storyBean = new StoryBean();
                storyBean.setPost(request.getStatus().getPost());
                storyBean.setAlias(request.getStatus().getUser().getAlias());
                storyBean.setMentions(request.getStatus().getMentions());
                storyBean.setUrls(request.getStatus().getUrls());
                storyBean.setTimestamp(request.getStatus().getTimestamp());
                storyBean.setUser(new Gson().toJson(request.getStatus().getUser()));
                table.putItem(storyBean);
                StoryBean feedBean = new StoryBean();
                feedBean.setPost(request.getStatus().getPost());
                feedBean.setMentions(request.getStatus().getMentions());
                feedBean.setUrls(request.getStatus().getUrls());
                feedBean.setTimestamp(request.getStatus().getTimestamp());
                feedBean.setUser(new Gson().toJson(request.getStatus().getUser()));
                String messageBody = new Gson().toJson(feedBean);
                String queueUrl = "https://sqs.us-east-1.amazonaws.com/978529209683/TweeterPostStatus";

                SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(queueUrl).withMessageBody(messageBody);
                AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
                SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
                String msgId = send_msg_result.getMessageId();
                System.out.println("Message ID: " + msgId);
                return new SendStatusResponse();
            }
            else {
                return new SendStatusResponse("Invalid Auth Token");
            }

        }
        catch (Exception e) {
            return new SendStatusResponse(e.getMessage());
        }
    }

    @Override
    public StatusesResponse getStory(StatusesRequest request) {
        try {
            if (UserDAO.checkAuthToken(request.getAuthToken())) {
                List<Status> story = new ArrayList<>(request.getLimit());
                DataPage<StoryBean> beanDataPage = getPageOfStory(request.getUserAlias(), request.getLimit(), request.getLastStatus());
                for (StoryBean b : beanDataPage.getValues()) {
                    story.add(new Status(b.getPost(),new Gson().fromJson(b.getUser(), User.class),b.getTimestamp(),b.getUrls(),b.getMentions()));
                }
                return new StatusesResponse(story, beanDataPage.isHasMorePages());
            }
            else {
                return new StatusesResponse("Invalid Auth Token");
            }

        }
        catch (Exception e) {
            return new StatusesResponse(e.getMessage());
        }

    }

    @Override
    public StatusesResponse getFeed(StatusesRequest request) {
        try {
            if (UserDAO.checkAuthToken(request.getAuthToken())) {
                List<Status> feed = new ArrayList<>(request.getLimit());
                DataPage<StoryBean> beanDataPage = getPageOfFeed(request.getUserAlias(), request.getLimit(), request.getLastStatus());
                for (StoryBean b : beanDataPage.getValues()) {
                    feed.add(new Status(b.getPost(),new Gson().fromJson(b.getUser(), User.class),b.getTimestamp(),b.getUrls(),b.getMentions()));
                }
                return new StatusesResponse(feed, beanDataPage.isHasMorePages());
            }
            else {
                return new StatusesResponse("Invalid Auth Token");
            }
        }
        catch (Exception e) {
            return new StatusesResponse(e.getMessage());
        }
    }



    public DataPage<StoryBean> getPageOfFeed(String targetUserAlias, int pageSize, Status lastStatus) {
        table = enhancedClient.table("feed", TableSchema.fromBean(StoryBean.class));
        Key key = Key.builder().partitionValue(targetUserAlias).build();
        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize).scanIndexForward(false);
        if(lastStatus != null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("alias", AttributeValue.builder().s(targetUserAlias).build());
            startKey.put("timestamp", AttributeValue.builder().n(String.valueOf(lastStatus.getTimestamp())).build());
            requestBuilder.exclusiveStartKey(startKey);
        }
        QueryEnhancedRequest request = requestBuilder.build();
        DataPage<StoryBean> result = new DataPage<StoryBean>();

        SdkIterable<Page<StoryBean>> sdkIterable = table.query(request);
        PageIterable<StoryBean> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<StoryBean> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(visit -> result.getValues().add(visit));
                });
        return result;

    }
    public DataPage<StoryBean> getPageOfStory(String targetUserAlias, int pageSize, Status lastStatus) {
        table = enhancedClient.table("story", TableSchema.fromBean(StoryBean.class));
        Key key = Key.builder().partitionValue(targetUserAlias).build();
        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize).scanIndexForward(false);
        if(lastStatus != null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("alias", AttributeValue.builder().s(targetUserAlias).build());
            startKey.put("timestamp", AttributeValue.builder().n(String.valueOf(lastStatus.getTimestamp())).build());
            requestBuilder.exclusiveStartKey(startKey);
        }
        QueryEnhancedRequest request = requestBuilder.build();
        DataPage<StoryBean> result = new DataPage<StoryBean>();
        SdkIterable<Page<StoryBean>> sdkIterable = table.query(request);
        PageIterable<StoryBean> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<StoryBean> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(visit -> result.getValues().add(visit));
                });
        return result;
    }
    @Override
    public void postFeed(List<String> users,Status s) {
        List<StoryBean> batchToWrite = new ArrayList<>();
        for (String u : users) {
            StoryBean dto = new StoryBean(s,u);
            batchToWrite.add(dto);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfStoryBeans(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }
        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfStoryBeans(batchToWrite);
        }
    }
    private void writeChunkOfStoryBeans(List<StoryBean> storyBeans) {
        if(storyBeans.size() > 25)
            throw new RuntimeException("Too many statuses to write");

        table = enhancedClient.table("feed", TableSchema.fromBean(StoryBean.class));
        WriteBatch.Builder<StoryBean> writeBuilder = WriteBatch.builder(StoryBean.class).mappedTableResource(table);
        for (StoryBean item : storyBeans) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfStoryBeans(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
