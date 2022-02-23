package com.reading.start.presentation.ui.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.databinding.FragmentSurveyBinding;
import com.reading.start.domain.entity.Children;
import com.reading.start.domain.entity.CurrentScreen;
import com.reading.start.presentation.mvp.holders.SurveyViewHolder;
import com.reading.start.presentation.mvp.presenters.SurveyPresenter;
import com.reading.start.presentation.mvp.views.SurveyView;
import com.reading.start.presentation.ui.UiConstants;
import com.reading.start.presentation.ui.activities.MainActivity;
import com.reading.start.presentation.ui.dialogs.DialogOkCancelGeneral;
import com.reading.start.presentation.ui.fragments.base.BaseActionBarFragment;
import com.reading.start.tests.ITestModule;
import com.reading.start.utils.Utility;

/**
 * Screen that displaying info about child's survey.
 */
public class SurveyFragment extends BaseActionBarFragment implements SurveyView {

    public static final String TAG = SurveyFragment.class.getSimpleName();

    public static final String TEST_PARENT_TYPE = "com.reading.start.tests.test_parent";

    private FragmentSurveyBinding mBinding;

    private SurveyViewHolder mViewHolder;

    private SurveyPresenter mPresenter = null;

    private RecyclerView mRecyclerView;

    private LinearLayoutManager mLayoutManager;

    private int mChildId = -1;

    private int mSurveyId = -1;

    private int mSurveyNumber = 0;

    private Children mChildren = null;

    public SurveyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_survey, container, false);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bg_small);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mBinding.root.setBackground(bitmapDrawable);

        if (getArguments() != null) {
            if (getArguments().containsKey(UiConstants.CHILD_ID)) {
                mChildId = getArguments().getInt(UiConstants.CHILD_ID);
            }

            if (getArguments().containsKey(UiConstants.SURVEY_ID)) {
                mSurveyId = getArguments().getInt(UiConstants.SURVEY_ID);
            }

            if (getArguments().containsKey(UiConstants.SURVEY_NUMBER)) {
                mSurveyNumber = getArguments().getInt(UiConstants.SURVEY_NUMBER);
            }
        }

        mRecyclerView = mBinding.recyclerView;
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(inflater.getContext(), GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //init presenter
        mViewHolder = new SurveyViewHolder(mBinding.actionBar, mBinding.recyclerView,
                mBinding.cardViewChildInformation.findViewById(R.id.photo),
                mBinding.cardViewChildInformation.findViewById(R.id.name),
                mBinding.cardViewChildInformation.findViewById(R.id.child_info),
                mBinding.actionBar.findViewById(R.id.upload_button), mBinding.actionBar.findViewById(R.id.delete_button));

        mPresenter = new SurveyPresenter(getActivity(), mChildId, mSurveyId, mSurveyNumber);
        mPresenter.init(this, mViewHolder);
        mPresenter.onCreate(inflater, container, savedInstanceState);

        initActionBar(mBinding.actionBar);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.next_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.navigation_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.upload_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);

        updatePhotoViewSize();
        getPreferences().setCurrentScreen(CurrentScreen.SurveyInfo);
        getPreferences().setCurrentChild(mChildId);
        getPreferences().setCurrentSurvey(mSurveyId);
        return mBinding.getRoot();
    }

    @Override
    public void onBackPressed() {
        getPreferences().setCurrentScreen(CurrentScreen.ChildInfo);
        getPreferences().setCurrentChild(mChildId);
        getPreferences().setCurrentSurvey(-1);
        super.onBackPressed();
    }

    /**
     * Raises when have some error with fetching data
     */
    @Override
    public void onError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Raises when press delete survey button
     */
    @Override
    public void onDeleteSurvey() {
        DialogOkCancelGeneral dialog = DialogOkCancelGeneral.getInstance(getResources().getString(R.string.survey_dialog_delete_title),
                getResources().getText(R.string.survey_dialog_delete_message).toString(), new DialogOkCancelGeneral.DialogListener() {
                    @Override
                    public void onOK() {
                        if (mPresenter != null) {
                            mPresenter.deleteSurvey();
                        }
                    }

                    @Override
                    public void onCancel() {
                    }
                });

        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), TAG);
    }

    /**
     * Raises when survey deleted
     */
    @Override
    public void onDeletedSurvey() {
        if (getMainActivity() != null) {
            getMainActivity().onBackPressed();
        }
    }

    /**
     * Raises when start new survey.\
     */
    @Override
    public void onStartSurvey(ITestModule module) {
        final MainActivity activity = getMainActivity();

        if (activity != null) {
            activity.setCurrentTestModule(module);
        }

        if (module != null) {
            Bundle data = new Bundle();

            if (module.getSurveyType().equals(TEST_PARENT_TYPE) && mChildren != null) {
                String parentName1 = mChildren.getParentFirst().getName();
                String parentName2 = null;

                if (mChildren.getParentSecond() != null && mChildren.getParentSecond().getName() != null
                        && !mChildren.getParentSecond().getName().isEmpty()) {
                    parentName2 = mChildren.getParentSecond().getName();
                }

                data.putString(com.reading.start.tests.Constants.SURVEY_PARENT_1, parentName1);

                if (parentName2 != null) {
                    data.putString(com.reading.start.tests.Constants.SURVEY_PARENT_2, parentName2);
                }
            }

            data.putInt(com.reading.start.tests.Constants.SURVEY_NUMBER, mSurveyNumber);
            module.startTest(getMainActivity(), mSurveyId, data);
        }
    }

    /**
     * Raises when open survey
     */
    @Override
    public void onOpenSurvey(ITestModule module, int attempt) {
        if (module != null) {
            module.showResult(getMainActivity(), mSurveyId, attempt);
        }
    }

    /**
     * Raises when child info update
     */
    @Override
    public void onChildInfoUpdated(Children value) {
        mChildren = value;
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
        updatePhotoViewSize();

        if (mPresenter != null) {
            mPresenter.onResume();
        }

        super.onResume();
    }

    /**
     * Updated size od view for profile photo
     */
    private void updatePhotoViewSize() {
        Size size = Utility.getDisplaySize(AppCore.getInstance());

        if (size != null) {
            int photoSize = size.getHeight() * Constants.PHOTO_SIZE_UI_PERCENT / 100;
            View photo = mBinding.cardViewChildInformation.findViewById(R.id.photo_holder);
            photo.setLayoutParams(new FrameLayout.LayoutParams(photoSize, photoSize));
            mBinding.getRoot().requestLayout();
        }
    }
}
