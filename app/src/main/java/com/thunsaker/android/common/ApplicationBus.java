//package com.thunsaker.android.common;
//
//import android.os.Handler;
//import android.os.Looper;
//
//import com.squareup.otto.Bus;
//
//import javax.inject.Singleton;
//
///**
// * A custom Bus to post events on the Main Thread only.
// */
//@Singleton
//public class ApplicationBus extends Bus {
//    private final Handler mainThread = new Handler(Looper.getMainLooper());
//
//    public ApplicationBus() {
//        super();
//    }
//
//    @Override
//    public void post(final Object event) {
//
//        if (event == null) {
//            return;
//        }
//
//        if (Looper.myLooper() == Looper.getMainLooper()) {
//            super.post(event);
//        } else {
//            mainThread.post(new Runnable() {
//                @Override
//                public void run() {
//                    post(event);
//                }
//            });
//        }
//    }
//}