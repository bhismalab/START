package com.reading.start.tests.test_parent.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_parent.R;
import com.reading.start.tests.test_parent.domain.entity.IParentTestItem;
import com.reading.start.tests.test_parent.domain.entity.TestDataItem;
import com.reading.start.tests.test_parent.domain.entity.TestDataVideoItem;
import com.sprylab.android.widget.TextureVideoView;

import java.util.ArrayList;
import java.util.Locale;

public class ParentTestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = ParentTestAdapter.class.getSimpleName();

    public static final int TEXT = 0;

    public static final int VIDEO = 1;

    private Context mContext;

    private ArrayList<IParentTestItem> mTestList;

    private TextureVideoView mCurrentVideoView = null;

    private View.OnTouchListener mLeftVideoListener = (v, event) -> {
        if (v.getTag() != null && v.getTag() instanceof ViewHolderTestVideoItem) {
            ViewHolderTestVideoItem holder = ((ViewHolderTestVideoItem) v.getTag());
            TestDataVideoItem videoItem = holder.getItem();
            TextureVideoView videoView = holder.getLeftVideoView();
            boolean needPlay = true;

            if (mCurrentVideoView == null) {
                needPlay = true;
            } else {
                if (mCurrentVideoView == videoView) {
                    needPlay = !mCurrentVideoView.isPlaying();
                } else {
                    mCurrentVideoView.stopPlayback();
                    mCurrentVideoView = null;
                    needPlay = true;
                }
            }

            if (needPlay) {
                if (videoView != null && videoItem != null && videoItem.getLeftVideoUrl() != null) {
                    videoView.setVideoPath(videoItem.getLeftVideoUrl());
                    videoView.setOnCompletionListener(mp -> {
                        videoView.stopPlayback();
                        mCurrentVideoView = null;
                    });

                    videoView.start();
                    mCurrentVideoView = videoView;
                    holder.getLeftVideoImageView().setVisibility(View.GONE);
                    holder.getRightVideoImageView().setVisibility(View.VISIBLE);
                    holder.getLeftVideoView().setVisibility(View.VISIBLE);
                }
            } else {
                videoView.stopPlayback();
                holder.getLeftVideoImageView().setVisibility(View.VISIBLE);
                holder.getLeftVideoView().setVisibility(View.GONE);
            }
        }

        return false;
    };

    private View.OnTouchListener mRightVideoListener = (v, event) -> {
        if (v.getTag() != null && v.getTag() instanceof ViewHolderTestVideoItem) {
            ViewHolderTestVideoItem holder = ((ViewHolderTestVideoItem) v.getTag());
            TestDataVideoItem videoItem = holder.getItem();
            TextureVideoView videoView = holder.getRightVideoView();
            boolean needPlay = true;

            if (mCurrentVideoView == null) {
                needPlay = true;
            } else {
                if (mCurrentVideoView == videoView) {
                    needPlay = !mCurrentVideoView.isPlaying();
                } else {
                    mCurrentVideoView.stopPlayback();
                    mCurrentVideoView = null;
                    needPlay = true;
                }
            }

            if (needPlay) {
                if (videoView != null && videoItem != null && videoItem.getRightVideoUrl() != null) {
                    videoView.setVideoPath(videoItem.getRightVideoUrl());
                    videoView.setOnCompletionListener(mp -> {
                        videoView.stopPlayback();
                        mCurrentVideoView = null;
                    });

                    videoView.start();
                }

                mCurrentVideoView = videoView;
                holder.getRightVideoImageView().setVisibility(View.GONE);
                holder.getLeftVideoImageView().setVisibility(View.VISIBLE);
                holder.getRightVideoView().setVisibility(View.VISIBLE);

            } else {
                videoView.stopPlayback();
                holder.getRightVideoImageView().setVisibility(View.VISIBLE);
                holder.getRightVideoView().setVisibility(View.GONE);
            }
        }

        return false;
    };

    private View.OnClickListener mCase1VideoClickListener = v -> {
        if (v.getTag() != null) {
            if (v.getTag() instanceof ViewHolderTestVideoItem) {
                ViewHolderTestVideoItem holder = (ViewHolderTestVideoItem) v.getTag();
                TestDataVideoItem item = holder.getItem();
                item.setSelectedChoice(1);
                holder.getQuestionState().setImageResource(R.drawable.question_done);
            }
        }
    };

    private View.OnClickListener mCase2VideoClickListener = v -> {
        if (v.getTag() != null) {
            if (v.getTag() instanceof ViewHolderTestVideoItem) {
                ViewHolderTestVideoItem holder = (ViewHolderTestVideoItem) v.getTag();
                TestDataVideoItem item = holder.getItem();
                item.setSelectedChoice(2);
                holder.getQuestionState().setImageResource(R.drawable.question_done);
            }
        }
    };


    private View.OnClickListener mCase1ClickListener = v -> {
        if (v.getTag() != null) {
            if (v.getTag() instanceof ViewHolderTestItem) {
                ViewHolderTestItem holder = (ViewHolderTestItem) v.getTag();
                TestDataItem item = holder.getItem();
                item.setSelectedChoice(1);
                holder.getQuestionState().setImageResource(R.drawable.question_done);
            }
        }
    };

    private View.OnClickListener mCase2ClickListener = v -> {
        if (v.getTag() != null) {
            if (v.getTag() instanceof ViewHolderTestItem) {
                ViewHolderTestItem holder = (ViewHolderTestItem) v.getTag();
                TestDataItem item = holder.getItem();
                item.setSelectedChoice(2);
                holder.getQuestionState().setImageResource(R.drawable.question_done);
            }
        }
    };

    private View.OnClickListener mCase3ClickListener = v -> {
        if (v.getTag() != null) {
            if (v.getTag() instanceof ViewHolderTestItem) {
                ViewHolderTestItem holder = (ViewHolderTestItem) v.getTag();
                TestDataItem item = holder.getItem();
                item.setSelectedChoice(3);
                holder.getQuestionState().setImageResource(R.drawable.question_done);
            }
        }
    };

    private View.OnClickListener mCase4ClickListener = v -> {
        if (v.getTag() != null) {
            if (v.getTag() instanceof ViewHolderTestItem) {
                ViewHolderTestItem holder = (ViewHolderTestItem) v.getTag();
                TestDataItem item = holder.getItem();

                if (item.getChoices().getChoicesEnglish().size() == 2) {
                    item.setSelectedChoice(2);
                } else if (item.getChoices().getChoicesEnglish().size() == 3) {
                    item.setSelectedChoice(3);
                } else {
                    item.setSelectedChoice(4);
                }

                holder.getQuestionState().setImageResource(R.drawable.question_done);
            }
        }
    };

    public ParentTestAdapter(ArrayList<IParentTestItem> items, Context context) {
        mTestList = items;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (mTestList.get(position) instanceof TestDataItem) {
            return TEXT;
        } else if (mTestList.get(position) instanceof TestDataVideoItem) {
            return VIDEO;
        } else {
            return -1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;

        switch (viewType) {
            case VIDEO: {
                view = inflater.inflate(R.layout.test_parent_card_video_question, parent, false);
                viewHolder = new ViewHolderTestVideoItem(view);
                ((ViewHolderTestVideoItem) viewHolder).getLeftVideoView().setOnTouchListener(mLeftVideoListener);
                ((ViewHolderTestVideoItem) viewHolder).getLeftVideoImageView().setOnTouchListener(mLeftVideoListener);
                ((ViewHolderTestVideoItem) viewHolder).getRightVideoView().setOnTouchListener(mRightVideoListener);
                ((ViewHolderTestVideoItem) viewHolder).getRightVideoImageView().setOnTouchListener(mRightVideoListener);
                ((ViewHolderTestVideoItem) viewHolder).getCase1().setOnClickListener(mCase1VideoClickListener);
                ((ViewHolderTestVideoItem) viewHolder).getCase2().setOnClickListener(mCase2VideoClickListener);
                break;
            }
            case TEXT:
            default: {
                view = inflater.inflate(R.layout.test_parent_card_question, parent, false);
                viewHolder = new ViewHolderTestItem(view);
                ((ViewHolderTestItem) viewHolder).getCase1().setOnClickListener(mCase1ClickListener);
                ((ViewHolderTestItem) viewHolder).getCase2().setOnClickListener(mCase2ClickListener);
                ((ViewHolderTestItem) viewHolder).getCase3().setOnClickListener(mCase3ClickListener);
                ((ViewHolderTestItem) viewHolder).getCase4().setOnClickListener(mCase4ClickListener);
                break;
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIDEO: {
                ViewHolderTestVideoItem viewHolderTestVideoItem = (ViewHolderTestVideoItem) holder;
                configureViewHolderTestVideoItem(viewHolderTestVideoItem, position);
                break;
            }
            case TEXT:
            default: {
                ViewHolderTestItem viewHolderTestItem = (ViewHolderTestItem) holder;
                configureViewHolderTestItem(viewHolderTestItem, position);
                break;
            }
        }
    }

    private void configureViewHolderTestItem(ViewHolderTestItem viewHolderTestItem, int position) {
        TestDataItem item = (TestDataItem) mTestList.get(position);

        if (item != null) {
            viewHolderTestItem.setItem(item);

            if (isHindiLanguage()) {
                viewHolderTestItem.getText().setText(item.getTextHindi());
            } else {
                viewHolderTestItem.getText().setText(item.getText());
            }

            viewHolderTestItem.getCase1().setTag(viewHolderTestItem);
            viewHolderTestItem.getCase2().setTag(viewHolderTestItem);
            viewHolderTestItem.getCase3().setTag(viewHolderTestItem);
            viewHolderTestItem.getCase4().setTag(viewHolderTestItem);

            boolean isHindi = isHindiLanguage();

            if (item.getChoices().getChoicesEnglish().size() == 4) {
                viewHolderTestItem.getCase1().setVisibility(View.VISIBLE);
                viewHolderTestItem.getCase2().setVisibility(View.VISIBLE);
                viewHolderTestItem.getCase3().setVisibility(View.VISIBLE);
                viewHolderTestItem.getCase4().setVisibility(View.VISIBLE);
                viewHolderTestItem.getCase1().setText(isHindi ? item.getChoices().getChoicesHindi().get(0) : item.getChoices().getChoicesEnglish().get(0));
                viewHolderTestItem.getCase2().setText(isHindi ? item.getChoices().getChoicesHindi().get(1) : item.getChoices().getChoicesEnglish().get(1));
                viewHolderTestItem.getCase3().setText(isHindi ? item.getChoices().getChoicesHindi().get(2) : item.getChoices().getChoicesEnglish().get(2));
                viewHolderTestItem.getCase4().setText(isHindi ? item.getChoices().getChoicesHindi().get(3) : item.getChoices().getChoicesEnglish().get(3));
            } else if (item.getChoices().getChoicesEnglish().size() == 3) {
                viewHolderTestItem.getCase1().setVisibility(View.VISIBLE);
                viewHolderTestItem.getCase2().setVisibility(View.VISIBLE);
                viewHolderTestItem.getCase3().setVisibility(View.GONE);
                viewHolderTestItem.getCase4().setVisibility(View.VISIBLE);
                viewHolderTestItem.getCase1().setText(isHindi ? item.getChoices().getChoicesHindi().get(0) : item.getChoices().getChoicesEnglish().get(0));
                viewHolderTestItem.getCase2().setText(isHindi ? item.getChoices().getChoicesHindi().get(1) : item.getChoices().getChoicesEnglish().get(1));
                viewHolderTestItem.getCase4().setText(isHindi ? item.getChoices().getChoicesHindi().get(2) : item.getChoices().getChoicesEnglish().get(2));
            } else {
                viewHolderTestItem.getCase1().setVisibility(View.VISIBLE);
                viewHolderTestItem.getCase2().setVisibility(View.GONE);
                viewHolderTestItem.getCase3().setVisibility(View.GONE);
                viewHolderTestItem.getCase4().setVisibility(View.VISIBLE);
                viewHolderTestItem.getCase1().setText(isHindi ? item.getChoices().getChoicesHindi().get(0) : item.getChoices().getChoicesEnglish().get(0));
                viewHolderTestItem.getCase4().setText(isHindi ? item.getChoices().getChoicesHindi().get(1) : item.getChoices().getChoicesEnglish().get(1));
            }

            if (item.getChoices().getChoicesEnglish().size() == 4) {
                switch (item.getSelectedChoice()) {
                    case 1: {
                        viewHolderTestItem.getButtons().check(viewHolderTestItem.getCase1().getId());
                        break;
                    }
                    case 2: {
                        viewHolderTestItem.getButtons().check(viewHolderTestItem.getCase2().getId());
                        break;
                    }
                    case 3: {
                        viewHolderTestItem.getButtons().check(viewHolderTestItem.getCase3().getId());
                        break;
                    }
                    case 4: {
                        viewHolderTestItem.getButtons().check(viewHolderTestItem.getCase4().getId());
                        break;
                    }
                    default: {
                        viewHolderTestItem.getButtons().clearCheck();
                        break;
                    }
                }
            } else if (item.getChoices().getChoicesEnglish().size() == 3) {
                switch (item.getSelectedChoice()) {
                    case 1: {
                        viewHolderTestItem.getButtons().check(viewHolderTestItem.getCase1().getId());
                        break;
                    }
                    case 2: {
                        viewHolderTestItem.getButtons().check(viewHolderTestItem.getCase2().getId());
                        break;
                    }
                    case 3: {
                        viewHolderTestItem.getButtons().check(viewHolderTestItem.getCase4().getId());
                        break;
                    }
                    default: {
                        viewHolderTestItem.getButtons().clearCheck();
                        break;
                    }
                }
            } else {
                switch (item.getSelectedChoice()) {
                    case 1: {
                        viewHolderTestItem.getButtons().check(viewHolderTestItem.getCase1().getId());
                        break;
                    }
                    case 2: {
                        viewHolderTestItem.getButtons().check(viewHolderTestItem.getCase4().getId());
                        break;
                    }
                    default: {
                        viewHolderTestItem.getButtons().clearCheck();
                        break;
                    }
                }
            }

            viewHolderTestItem.getQuestionNumber().setText(String.valueOf(position + 1));

            if (item.getSelectedChoice() == -1) {
                viewHolderTestItem.getQuestionState().setImageResource(R.drawable.question_open);
            } else {
                viewHolderTestItem.getQuestionState().setImageResource(R.drawable.question_done);
            }

            if (position == 0) {
                viewHolderTestItem.getProgress1().setVisibility(View.INVISIBLE);
                viewHolderTestItem.getProgress2().setVisibility(View.VISIBLE);
            } else if (position == getItemCount() - 1) {
                viewHolderTestItem.getProgress1().setVisibility(View.VISIBLE);
                viewHolderTestItem.getProgress2().setVisibility(View.INVISIBLE);
            } else {
                viewHolderTestItem.getProgress1().setVisibility(View.VISIBLE);
                viewHolderTestItem.getProgress2().setVisibility(View.VISIBLE);
            }
        }
    }

    private void configureViewHolderTestVideoItem(ViewHolderTestVideoItem viewHolderTestVideoItem, int position) {
        TestDataVideoItem item = (TestDataVideoItem) mTestList.get(position);

        if (item != null) {
            viewHolderTestVideoItem.setItem(item);

            if (isHindiLanguage()) {
                viewHolderTestVideoItem.getText().setText(item.getTextHindi());
            } else {
                viewHolderTestVideoItem.getText().setText(item.getText());
            }

            viewHolderTestVideoItem.getLeftVideoView().setTag(viewHolderTestVideoItem);
            viewHolderTestVideoItem.getLeftVideoImageView().setTag(viewHolderTestVideoItem);

            if (viewHolderTestVideoItem.getLeftVideoView().isPlaying()) {
                viewHolderTestVideoItem.getLeftVideoView().setVisibility(View.VISIBLE);
                viewHolderTestVideoItem.getLeftVideoImageView().setVisibility(View.GONE);
            } else {
                viewHolderTestVideoItem.getLeftVideoView().setVisibility(View.GONE);
                viewHolderTestVideoItem.getLeftVideoImageView().setVisibility(View.VISIBLE);

                try {
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(item.getLeftVideoUrl(), MediaStore.Images.Thumbnails.MINI_KIND);
                    viewHolderTestVideoItem.getLeftVideoImageView().setImageBitmap(thumb);
                } catch (Exception e) {
                    TestLog.e(TAG, e);
                }
            }

            viewHolderTestVideoItem.getRightVideoView().setTag(viewHolderTestVideoItem);
            viewHolderTestVideoItem.getRightVideoImageView().setTag(viewHolderTestVideoItem);

            if (viewHolderTestVideoItem.getRightVideoView().isPlaying()) {
                viewHolderTestVideoItem.getRightVideoView().setVisibility(View.VISIBLE);
                viewHolderTestVideoItem.getRightVideoImageView().setVisibility(View.GONE);
            } else {
                viewHolderTestVideoItem.getRightVideoView().setVisibility(View.GONE);
                viewHolderTestVideoItem.getRightVideoImageView().setVisibility(View.VISIBLE);

                try {
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(item.getRightVideoUrl(), MediaStore.Images.Thumbnails.MINI_KIND);
                    viewHolderTestVideoItem.getRightVideoImageView().setImageBitmap(thumb);
                } catch (Exception e) {
                    TestLog.e(TAG, e);
                }
            }

            viewHolderTestVideoItem.getCase1().setTag(viewHolderTestVideoItem);
            viewHolderTestVideoItem.getCase2().setTag(viewHolderTestVideoItem);

            boolean isHindi = isHindiLanguage();
            viewHolderTestVideoItem.getCase1().setText(isHindi ? item.getChoices().getChoicesHindi().get(0) : item.getChoices().getChoicesEnglish().get(0));
            viewHolderTestVideoItem.getCase2().setText(isHindi ? item.getChoices().getChoicesHindi().get(1) : item.getChoices().getChoicesEnglish().get(1));

            if (item.getSelectedChoice() == 1) {
                viewHolderTestVideoItem.getCase1().setChecked(true);
                viewHolderTestVideoItem.getCase2().setChecked(false);
                viewHolderTestVideoItem.getButtons().check(viewHolderTestVideoItem.getCase1().getId());
            } else if (item.getSelectedChoice() == 2) {
                viewHolderTestVideoItem.getCase1().setChecked(false);
                viewHolderTestVideoItem.getCase2().setChecked(true);
                viewHolderTestVideoItem.getButtons().check(viewHolderTestVideoItem.getCase2().getId());
            } else {
                viewHolderTestVideoItem.getButtons().clearCheck();
            }

            viewHolderTestVideoItem.getQuestionNumber().setText(String.valueOf(position + 1));

            if (item.getSelectedChoice() == -1) {
                viewHolderTestVideoItem.getQuestionState().setImageResource(R.drawable.question_open);
            } else {
                viewHolderTestVideoItem.getQuestionState().setImageResource(R.drawable.question_done);
            }

            if (position == 0) {
                viewHolderTestVideoItem.getProgress1().setVisibility(View.INVISIBLE);
                viewHolderTestVideoItem.getProgress2().setVisibility(View.VISIBLE);
            } else if (position == getItemCount() - 1) {
                viewHolderTestVideoItem.getProgress1().setVisibility(View.VISIBLE);
                viewHolderTestVideoItem.getProgress2().setVisibility(View.INVISIBLE);
            } else {
                viewHolderTestVideoItem.getProgress1().setVisibility(View.VISIBLE);
                viewHolderTestVideoItem.getProgress2().setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mTestList.size();
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

    public class ViewHolderTestItem extends RecyclerView.ViewHolder {

        private TestDataItem mItem;
        private TextView mText;
        private RadioGroup mButtons;
        private RadioButton mCase1;
        private RadioButton mCase2;
        private RadioButton mCase3;
        private RadioButton mCase4;
        private View mView;
        private ImageView mQuestionState;
        private TextView mQuestionNumber;
        private View mProgress1;
        private View mProgress2;

        public ViewHolderTestItem(View itemView) {
            super(itemView);

            mView = itemView;
            mText = itemView.findViewById(R.id.question_text);
            mButtons = itemView.findViewById(R.id.buttons_holder);
            mCase1 = itemView.findViewById(R.id.case_1);
            mCase2 = itemView.findViewById(R.id.case_2);
            mCase3 = itemView.findViewById(R.id.case_3);
            mCase4 = itemView.findViewById(R.id.case_4);
            mQuestionState = itemView.findViewById(R.id.questionState);
            mQuestionNumber = itemView.findViewById(R.id.questionNumber);
            mProgress1 = itemView.findViewById(R.id.progress_1);
            mProgress2 = itemView.findViewById(R.id.progress_2);
        }

        public TextView getText() {
            return mText;
        }

        public void setText(TextView text) {
            mText = text;
        }

        public RadioButton getCase1() {
            return mCase1;
        }

        public RadioButton getCase2() {
            return mCase2;
        }

        public RadioButton getCase3() {
            return mCase3;
        }

        public RadioButton getCase4() {
            return mCase4;
        }

        public RadioGroup getButtons() {
            return mButtons;
        }

        public View getView() {
            return mView;
        }

        public void setView(View view) {
            mView = view;
        }

        public ImageView getQuestionState() {
            return mQuestionState;
        }

        public TextView getQuestionNumber() {
            return mQuestionNumber;
        }

        public View getProgress1() {
            return mProgress1;
        }

        public View getProgress2() {
            return mProgress2;
        }

        public TestDataItem getItem() {
            return mItem;
        }

        public void setItem(TestDataItem item) {
            mItem = item;
        }
    }

    public class ViewHolderTestVideoItem extends RecyclerView.ViewHolder {

        private TestDataVideoItem mItem;
        private TextView mText;
        private TextureVideoView mLeftVideoView;
        private TextureVideoView mRightVideoView;
        private ImageView mLeftVideoImageView;
        private ImageView mRightVideoImageView;
        private RadioGroup mButtons;
        private RadioButton mCase1;
        private RadioButton mCase2;
        private View mView;
        private ImageView mQuestionState;
        private TextView mQuestionNumber;
        private View mProgress1;
        private View mProgress2;

        public ViewHolderTestVideoItem(View itemView) {
            super(itemView);

            mText = itemView.findViewById(R.id.question_text);
            mLeftVideoView = itemView.findViewById(R.id.video_view_left);
            mRightVideoView = itemView.findViewById(R.id.video_view_right);
            mLeftVideoImageView = itemView.findViewById(R.id.video_view_left_image);
            mRightVideoImageView = itemView.findViewById(R.id.video_view_right_image);
            mButtons = itemView.findViewById(R.id.buttons_holder);
            mCase1 = itemView.findViewById(R.id.case_1);
            mCase2 = itemView.findViewById(R.id.case_2);
            mView = itemView;
            mQuestionState = itemView.findViewById(R.id.questionState);
            mQuestionNumber = itemView.findViewById(R.id.questionNumber);
            mProgress1 = itemView.findViewById(R.id.progress_1);
            mProgress2 = itemView.findViewById(R.id.progress_2);
        }

        public TextView getText() {
            return mText;
        }

        public void setText(TextView text) {
            mText = text;
        }

        public TextureVideoView getLeftVideoView() {
            return mLeftVideoView;
        }

        public TextureVideoView getRightVideoView() {
            return mRightVideoView;
        }

        public RadioButton getCase1() {
            return mCase1;
        }

        public RadioButton getCase2() {
            return mCase2;
        }

        public RadioGroup getButtons() {
            return mButtons;
        }

        public View getView() {
            return mView;
        }

        public void setView(View view) {
            mView = view;
        }

        public ImageView getQuestionState() {
            return mQuestionState;
        }

        public TextView getQuestionNumber() {
            return mQuestionNumber;
        }

        public View getProgress1() {
            return mProgress1;
        }

        public View getProgress2() {
            return mProgress2;
        }

        public TestDataVideoItem getItem() {
            return mItem;
        }

        public void setItem(TestDataVideoItem item) {
            mItem = item;
        }

        public ImageView getLeftVideoImageView() {
            return mLeftVideoImageView;
        }

        public ImageView getRightVideoImageView() {
            return mRightVideoImageView;
        }
    }
}
