package com.reading.start.presentation.ui.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager;

import com.reading.start.AppCore;
import com.reading.start.AppPermissions;
import com.reading.start.R;
import com.reading.start.domain.entity.Survey;
import com.reading.start.general.TLog;
import com.reading.start.presentation.mvp.holders.MainActivityViewHolder;
import com.reading.start.presentation.mvp.presenters.MainActivityPresenter;
import com.reading.start.presentation.mvp.views.MainActivityView;
import com.reading.start.presentation.ui.UiConstants;
import com.reading.start.presentation.ui.fragments.ChildInformationFragment;
import com.reading.start.presentation.ui.fragments.MySurveysFragment;
import com.reading.start.presentation.ui.fragments.SurveyFragment;
import com.reading.start.presentation.ui.fragments.general.MenuFragment;
import com.reading.start.presentation.ui.interfaces.IFragmentBack;
import com.reading.start.tests.Constants;
import com.reading.start.tests.ITestModule;
import com.reading.start.tests.TestsProvider;
import com.reading.start.utils.DatabaseLogHelper;

import java.io.File;

/**
 * Main screen. Intended for displaying header, contains logic for switching between screens.
 */
public class MainActivity extends BasePermissionActivity implements MainActivityView {
    private static final String TAG = MainActivity.class.getSimpleName();

    private MainActivityViewHolder mViewHolder;

    private MainActivityPresenter mPresenter = null;

    private ITestModule mCurrentTestModule = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        openMySurveysFragment(true);

        mPresenter = new MainActivityPresenter();
        mPresenter.init(this, mViewHolder);
        mPresenter.onCreate(null, null, savedInstanceState);
    }

    public ITestModule getCurrentTestModule() {
        return mCurrentTestModule;
    }

    public void setCurrentTestModule(ITestModule currentTestModule) {
        mCurrentTestModule = currentTestModule;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UiConstants.CHILD_ACTIVITY_START_ADD || requestCode == UiConstants.CHILD_ACTIVITY_START_EDIT) {
            if (data != null && data.getExtras() != null && data.getExtras().containsKey(UiConstants.CHILD_ID)) {
                int childId = data.getExtras().getInt(UiConstants.CHILD_ID);

                if (resultCode == UiConstants.CHILD_ACTIVITY_RESULT_OPEN_START_SURVEY) {
                    openChildInformationFragment(false, childId);

                    if (mPresenter != null) {
                        mPresenter.addSurvey(childId);
                    }
                } else if (resultCode == UiConstants.CHILD_ACTIVITY_RESULT_OPEN_MY_SURVEY) {
                    openMySurveysFragment(false);
                }
            }
        }

        if (requestCode == Constants.ACTIVITY_SURVEY) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                int surveyId = bundle.getInt(Constants.SURVEY_ID);

                if (com.reading.start.Constants.TEST_MAKE_DUMP && mCurrentTestModule != null) {
                    String root = Environment.getExternalStorageDirectory().toString();
                    File folder = new File(root + "/" + com.reading.start.Constants.TEST_DUMP_FOLDER);

                    if (!folder.exists()) {
                        try {
                            folder.mkdir();
                        } catch (Exception e) {
                            TLog.e(TAG, e);
                        }
                    }

                    // this code make dump of test data
                    if (folder.exists()) {
                        mCurrentTestModule.makeDump(this, surveyId, 0, folder.getPath());
                    }
                }

                if (mPresenter != null) {
                    mPresenter.checkIsSurveyCompleted(surveyId);
                    mPresenter.checkIsSurveyUploaded(surveyId);
                }

                if (resultCode == Constants.ACTIVITY_RESULT_HOME) {
                    openMySurveysFragment(false);
                } else if (resultCode == Constants.ACTIVITY_RESULT_TRY_AGAIN) {
                    if (bundle != null) {
                        if (mCurrentTestModule != null) {
                            mCurrentTestModule.startTest(this, surveyId);
                        }
                    }
                } else if (resultCode == Constants.ACTIVITY_RESULT_START_NEXT_SURVEY) {
                    if (bundle != null) {
                        ITestModule nextTest = TestsProvider.getInstance(AppCore.getInstance()).getNextTest(mCurrentTestModule);

                        if (nextTest != null) {
                            mCurrentTestModule = nextTest;
                            nextTest.startTest(this, surveyId);
                        }
                    }
                }
            }
        }
    }

    /**
     * Open my surveys screen.
     */
    public void openMySurveysFragment(boolean restore) {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof MySurveysFragment) {
            ft.attach(frag);
        } else {
            frag = new MySurveysFragment();
            ft.replace(R.id.content_frame, frag);
        }

        if (frag.getArguments() == null) {
            frag.setArguments(new Bundle());
        }

        if (restore) {
            frag.getArguments().putBoolean(UiConstants.RESTORE_ID, true);
        }

        ft.commit();
    }

    /**
     * Open child information
     */
    public void openChildInformationFragment(boolean restore, int childId) {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof ChildInformationFragment) {
            ft.attach(frag);
        } else {
            frag = new ChildInformationFragment();
            ft.replace(R.id.content_frame, frag).addToBackStack(null);
        }

        if (frag.getArguments() == null) {
            frag.setArguments(new Bundle());
        }

        frag.getArguments().putInt(UiConstants.CHILD_ID, childId);

        if (restore) {
            frag.getArguments().putBoolean(UiConstants.RESTORE_ID, true);
        }

        ft.commit();
    }

    /**
     * Open survey
     */
    public void openSurveyFragment(boolean restore, int childId, int surveyId, int surveyNumber) {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof SurveyFragment) {
            ft.attach(frag);
        } else {
            frag = new SurveyFragment();
            ft.replace(R.id.content_frame, frag).addToBackStack(null);
        }

        if (frag.getArguments() == null) {
            frag.setArguments(new Bundle());
        }

        frag.getArguments().putInt(UiConstants.CHILD_ID, childId);
        frag.getArguments().putInt(UiConstants.SURVEY_ID, surveyId);
        frag.getArguments().putInt(UiConstants.SURVEY_NUMBER, surveyNumber);

        if (restore) {
            frag.getArguments().putInt(UiConstants.RESTORE_ID, surveyNumber);
        }

        ft.commit();
    }

    /**
     * Open user guide screen.
     */
    public void openMenuFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (frag != null && frag instanceof MenuFragment) {
            ft.attach(frag);
        } else {
            frag = new MenuFragment();
            ft.replace(R.id.content_frame, frag).addToBackStack(null);
        }

        ft.commit();
    }

    @Override
    protected void onPause() {
        DatabaseLogHelper.makeDump();

        if (mPresenter != null) {
            mPresenter.onPause();
        }

        super.onPause();
        System.gc();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mPresenter != null) {
            mPresenter.onResume();
        }

        if (AppPermissions.checkPermissionGranted(this)) {

            if (getPreferences() != null) {
                if (!getPreferences().isFirstRun()) {
                    // need action for first run
                }
            }
        }
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
    protected void onAllowPermissionOk() {
    }

    @Override
    protected boolean onPermissionDenied() {
        return false;
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }

        super.onDestroy();
    }

    @Override
    public void onAddSurvey(int childId, Survey survey, int surveyNumber) {
        if (survey != null) {
            openSurveyFragment(false, childId, survey.getId(), surveyNumber);
        }
    }
}
