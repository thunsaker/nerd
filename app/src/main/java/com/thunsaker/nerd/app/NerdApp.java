package com.thunsaker.nerd.app;

import com.thunsaker.android.common.dagger.DaggerApplication;

import java.util.Collections;
import java.util.List;

public class NerdApp extends DaggerApplication {

    @Override
    protected List<Object> getAppModules() {
        return Collections.<Object>singletonList(new NerdAppModule());
    }
}