package com.thunsaker.nerd.classes.api;

import com.thunsaker.android.common.BaseEvent;

import twitter4j.DirectMessage;

public class NerdAnswerSendEvent extends BaseEvent {
    public DirectMessage answerDM;

    public NerdAnswerSendEvent(Boolean result, String resultMessage, DirectMessage answerDM) {
        super(result, resultMessage);
        this.answerDM = answerDM;
    }
}
