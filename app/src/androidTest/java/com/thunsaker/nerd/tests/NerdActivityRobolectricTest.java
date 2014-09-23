package com.thunsaker.nerd.tests;

import android.app.Activity;

import com.thunsaker.nerd.activity.MainActivity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Config(emulateSdk = 18, manifest = "src/main/AndroidManifest.xml")
@RunWith(RobolectricTestRunner.class)
public class NerdActivityRobolectricTest {
    @Test
    public void testSomething() throws Exception {
        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
        Assert.assertTrue(activity != null);
    }

    @Test
    public void testTheTruth() throws Exception {
        Assert.assertTrue(true);
    }
}