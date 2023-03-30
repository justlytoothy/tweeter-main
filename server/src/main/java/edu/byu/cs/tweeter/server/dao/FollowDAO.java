package edu.byu.cs.tweeter.server.dao;


import com.amazonaws.services.dynamodbv2.document.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.CountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowingRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowingResponse;
import edu.byu.cs.tweeter.model.net.response.UserListResponse;
import edu.byu.cs.tweeter.server.dao.beans.Follows;
import edu.byu.cs.tweeter.server.dao.beans.UserBean;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO extends BaseDAO implements IFollowDAO {
    private static final String TableName = "follows";
    public static final String IndexName = "follows_index";
    private static final String FollowerHandle = "follower_handle";
    private static final String FolloweeHandle = "followee_handle";
    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_1)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    public FollowDAO() {
        this.initializeDatabase();
        this.switchTable("follows");
    }

    @Override
    public UserListResponse getFollowees(FollowingRequest request) {
        assert request.getLimit() > 0;
        assert request.getFollowerAlias() != null;
        List<User> responseFollowees = new ArrayList<>(request.getLimit());
        boolean hasMorePages = false;
        if(request.getLimit() > 0) {
            DataPage<Follows> followees = getPageOfFollowees(request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias());

            hasMorePages = followees.isHasMorePages();
            for (Follows f : followees.getValues()) {
                UserBean user = new UserDAO().getUserUsername(f.getFollowee_handle());
                responseFollowees.add(new User(user.getFirst_name(), user.getLast_name(), user.getUsername(), user.getImage()));
            }
        }

        return new UserListResponse(responseFollowees, hasMorePages);
    }

    @Override
    public UserListResponse getFollowers(FollowerRequest request) {
        assert request.getLimit() > 0;
        assert request.getFolloweeAlias() != null;
        List<User> responseFollowers = new ArrayList<>(request.getLimit());
        boolean hasMorePages = false;
        if(request.getLimit() > 0) {
            DataPage<Follows> followers = getPageOfFollowers(request.getFolloweeAlias(), request.getLimit(), request.getLastFolloweeAlias());
            hasMorePages = followers.isHasMorePages();
            for (Follows f : followers.getValues()) {
                UserBean user = new UserDAO().getUserUsername(f.getFollower_handle());
                responseFollowers.add(new User(user.getFirst_name(), user.getLast_name(), user.getUsername(), user.getImage()));
            }
        }

        return new UserListResponse(responseFollowers, hasMorePages);
    }


    @Override
    public CountResponse getFollowerCount(CountRequest countRequest) {
        try {
            DynamoDbIndex<Follows> index = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class)).index(IndexName);
            Key key = Key.builder().partitionValue(countRequest.getTargetUser()).build();
            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(key));
            QueryEnhancedRequest request = requestBuilder.build();
            SdkIterable<Page<Follows>> sdkIterable = index.query(request);
            PageIterable<Follows> pages = PageIterable.create(sdkIterable);
            AtomicInteger count = new AtomicInteger();
            pages.stream()
                    .limit(1)
                    .forEach((Page<Follows> page) -> {
                        page.items().forEach(visit -> count.getAndIncrement());
                    });
            return new CountResponse(count.get());
        }
        catch (Exception e) {
            return new CountResponse(e.getMessage());
        }
    }

    @Override
    public CountResponse getFollowingCount(CountRequest countRequest) {
        try {
            DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
            Key key = Key.builder().partitionValue(countRequest.getTargetUser()).build();
            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(key));
            QueryEnhancedRequest request = requestBuilder.build();
            SdkIterable<Page<Follows>> sdkIterable = table.query(request);
            PageIterable<Follows> pages = PageIterable.create(sdkIterable);
            AtomicInteger count = new AtomicInteger();
            pages.stream()
                    .limit(1)
                    .forEach((Page<Follows> page) -> {
                        page.items().forEach(visit -> count.getAndIncrement());
                    });
            return new CountResponse(count.get());
        }
        catch (Exception e) {
            return new CountResponse(e.getMessage());
        }

    }

    @Override
    public FollowResponse follow(FollowRequest request) {
        try {
            table.putItem(new Item().withPrimaryKey("follower_handle",request.getCurrentUser()).withString("followee_handle", request.getTargetUserAlias()));
            return new FollowResponse();
        }
        catch (Exception e) {
            return new FollowResponse(e.getMessage());
        }
    }

    @Override
    public FollowResponse unfollow(FollowRequest request) {
        deleteFollows(request.getCurrentUser(), request.getTargetUserAlias());
        return new FollowResponse();
    }

    @Override
    public IsFollowingResponse isFollowing(IsFollowingRequest request) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName,TableSchema.fromBean(Follows.class));
        Key key = Key.builder().partitionValue(request.getCurrentUser()).sortValue(request.getTargetUserAlias()).build();
        if (table.getItem(key) != null) {
            return new IsFollowingResponse(true);
        }
        else {
            return new IsFollowingResponse(false);
        }
    }

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }


    public void deleteFollows(String followeeHandle, String followerHandle) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName,TableSchema.fromBean(Follows.class));
        Key key = Key.builder().partitionValue(followeeHandle).sortValue(followerHandle).build();
        table.deleteItem(key);
    }

    public DataPage<Follows> getPageOfFollowers(String targetUserAlias, int pageSize, String lastUserAlias) {
        DynamoDbIndex<Follows> index = enhancedClient.table(TableName,TableSchema.fromBean(Follows.class)).index(IndexName);
        Key key = Key.builder().partitionValue(targetUserAlias).build();
        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize).scanIndexForward(true);
        if(isNonEmptyString(lastUserAlias)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FolloweeHandle, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(FollowerHandle, AttributeValue.builder().s(lastUserAlias).build());
            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<Follows> result = new DataPage<Follows>();

        SdkIterable<Page<Follows>> sdkIterable = index.query(request);
        PageIterable<Follows> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<Follows> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(visit -> result.getValues().add(visit));
                });
        return result;

    }

    public DataPage<Follows> getPageOfFollowees(String targetUserAlias, int pageSize, String lastUserAlias ) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName, TableSchema.fromBean(Follows.class));
        Key key = Key.builder().partitionValue(targetUserAlias).build();
        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize).scanIndexForward(true);
        if(isNonEmptyString(lastUserAlias)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FollowerHandle, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(FolloweeHandle, AttributeValue.builder().s(lastUserAlias).build());
            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<Follows> result = new DataPage<Follows>();

        SdkIterable<Page<Follows>> sdkIterable = table.query(request);
        PageIterable<Follows> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<Follows> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(visit -> result.getValues().add(visit));
                });

        return result;
    }

}
