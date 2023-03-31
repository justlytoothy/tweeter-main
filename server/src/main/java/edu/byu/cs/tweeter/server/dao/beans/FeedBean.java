package edu.byu.cs.tweeter.server.dao.beans;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;


@DynamoDbBean
public class FeedBean {
    private String alias;
    private Long timestamp;
    public String post;
    public List<String> urls;
    public List<String> mentions;
    public String user;

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

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }

    private String arrayToString(List<String> stuff) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < stuff.size(); i++) {
            stringBuilder.append(stuff.get(i));
            if (i != stuff.size()-1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    @Override
    public String toString() {
        return "Status{" +
                "alias='" + alias + '\'' +
                "timestamp='" + timestamp.toString() + '\'' +
                "urls='" + arrayToString(urls) + '\'' +
                "user='" + user + '\'' +
                ", mentions='" + arrayToString(mentions) +
                '}';
    }
}