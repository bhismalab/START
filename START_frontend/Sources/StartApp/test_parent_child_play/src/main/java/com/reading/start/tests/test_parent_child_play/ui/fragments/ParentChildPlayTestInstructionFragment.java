package com.reading.start.tests.test_parent_child_play.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_parent_child_play.Constants;
import com.reading.start.tests.test_parent_child_play.R;
import com.reading.start.tests.test_parent_child_play.data.DataProvider;
import com.reading.start.tests.test_parent_child_play.databinding.TestParentChildPlayFragmentInstructionTestBinding;
import com.reading.start.tests.test_parent_child_play.domain.entity.ParentChildPlayTestContent;
import com.reading.start.tests.test_parent_child_play.ui.activities.MainActivity;

import java.util.Locale;

import io.realm.Realm;

public class ParentChildPlayTestInstructionFragment extends Fragment {
    private static final String TAG = ParentChildPlayTestInstructionFragment.class.getSimpleName();

    private TestParentChildPlayFragmentInstructionTestBinding mBinding;

    private Handler mHandler = new Handler();

    public ParentChildPlayTestInstructionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_parent_child_play_fragment_instruction_test, container, false);

        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);
        title.setText(R.string.test_parent_child_play_action_bar_instruction_colour_the_image);

        mBinding.actionBar.findViewById(R.id.back_button).setOnClickListener(v -> {
            final Activity activity = getActivity();

            if (activity != null) {
                activity.onBackPressed();
            }
        });

        mBinding.actionBar.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.navigation_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.next_button).setVisibility(View.GONE);

        mBinding.buttonStart.setEnabled(false);
        TestTimer autoStart = new TestTimer(() -> mBinding.buttonStart.setEnabled(true));
        mBinding.buttonStart.setOnClickListener(v -> {
            ((MainActivity) getActivity()).openTestFragment();
        });

        mHandler.postDelayed(autoStart, Constants.AUTO_START_TEST);
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Realm realm = null;

        try {
            realm = DataProvider.getInstance(getActivity()).getRealm();
            ParentChildPlayTestContent data = realm.where(ParentChildPlayTestContent.class).findFirst();

            if (data != null) {
                if (isHindiLanguage()) {
                    mBinding.testInstructionText.setText(data.getInstructionHindi());
                } else {
                    mBinding.testInstructionText.setText(data.getInstruction());
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private boolean isHindiLanguage() {
        boolean result = false;

        try {
            if (Locale.getDefault().getLanguage().equals("hi")) {
                result = true;
            }
        } catch (Exception e) {
            TestLog.e(TAG, "updateLanguage", e);
        }

        return result;
    }

    private static class TestTimer implements Runnable {
        public boolean needProcess = true;

        private Runnable mLocalRun = null;

        public TestTimer(Runnable runnable) {
            mLocalRun = runnable;
        }

        @Override
        public void run() {
            if (needProcess && mLocalRun != null) {
                mLocalRun.run();
            }
        }
    }
}
