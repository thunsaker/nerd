package com.thunsaker.nerd.tests;

import com.thunsaker.android.common.test.TestsDaggerInit;
import com.thunsaker.nerd.tests.app.TestNerdAppModule;

import dagger.ObjectGraph;

public class NerdTestsDaggerInit implements TestsDaggerInit {
    @Override
    public ObjectGraph getObjectGraph() {
        ObjectGraph objectGraph = ObjectGraph.create(new TestNerdAppModule());
        objectGraph.validate();
        return objectGraph;
    }
}
