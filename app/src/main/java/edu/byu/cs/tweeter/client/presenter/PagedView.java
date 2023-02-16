package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public interface PagedView<T> {
    void displayMessage(String message);
    void setLoadingFooter(boolean set);
    void addMoreItems(List<T> items);
    void userReceived(User user);
}
