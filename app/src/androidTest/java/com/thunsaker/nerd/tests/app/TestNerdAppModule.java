package com.thunsaker.nerd.tests.app;

import com.thunsaker.nerd.tests.TwitterTests;

import dagger.Module;

@Module(
    injects = {
        TwitterTests.class
    }
)

public class TestNerdAppModule {
}