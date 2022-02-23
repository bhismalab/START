package com.reading.start.presentation.ui.fragments.base;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.reading.start.AppCore;
import com.reading.start.R;
import com.reading.start.presentation.mvp.holders.ActionBarViewHolder;
import com.reading.start.presentation.mvp.presenters.ActionBarPresenter;
import com.reading.start.presentation.mvp.views.ActionBarView;
import com.reading.start.presentation.ui.activities.ChildActivity;
import com.reading.start.presentation.ui.activities.MainActivity;
import com.reading.start.presentation.ui.activities.SignInActivity;
import com.reading.start.presentation.ui.dialogs.DialogLogoutConfirmation;

/**
 * Base fragment that process actions and events with custom action bar.
 */
public abstract class BaseActionBarFragment extends BaseFragment implements ActionBarView {
    private static final String TAG = BaseActionBarFragment.class.getSimpleName();

    private ActionBarViewHolder mViewHolder;

    private ActionBarPresenter mPresenter;

    protected void initActionBar(View view) {
        //init presenter
        mViewHolder = new ActionBarViewHolder(view.findViewById(R.id.home_button),
                view.findViewById(R.id.back_button),
                view.findViewById(R.id.navigation_button),
                view.findViewById(R.id.text_actionbar), view);

        mPresenter = new ActionBarPresenter();
        mPresenter.init(this, mViewHolder);
    }

    /**
     * Process logout current social worker
     */
    protected void processLogout() {
        DialogLogoutConfirmation dialog = DialogLogoutConfirmation.getInstance(getString(R.string.logout_dialog_title),
                getText(R.string.logout_dialog_message).toString(), new DialogLogoutConfirmation.DialogLogoutConfirmationListener() {
                    @Override
                    public void onCancel() {
                        // no need any action
                    }

                    @Override
                    public void onLogOut() {
                        AppCore.getInstance().getPreferences().setSyncTime(0);
                        AppCore.getInstance().getPreferences().setLoginToken(null);
                        AppCore.getInstance().getPreferences().setLoginWorker(null);
                        AppCore.getInstance().getPreferences().setLoginWorkerId(-1);
                        AppCore.getInstance().getPreferences().setServerToken(null);

                        if (getMainActivity() != null) {
                            Intent intent = new Intent(getMainActivity(), SignInActivity.class);
                            startActivity(intent);
                            getMainActivity().finish();
                        }
                    }
                });

        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), TAG);
    }

    @Override
    public void onBackPressed() {
        getActivity().onBackPressed();
    }

    @Override
    public void onHomePressed() {
        if (getActivity() instanceof MainActivity) {
            getMainActivity().openMySurveysFragment(false);
        } else if (getActivity() instanceof ChildActivity) {
            ChildActivity activity = (ChildActivity) getActivity();
            activity.openMySurveysFragment();
        }
    }

    @Override
    public void onNavigationMenuPressed() {
        if (getActivity() instanceof MainActivity) {
            getMainActivity().openMenuFragment();
        }
    }
}
