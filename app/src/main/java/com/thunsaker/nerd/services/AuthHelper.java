package com.thunsaker.nerd.services;

public class AuthHelper {
    // TwitterClient
    public final static String TWITTER_KEY = System.getenv("NERD_TWIT_KEY");
    public final static String TWITTER_SECRET = System.getenv("NERD_TWIT_SECRET");
    public final static String TWITTER_CALLBACK_URL = "http://127.0.0.1/twitter";
}