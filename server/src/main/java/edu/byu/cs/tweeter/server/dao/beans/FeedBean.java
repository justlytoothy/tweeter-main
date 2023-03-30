package edu.byu.cs.tweeter.server.dao.beans;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;


@DynamoDbBean
public class FeedBean {
    private String alias;

    private Long timestamp;
    public String post;

    @DynamoDbPartitionKey
    public String getAlias() {
        return alias;
    }

    @DynamoDbSortKey
    public Long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }





    @Override
    public String toString() {
        return "Status{" +
                "alias='" + alias + '\'' +
                "timestamp='" + timestamp + '\'' +
                "alias='" + alias + '\'' +
                "alias='" + alias + '\'' +
                ", timestamp='" + timestamp +
                '}';
    }
}