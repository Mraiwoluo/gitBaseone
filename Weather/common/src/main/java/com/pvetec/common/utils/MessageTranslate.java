package com.pvetec.common.utils;

import android.os.Handler;
import android.os.Message;

public class MessageTranslate {

    public interface Translater {

        public void Translate();
    }

    public static void getInstance() {

    }

    public static final int MEESSAGE_TRANSLATE = 10;

    static Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (MEESSAGE_TRANSLATE == msg.what) {
                Translater translater = (Translater) msg.obj;
                MessageTranslate.DoTranslate(translater);
            }
        }
    };

    public static void PostTranslate(Translater translater) {
        if (translater == null) return;
        Message msg = messageHandler.obtainMessage();
        ;
        msg.what = MEESSAGE_TRANSLATE;
        msg.obj = translater;
        messageHandler.sendMessage(msg);
    }

    public static void PostTranslate(Translater translater, long delayMillis) {
        if (translater == null) return;
        Message msg = messageHandler.obtainMessage();
        ;
        msg.what = MEESSAGE_TRANSLATE;
        msg.obj = translater;
        messageHandler.sendMessageDelayed(msg, delayMillis);
    }

    public static void DoTranslate(Translater translater) {
        if (translater == null) return;
        translater.Translate();
    }

    public static void postDelayed(Runnable runnable, long delayMillis) {
        messageHandler.postDelayed(runnable, delayMillis);
    }

    public static void removeCallbacks(Runnable runnable) {
        messageHandler.removeCallbacks(runnable);
    }
}
