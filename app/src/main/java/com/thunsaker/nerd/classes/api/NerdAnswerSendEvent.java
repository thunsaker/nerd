package com.thunsaker.nerd.classes.api;

public class NerdAnswerSendEvent {
    public String answer;
    public Boolean result;
    public String resultMessage;

    public NerdAnswerSendEvent(String answer, Boolean result, String resultMessage) {
        this.result = result;
        this.resultMessage = resultMessage;
    }
}
