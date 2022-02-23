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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reading.start.R;
import com.reading.start.databinding.FragmentMySurveysBinding;
import com.reading.start.domain.entity.Children;
import com.reading.start.presentation.mvp.holders.MySurveysViewHolder;
import com.reading.start.presentation.mvp.presenters.MySurveysPresenter;
import com.reading.start.presentation.mvp.views.MySurveysView;
import com.reading.start.presentation.ui.UiConstants;
import com.reading.start.presentation.ui.activities.ChildActivity;
import com.reading.start.presentation.ui.activities.MainActivity;
import com.reading.start.presentation.ui.fragments.base.BaseActionBarFragment;

/**
 * Screen that displaying all children that passing tests.
 */
public class MySurveysFragment extends BaseActionBarFragment implements MySurveysView {

    public static final String TAG = MySurveysFragment.class.getSimpleName();

    private MySurveysPresenter mPresenter = null;

    private FragmentMySurveysBinding mBinding;

    private RecyclerView mRecyclerView;

    private GridLayoutManager mLayoutManager;

    private MySurveysViewHolder mViewHolder;

    public MySurveysFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_surveys, container, false);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bg_small);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mBinding.root.setBackground(bitmapDrawable);

        mRecyclerView = mBinding.recyclerView;
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(inflater.getContext(), 2, GridLayoutManager.HORIZONTAL, false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //init presenter
        mViewHolder = new MySurveysViewHolder(mBinding.actionBar,
                mBinding.actionBar.findViewById(R.id.navigation_button), mRecyclerView);

        boolean restore = false;

        if (getArguments() != null && getArguments().containsKey(UiConstants.RESTORE_ID)) {
            restore = true;
        }

        mPresenter = new MySurveysPresenter(restore);
        mPresenter.init(this, mViewHolder);
        mPresenter.onCreate(inflater, container, savedInstanceState);

        TextView titleActionBar = mBinding.actionBar.findViewById(R.id.text_actionbar);
        titleActionBar.setText(R.string.action_bar_my_surveys_title);

        initActionBar(mBinding.actionBar);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.back_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.next_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.navigation_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.upload_button_all).setVisibility(View.VISIBLE);
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
     * Raises when add new child
     */
    @Override
    public void onAddChild() {
        final Activity activity = getActivity();

        if (activity != null) {
            Intent intent = new Intent(activity, ChildActivity.class);
            activity.startActivityForResult(intent, UiConstants.CHILD_ACTIVITY_START_ADD);
        }
    }

    /**
     * Raises when open child.
     */
    @Override
    public void onChildInformationOpen(boolean restore, Children child) {
        final MainActivity activity = (MainActivity) getActivity();

        if (activity != null && child != null) {
            activity.openChildInformationFragment(restore, child.getId());
        }
    }

    @Override
    public void onError(String message) {
    }
}
