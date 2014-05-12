package com.thunsaker.nerd.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.nerd.R;
import com.thunsaker.nerd.app.BaseNerdActivity;
import com.thunsaker.nerd.app.NerdApp;
import com.thunsaker.nerd.classes.api.TwitterConnectedEvent;
import com.thunsaker.nerd.classes.api.TwitterFollowingEvent;
import com.thunsaker.nerd.services.TwitterClient;
import com.thunsaker.nerd.services.TwitterTasks;
import com.thunsaker.nerd.util.PreferencesHelper;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class WizardActivity extends BaseNerdActivity {

    @Inject
    @ForApplication
    Context mContext;

    @Inject
    EventBus mBus;

    private static final int NUM_PAGES = 3;

    private ViewPager mPager;

    private PagerAdapter mPagerAdapter;

    private String LOG_TAG = "WizardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        mBus.register(this, TwitterConnectedEvent.class);
        mBus.register(this, TwitterFollowingEvent.class);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayUseLogoEnabled(false);
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setTitle(R.string.app_name);
        ab.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        mPager = (ViewPager) findViewById(R.id.wizard_pager);
        mPagerAdapter = new WizardPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wizard_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_previous:
                handlePreviousButton();
                return true;
            case R.id.action_next:
                return handleNextButton();
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        handlePreviousButton();
    }

    private void handlePreviousButton() {
        if(mPager.getCurrentItem() == 0)
            super.onBackPressed();
        else
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
    }

    private boolean handleNextButton() {
        switch (mPager.getCurrentItem()) {
            case 0:
                mPager.setCurrentItem(1);
                break;
            case 1:
                if (!MainActivity.isTwitterConnected)
                    ShowTwitterAuth();
                else
                    mPager.setCurrentItem(2);
                break;
            case 2:
                if(!MainActivity.isTwitterConnected) {
                    ShowTwitterAuth();
                } else if (!MainActivity.isFollowingNerdTrivia) {
                    AttemptFollow();
                } else
                    ShowMainActivity();
                break;
        }

        return true;
    }

    private void AttemptFollow() {
        Toast.makeText(mContext, R.string.wizard_follow_attempting, Toast.LENGTH_SHORT).show();
        TwitterTasks twitterTask = new TwitterTasks((NerdApp)getApplication());
        twitterTask.new FollowUser("NerdTrivia").execute();
    }

    private void ShowMainActivity() {
        Intent mainIntent = new Intent(mContext, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private class WizardPagerAdapter extends FragmentStatePagerAdapter {
        public WizardPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new WizardFragment(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public static class WizardFragment extends Fragment {
        private int pos = 0;
        public WizardFragment(int position) {
            pos = position;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_wizard_page_about, container, false);

            switch (pos) {
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_wizard_page_twitter, container, false);
                    TextView textViewTwitterInfo = (TextView)rootView.findViewById(R.id.wizard_twitter_info);
                    Linkify.addLinks(textViewTwitterInfo, TwitterClient.twitterMentionPattern, TwitterClient.twitterUrlScheme, null, TwitterClient.twitterMentionFilter);
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_wizard_page_follow, container, false);
                    TextView textViewFollowFollow = (TextView)rootView.findViewById(R.id.wizard_follow_follow);
                    Linkify.addLinks(textViewFollowFollow, TwitterClient.twitterMentionPattern, TwitterClient.twitterUrlScheme, null, TwitterClient.twitterMentionFilter);
                    break;
                default:
                    TextView textViewAboutInfo = (TextView)rootView.findViewById(R.id.wizard_about_info);
                    Linkify.addLinks(textViewAboutInfo, Linkify.WEB_URLS);
                    Linkify.addLinks(textViewAboutInfo, TwitterClient.twitterMentionPattern, TwitterClient.twitterUrlScheme, null, TwitterClient.twitterMentionFilter);
                    break;
            }
            return rootView;
        }
    }

    private void ShowTwitterAuth() {
        Intent twitterAuth = new Intent(mContext, TwitterAuthorizationActivity.class);
        startActivity(twitterAuth);
    }

    public void onEvent(TwitterConnectedEvent event) {
        if(event.result) {
            MainActivity.isTwitterConnected = true;
            handleNextButton();
        } else {
            Log.d(LOG_TAG, "Auth failed...");
        }
    }

    public void onEvent(TwitterFollowingEvent event) {
        if(event.result) {
            MainActivity.isFollowingNerdTrivia = true;
            switch (event.followingOutcome) {
                case FOLLOW_EVENT_CAN_DM:
                    Toast.makeText(mContext, R.string.wizard_follow_following, Toast.LENGTH_SHORT).show();
                    MainActivity.canDmNerdTrivia = true;
                    PreferencesHelper.setTwitterCanDm(mContext, true);
                    break;
                default:
                    // TODO: Let the user know they can't play until NerdTrivia follows them
                    MainActivity.canDmNerdTrivia = false;
                    ShowUserWaitForFollowScreen();
                    // TODO: Allow user to see question but hide the answer form...then notify them once they have been followed by NerdTrivia, let a background process run for 10 minutes? Check every 2 minutes?
//                    StartBackgroundFollowCheck();
                    break;
            }

            handleNextButton();
        } else {
            MainActivity.isFollowingNerdTrivia = true;
            MainActivity.canDmNerdTrivia = false;
            Toast.makeText(mContext, R.string.wizard_follow_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void ShowUserWaitForFollowScreen() {
        Toast.makeText(mContext, R.string.wizard_follow_waiting, Toast.LENGTH_SHORT).show();
        ShowMainActivity();
    }

    private void StartBackgroundFollowCheck() {
        // TODO: Implement twitter follow checker here...
    }
}