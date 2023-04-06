package edu.byu.cs.tweeter.server.dao.beans;

import java.util.List;

public class UpdateFeedUtil {
    public List<String> users;
    public String alias;
    public Long timestamp;
    public String post;
    public List<String> urls;
    public List<String> mentions;
    public String user;

    public UpdateFeedUtil(List<String> users, String alias, Long timestamp, String post, List<String> urls, List<String> mentions, String user) {
        this.users = users;
        this.alias = alias;
        this.timestamp = timestamp;
        this.post = post;
        this.urls = urls;
        this.mentions = mentions;
        this.user = user;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
