package com.reading.start.presentation.ui.fragments.sign_in;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.reading.start.R;
import com.reading.start.databinding.FragmentLoginBinding;
import com.reading.start.general.TLog;
import com.reading.start.presentation.mvp.holders.LoginViewHolder;
import com.reading.start.presentation.mvp.presenters.LoginPresenter;
import com.reading.start.presentation.mvp.views.LoginView;
import com.reading.start.presentation.ui.activities.AdminActivity;
import com.reading.start.presentation.ui.activities.SignInActivity;
import com.reading.start.presentation.ui.dialogs.DialogLoginFailed;
import com.reading.start.presentation.ui.fragments.base.BaseFragment;
import com.reading.start.utils.Utility;

/**
 * Screen that contain logic for login.
 */
public class LoginFragment extends BaseFragment implements LoginView {

    public static final String TAG = LoginFragment.class.getSimpleName();

    private LoginPresenter mPresenter = null;

    private FragmentLoginBinding mBinding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);

        // init presenter
        final LoginViewHolder viewHolder = new LoginViewHolder(mBinding.userName, mBinding.password,
                mBinding.login, mBinding.forgot);
        mPresenter = new LoginPresenter();
        mPresenter.init(this, viewHolder);
        mPresenter.onCreate(inflater, container, savedInstanceState);

        updatePromoSize();
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
        updatePromoSize();
    }

    /**
     * Raises when login as admin.
     */
    @Override
    public void onAdminLogin() {
        Intent intent = new Intent(getActivity(), AdminActivity.class);
        startActivity(intent);
    }

    /**
     * Raises when login success
     */
    @Override
    public void onLoginSuccess() {
        final SignInActivity activity = (SignInActivity) getActivity();

        if (activity != null) {
            activity.openSynchronizeFragment();
        }
    }

    /**
     * Raises when pressed forgot password
     */
    @Override
    public void onForgotPassword() {
        SignInActivity activity = (SignInActivity) getActivity();
        activity.openForgotPasswordFragment();
    }

    /**
     * Notify that sign in error.
     */
    @Override
    public void onError(String message) {
        runOnUiThread(() -> {
            try {
                final Activity activity = getActivity();

                if (activity != null) {
                    if (message != null && !message.isEmpty()) {
                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(activity, R.string.error_message_login_failed, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                TLog.e(TAG, e);
            }
        });
    }

    /**
     * Raises when social worker locked from server
     */
    @Override
    public void onAttemptFailServer(String message) {
        DialogLoginFailed dialog = DialogLoginFailed.getInstance(getResources().getString(R.string.error_message_login_failed_title),
                getResources().getText(R.string.error_message_login_attempts_end).toString());

        dialog.show(getFragmentManager(), TAG);
    }

    /**
     * Raises when social worker locked on the application side
     */
    @Override
    public void onAttemptFailLocal(String message) {
        DialogLoginFailed dialog = DialogLoginFailed.getInstance(getResources().getString(R.string.error_message_login_failed_title),
                getResources().getText(R.string.error_message_login_attempt_failed).toString());

        dialog.show(getFragmentManager(), TAG);
    }

    private void updatePromoSize() {
        try {
            Size size = Utility.getDisplaySize(getActivity());
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.login_promo);
            double promoWidth = bitmap.getWidth();
            double promoHeight = bitmap.getHeight();

            double factor = promoWidth / promoHeight;
            double viewWidth = size.getWidth();
            double viewHeight = viewWidth / factor;

            RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) mBinding.promo.getLayoutParams();
            param.width = (int) viewWidth;
            param.height = (int) viewHeight;
            mBinding.promo.setLayoutParams(param);
        } catch (Exception e) {
            TLog.e(TAG, e);
        }
    }
}
