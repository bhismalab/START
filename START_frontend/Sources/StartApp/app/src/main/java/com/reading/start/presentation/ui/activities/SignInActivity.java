package com.reading.start.presentation.ui.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.WindowManager;

import com.reading.start.R;
import com.reading.start.presentation.ui.dialogs.DialogPasswordReset;
import com.reading.start.presentation.ui.dialogs.DialogPasswordSet;
import com.reading.start.presentation.ui.fragments.sign_in.LoginFragment;
import com.reading.start.presentation.ui.fragments.sign_in.SynchronizeFragment;
import com.reading.start.presentation.ui.interfaces.IFragmentBack;

/**
 * Activity that contains logic for login.
 */
public class SignInActivity extends BaseLanguageActivity {
    public static final String TAG = SignInActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        openLoginFragment();
    }

    /**
     * Open login screen.
     */
    public void openLoginFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof LoginFragment) {
            ft.attach(frag);
        } else {
            frag = new LoginFragment();
            ft.replace(R.id.content_frame, frag);
        }

        ft.commit();
    }

    /**
     * Open forgot password screen
     */
    public void openForgotPasswordFragment() {
        DialogPasswordReset dialog = DialogPasswordReset.getInstance(getString(R.string.login_password_reset_title),
                getText(R.string.login_password_reset_message).toString(), new DialogPasswordReset.DialogPasswordResetListener() {
                    @Override
                    public void onCancel() {
                        // no need any action
                    }

                    @Override
                    public void onReset(String userId, String hash) {
                        DialogPasswordSet dialog = DialogPasswordSet.getInstance(getString(R.string.login_password_reset_title),
                                getText(R.string.login_password_set_message).toString(), userId, hash, () -> {
                                    // no need any action
                                });

                        dialog.setCancelable(false);
                        dialog.show(getFragmentManager(), TAG);
                    }
                });

        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), TAG);
    }

    /**
     * Open synchronize screen
     */
    public void openSynchronizeFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof SynchronizeFragment) {
            ft.attach(frag);
        } else {
            frag = new SynchronizeFragment();
            ft.replace(R.id.content_frame, frag);
        }

        ft.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.gc();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        boolean needBaseBack = true;

        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);

        if (frag != null && frag instanceof IFragmentBack) {
            IFragmentBack back = (IFragmentBack) frag;
            needBaseBack = !back.onBackPressed();
        }

        if (needBaseBack) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
