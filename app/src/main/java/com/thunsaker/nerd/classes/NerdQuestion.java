package com.thunsaker.nerd.classes;

import org.joda.time.DateTime;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Status;
import twitter4j.URLEntity;

public class NerdQuestion {
    private long tweetId;
    private String tweetText;
    private String canonicalUrl;
    private String question;
    private Integer points;
    private String photoUrl;
    private String userGuess;
    private List<String> answers;
    private DateTime timePosted;
    private Boolean hasExpired;

    public long getTweetId() {
        return tweetId;
    }
    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }

    public String getTweetText() {
        return tweetText;
    }
    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }
    public void setCanonicalUrl(String canonicalUrl) {
        this.canonicalUrl = canonicalUrl;
    }

    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getPoints() {
        return points;
    }
    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUserGuess() {
        return userGuess;
    }
    public void setUserGuess(String userGuess) {
        this.userGuess = userGuess;
    }

    public List<String> getAnswers() {
        return answers;
    }
    public void setAnswer(List<String> answer) {
        this.answers = answer;
    }

    public DateTime getTimePosted() {
        return timePosted;
    }
    public void setTimePosted(DateTime timePosted) {
        this.timePosted = timePosted;
    }

    public Boolean getHasExpired() {
        return hasExpired;
    }
    public void setHasExpired(Boolean hasExpired) {
        this.hasExpired = hasExpired;
    }

    public static NerdQuestion parseTwitterStatus(Status status) throws ParsingError {
        NerdQuestion parsed = new NerdQuestion();
        long id = status.getId();
        parsed.setTweetId(id);

        String text = status.getText();
        parsed.setTweetText(text);
        parsed.setCanonicalUrl(String.format("https://twitter.com/nerdtrivia/status/%s", id));
        parsed.setTimePosted(new DateTime().withMillis(status.getCreatedAt().getTime()));

        String pattern = "^(For\\s(5|10|15|20)\\spoints:?\\s)(.+)$"; // http://regex101.com/r/yV4mH7 // http://regex101.com/r/dY1jS3
        Pattern pointsPattern = Pattern.compile(pattern);
        Matcher pointMatcher = pointsPattern.matcher(text);
        while (pointMatcher.find()) {
            if (pointMatcher.groupCount() > 0) {
                parsed.setPoints(Integer.parseInt(pointMatcher.group(2)));
                parsed.setQuestion(pointMatcher.group(3));
            }
        }

        // If we have URLs in the tweet, they are most likely images
        // TODO: Consider using TwitterClient-Text for this.
        URLEntity[] urls = status.getURLEntities();
        if(urls.length > 0) {
            String photoUrl = urls[0].getURL();
            parsed.setPhotoUrl(photoUrl);
            // Remove the url.
            parsed.setQuestion(parsed.getQuestion().replace(photoUrl, ""));
        }

        return parsed;
    }
}