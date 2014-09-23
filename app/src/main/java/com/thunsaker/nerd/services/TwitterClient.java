package com.thunsaker.nerd.services;

import android.content.Context;
import android.text.util.Linkify;
import android.util.Log;

import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.nerd.activity.MainActivity;
import com.thunsaker.nerd.app.NerdApp;
import com.thunsaker.nerd.classes.NerdQuestion;
import com.thunsaker.nerd.classes.ParsingError;
import com.thunsaker.nerd.classes.api.NerdAnswerSendEvent;
import com.thunsaker.nerd.classes.api.NerdQuestionEvent;
import com.thunsaker.nerd.classes.api.TwitterFollowingEvent;
import com.thunsaker.nerd.util.PreferencesHelper;
import com.thunsaker.nerd.classes.api.TwitterFollowingEvent.FollowEventOutcome;
import com.twitter.Extractor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import twitter4j.DirectMessage;
import twitter4j.Relationship;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
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

    public NerdQuestionEvent getLatestQuestion(Boolean showNotification) throws TwitterException {
        Log.d(LOG_TAG, "Getting latest Questions");
        setTwitterCredentials();

        Status status;
        List<Status> statuses = mTwitter.getUserTimeline("NerdTrivia");
        if(statuses.size() > 0) {
            Log.d(LOG_TAG, "We have some tweets!");
            status = statuses.get(0);
            if(status.getText().startsWith("For")) {
                Log.d(LOG_TAG, "We have a question");
                NerdQuestion question;
                try {
                    question = NerdQuestion.parseTwitterStatus(status);
                    Log.d(LOG_TAG, "Parsing succeeded");
                } catch (ParsingError parsingError) {
                    parsingError.printStackTrace();
                    return new NerdQuestionEvent(false, "Parsing error", null, false);
                }

                if(question != null) {
                    return new NerdQuestionEvent(true, "", question, showNotification);
                } else {
                    Log.d(LOG_TAG, "Something went wrong...");
                    return new NerdQuestionEvent(false, "Something went wrong...", null, false);
                }
            } else {
                Log.d(LOG_TAG, "No questions...");
                return new NerdQuestionEvent(true, "", null, false);
            }
        } else {
            Log.d(LOG_TAG, "No tweets");
            return new NerdQuestionEvent(false, "No new questions...", null, false);
        }
    }

    private void setTwitterCredentials() {
       String userToken = PreferencesHelper.getTwitterToken(mContext);
        String userSecret = PreferencesHelper.getTwitterSecret(mContext);
        mTwitter.setOAuthAccessToken(new AccessToken(userToken, userSecret));
    }

    public NerdAnswerSendEvent sendDM(String user, String message) throws TwitterException {
        Log.d(LOG_TAG, "Sending DM");
        setTwitterCredentials();

        DirectMessage dm = mTwitter.sendDirectMessage(user, message);
        if(dm != null)
            return new NerdAnswerSendEvent(true, "", dm);
        else
            return new NerdAnswerSendEvent(false, "", null);
    }

    public TwitterFollowingEvent followUser(String user) throws TwitterException {
        setTwitterCredentials();

        Relationship relationship = mTwitter.showFriendship(mTwitter.getScreenName(), user);
        if(relationship.canSourceDm()) {
            MainActivity.canDmNerdTrivia = true;
            return new TwitterFollowingEvent(true, "", null, FollowEventOutcome.FOLLOW_EVENT_CAN_DM);
        } else {
            if(relationship.isSourceFollowingTarget()) {
                return new TwitterFollowingEvent(true, "", null, FollowEventOutcome.FOLLOW_EVENT_NOT_FOLLOWED);
            } else {
                User followedUser = mTwitter.createFriendship(user);
                if(followedUser != null) {
                    if(followedUser.isFollowRequestSent()) {
                        return new TwitterFollowingEvent(true, "", followedUser, FollowEventOutcome.FOLLOW_EVENT_FOLLOWING);
                    }
                }
            }
        }
        return new TwitterFollowingEvent(false, "", null, FollowEventOutcome.FOLLOW_EVENT_ERROR);
    }


    public static Linkify.TransformFilter twitterMentionFilter = new Linkify.TransformFilter() {
        @Override
        public String transformUrl(Matcher match, String url) {
            return match.group(1);
        }
    };

    public static Pattern twitterMentionPattern = Pattern.compile("@([A-Za-z0-9_-]+)");
    public static String twitterUrlScheme = "http://twitter.com/";

    public static List<String> GetLinksInText(String textToCheck) {
        Extractor twitterExtractor = new Extractor();
        List<String> myLinks = twitterExtractor.extractURLs(textToCheck);

        if(myLinks != null && !myLinks.isEmpty()) {
            return myLinks;
        }

        return null;
    }
}