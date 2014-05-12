package com.thunsaker.nerd.app;

import android.app.NotificationManager;
import android.content.Context;

import com.squareup.picasso.Picasso;
import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.android.common.dagger.AndroidApplicationModule;
import com.thunsaker.nerd.BuildConfig;
import com.thunsaker.nerd.activity.MainActivity;
import com.thunsaker.nerd.services.AuthHelper;
import com.thunsaker.nerd.services.TwitterClient;
import com.thunsaker.nerd.services.TwitterTasks;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

@Module(
        complete = true,
        library = true,
        addsTo = AndroidApplicationModule.class,
        injects = {
                NerdApp.class,
                MainActivity.class,
                TwitterClient.class,
                TwitterTasks.class,
                NerdBroadcastReceiver.class
        }
)
public class NerdAppModule {
    public NerdAppModule() { }

    @Provides
    Picasso providesPicasso(@ForApplication Context context) {
        Picasso picasso = Picasso.with(context);

        picasso.setDebugging(BuildConfig.DEBUG);
        return picasso;
    }

    @Provides
    @Singleton
    NotificationManager providesNotificationManager(@ForApplication Context myContext) {
        return (NotificationManager) myContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    @Singleton
    Twitter providesTwitter() {
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(AuthHelper.TWITTER_KEY, AuthHelper.TWITTER_SECRET);
        return twitter;
    }
}