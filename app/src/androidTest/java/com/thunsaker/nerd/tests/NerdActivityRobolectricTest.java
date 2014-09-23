package com.thunsaker.nerd.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class NerdActivityRobolectricTest {
//    @Test
//    public void testSomething() throws Exception {
//        Activity activity = Robolectric.buildActivity(MainActivity.class).create().get();
//        assertTrue(activity != null);
//    }

    @Test
    public void testTheTruth() throws Exception {
        assertTrue(true);
    }
}