package com.thunsaker.nerd.classes.api;

import com.thunsaker.nerd.classes.NerdQuestion;

public class NerdQuestionEvent {
    public NerdQuestion nerdQuestion;
    public Boolean result;
    public String resultMessage;

    public NerdQuestionEvent(NerdQuestion nerdQuestion, Boolean result, String resultMessage) {
        this.nerdQuestion = nerdQuestion;
        this.result = result;
        this.resultMessage = resultMessage;
    }
}