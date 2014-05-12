package com.thunsaker.nerd.activity;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;
import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.nerd.R;
import com.thunsaker.nerd.app.BaseNerdActivity;
import com.thunsaker.nerd.app.NerdApp;
import com.thunsaker.nerd.app.NerdBroadcastReceiver;
import com.thunsaker.nerd.classes.NerdQuestion;
import com.thunsaker.nerd.classes.api.NerdAnswerSendEvent;
import com.thunsaker.nerd.classes.api.NerdQuestionEvent;
import com.thunsaker.nerd.services.TwitterTasks;
import com.thunsaker.nerd.util.PreferencesHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import de.greenrobot.event.EventBus;

public class MainActivity extends BaseNerdActivity {
    private static final String LOG_TAG = "MainActivity";
    private static final long REPEAT_TIME = 1000 * 60 * 60;

    @InjectView(R.id.main_question_wrapper)RelativeLayout mRelativeLayoutQuestionWrapper;
    @InjectView(R.id.main_question_current)TextView mTextViewCurrentQuestion;
    @InjectView(R.id.main_question_points)TextView mTextViewPoints;
    @InjectView(R.id.main_question_time)TextView mTextViewQuestionTimestamp;
    @InjectView(R.id.main_question_photo_wrapper)FrameLayout mFrameLayoutPhotoWrapper;
    @InjectView(R.id.main_question_photo)ImageView mImageViewPhoto;

    @InjectView(R.id.main_answer_wrapper)RelativeLayout mRelativeLayoutAnswer;
    @InjectView(R.id.main_answer_current)TextView mTextViewAnswer;
    @InjectView(R.id.main_answer_timestamp)TextView mTextViewAnswerTimestamp;

    @InjectView(R.id.main_send_answer_text)EditText mEditTextAnswer;

    @InjectView(R.id.main_question_none_wrapper)RelativeLayout mRelativeLayoutNone;

    public static boolean isTwitterConnected = false;
    public static boolean isFollowingNerdTrivia = false;
    public static boolean canDmNerdTrivia = false;

    @Inject @ForApplication
    Context mContext;

    @Inject
    EventBus mBus;

    @Inject
    Picasso mPicasso;

    @Inject
    NotificationManager mNotificationManager;

    public static NerdQuestion mCurrentNerdQuestion;
    public static String mCurrentAnswer;
    private boolean mCurrentAnswerSent = false;

    SimpleDateFormat dateFormatTime;

    NotificationCompat.Builder mNotificationBuilder;
    private PendingIntent genericPendingIntent;

    public static final int NERD_QUESTION = 0;
    public static final int NERD_SEND = 1;
    private String EXTRA_NERD_SEND_ANSWER = "NERD_STRING_EXTRA_ANSWER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);


        mBus.register(this, NerdQuestionEvent.class);
        mBus.register(this, NerdAnswerSendEvent.class);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayUseLogoEnabled(false);
        ab.setDisplayHomeAsUpEnabled(false);

        ab.setTitle(R.string.app_name);
        ab.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        dateFormatTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        setupNotificationIntents(null);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        showProgress();

        isTwitterConnected = PreferencesHelper.getTwitterConnected(mContext);

        if(isTwitterConnected) {
            LoadLatestQuestion();
            startQuestionFetcher();
        } else {
            ShowWizard();
        }
    }

    private void setupNotificationIntents(String stringExtraAnswer) {
        Intent genericIntent = new Intent(getApplicationContext(), MainActivity.class);
        if(stringExtraAnswer != null && stringExtraAnswer.length() > 0) {
            genericIntent.putExtra(EXTRA_NERD_SEND_ANSWER, stringExtraAnswer);
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.from(getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(genericIntent);
        genericPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_CANCEL_CURRENT
        );
    }

    private void showProgress() {
        setProgressBarVisibility(true);
        setProgressBarIndeterminate(true);
    }

    private void hideProgress() {
        setProgressBarVisibility(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_refresh:
                LoadLatestQuestion();
                return true;
            case R.id.action_sign_out:
                ShowWizard();
            default:
                return false;
        }
    }

    private void LoadLatestQuestion() {
        showProgress();
        TwitterTasks twitterTask = new TwitterTasks((NerdApp)getApplication());
        twitterTask.new GetLatestQuestion(false).execute();
    }

    private void ShowWizard() {
        ClearCredentials();
        Intent wizard = new Intent(mContext, WizardActivity.class);
        startActivity(wizard);
        finish();
    }

    private void ClearCredentials() {
        isTwitterConnected = false;
        isFollowingNerdTrivia = false;
        PreferencesHelper.setTwitterConnected(mContext, false);
        PreferencesHelper.setTwitterEnabled(mContext, false);
        PreferencesHelper.setTwitterToken(mContext, "");
        PreferencesHelper.setTwitterSecret(mContext, "");
    }

    public void onEvent(NerdQuestionEvent event) {
        Log.d(LOG_TAG, "New NerdQuestionEvent!");
        clearQuestion();
        clearAnswer();
        hideProgress();
        if(event.result) {
            if(event.nerdQuestion != null) {
                mCurrentNerdQuestion = event.nerdQuestion;
                if(event.showNotification)
                    showNewQuestionNotification(mCurrentNerdQuestion);
                showQuestion();
            } else {
                mCurrentNerdQuestion = null;
                mRelativeLayoutNone.setVisibility(View.VISIBLE);
                hideKeyboard();

            }
        } else {
            mCurrentNerdQuestion = null;
            // TODO: Show different message if something fails.
            Toast.makeText(mContext, "There was a problem getting the latest question", Toast.LENGTH_SHORT).show();
        }
    }

    private void showQuestion() {
        mRelativeLayoutNone.setVisibility(View.GONE);
        mTextViewCurrentQuestion.setText(mCurrentNerdQuestion.getQuestion());
        mTextViewPoints.setText(String.format(getString(R.string.main_question_points), mCurrentNerdQuestion.getPoints().toString()));
        mTextViewQuestionTimestamp.setText(dateFormatTime.format(mCurrentNerdQuestion.getTimePosted().toCalendar(Locale.getDefault()).getTimeInMillis()));
        if (mCurrentNerdQuestion.getPhotoUrl() != null && !mCurrentNerdQuestion.getPhotoUrl().equals("")) {
            if(mCurrentNerdQuestion.getPhotoUrl().equals("404")) {
//                mFrameLayoutPhotoWrapper.setVisibility(View.VISIBLE);
                // TODO: Add message and allow opening in browser...
                mImageViewPhoto.setImageDrawable(null);
            } else {
                mFrameLayoutPhotoWrapper.setVisibility(View.VISIBLE);
                mPicasso.load(mCurrentNerdQuestion.getPhotoUrl()).into(mImageViewPhoto);
            }
        } else {
            mFrameLayoutPhotoWrapper.setVisibility(View.GONE);
            mImageViewPhoto.setImageDrawable(null);
        }
    }

    private void clearQuestion() {
        mTextViewQuestionTimestamp.setText("");
        mTextViewCurrentQuestion.setText("");
        mTextViewPoints.setText("");
        mFrameLayoutPhotoWrapper.setVisibility(View.GONE);
        mImageViewPhoto.setImageDrawable(null);
    }

    public void onEvent(NerdAnswerSendEvent event) {
        Log.d(LOG_TAG, "New NerdAnswerSendEvent");
        if(event.result) {
            mTextViewAnswerTimestamp.setText(String.format(getString(R.string.main_answer_time_sent), dateFormatTime.format(event.answerDM.getCreatedAt())));
            mTextViewAnswerTimestamp.setTextColor(getResources().getColor(R.color.blue));
            mCurrentAnswerSent = true;
            updateSendNotification();
            hideSendNotification();
        } else {
            mTextViewAnswerTimestamp.setText(R.string.error_send_answer);
            mTextViewAnswerTimestamp.setTextColor(getResources().getColor(R.color.red_text_states));
            showSendNotification(event.answerDM.getText(), true);
            mCurrentAnswerSent = false;
        }
    }

//    public void onEvent(TwitterConnectedEvent event) {
//        Log.d(LOG_TAG, "Twitter auth connected event");
//        if(event.result)
//            LoadLatestQuestion();
//        else
//            Log.d(LOG_TAG, "Auth failed...");
//    }

    @OnClick(R.id.main_question_photo_wrapper)
    public void openPhoto() {
        if(!mCurrentNerdQuestion.getPhotoUrl().equals(""))
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mCurrentNerdQuestion.getPhotoUrl())));
    }

    @OnLongClick(R.id.main_question_photo_wrapper)
    public boolean openPhotoToast() {
        if(!mCurrentNerdQuestion.getPhotoUrl().equals(""))
            Toast.makeText(mContext, R.string.main_question_photo_open_browser_toast, Toast.LENGTH_SHORT).show();
        return true;
    }

    @OnClick(R.id.main_send_answer_send_button)
    public void submitAnswerButton() {
        hideKeyboard();

        if(canDmNerdTrivia) {
            if (!mEditTextAnswer.getText().toString().equals(""))
                SubmitAnswer(mEditTextAnswer.getText().toString());
            else
                Toast.makeText(mContext, R.string.main_answer_submit_no_text, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, R.string.error_send_answer, Toast.LENGTH_SHORT).show();
//            TwitterTasks twitterTask = new TwitterTasks((NerdApp)getApplication());
//            twitterTask.new FollowUser("NerdTrivia").execute();
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mEditTextAnswer.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @OnLongClick(R.id.main_send_answer_send_button)
    public boolean openSendToast() {
        Toast.makeText(mContext, R.string.main_answer_send_toast, Toast.LENGTH_SHORT).show();
        return true;
    }

    @OnClick(R.id.main_question_time)
    public void openTwitterQuestionInBrowser() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mCurrentNerdQuestion.getCanonicalUrl())));
    }

    @OnClick(R.id.main_answer_timestamp)
    public void resubmitAnswer() {
        if (!mCurrentAnswerSent) {
            if (mCurrentAnswer.length() > 0)
                SubmitAnswer(mCurrentAnswer);
            else {
                Toast.makeText(mContext, R.string.error_resending, Toast.LENGTH_SHORT).show();
                mEditTextAnswer.requestFocus();
                clearAnswer();
            }
        }
    }

    private void SubmitAnswer(String answer) {
        mEditTextAnswer.setText(null);

        displayAnswer(answer);

        mCurrentNerdQuestion.setUserGuess(answer);
        mCurrentAnswer = answer;

        showSendNotification(answer, false);
        TwitterTasks twitterTask = new TwitterTasks((NerdApp)getApplication());
        twitterTask.new SendNerdAnswer(answer).execute();
    }

    private void displayAnswer(String answer) {
        mRelativeLayoutAnswer.setVisibility(View.VISIBLE);
        mTextViewAnswer.setText(answer);
    }

    private void clearAnswer() {
        mRelativeLayoutAnswer.setVisibility(View.GONE);
        mCurrentAnswer = null;
        mTextViewAnswer.setText(null);
        mTextViewAnswerTimestamp.setText(null);
    }

    private void showSendNotification(String answer, boolean retry){
        if(answer != null && answer.length() > 0) {
            setupNotificationIntents(answer);
        }

        NotificationCompat.Builder mNotificationSend = createSendNotification(getString(R.string.notification_sending_text));
        // TODO: Add answer to intent
        mNotificationManager.notify(NERD_SEND, mNotificationSend.getNotification());
    }

    private NotificationCompat.Builder createSendNotification(String notificationText) {
        return new NotificationCompat.Builder(mContext)
        .setSmallIcon(R.drawable.ic_stat_notification)
        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
        .setContentTitle(getString(R.string.app_name))
        .setContentText(notificationText)
        .setContentIntent(genericPendingIntent)
        .setAutoCancel(true);
    }

    public void updateSendNotification() {
        NotificationCompat.Builder mNotificationSend = createSendNotification(getString(R.string.notification_sent));
        // TODO: Add answer to intent
        mNotificationManager.notify(NERD_SEND, mNotificationSend.getNotification());
    }

    public void hideSendNotification() {
        mNotificationManager.cancel(NERD_SEND);
    }

    private void showNewQuestionNotification(NerdQuestion nerdQuestion){
        if(nerdQuestion != null) {
            String questionText = String.format("For %s points: %s", nerdQuestion.getPoints(), nerdQuestion.getQuestion());
            NotificationCompat.Builder mNotificationNewQuestion =
                    new NotificationCompat.Builder(mContext)
                            .setSmallIcon(R.drawable.ic_stat_question)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                            .setContentTitle(getString(R.string.notification_question_new))
                            .setContentText(questionText)
                            .setContentIntent(genericPendingIntent)
                            .setAutoCancel(true);
            // TODO: Decide if I want to try and fetch the picture first.
//            if (nerdQuestion.getPhotoUrl() != null && !nerdQuestion.getPhotoUrl().equals("404")) {
//                NotificationCompat.BigPictureStyle bigPictureStyle =
//                        new NotificationCompat.BigPictureStyle();
//                bigPictureStyle.bigPicture()
//                mNotificationNewQuestion.setStyle()
//            }

            NotificationCompat.BigTextStyle bigTextStyle =
                    new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText(questionText);
            mNotificationNewQuestion.setStyle(bigTextStyle);

            mNotificationManager.notify(NERD_QUESTION, mNotificationNewQuestion.getNotification());
        }
    }
    @SuppressWarnings("all") // There is a bug with inspection of calendar resources something: https://code.google.com/p/android/issues/detail?id=68894
    private void startQuestionFetcher() {
        Log.d(LOG_TAG, "Setting up the alarm service");
        AlarmManager alarmService =
                (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent fetchLatestQuestionIntent =
                new Intent(mContext, NerdBroadcastReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(mContext, 0, fetchLatestQuestionIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"), Locale.ENGLISH);
        Calendar nerdCalendar = currentCalendar;
        Log.d(LOG_TAG, String.format("Current time: %s", dateFormatTime.format(nerdCalendar.getTimeInMillis())));
        nerdCalendar.set(Calendar.HOUR_OF_DAY, 8);
        nerdCalendar.set(Calendar.SECOND, 15);

        alarmService.setExact(AlarmManager.RTC_WAKEUP, nerdCalendar.getTimeInMillis(), pendingIntent); // Call at exactly 8am...

        if(currentCalendar.get(Calendar.HOUR_OF_DAY) >= 8 && currentCalendar.get(Calendar.HOUR_OF_DAY) < 16) {
            currentCalendar.add(Calendar.HOUR, 1);
            currentCalendar.set(Calendar.MINUTE, 0);
            currentCalendar.set(Calendar.SECOND, 15);
            alarmService.setInexactRepeating(AlarmManager.RTC_WAKEUP, currentCalendar.getTimeInMillis(), REPEAT_TIME, pendingIntent);
        }
    }
}