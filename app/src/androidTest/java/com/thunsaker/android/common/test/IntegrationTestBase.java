package com.thunsaker.android.common.test;

import com.thunsaker.nerd.tests.NerdTestsDaggerInit;

import org.junit.BeforeClass;

import dagger.ObjectGraph;

public class IntegrationTestBase {
    protected static ObjectGraph objectGraph;

    @BeforeClass
    public static void beforeClass() {
        objectGraph = new NerdTestsDaggerInit().getObjectGraph();
    }
}