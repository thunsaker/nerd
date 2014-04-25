package com.thunsaker.android.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import com.twitter.Extractor;

import java.util.List;

public class Util {
	private static final String LOG_TAG = "Util";
	public static final int FACEBOOK_UPDATE = 0;
	public static final int TWITTER_UPDATE = 1;
	public static final int FOURSQUARE_UPDATE = 2;
	public static final int GOOGLEPLUS_UPDATE = 3;
	public static final int APPDOTNET_POST = 4;

	public static final String ENCODER_CHARSET = "UTF-8";

	public static final int CHAR_LIMIT_FACEBOOK = 1000;
	public static final int CHAR_LIMIT_TWITTER = 140;
	public static final int CHAR_LIMIT_APP_DOT_NET = 256;

	public static String contentType = "json/application";

	@SuppressWarnings("static-access")
	public static Boolean HasInternet(Context myContext) {
		Boolean HasConnection = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) myContext
				.getSystemService(myContext.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		if (activeNetworkInfo != null) {
			State myState = activeNetworkInfo.getState();
			if (myState == State.CONNECTED || myState == State.CONNECTING) {
				HasConnection = true;
			}
		}
		return HasConnection;
	}

	public static List<String> GetLinksInText(String textToCheck) {
		Extractor twitterExtractor = new Extractor();
		List<String> myLinks = twitterExtractor.extractURLs(textToCheck);

		if(myLinks != null && !myLinks.isEmpty()) {
			return myLinks;
		}

		return null;
	}
}