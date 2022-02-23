package com.reading.start.presentation.ui.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.reading.start.R;

/**
 * Dialog for displaying progress.
 */
public class DialogProgress extends DialogFragment {
    public static final String TAG = DialogProgress.class.getSimpleName();

    private static final String MESSAGE_TAG = "Message";

    private View mRootView;

    public static DialogProgress getInstance(String message) {
        DialogProgress frag = new DialogProgress();
        Bundle bundle = new Bundle();
        bundle.putString(MESSAGE_TAG, message);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mRootView = inflater.inflate(R.layout.dialog_progress, container);

        Bundle bundle = getArguments();
        String mMessage = bundle.getString(MESSAGE_TAG);

        TextView message = mRootView.findViewById(R.id.content_text);
        message.setText(mMessage);
        startAnimation();
        return mRootView;
    }

    @Override
    public Dialog getDialog() {
        return super.getDialog();
    }

    private void startAnimation() {
        mRootView.findViewById(R.id.progress_bar).startAnimation(getImageCycleAnimation());

        final AlphaAnimation animationTo = getImageAlphaAnimationTo();
        final AlphaAnimation animationFrom = getImageAlphaAnimationFrom();

        animationTo.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRootView.findViewById(R.id.synchronize_text_view).startAnimation(animationFrom);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        animationFrom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRootView.findViewById(R.id.synchronize_text_view).startAnimation(animationTo);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mRootView.findViewById(R.id.synchronize_text_view).startAnimation(animationTo);
    }

    private RotateAnimation getImageCycleAnimation() {
        RotateAnimation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(2500);

        return anim;
    }

    private AlphaAnimation getImageAlphaAnimationTo() {
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(1000);
        animation1.setStartOffset(0);
        animation1.setFillAfter(true);

        return animation1;
    }

    private AlphaAnimation getImageAlphaAnimationFrom() {
        AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.2f);
        animation1.setDuration(1000);
        animation1.setStartOffset(0);
        animation1.setFillAfter(true);

        return animation1;
    }
}
