package edu.byu.cs.tweeter.server.dao;


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
import edu.byu.cs.tweeter.server.dao.beans.FeedBean;
import edu.byu.cs.tweeter.server.dao.beans.FollowBean;
import edu.byu.cs.tweeter.server.dao.beans.StoryBean;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class StatusDAO extends BaseDAO<StoryBean> implements IStatusDAO {


    @Override
    public SendStatusResponse post(SendStatusRequest request) {
        try {
            table = enhancedClient.table("story", TableSchema.fromBean(StoryBean.class));
            StoryBean storyBean = new StoryBean();
            storyBean.setPost(request.getStatus().getPost());
            storyBean.setAlias(request.getStatus().getUser().getAlias());
            storyBean.setMentions(request.getStatus().getMentions());
            storyBean.setUrls(request.getStatus().getUrls());
            storyBean.setTimestamp(request.getStatus().getTimestamp());
            storyBean.setUser(new Gson().toJson(request.getStatus().getUser()));
            table.putItem(storyBean);
            FeedBean feedBean = new FeedBean();
            feedBean.setPost(request.getStatus().getPost());
            feedBean.setMentions(request.getStatus().getMentions());
            feedBean.setUrls(request.getStatus().getUrls());
            feedBean.setTimestamp(request.getStatus().getTimestamp());
            feedBean.setUser(new Gson().toJson(request.getStatus().getUser()));
            DynamoDbTable<FeedBean> feedTable = enhancedClient.table("feed", TableSchema.fromBean(FeedBean.class));
            feedBean.setAlias(request.getStatus().getUser().getAlias());
            feedTable.putItem(feedBean);
            for (String s : request.getStatus().getMentions()) {
                feedBean.setAlias(s);
                feedTable.putItem(feedBean);
            }
            boolean morePages = true;
            String lastUser = null;
            while (morePages) {
                DataPage<FollowBean> page = new FollowDAO().getPageOfFollowers(request.getStatus().getUser().getAlias(),100,lastUser);
                for (FollowBean f : page.getValues()) {
                    feedBean.setAlias(f.getFollower_handle());
                    feedTable.putItem(feedBean);
                    lastUser = f.getFollower_handle();
                }
                morePages = page.isHasMorePages();
            }
            return new SendStatusResponse();
        }
        catch (Exception e) {
            return new SendStatusResponse(e.getMessage());
        }
    }

    @Override
    public StatusesResponse getStory(StatusesRequest request) {
        try {
            List<Status> story = new ArrayList<>(request.getLimit());
            DataPage<StoryBean> beanDataPage = getPageOfStory(request.getUserAlias(), request.getLimit(), request.getLastStatus());
            for (StoryBean b : beanDataPage.getValues()) {
                story.add(new Status(b.getPost(),new Gson().fromJson(b.getUser(), User.class),b.getTimestamp(),b.getUrls(),b.getMentions()));
            }
            return new StatusesResponse(story, beanDataPage.isHasMorePages());
        }
        catch (Exception e) {
            return new StatusesResponse(e.getMessage());
        }

    }

    @Override
    public StatusesResponse getFeed(StatusesRequest request) {
        try {
            List<Status> feed = new ArrayList<>(request.getLimit());
            DataPage<FeedBean> beanDataPage = getPageOfFeed(request.getUserAlias(), request.getLimit(), request.getLastStatus());
            for (FeedBean b : beanDataPage.getValues()) {
                feed.add(new Status(b.getPost(),new Gson().fromJson(b.getUser(), User.class),b.getTimestamp(),b.getUrls(),b.getMentions()));
            }
            return new StatusesResponse(feed, beanDataPage.isHasMorePages());
        }
        catch (Exception e) {
            return new StatusesResponse(e.getMessage());
        }
    }

    public DataPage<FeedBean> getPageOfFeed(String targetUserAlias, int pageSize, Status lastStatus) {
        DynamoDbTable<FeedBean> feedTable = enhancedClient.table("feed", TableSchema.fromBean(FeedBean.class));
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

        SdkIterable<Page<FeedBean>> sdkIterable = feedTable.query(request);
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
}
