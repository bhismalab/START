package com.reading.start.tests.test_jabble.utils;

import android.content.Context;
import android.media.SoundPool;
import android.util.Log;

import com.reading.start.tests.test_jabble.R;

public class SoundManager {
    private static final String TAG = SoundManager.class.getSimpleName();

    private static final int sSound = R.raw.bubble1;

    private Context mContext;

    private SoundPool mSoundPool = null;

    private static SoundManager sInstance = null;

    private boolean mLoaded = false;

    private int mSoundId = -1;

    public static SoundManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SoundManager(context);
        }

        return sInstance;
    }

    private SoundManager(Context context) {
        mContext = context;

        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(3);

        mSoundPool = builder.build();
        mSoundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> mLoaded = true);

        mSoundId = mSoundPool.load(context, sSound, 1);
    }

    public synchronized void playSound1() {
        playMediaPlayer();
    }

    private void playMediaPlayer() {
        try {
            if (mLoaded) {
                mSoundPool.play(mSoundId, 1, 1, 1, 0, 1f);
            }
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
    }
}
