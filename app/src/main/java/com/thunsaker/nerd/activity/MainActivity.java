package com.thunsaker.nerd.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.thunsaker.nerd.R;
import com.thunsaker.nerd.app.BaseNerdActivity;
import com.thunsaker.nerd.app.NerdApp;
import com.thunsaker.nerd.classes.NerdQuestion;
import com.thunsaker.nerd.classes.api.NerdAnswerSendEvent;
import com.thunsaker.nerd.classes.api.NerdQuestionEvent;
import com.thunsaker.nerd.services.TwitterTasks;
import com.thunsaker.nerd.util.PreferencesHelper;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MainActivity extends BaseNerdActivity {
    private static final String LOG_TAG = "MainActivity";
    @InjectView(R.id.main_question_current)TextView mTextViewCurrentQuestion;
    @InjectView(R.id.main_question_points)TextView mTextViewPoints;
    @InjectView(R.id.main_question_photo_wrapper)LinearLayout mLinearLayoutPhotoWrapper;
    @InjectView(R.id.main_question_photo)ImageView mImageViewPhoto;

    @InjectView(R.id.main_answer_wrapper)RelativeLayout mRelativeLayoutAnswer;
    @InjectView(R.id.main_answer_current)TextView mTextViewAnswer;

    @InjectView(R.id.main_question_none_wrapper)RelativeLayout mRelativeLayoutNone;

    @InjectView(R.id.main_send_answer_text)EditText mEditText;

    public static boolean isTwitterConnected = false;

//    @Inject Context mContext;
    @Inject
    EventBus mBus;

    @Inject
    Picasso mPicasso;

    public static NerdQuestion mCurrentNerdQuestion;
    public static String mCurrentAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBus.registerSticky(this, NerdQuestionEvent.class);

        ActionBar ab = getSupportActionBar();
        ab.setIcon(null);
        ab.setDisplayUseLogoEnabled(false);
        ab.setDisplayHomeAsUpEnabled(false);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        isTwitterConnected = PreferencesHelper.getTwitterConnected(getApplicationContext());

        if(isTwitterConnected) {
            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
            TwitterTasks twitterTask = new TwitterTasks((NerdApp)getApplication());
            twitterTask.new GetLatestQuestion().execute();
        } else {
            ShowTwitterAuth();
        }
    }

    private void ShowTwitterAuth() {
        MainActivity.isTwitterConnected = false;
        finish();
        Intent twitterAuth = new Intent(getApplicationContext(), TwitterAuthorizationActivity.class);
        startActivity(twitterAuth);
    }

    public void onEvent(NerdQuestionEvent event) {
        Log.i(LOG_TAG, "New NerdQuestionEvent!");
        if(event.result) {
            mCurrentNerdQuestion = event.nerdQuestion;
            mTextViewCurrentQuestion.setText(mCurrentNerdQuestion.getQuestion());
            mTextViewPoints.setText(mCurrentNerdQuestion.getPoints().toString());
            if(!mCurrentNerdQuestion.getPhotoUrl().equals("")) {
                mLinearLayoutPhotoWrapper.setVisibility(View.VISIBLE);
                mPicasso.load(mCurrentNerdQuestion.getPhotoUrl()).into(mImageViewPhoto);
            }
        } else {
            mCurrentNerdQuestion = null;
            mRelativeLayoutNone.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "There was a problem getting the latest question", Toast.LENGTH_SHORT).show();
        }
    }

    public void onEvent(NerdAnswerSendEvent event) {
        Log.i(LOG_TAG, "New NerdAnswerSendEvent");
        if(event.result) {
            mRelativeLayoutAnswer.setBackgroundColor(R.color.blue);
        } else {
            mRelativeLayoutAnswer.setBackgroundColor(R.color.red);
        }
    }

    @OnClick(R.id.main_question_photo_wrapper)
    public void zoomPhoto() {
        // TODO: Implement an actual zoom thing here instead of redirecting to browser...
        if(!mCurrentNerdQuestion.getPhotoUrl().equals("")) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mCurrentNerdQuestion.getPhotoUrl())));
        }
    }

    @OnClick(R.id.main_send_answer_send_button)
    public void submitAnswer() {
        if(!mEditText.getText().toString().equals("")) {
            String answer = mEditText.getText().toString();
            mRelativeLayoutAnswer.setVisibility(View.VISIBLE);
            mTextViewAnswer.setText(answer);

            // TODO: Send the DM to NerdTrivia
            TwitterTasks twitterTask = new TwitterTasks((NerdApp)getApplication());
            mCurrentNerdQuestion.setUserGuess(answer);
            mCurrentAnswer = answer;
            twitterTask.new SendNerdAnswer(answer).execute();

        }
    }
}