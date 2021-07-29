package com.project.onlineshop.api;

import com.project.onlineshop.model.User;

import java.util.List;

public class ServerReport {

    private int usersCount;
    private int postersCount;

    private List<User> topSellers;

    public ServerReport(int usersCount, int postersCount, List<User> topSellers) {
        this.usersCount = usersCount;
        this.postersCount = postersCount;
        this.topSellers = topSellers;
    }

    public int getUsersCount() {
        return usersCount;
    }

    public int getPostersCount() {
        return postersCount;
    }

    public List<User> getTopSellers() {
        return topSellers;
    }
}
