package com.thunsaker.nerd.app;

import com.thunsaker.android.common.BaseActivity;

public class BaseNerdActivity extends BaseActivity {
    @Override
    protected Object[] getActivityModules() {
        return new Object[] {
                new NerdActivityModule(this)
        };
    }
}
