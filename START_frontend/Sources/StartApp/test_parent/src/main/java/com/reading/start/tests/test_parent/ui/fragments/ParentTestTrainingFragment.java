package com.reading.start.tests.test_parent.ui.fragments;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reading.start.tests.test_parent.R;
import com.reading.start.tests.test_parent.data.ParentTestTrainingModel;
import com.reading.start.tests.test_parent.databinding.TestParentFragmentTrainingTestBinding;
import com.reading.start.tests.test_parent.domain.entity.IParentTestItem;
import com.reading.start.tests.test_parent.domain.entity.TestData;
import com.reading.start.tests.test_parent.ui.adapters.ParentTestAdapter;
import com.reading.start.tests.test_parent.utils.Utility;

import java.util.ArrayList;

public class ParentTestTrainingFragment extends BaseFragment {

    private TestParentFragmentTrainingTestBinding mBinding;

    private RecyclerView mRecyclerView;

    private GridLayoutManager mLayoutManager;

    private ParentTestAdapter mAdapter;

    private TestData mTestData;

    public ParentTestTrainingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_parent_fragment_training_test, container, false);

        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);
        title.setText(R.string.test_parent_actionbar_parent_assessment);

        mRecyclerView = mBinding.recyclerView;
        mLayoutManager = new GridLayoutManager(inflater.getContext(), 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mAdapter.getItemViewType(position)) {
                    case ParentTestAdapter.TEXT:
                        return 1;
                    case ParentTestAdapter.VIDEO:
                        return mLayoutManager.getSpanCount();
                    default:
                        return -1;
                }
            }
        });

        mRecyclerView.setLayoutManager(mLayoutManager);

        ParentTestTrainingModel model = new ParentTestTrainingModel();
        Size screenSize = Utility.getDisplaySize(getActivity());
        float dpi[] = Utility.getXYDpi(getActivity());
        mTestData = new TestData(screenSize.getWidth(), screenSize.getHeight(), dpi[0], dpi[1]);
        //model.fillTestDate(mTestData);
        ArrayList<IParentTestItem> items = new ArrayList<>();
        items.addAll(mTestData.getItems());
        items.addAll(mTestData.getVideoItems());

        mAdapter = new ParentTestAdapter(items, getMainActivity().getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        mBinding.actionBar.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.GONE);

        mBinding.actionBar.findViewById(R.id.back_button).setOnClickListener(v -> {
            final Activity activity = getActivity();

            if (activity != null) {
                activity.onBackPressed();
            }
        });

        mBinding.buttonStart.setOnClickListener(v -> getMainActivity().openTestFragment(null, null));

//        ArrayList<String> parentArrayList = new ArrayList<>();
//        parentArrayList.add(getString(R.string.test_parent_parent_1));
//        parentArrayList.add(getString(R.string.test_parent_parent_2));
//        final ArrayAdapter<String> parentAdapter = new ArrayAdapter<>(getMainActivity().getApplicationContext(), R.layout.test_parent_item_spinner, parentArrayList);
//        mBinding.spinnerParrent.setAdapter(parentAdapter);

        return mBinding.getRoot();
    }
}
