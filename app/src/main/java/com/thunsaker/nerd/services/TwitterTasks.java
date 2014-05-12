package com.thunsaker.nerd.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.nerd.R;
import com.thunsaker.nerd.app.NerdApp;
import com.thunsaker.nerd.classes.api.NerdAnswerSendEvent;
import com.thunsaker.nerd.classes.api.NerdQuestionEvent;
import com.thunsaker.nerd.classes.api.TwitterConnectedEvent;
import com.thunsaker.nerd.classes.api.TwitterFollowingEvent;
import com.thunsaker.nerd.util.PreferencesHelper;

import java.io.IOException;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import twitter4j.TwitterException;

public class TwitterTasks {
    private static final String LOG_TAG = "TwitterTasks";

    @Inject
    EventBus mBus;

    @Inject @ForApplication
    Context mContext;

    TwitterClient mTwitterClient;

    public TwitterTasks(NerdApp app) {
        app.inject(this);
        mTwitterClient = new TwitterClient(app);
    }

    public class TokenFetcher extends AsyncTask<Void, Integer, Boolean> {
        OAuthGetAccessToken myOauthGetAccessToken;

        public TokenFetcher(OAuthGetAccessToken theGetAccessToken) {
            myOauthGetAccessToken = theGetAccessToken;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            OAuthCredentialsResponse myResponse = null;
            String token = PreferencesHelper.getTwitterToken(mContext);

            if(token == null || token.equalsIgnoreCase("")) {
                try {
                    myResponse = myOauthGetAccessToken.execute();
                } catch (IOException e) {
                    Log.d(LOG_TAG, "IOException");
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.d(LOG_TAG, "Exception");
                    e.printStackTrace();
                }

                Log.d(LOG_TAG, "Response: " + myResponse);

                if(myResponse != null && myResponse.token != null && myResponse.tokenSecret != null) {
                    PreferencesHelper.setTwitterToken(mContext, myResponse.token);
                    PreferencesHelper.setTwitterSecret(mContext, myResponse.tokenSecret);
                    PreferencesHelper.setTwitterEnabled(mContext, true);
                    PreferencesHelper.setTwitterConnected(mContext, true);
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            String resultMessage = mContext.getString(R.string.twitter_auth_failed);

            if(PreferencesHelper.getTwitterConnected(mContext))
                resultMessage = mContext.getString(R.string.twitter_auth_connected);

            Toast.makeText(mContext, resultMessage, Toast.LENGTH_SHORT).show();
            mBus.post(new TwitterConnectedEvent(result, resultMessage));
        }
    }

    public class GetLatestQuestion extends AsyncTask<Void, Integer, NerdQuestionEvent> {
        private Boolean showNotification;

        public GetLatestQuestion(Boolean showNotification) {
            this.showNotification = showNotification;
        }

        @Override
        protected NerdQuestionEvent doInBackground(Void... params) {
            try {
                return mTwitterClient.getLatestQuestion(showNotification);
            } catch (TwitterException e) {
                e.printStackTrace();
                return new NerdQuestionEvent(false, e.getMessage(), null, false);
            } catch (Exception e) {
                e.printStackTrace();
                return new NerdQuestionEvent(false, e.getMessage(), null, false);
            }
        }

        @Override
        protected void onPostExecute(NerdQuestionEvent nerdQuestionEvent) {
            Log.d(LOG_TAG, "Posting to the bus...");
            mBus.post(nerdQuestionEvent);
        }
    }

    public class SendNerdAnswer extends AsyncTask<Void, Integer, NerdAnswerSendEvent> {
        String myMessage;
        public SendNerdAnswer(String message) {
            myMessage = message;
        }

        @Override
        protected NerdAnswerSendEvent doInBackground(Void... params) {
            try {
                return mTwitterClient.sendDM("NerdTrivia", myMessage);
            } catch (TwitterException e) {
                e.printStackTrace();
                return new NerdAnswerSendEvent(false, e.getMessage(), null);
            } catch (Exception e) {
                e.printStackTrace();
                return new NerdAnswerSendEvent(false, e.getMessage(), null);
            }
        }

        @Override
        protected void onPostExecute(NerdAnswerSendEvent nerdAnswerSendEvent) {
            Log.d(LOG_TAG, "Posting to the bus...");
            mBus.post(nerdAnswerSendEvent);
        }
    }

    public class FollowUser extends AsyncTask<Void, Integer, TwitterFollowingEvent> {
        String myUser;
        public FollowUser(String user) {
            myUser = user;
        }

        @Override
        protected TwitterFollowingEvent doInBackground(Void... params) {
            try {
                return mTwitterClient.followUser("NerdTrivia");
            } catch (TwitterException e) {
                e.printStackTrace();
                return new TwitterFollowingEvent(false, e.getMessage(), null, null);
            } catch (Exception e) {
                e.printStackTrace();
                return new TwitterFollowingEvent(false, e.getMessage(), null, null);
            }
        }

        @Override
        protected void onPostExecute(TwitterFollowingEvent twitterFollowingEvent) {
            mBus.post(twitterFollowingEvent);
        }
    }
}