package com.thunsaker.nerd.tests;

import com.thunsaker.nerd.classes.NerdQuestion;
import com.thunsaker.nerd.classes.ParsingError;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

@Config(emulateSdk = 18, manifest = "src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class TwitterTest {
    @Test
    public void testHasTwitterKeys() throws Exception {
        String twit_key = System.getenv("NERD_TWIT_KEY");
        Assert.assertTrue(twit_key != null);
    }

    @Ignore // TODO: Need to pass my personal oauth token or log the user in before the test
    @Test
    public void ParseTweetTest() throws TwitterException {
        NerdQuestion expected = new NerdQuestion();
        expected.setTweetId(459013841620180992L);
        String originalText = "For 10 points: Name the movie character: http://twitpic.com/6fa33l";
        expected.setTweetText("For 10 points: Name the movie character:");
        expected.setCanonicalUrl("https://twitter.com/NerdTrivia/status/459013841620180992");
        expected.setTimePosted(new DateTime().withDate(2014,4,23).withTime(10,0,0,0));
        expected.setHasExpired(true);

        List<String> answers = new ArrayList<String>();
        answers.add("Luna Lovegood");
        answers.add("LunaLovegood");
        answers.add("Luna Lovegoode");
        answers.add("Luma Lovegood");
        expected.setAnswer(answers);
        expected.setUserGuess("luna lovegood");

        // Stuff to parse from tweet
        expected.setPoints(10);
        expected.setQuestion("Name the movie character");
        expected.setPhotoUrl("twitpic.com/6fa33l");

        Twitter twitter = TwitterFactory.getSingleton();
//        List<Status> statuses = twitter.getUserTimeline("nerdtrivia");
        Status status = twitter.showStatus(459013841620180992L);
        NerdQuestion actual = new NerdQuestion();
        try {
            actual = NerdQuestion.parseTwitterStatus(status);
        } catch (ParsingError parsingError) {
            parsingError.printStackTrace();
            Assert.fail("Problem Parsing status update");
        }

        Assert.assertEquals(expected.getPoints(), actual.getPoints());
        Assert.assertEquals(expected.getQuestion(), actual.getQuestion());
        Assert.assertEquals(expected.getCanonicalUrl(), actual.getCanonicalUrl());
        Assert.assertEquals(expected.getTweetText(), actual.getTweetText());
    }
}