package edu.byu.cs.tweeter.server.dao;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import edu.byu.cs.tweeter.util.FakeData;
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
            DataPage<Follows> followees = getPageOfFollowers(request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias());
            hasMorePages = followees.isHasMorePages();
            //TODO finish this
            for (Follows f : followees.getValues()) {
                String first = f.getFollowee_name().split(" ")[0];
                String last = f.getFollowee_name().split(" ")[1];
                responseFollowees.add(new User(first,last,f.getFollowee_handle(),"https://tweeter-images-jschilling.s3.us-east-1.amazonaws.com/" + f.getFollowee_handle()));
            }
        }

        return new UserListResponse(responseFollowees, hasMorePages);
    }

    @Override
    public UserListResponse getFollowers(FollowerRequest request) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getFolloweeAlias() != null;
        List<User> responseFollowers = new ArrayList<>(request.getLimit());
        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            DataPage<Follows> followers = getPageOfFollowers(request.getFolloweeAlias(), request.getLimit(), request.getLastFolloweeAlias());
            hasMorePages = followers.isHasMorePages();
            //TODO finish this
            for (Follows f : followers.getValues()) {
                String first = f.getFollower_name().split(" ")[0];
                String last = f.getFollower_name().split(" ")[1];
                responseFollowers.add(new User(first,last,f.getFollower_handle(),"https://tweeter-images-jschilling.s3.us-east-1.amazonaws.com/" + f.getFollower_handle()));
            }
        }

        return new UserListResponse(responseFollowers, hasMorePages);
    }


    @Override
    public CountResponse getFollowerCount(CountRequest request) {
        return new CountResponse(getDummyFollowers().size());
    }

    @Override
    public CountResponse getFollowingCount(CountRequest request) {
        return new CountResponse(getDummyFollowers().size());
    }

    @Override
    public FollowResponse follow(FollowRequest request) {
        return new FollowResponse();
    }

    @Override
    public FollowResponse unfollow(FollowRequest request) {
        return new FollowResponse();
    }

    @Override
    public IsFollowingResponse isFollowing(IsFollowingRequest request) {
        //TODO check if acceptable
        return new Random().nextInt() % 2 == 0 ? new IsFollowingResponse(true) : new IsFollowingResponse(false);
    }

    /**
     * Returns the list of dummy follower data. This is written as a separate method to allow
     * mocking of the followers.
     *
     * @return the followers.
     */
    List<User> getDummyFollowers() {
        return getFakeData().getFakeUsers();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy followees.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }
    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }

    /**
     * Returns the list of dummy followee data. This is written as a separate method to allow
     * mocking of the followees.
     *
     * @return the followees.
     */
    List<User> getDummyFollowees() {
        return getFakeData().getFakeUsers();
    }



    /**
     * Determines the index for the first follower in the specified 'allFollowers' list that should
     * be returned in the current request. This will be the index of the next follower after the
     * specified 'lastFollower'.
     *
     * @param lastFollowerAlias the alias of the last follower that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowers the generated list of followers from which we are returning paged results.
     * @return the index of the first follower to be returned.
     */
    private int getFollowersStartingIndex(String lastFollowerAlias, List<User> allFollowers) {

        int followersIndex = 0;

        if(lastFollowerAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowers.size(); i++) {
                if(lastFollowerAlias.equals(allFollowers.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followersIndex = i + 1;
                    break;
                }
            }
        }

        return followersIndex;
    }


















    private static final String TableName = "follows";
    public static final String IndexName = "follows_index";
    private static final String FollowerName = "follower_name";
    private static final String FollowerHandle = "follower_handle";
    private static final String FolloweeName = "followee_name";
    private static final String FolloweeHandle = "followee_handle";

    // DynamoDB client
    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_EAST_1)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }


    public Follows getFollower(String followerHandle, String followeeHandle){
        DynamoDbTable<Follows> table = enhancedClient.table(TableName,TableSchema.fromBean(Follows.class));
        Key key = Key.builder().partitionValue(followerHandle).sortValue(followeeHandle).build();
        Follows follows = table.getItem(key);
        return follows;
    }

    public Follows getFollowee(String followeeHandle, String followerHandle){
        DynamoDbTable<Follows> table = enhancedClient.table(TableName,TableSchema.fromBean(Follows.class));
        Key key = Key.builder().partitionValue(followeeHandle).sortValue(followerHandle).build();
        Follows follows = table.getItem(key);
        return follows;
    }

    public void putFollows(String followerHandle,String followerName,String followeeHandle, String followeeName) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName,TableSchema.fromBean(Follows.class));
        Follows newFollows = new Follows();
        newFollows.setFollower_handle(followerHandle);
        newFollows.setFollower_name(followerName);
        newFollows.setFollowee_handle(followeeHandle);
        newFollows.setFollowee_name(followeeName);
        table.putItem(newFollows);
    }

    public void updateFollows(String followerHandle,String followerName,String followeeHandle, String followeeName) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName,TableSchema.fromBean(Follows.class));
        Key key = Key.builder().partitionValue(followerHandle).sortValue(followeeHandle).build();
        Follows follows = table.getItem(key);
        follows.setFollowee_name(followeeName);
        follows.setFollower_name(followerName);
        table.updateItem(follows);
    }

    public void deleteFollows(String followeeHandle, String followerHandle) {
        DynamoDbTable<Follows> table = enhancedClient.table(TableName,TableSchema.fromBean(Follows.class));
        Key key = Key.builder().partitionValue(followeeHandle).sortValue(followerHandle).build();
        table.deleteItem(key);
    }

    public DataPage<Follows> getPageOfFollowers(String targetUserAlias, int pageSize, String lastUserAlias ) {
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
