package com.reading.start.presentation.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.tests.ITestModule;
import com.reading.start.tests.ITestModuleResult;

import java.util.ArrayList;

/**
 * Adapter for displaying all tests.
 */
public class SurveyTestAdapter extends RecyclerView.Adapter<SurveyTestAdapter.ViewHolderSurveyTestItem> {

    public interface SurveyTestAdapterListener {
        void onAttempt(ITestModule module, int attempt);

        void onAddAttempt(ITestModule module);
    }

    private int mSurveyId = -1;

    private ArrayList<ITestModule> mSurveyInfoList;

    private SurveyTestAdapterListener mListener = null;

    public SurveyTestAdapter(int surveyId, ArrayList<ITestModule> items, SurveyTestAdapterListener listener) {
        mSurveyId = surveyId;
        mSurveyInfoList = items;
        mListener = listener;
    }

    @Override
    public ViewHolderSurveyTestItem onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_survey_attempt, parent, false);
        ViewHolderSurveyTestItem viewHolder = new SurveyTestAdapter.ViewHolderSurveyTestItem(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderSurveyTestItem holder, int position) {
        final ITestModule module = mSurveyInfoList.get(position);

        if (module != null) {
            holder.getTestName().setText(AppCore.getInstance().getString(module.getNameResource()));
            boolean attempt1Clickable = false;
            boolean attempt2Clickable = false;
            boolean attempt3Clickable = false;

            if (module.getTestResults(mSurveyId) != null) {
                if (module.hasExtendedResult(AppCore.getInstance(), mSurveyId, 0)) {
                    attempt1Clickable = true;
                    holder.mPlayButton1.setVisibility(View.VISIBLE);
                } else {
                    attempt1Clickable = false;
                    holder.mPlayButton1.setVisibility(View.GONE);
                }

                if (module.hasExtendedResult(AppCore.getInstance(), mSurveyId, 1)) {
                    attempt2Clickable = true;
                    holder.mPlayButton2.setVisibility(View.VISIBLE);
                } else {
                    attempt2Clickable = false;
                    holder.mPlayButton2.setVisibility(View.GONE);
                }

                if (module.hasExtendedResult(AppCore.getInstance(), mSurveyId, 2)) {
                    attempt3Clickable = true;
                    holder.mPlayButton3.setVisibility(View.VISIBLE);
                } else {
                    attempt3Clickable = false;
                    holder.mPlayButton3.setVisibility(View.GONE);
                }

                if (module.getTestResults(mSurveyId).size() == 0) {
                    holder.mAttempt1.setVisibility(View.GONE);
                    holder.mAttempt1Title.setVisibility(View.GONE);
                    holder.mAttempt2.setVisibility(View.GONE);
                    holder.mAttempt3.setVisibility(View.GONE);
                    holder.mSeparatorView1.setVisibility(View.GONE);
                    holder.mSeparatorView2.setVisibility(View.GONE);
                    holder.mSeparatorView3.setVisibility(View.GONE);
                    holder.mAttemptAdd.setVisibility(View.VISIBLE);
                } else if (module.getTestResults(mSurveyId).size() == 1) {
                    holder.mAttempt1.setVisibility(View.VISIBLE);
                    holder.mAttempt2.setVisibility(View.GONE);
                    holder.mAttempt3.setVisibility(View.GONE);
                    holder.mSeparatorView1.setVisibility(View.VISIBLE);
                    holder.mSeparatorView2.setVisibility(View.GONE);
                    holder.mSeparatorView3.setVisibility(View.GONE);

                    if (isNeedShowNewAttempt(module)) {
                        holder.mAttemptAdd.setVisibility(View.VISIBLE);
                        holder.mAttempt1Title.setVisibility(View.VISIBLE);
                    } else {
                        holder.mAttemptAdd.setVisibility(View.GONE);
                        holder.mAttempt1Title.setVisibility(View.GONE);
                    }

                    if (module.getTestResults(mSurveyId).get(0).isInterrupted()) {
                        holder.mInterrupted1.setVisibility(View.VISIBLE);
                    } else {
                        holder.mInterrupted1.setVisibility(View.GONE);
                    }

                    fillAttempt(holder.mAttempt1, module.getTestResults(mSurveyId).get(0));
                } else if (module.getTestResults(mSurveyId).size() == 2) {
                    holder.mAttempt1.setVisibility(View.VISIBLE);
                    holder.mAttempt1Title.setVisibility(View.VISIBLE);
                    holder.mAttempt2.setVisibility(View.VISIBLE);
                    holder.mAttempt3.setVisibility(View.GONE);
                    holder.mSeparatorView1.setVisibility(View.VISIBLE);
                    holder.mSeparatorView2.setVisibility(View.VISIBLE);
                    holder.mSeparatorView3.setVisibility(View.GONE);

                    if (isNeedShowNewAttempt(module)) {
                        holder.mAttemptAdd.setVisibility(View.VISIBLE);
                    } else {
                        holder.mAttemptAdd.setVisibility(View.GONE);
                    }

                    if (module.getTestResults(mSurveyId).get(0).isInterrupted()) {
                        holder.mInterrupted1.setVisibility(View.VISIBLE);
                    } else {
                        holder.mInterrupted1.setVisibility(View.GONE);
                    }

                    if (module.getTestResults(mSurveyId).get(1).isInterrupted()) {
                        holder.mInterrupted2.setVisibility(View.VISIBLE);
                    } else {
                        holder.mInterrupted2.setVisibility(View.GONE);
                    }

                    fillAttempt(holder.mAttempt1, module.getTestResults(mSurveyId).get(0));
                    fillAttempt(holder.mAttempt2, module.getTestResults(mSurveyId).get(1));
                } else {
                    holder.mAttempt1.setVisibility(View.VISIBLE);
                    holder.mAttempt1Title.setVisibility(View.VISIBLE);
                    holder.mAttempt2.setVisibility(View.VISIBLE);
                    holder.mAttempt3.setVisibility(View.VISIBLE);
                    holder.mSeparatorView1.setVisibility(View.VISIBLE);
                    holder.mSeparatorView2.setVisibility(View.VISIBLE);
                    holder.mSeparatorView3.setVisibility(View.VISIBLE);
                    holder.mAttemptAdd.setVisibility(View.GONE);

                    if (module.getTestResults(mSurveyId).get(0).isInterrupted()) {
                        holder.mInterrupted1.setVisibility(View.VISIBLE);
                    } else {
                        holder.mInterrupted1.setVisibility(View.GONE);
                    }

                    if (module.getTestResults(mSurveyId).get(1).isInterrupted()) {
                        holder.mInterrupted2.setVisibility(View.VISIBLE);
                    } else {
                        holder.mInterrupted2.setVisibility(View.GONE);
                    }

                    if (module.getTestResults(mSurveyId).get(2).isInterrupted()) {
                        holder.mInterrupted3.setVisibility(View.VISIBLE);
                    } else {
                        holder.mInterrupted3.setVisibility(View.GONE);
                    }

                    fillAttempt(holder.mAttempt1, module.getTestResults(mSurveyId).get(0));
                    fillAttempt(holder.mAttempt2, module.getTestResults(mSurveyId).get(1));
                    fillAttempt(holder.mAttempt3, module.getTestResults(mSurveyId).get(2));
                }
            } else {
                holder.mAttempt1.setVisibility(View.GONE);
                holder.mAttempt2.setVisibility(View.GONE);
                holder.mAttempt3.setVisibility(View.GONE);
                holder.mSeparatorView1.setVisibility(View.GONE);
                holder.mSeparatorView2.setVisibility(View.GONE);
                holder.mSeparatorView3.setVisibility(View.GONE);
                holder.mAttemptAdd.setVisibility(View.VISIBLE);
            }

            if (attempt1Clickable) {
                holder.mAttempt1.setOnClickListener(v -> {
                    if (mListener != null) {
                        mListener.onAttempt(module, 0);
                    }
                });
            } else {
                holder.mAttempt1.setOnClickListener(null);
            }

            if (attempt2Clickable) {
                holder.mAttempt2.setOnClickListener(v -> {
                    if (mListener != null) {
                        mListener.onAttempt(module, 1);
                    }
                });
            } else {
                holder.mAttempt2.setOnClickListener(null);
            }

            if (attempt3Clickable) {
                holder.mAttempt3.setOnClickListener(v -> {
                    if (mListener != null) {
                        mListener.onAttempt(module, 2);
                    }
                });
            } else {
                holder.mAttempt3.setOnClickListener(null);
            }

            holder.mAttemptAdd.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onAddAttempt(module);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mSurveyInfoList.size();
    }

    public ArrayList<ITestModule> getItems() {
        return mSurveyInfoList;
    }

    private void fillAttempt(View view, ITestModuleResult info) {
        if (view != null && info != null) {
            TextView attemptTime = view.findViewById(R.id.time_textView);
            String valueTime = formatElapsedTime(info.getTestTime());
            attemptTime.setText(String.format(AppCore.getInstance().getString(R.string.survey_test_time), valueTime));

            TextView attemptInfo = view.findViewById(R.id.result_textView);

            if (info.getInfo() != null && !info.getInfo().isEmpty()) {
                attemptInfo.setText(info.getInfo());
                attemptInfo.setVisibility(View.VISIBLE);
            } else {
                attemptInfo.setVisibility(View.GONE);
            }
        }
    }

    public static String formatElapsedTime(long milliseconds) {
        long millis = milliseconds % 1000;
        long x = milliseconds / 1000;
        long seconds = x % 60;
        x /= 60;
        long minutes = x % 60;

        return String.format(Constants.ATTEMPT_TIME_FORMAT, minutes, seconds, millis);
    }

    private boolean isNeedShowNewAttempt(ITestModule module) {
       return true;
    }

    public class ViewHolderSurveyTestItem extends RecyclerView.ViewHolder {

        private TextView mTestName;

        private View mPlayButton1;

        private View mPlayButton2;

        private View mPlayButton3;

        private View mInterrupted1;

        private View mInterrupted2;

        private View mInterrupted3;

        private TextView mResult1;

        private TextView mResult2;

        private TextView mResult3;

        private View mView;

        private View mAttempt1;

        private TextView mAttempt1Title;

        private View mAttempt2;

        private TextView mAttempt2Title;

        private View mAttempt3;

        private TextView mAttempt3Title;

        private View mSeparatorView1;

        private View mSeparatorView2;

        private View mSeparatorView3;

        private View mAttemptAdd;

        public ViewHolderSurveyTestItem(View itemView) {
            super(itemView);

            mView = itemView;
            mTestName = itemView.findViewById(R.id.title_test);

            mAttempt1 = itemView.findViewById(R.id.attempt_1);
            mAttempt1Title = mAttempt1.findViewById(R.id.attempt_textView);
            mPlayButton1 = mAttempt1.findViewById(R.id.play_button_holder);
            mResult1 = mAttempt1.findViewById(R.id.result_textView);
            mInterrupted1 = mAttempt1.findViewById(R.id.interrupted_textView);

            mAttempt2 = itemView.findViewById(R.id.attempt_2);
            mAttempt2Title = mAttempt2.findViewById(R.id.attempt_textView);
            mPlayButton2 = mAttempt2.findViewById(R.id.play_button_holder);
            mResult2 = mAttempt2.findViewById(R.id.result_textView);
            mInterrupted2 = mAttempt2.findViewById(R.id.interrupted_textView);

            mAttempt3 = itemView.findViewById(R.id.attempt_3);
            mAttempt3Title = mAttempt3.findViewById(R.id.attempt_textView);
            mPlayButton3 = mAttempt3.findViewById(R.id.play_button_holder);
            mResult3 = mAttempt3.findViewById(R.id.result_textView);
            mInterrupted3 = mAttempt3.findViewById(R.id.interrupted_textView);

            mSeparatorView1 = itemView.findViewById(R.id.view_1);
            mSeparatorView2 = itemView.findViewById(R.id.view_2);
            mSeparatorView3 = itemView.findViewById(R.id.view_3);
            mAttemptAdd = itemView.findViewById(R.id.add_attempt);

            mAttempt1Title.setText(R.string.survey_test_attempt_1);
            mAttempt2Title.setText(R.string.survey_test_attempt_2);
            mAttempt3Title.setText(R.string.survey_test_attempt_3);
        }

        public TextView getTestName() {
            return mTestName;
        }

        public View getView() {
            return mView;
        }
    }
}
