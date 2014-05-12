package com.thunsaker.nerd.app;

import android.app.Activity;
import android.content.Context;

import com.thunsaker.android.common.annotations.ForActivity;
import com.thunsaker.nerd.activity.MainActivity;
import com.thunsaker.nerd.activity.TwitterAuthorizationActivity;
import com.thunsaker.nerd.activity.WizardActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = true,
        library =  true,
        addsTo = NerdAppModule.class,
        injects = {
                MainActivity.class,
                WizardActivity.class,
                TwitterAuthorizationActivity.class
        }
)
public class NerdActivityModule {
    private final BaseNerdActivity mActivity;

    public NerdActivityModule(BaseNerdActivity activity) {
        mActivity = activity;
    }

    @Provides
    @Singleton
    @ForActivity
    Context providesActivityContext() {
        return mActivity;
    }

    @Provides
    @Singleton
    Activity providesActivity() {
        return mActivity;
    }
}
