package com.programyourhome.huebridgesimulator.model.connection;

public class UserActivity {

    private final User user;
    private final ActivityType activityType;
    private final String data;

    public UserActivity(final User user, final ActivityType activityType) {
        this(user, activityType, null);
    }

    public UserActivity(final User user, final ActivityType activityType, final String data) {
        this.user = user;
        this.activityType = activityType;
        this.data = data;
    }

    public User getUser() {
        return this.user;
    }

    public ActivityType getActivityType() {
        return this.activityType;
    }

    public String getData() {
        return this.data;
    }

}
