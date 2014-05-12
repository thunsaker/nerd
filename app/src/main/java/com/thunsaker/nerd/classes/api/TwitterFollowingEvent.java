package com.thunsaker.nerd.classes.api;

import com.thunsaker.android.common.BaseEvent;

import twitter4j.User;

public class TwitterFollowingEvent extends BaseEvent {
//    public static int FOLLOW_EVENT_CAN_DM = 0;
//    public static int FOLLOW_EVENT_FOLLOWING = 1;
//    public static int FOLLOW_EVENT_NOT_FOLLOWED = 2;
//    public static int FOLLOW_EVENT_ERROR = 3;

    public User followedUser;
    public FollowEventOutcome followingOutcome;
    public TwitterFollowingEvent(Boolean result, String resultMessage, User followedUser, FollowEventOutcome followingOutcome) {
        super(result, resultMessage);
        this.followedUser = followedUser;
        this.followingOutcome = followingOutcome;
    }

    public enum FollowEventOutcome {
        FOLLOW_EVENT_CAN_DM,
        FOLLOW_EVENT_FOLLOWING,
        FOLLOW_EVENT_NOT_FOLLOWED,
        FOLLOW_EVENT_ERROR
    }
}
