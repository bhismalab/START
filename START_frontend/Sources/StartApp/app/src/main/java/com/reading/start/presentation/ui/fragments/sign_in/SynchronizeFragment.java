package com.reading.start.presentation.ui.fragments.sign_in;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.reading.start.R;
import com.reading.start.databinding.FragmentSynchronizeBinding;
import com.reading.start.general.TLog;
import com.reading.start.presentation.mvp.holders.SynchronizeViewHolder;
import com.reading.start.presentation.mvp.presenters.SynchronizePresenter;
import com.reading.start.presentation.mvp.views.SynchronizeView;
import com.reading.start.presentation.ui.activities.MainActivity;
import com.reading.start.presentation.ui.activities.SignInActivity;
import com.reading.start.presentation.ui.dialogs.DialogSynchronizeError;
import com.reading.start.presentation.ui.fragments.base.BaseFragment;

/**
 * Screen that display synchronization progress.
 */
public class SynchronizeFragment extends BaseFragment implements SynchronizeView {

    public static final String TAG = SynchronizeFragment.class.getSimpleName();

    private SynchronizePresenter mPresenter = null;

    private FragmentSynchronizeBinding mBinding;

    public SynchronizeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_synchronize, container, false);

        // init presenter
        final SynchronizeViewHolder viewHolder = new SynchronizeViewHolder(mBinding.synchronizeTextView, mBinding.progressBar, mBinding.logo);
        mPresenter = new SynchronizePresenter();
        mPresenter.init(this, viewHolder);
        mPresenter.onCreate(inflater, container, savedInstanceState);

        return mBinding.getRoot();
    }

    @Override
    public void onPause() {
        if (mPresenter != null) {
            mPresenter.onPause();
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }

        super.onDestroy();
    }

    @Override
    public void onResume() {
        if (mPresenter != null) {
            mPresenter.onResume();
        }

        super.onResume();
    }

    /**
     * Raises when start synchronization with server
     */
    @Override
    public void onLoading() {
        mBinding.progressBar.startAnimation(getImageCycleAnimation());

        final AlphaAnimation animationTo = getImageAlphaAnimationTo();
        final AlphaAnimation animationFrom = getImageAlphaAnimationFrom();

        animationTo.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBinding.synchronizeTextView.startAnimation(animationFrom);
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
                mBinding.synchronizeTextView.startAnimation(animationTo);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        mBinding.synchronizeTextView.startAnimation(animationTo);
    }

    /**
     * Raises when synchronization with server completed
     */
    @Override
    public void onLoadSuccess() {
        final Activity activity = getActivity();

        if (activity != null) {
            activity.finish();
            Intent intent = new Intent(activity, MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Raises when got error during synchronization
     */
    @Override
    public void onError(String message) {
        runOnUiThread(() -> {
            try {
                DialogSynchronizeError dialog = DialogSynchronizeError.getInstance(getResources().getString(R.string.synchronize_dialog_error_title),
                        getResources().getText(R.string.synchronize_dialog_error_message).toString(), new DialogSynchronizeError.DialogListener() {
                            @Override
                            public void onTryAgain() {
                                if (mPresenter != null) {
                                    mPresenter.processLoading();
                                }
                            }

                            @Override
                            public void onCancel() {
                                final SignInActivity activity = getSignInActivity();

                                if (activity != null) {
                                    activity.openLoginFragment();
                                }
                            }
                        });

                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), TAG);
            } catch (Exception e) {
                TLog.e(TAG, e);
            }
        });
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

    protected SignInActivity getSignInActivity() {
        if (getActivity() instanceof SignInActivity) {
            return (SignInActivity) getActivity();
        } else {
            return null;
        }
    }
}
