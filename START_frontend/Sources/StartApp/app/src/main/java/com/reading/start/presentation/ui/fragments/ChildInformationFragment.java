package com.reading.start.presentation.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.databinding.FragmentChildInformationBinding;
import com.reading.start.domain.entity.CurrentScreen;
import com.reading.start.domain.entity.Survey;
import com.reading.start.presentation.mvp.holders.ChildInformationViewHolder;
import com.reading.start.presentation.mvp.presenters.ChildInformationPresenter;
import com.reading.start.presentation.mvp.views.ChildInformationView;
import com.reading.start.presentation.ui.UiConstants;
import com.reading.start.presentation.ui.activities.ChildActivity;
import com.reading.start.presentation.ui.dialogs.DialogOkCancelGeneral;
import com.reading.start.presentation.ui.fragments.base.BaseActionBarFragment;
import com.reading.start.utils.Utility;

/**
 * Screen that displaying info about child and their surveys (progress, upload status and etc.)
 */
public class ChildInformationFragment extends BaseActionBarFragment implements ChildInformationView {
    private static final String TAG = ChildInformationFragment.class.getSimpleName();

    private FragmentChildInformationBinding mBinding;

    private RecyclerView mRecyclerView;

    private GridLayoutManager mLayoutManager;

    private ChildInformationViewHolder mViewHolder;

    private ChildInformationPresenter mPresenter = null;

    private int mChildId = -1;

    public ChildInformationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_child_information, container, false);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bg_small);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mBinding.root.setBackground(bitmapDrawable);

        if (getArguments() != null && getArguments().containsKey(UiConstants.CHILD_ID)) {
            mChildId = getArguments().getInt(UiConstants.CHILD_ID);
        }

        boolean restore = false;

        if (getArguments() != null && getArguments().containsKey(UiConstants.RESTORE_ID)) {
            restore = true;
        }

        mRecyclerView = mBinding.recyclerView;
        RecyclerView.ItemAnimator animator = mRecyclerView.getItemAnimator();

        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        mLayoutManager = new GridLayoutManager(inflater.getContext(),
                (Utility.isNarrow(AppCore.getInstance()) ? Constants.CHILD_INFO_ITEMS_COLUMN_NARROW : Constants.CHILD_INFO_ITEMS_COLUMN_DEFAULT),
                GridLayoutManager.VERTICAL, false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //init presenter
        mViewHolder = new ChildInformationViewHolder(mBinding.actionBar, mBinding.recyclerView,
                mBinding.cardViewChildInformation.findViewById(R.id.photo),
                mBinding.cardViewChildInformation.findViewById(R.id.name),
                mBinding.cardViewChildInformation.findViewById(R.id.child_info),
                mBinding.cardViewChildInformation.findViewById(R.id.edit_button));

        mPresenter = new ChildInformationPresenter(getActivity(), mChildId, restore);
        mPresenter.init(this, mViewHolder);
        mPresenter.onCreate(inflater, container, savedInstanceState);

        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);
        title.setText(R.string.action_bar_child_information);

        initActionBar(mBinding.actionBar);
        updatePhotoViewSize();

        return mBinding.getRoot();
    }

    @Override
    public void onBackPressed() {
        getPreferences().setCurrentScreen(CurrentScreen.ChildList);
        getPreferences().setCurrentChild(-1);
        getPreferences().setCurrentSurvey(-1);
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();

        mBinding.cardViewChildInformation.findViewById(R.id.edit_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.navigation_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.next_button).setVisibility(View.GONE);
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
     * Raises when add new survey.
     */
    @Override
    public void onSurveyAdd(Survey survey, int surveyNumber) {
        if (mPresenter != null) {
            getMainActivity().openSurveyFragment(false, mChildId, survey.getId(), surveyNumber);
        }
    }

    /**
     * Raises when open existed survey
     */
    @Override
    public void onSurveyOpen(boolean restore, Survey survey, int surveyNumber) {
        getMainActivity().openSurveyFragment(restore, mChildId, survey.getId(), surveyNumber);
    }

    /**
     * Raises when need to download survey from server
     */
    @Override
    public void onSurveyDownload(Survey survey, int surveyNumber) {
        DialogOkCancelGeneral dialog = DialogOkCancelGeneral.getInstance(getResources().getString(R.string.survey_dialog_download_title),
                getResources().getText(R.string.survey_dialog_download_message).toString(), new DialogOkCancelGeneral.DialogListener() {
                    @Override
                    public void onOK() {
                        if (mPresenter != null) {
                            mPresenter.downloadSurvey(survey.getId());
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
     * Raises when press edit child
     */
    @Override
    public void onEditChild() {
        final Activity activity = getActivity();

        if (activity != null) {
            Intent intent = new Intent(activity, ChildActivity.class);
            intent.putExtra(UiConstants.CHILD_ID, mChildId);
            startActivityForResult(intent, UiConstants.CHILD_ACTIVITY_START_EDIT);
        }
    }

    @Override
    public void onHomePressed() {
        getActivity().onBackPressed();
    }

    @Override
    public void onError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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
