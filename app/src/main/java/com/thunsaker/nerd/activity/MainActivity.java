package com.thunsaker.nerd.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.TextView;

import com.thunsaker.nerd.R;
import com.thunsaker.nerd.app.BaseNerdActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseNerdActivity {
    @InjectView(R.id.main_question_current)TextView mTextViewCurrentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        ab.setIcon(null);
        ab.setDisplayUseLogoEnabled(false);
        ab.setDisplayHomeAsUpEnabled(false);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
//        mTextViewCurrentQuestion.setText(R.string.test_title_text);
    }
}