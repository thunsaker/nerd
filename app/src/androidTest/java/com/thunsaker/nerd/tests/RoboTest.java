package com.thunsaker.nerd.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class RoboTest {
    @Test
    public void testTheTruth() throws Exception {
        assertEquals(true, true);
    }
}