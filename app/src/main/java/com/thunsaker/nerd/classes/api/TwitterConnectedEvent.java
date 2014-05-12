package com.thunsaker.nerd.classes.api;

import com.thunsaker.android.common.BaseEvent;

public class TwitterConnectedEvent extends BaseEvent {
    public TwitterConnectedEvent(Boolean result, String resultMessage) {
        super(result, resultMessage);
    }
}
