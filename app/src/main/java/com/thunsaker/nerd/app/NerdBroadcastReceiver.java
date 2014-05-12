package com.thunsaker.nerd.app;

import android.content.Context;
import android.content.Intent;

import com.thunsaker.nerd.services.TwitterTasks;

public class NerdBroadcastReceiver extends NerdInjectingBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        TwitterTasks twitterTask = new TwitterTasks((NerdApp)context.getApplicationContext());
        twitterTask.new GetLatestQuestion(true).execute();
    }
}