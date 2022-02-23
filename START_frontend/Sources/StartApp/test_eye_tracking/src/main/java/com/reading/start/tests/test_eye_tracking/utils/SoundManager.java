package com.reading.start.tests.test_eye_tracking.utils;

import android.content.Context;
import android.media.SoundPool;
import android.util.Log;

import com.reading.start.tests.test_eye_tracking.R;

public class SoundManager {
    private static final String TAG = SoundManager.class.getSimpleName();

    private static final int sSoundElephant = R.raw.elephant;
    private static final int sSoundGiraffe = R.raw.giraffe;
    private static final int sSoundLion = R.raw.lion;
    private static final int sSoundMonkey = R.raw.monkey;

    private Context mContext;

    private static SoundManager sInstance = null;

    private SoundPool mSoundPool = null;

    private boolean mLoaded = false;

    private int mSoundIdElephant = -1;
    private int mSoundIdGiraffe = -1;
    private int mSoundIdLion = -1;
    private int mSoundIdMonkey = -1;

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

        mSoundIdElephant = mSoundPool.load(context, sSoundElephant, 1);
        mSoundIdGiraffe = mSoundPool.load(context, sSoundGiraffe, 1);
        mSoundIdLion = mSoundPool.load(context, sSoundLion, 1);
        mSoundIdMonkey = mSoundPool.load(context, sSoundMonkey, 1);
    }

    public synchronized void playSoundElephant() {
        playMediaPlayer(mSoundIdElephant);
    }

    public synchronized void playSoundGiraffe() {
        playMediaPlayer(mSoundIdGiraffe);
    }

    public synchronized void playSoundLion() {
        playMediaPlayer(mSoundIdLion);
    }

    public synchronized void playSoundMonkey() {
        playMediaPlayer(mSoundIdMonkey);
    }

    private void playMediaPlayer(int soundId) {
        try {
            if (mLoaded) {
                mSoundPool.play(soundId, 1, 1, 1, 0, 1f);
            }
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
    }
}
