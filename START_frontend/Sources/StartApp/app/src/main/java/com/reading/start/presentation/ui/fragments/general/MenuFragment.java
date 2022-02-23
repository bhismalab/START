package com.reading.start.presentation.ui.fragments.general;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.databinding.FragmentMenuBinding;
import com.reading.start.presentation.ui.dialogs.DialogOkCancelGeneral;
import com.reading.start.presentation.ui.fragments.base.BaseActionBarFragment;
import com.reading.start.utils.Utility;

/**
 * Represent screen that display About info and instruction.
 */
public class MenuFragment extends BaseActionBarFragment {
    private static final String TAG = MenuFragment.class.getSimpleName();

    private FragmentMenuBinding mBinding;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu, container, false);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bg_small);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mBinding.root.setBackground(bitmapDrawable);

        initActionBar(mBinding.actionBar);

        RxView.clicks(mBinding.actionBar.findViewById(R.id.back_button))
                .subscribe(aVoid -> onBackPressed());
        RxView.clicks(mBinding.actionBar.findViewById(R.id.home_button))
                .subscribe(aVoid -> onHomePressed());
        RxView.clicks(mBinding.actionBar.findViewById(R.id.navigation_button))
                .subscribe(aVoid -> onNavigationMenuPressed());
        RxView.clicks(mBinding.actionBar.findViewById(R.id.logout_button))
                .subscribe(aVoid -> processLogout());

        mBinding.menuSelector.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.menu_user_guide) {
                mBinding.contentAboutHolder.setVisibility(View.GONE);
                mBinding.contentUserGuideHolder.setVisibility(View.VISIBLE);

                TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);
                title.setText(R.string.menu_user_guide);
            } else {
                mBinding.contentAboutHolder.setVisibility(View.VISIBLE);
                mBinding.contentUserGuideHolder.setVisibility(View.GONE);

                TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);
                title.setText(R.string.menu_about_project);
            }
        });

        if (getPreferences().getLanguage() == Constants.LANGUAGE_VALUE_HI) {
            ((TextView) mBinding.actionBar.findViewById(R.id.language_button)).setText(R.string.menu_navigation_english);
        } else {
            ((TextView) mBinding.actionBar.findViewById(R.id.language_button)).setText(R.string.menu_navigation_hindi);
        }

        RxView.clicks(mBinding.actionBar.findViewById(R.id.language_button))
                .subscribe(aVoid -> {
                    DialogOkCancelGeneral dialog = DialogOkCancelGeneral.getInstance(getResources().getString(R.string.language_dialog_title),
                            getResources().getText(R.string.language_dialog_message).toString(), new DialogOkCancelGeneral.DialogListener() {
                                @Override
                                public void onOK() {
                                    if (getPreferences().getLanguage() == Constants.LANGUAGE_VALUE_HI) {
                                        getPreferences().setLanguage(Constants.LANGUAGE_VALUE_EN);
                                    } else {
                                        getPreferences().setLanguage(Constants.LANGUAGE_VALUE_HI);
                                    }

                                    Utility.doRestart(getActivity());
                                }

                                @Override
                                public void onCancel() {
                                }
                            });

                    dialog.setCancelable(false);
                    dialog.show(getFragmentManager(), TAG);
                });

        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);
        title.setText(R.string.menu_about_project);
        mBinding.actionBar.findViewById(R.id.logout_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.language_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.next_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.navigation_button).setVisibility(View.GONE);

        return mBinding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        System.gc();
    }
}
