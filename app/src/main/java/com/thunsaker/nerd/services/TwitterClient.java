package com.thunsaker.nerd.services;

import android.content.Context;
import android.util.Log;

import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.nerd.app.NerdApp;
import com.thunsaker.nerd.classes.NerdQuestion;
import com.thunsaker.nerd.classes.ParsingError;
import com.thunsaker.nerd.classes.api.NerdAnswerSendEvent;
import com.thunsaker.nerd.classes.api.NerdQuestionEvent;
import com.thunsaker.nerd.util.PreferencesHelper;

import java.util.List;

import javax.inject.Inject;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

public class TwitterClient {
	private static final String LOG_TAG = "TwitterClient";

    @Inject @ForApplication
    Context mContext;

    @Inject
    Twitter mTwitter;

    public TwitterClient(NerdApp app) {
        app.inject(this);
    }

    public NerdQuestionEvent getLatestQuestion() throws TwitterException {
        Log.i(LOG_TAG, "Getting latest Questions");
        setTwitterCredentials();

        Status status = null;
        List<Status> statuses = mTwitter.getUserTimeline("NerdTrivia");
        if(statuses.size() > 0) {
            Log.i(LOG_TAG, "We have some tweets!");
            status = statuses.get(0);
            if(status.getText().startsWith("For")) {
                Log.i(LOG_TAG, "We have a question");
                NerdQuestion question = null;
                try {
                    question = NerdQuestion.parseTwitterStatus(status);
                    Log.i(LOG_TAG, "Parsing succeeded");
                } catch (ParsingError parsingError) {
                    parsingError.printStackTrace();
                    return new NerdQuestionEvent(null, false, "Parsing error");
                }

                if(question != null) {
                    return new NerdQuestionEvent(question, true, "");
                } else {
                    Log.i(LOG_TAG, "Something went wrong...");
                    return new NerdQuestionEvent(null, false, "Something went wrong...");
                }
            } else {
                Log.i(LOG_TAG, "No questions...");
                return new NerdQuestionEvent(null, true, "");
            }
        } else {
            Log.i(LOG_TAG, "No tweets");
            return new NerdQuestionEvent(null, false, "No new questions...");
        }
    }

    private void setTwitterCredentials() {
       String userToken = PreferencesHelper.getTwitterToken(mContext);
        String userSecret = PreferencesHelper.getTwitterSecret(mContext);
        mTwitter.setOAuthAccessToken(new AccessToken(userToken, userSecret));
    }

    public NerdAnswerSendEvent sendDM(String user, String message) throws TwitterException {
        Log.i(LOG_TAG, "Sending DM");
        setTwitterCredentials();

        DirectMessage dm = mTwitter.sendDirectMessage(user, message);
        return new NerdAnswerSendEvent(message, true, "");
    }
}