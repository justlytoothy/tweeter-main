package edu.byu.cs.tweeter.client.model.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.PagedNotificationHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.client.model.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {

    public void loadMoreItems(User user, int pageSize, Status lastStatus, PagedNotificationObserver<Status> getStoryObserver) {
        GetStoryTask getStoryTask = new GetStoryTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastStatus, new PagedNotificationHandler<Status>(getStoryObserver));
        ServiceExecutor.execute(getStoryTask);

    }

    public void loadMoreItemsFeed(User user, int pageSize, Status lastStatus, PagedNotificationObserver<Status> getFeedObserver) {
        GetFeedTask getFeedTask = new GetFeedTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastStatus, new PagedNotificationHandler<Status>(getFeedObserver));
        ServiceExecutor.execute(getFeedTask);

    }

    public void postStatus(AuthToken currUserAuthToken, String post, User currUser, SimpleNotificationObserver postStatusObserver) throws ParseException {
        Status newStatus = new Status(post,currUser,getFormattedDateTime(),parseURLs(post),parseMentions(post));
        PostStatusTask statusTask = new PostStatusTask(currUserAuthToken,
                newStatus, new SimpleNotificationHandler(postStatusObserver));
        ServiceExecutor.execute(statusTask);
    }


    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }
    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }
    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }
    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

}
