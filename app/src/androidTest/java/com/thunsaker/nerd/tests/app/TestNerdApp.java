package com.thunsaker.nerd.tests.app;

import android.content.Context;

import com.thunsaker.nerd.app.NerdApp;

import java.util.List;

import dagger.ObjectGraph;

public class TestNerdApp extends NerdApp {
    public static TestNerdApp from(Context context) {
        return (TestNerdApp) context.getApplicationContext();
    }

    ObjectGraph objectGraph;

    @Override
    protected List<Object> getAppModules() {
        List<Object> modules = super.getAppModules();
        modules.add(new TestNerdAppModule());
        return modules;
    }

//    public static <T> T injectMocks(T object) {
//        TestNerdApp app = (TestNerdApp) Robolectric.application;
//        return app.inject(object);
//    }
}
