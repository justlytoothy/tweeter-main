package edu.byu.cs.tweeter.server.dao;


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
import edu.byu.cs.tweeter.server.dao.beans.FeedBean;
import edu.byu.cs.tweeter.server.dao.beans.StoryBean;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class StatusDAO extends BaseDAO<FeedBean> implements IStatusDAO {


    @Override
    public SendStatusResponse post(SendStatusRequest request) {
        return new SendStatusResponse();
    }

    @Override
    public StatusesResponse getStory(StatusesRequest request) {
        Pair<List<Status>,Boolean> res = getFakeData().getPageOfStatus(request.getLastStatus(), request.getLimit());
        return new StatusesResponse(res.getFirst(), res.getSecond());

    }

    @Override
    public StatusesResponse getFeed(StatusesRequest request) {
        List<Status> feed = new ArrayList<>(request.getLimit());
        DataPage<FeedBean> beanDataPage = getPageOfFeed(request.getUserAlias(), request.getLimit(), request.getLastStatus());
        for (FeedBean b : beanDataPage.getValues()) {
            feed.add(new Status(b.getPost(),b.getUser(),b.getTimestamp(),b.getUrls(),b.getMentions()));
        }
        return new StatusesResponse(feed, beanDataPage.isHasMorePages());
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }
    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    public DataPage<FeedBean> getPageOfFeed(String targetUserAlias, int pageSize, Status lastStatus) {
        table = enhancedClient.table("feed", TableSchema.fromBean(FeedBean.class));
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
        DataPage<FeedBean> result = new DataPage<FeedBean>();

        SdkIterable<Page<FeedBean>> sdkIterable = table.query(request);
        PageIterable<FeedBean> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<FeedBean> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(visit -> result.getValues().add(visit));
                });
        return result;

    }
    public DataPage<StoryBean> getPageOfStory(String targetUserAlias, int pageSize, Status lastStatus) {
        DynamoDbTable<StoryBean> storyTable = enhancedClient.table("story", TableSchema.fromBean(StoryBean.class));
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

        SdkIterable<Page<StoryBean>> sdkIterable = storyTable.query(request);
        PageIterable<StoryBean> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<StoryBean> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(visit -> result.getValues().add(visit));
                });
        return result;

    }
}
