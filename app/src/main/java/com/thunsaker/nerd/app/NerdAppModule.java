package com.thunsaker.nerd.app;

import android.content.Context;

import com.squareup.picasso.Picasso;
import com.thunsaker.android.common.dagger.AndroidApplicationModule;
import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.nerd.BuildConfig;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = true,
        library = true,
        addsTo = AndroidApplicationModule.class,
        injects = {
                NerdApp.class
        }
)
public class NerdAppModule {
    @Provides
    Picasso providesPicasso(@ForApplication Context context) {
        Picasso picasso = Picasso.with(context);

        picasso.setDebugging(BuildConfig.DEBUG);
        return picasso;
    }
}