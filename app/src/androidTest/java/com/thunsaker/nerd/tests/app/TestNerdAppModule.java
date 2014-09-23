package com.thunsaker.nerd.tests.app;

import com.thunsaker.nerd.tests.TwitterTest;

import dagger.Module;

@Module(
    injects = {
        TwitterTest.class
    }
)

public class TestNerdAppModule {
}