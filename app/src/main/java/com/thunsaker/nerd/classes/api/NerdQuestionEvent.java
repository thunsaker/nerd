package com.thunsaker.nerd.classes.api;

import com.thunsaker.android.common.BaseEvent;
import com.thunsaker.nerd.classes.NerdQuestion;

public class NerdQuestionEvent extends BaseEvent {
    public NerdQuestion nerdQuestion;
    public Boolean showNotification;

    public NerdQuestionEvent(Boolean result, String resultMessage, NerdQuestion nerdQuestion, Boolean showNotification) {
        super(result, resultMessage);
        this.nerdQuestion = nerdQuestion;
        this.showNotification = showNotification;
    }
}