package com.reading.start.presentation.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.domain.entity.ISurvey;
import com.reading.start.domain.entity.Survey;
import com.reading.start.domain.entity.SurveyStatus;
import com.reading.start.general.TLog;
import com.reading.start.presentation.ui.views.CircularProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

import io.realm.Realm;

/**
 * Adapter for displaying list of surveys.
 */
public class ChildInformationSurveysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ChildInformationSurveysAdapter.class.getSimpleName();

    public interface OnSurveysItemClickListener {
        void onFinishedSurveyItemClick(Survey survey, int position);

        void onAddSurveyItemClick();
    }

    private static final int GENERAL = 0;
    private static final int ADD = 1;

    private LinkedList<ISurvey> mSurveyList;

    private ChildInformationSurveysAdapter.OnSurveysItemClickListener mListener;

    private SimpleDateFormat mDateFormatter;

    public ChildInformationSurveysAdapter(ArrayList<ISurvey> items, ChildInformationSurveysAdapter.OnSurveysItemClickListener listener) {
        mSurveyList = new LinkedList<>();
        mSurveyList.addAll(items);
        mListener = listener;
        mSurveyList.add(new AddSurvey(R.drawable.button_add));
        mDateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
    }

    @Override
    public int getItemViewType(int position) {
        if (mSurveyList.get(position) instanceof Survey) {
            return GENERAL;
        } else if (mSurveyList.get(position) instanceof AddSurvey) {
            return ADD;
        } else {
            return -1;
        }
    }

    public Survey getSurvey(int surveyId) {
        Survey result = null;

        if (mSurveyList != null && mSurveyList.size() > 0) {
            for (ISurvey item : mSurveyList) {
                if (item instanceof Survey && ((Survey) item).getId() == surveyId) {
                    result = (Survey) item;
                    break;
                }
            }
        }

        return result;
    }

    public boolean updateSurvey(int surveyId, Survey update) {
        boolean result = false;

        if (update != null && isExistSurvey(surveyId)) {
            Survey survey = getSurvey(surveyId);
            int index = mSurveyList.indexOf(survey);
            mSurveyList.remove(index);
            mSurveyList.add(index, update);
            notifyItemChanged(index);
        }

        return result;
    }

    public boolean isExistSurvey(int surveyId) {
        boolean result = false;

        if (mSurveyList != null && mSurveyList.size() > 0) {
            for (ISurvey item : mSurveyList) {
                if (item instanceof Survey && ((Survey) item).getId() == surveyId) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view;

        switch (viewType) {
            case ADD: {
                view = inflater.inflate(R.layout.item_survey_add, parent, false);
                viewHolder = new ChildInformationSurveysAdapter.ViewHolderAddSurveyItem(view);
                break;
            }
            case GENERAL:
            default: {
                view = inflater.inflate(R.layout.item_survey_info, parent, false);
                viewHolder = new ChildInformationSurveysAdapter.ViewHolderSurveyItem(view);
                view.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> updateProgressSize((ViewHolderSurveyItem) viewHolder));
                break;
            }
        }

        int itemHeight = (int) ((double) parent.getMeasuredHeight() / Constants.CHILD_INFO_SURVEYS_ITEMS_ROWS);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, itemHeight));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ADD:
                ChildInformationSurveysAdapter.ViewHolderAddSurveyItem viewHolderAddSurveyItem = (ChildInformationSurveysAdapter.ViewHolderAddSurveyItem) holder;
                configureViewHolderAddSurveyItem(viewHolderAddSurveyItem, position);
                break;
            case GENERAL:
            default:
                ChildInformationSurveysAdapter.ViewHolderSurveyItem viewHolderSurveyItem = (ChildInformationSurveysAdapter.ViewHolderSurveyItem) holder;
                configureViewHolderSurveyItem(viewHolderSurveyItem, position);
                break;
        }
    }

    private void configureViewHolderSurveyItem(ChildInformationSurveysAdapter.ViewHolderSurveyItem viewHolderSurveyItem, int position) {
        Realm realm = null;

        try {
            Survey surveyItem = (Survey) mSurveyList.get(position);

            if (surveyItem != null) {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                if (realm != null) {
                    SurveyStatus surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, surveyItem.getId()).findFirst();

                    if (surveyStatus != null) {
                        viewHolderSurveyItem.getName().setText(String.format(AppCore.getInstance().getString(R.string.survey_name),
                                String.valueOf(position + 1)));

                        if (surveyStatus.isRemote()) {
                            viewHolderSurveyItem.getSurvey().setVisibility(View.GONE);
                            viewHolderSurveyItem.getDownload().setVisibility(View.VISIBLE);
                            viewHolderSurveyItem.getSurveyProgress().setVisibility(View.INVISIBLE);
                            viewHolderSurveyItem.getUploadStatus().setVisibility(View.INVISIBLE);
                            viewHolderSurveyItem.getUploadProgress().setProgress(surveyStatus.getUploadProgress());
                        } else {
                            viewHolderSurveyItem.getSurvey().setVisibility(View.VISIBLE);
                            viewHolderSurveyItem.getDownload().setVisibility(View.GONE);

                            boolean uploaded = surveyStatus.isUploaded();
                            viewHolderSurveyItem.getSurveyProgress().setVisibility(View.VISIBLE);
                            viewHolderSurveyItem.getSurveyProgress().setProgress(surveyStatus.getCompleteProgress());
                            viewHolderSurveyItem.getUploadProgress().setProgress(surveyStatus.getUploadProgress());
                            viewHolderSurveyItem.getUploadStatus().setVisibility(View.VISIBLE);

                            if (uploaded) {
                                viewHolderSurveyItem.getUploadStatus().setImageResource(R.drawable.image_upload_cloud_yes);
                            } else {
                                viewHolderSurveyItem.getUploadStatus().setImageResource(R.drawable.image_upload_cloud_no);
                            }
                        }

                        viewHolderSurveyItem.getView().findViewById(R.id.card_view)
                                .setOnClickListener((View v) -> mListener.onFinishedSurveyItemClick(surveyItem, position));
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private void configureViewHolderAddSurveyItem(ChildInformationSurveysAdapter.ViewHolderAddSurveyItem viewHolderAddSurveyItem, int position) {
        AddSurvey addSurveyItem = (AddSurvey) mSurveyList.get(position);

        if (addSurveyItem != null) {
            viewHolderAddSurveyItem.getPhoto().setImageResource(addSurveyItem.getImage());

            viewHolderAddSurveyItem.getView().findViewById(R.id.card_view)
                    .setOnClickListener(view -> mListener.onAddSurveyItemClick());
        }
    }

    private void updateProgressSize(ChildInformationSurveysAdapter.ViewHolderSurveyItem holder) {
        try {
            double heightProgress = AppCore.getInstance().getResources().getDimension(R.dimen.circle_progress_default_stroke_width);
            int parentWidth = holder.getProgressHolder().getMeasuredWidth();
            double progressSizeUpload = ((double) parentWidth) * 0.9;
            double progressSizeSurvey = (progressSizeUpload - heightProgress * 2) * 0.85;
            double statusMargin = heightProgress * 0.1;

            RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) holder.getUploadProgress().getLayoutParams();
            param.width = (int) progressSizeUpload;
            param.height = (int) progressSizeUpload;
            holder.getUploadProgress().setLayoutParams(param);
            statusMargin += holder.getUploadProgress().getY();

            param = (RelativeLayout.LayoutParams) holder.getSurveyProgress().getLayoutParams();
            param.width = (int) progressSizeSurvey;
            param.height = (int) progressSizeSurvey;
            holder.getSurveyProgress().setLayoutParams(param);

            param = (RelativeLayout.LayoutParams) holder.getUploadStatus().getLayoutParams();
            param.width = (int) (heightProgress);
            param.height = (int) (heightProgress);
            param.topMargin = (int) (statusMargin);
            holder.getUploadStatus().setLayoutParams(param);
            holder.getUploadStatus().setVisibility(View.VISIBLE);
        } catch (Exception e) {
            TLog.e(TAG, e);
        }
    }

    @Override
    public int getItemCount() {
        return mSurveyList.size();
    }

    public class ViewHolderSurveyItem extends RecyclerView.ViewHolder {

        private ImageView mSurvey;

        private ImageView mDownload;

        private TextView mName;

        private CircularProgressBar mSurveyProgress;

        private CircularProgressBar mUploadProgress;

        private ImageView mUploadStatus;

        private View mView;

        private View mProgressHolder;

        public ViewHolderSurveyItem(View itemView) {
            super(itemView);

            mView = itemView;
            mSurvey = itemView.findViewById(R.id.survey);
            mDownload = itemView.findViewById(R.id.download);
            mName = itemView.findViewById(R.id.survey_name);
            mSurveyProgress = itemView.findViewById(R.id.survey_progress);
            mUploadProgress = itemView.findViewById(R.id.upload_progress);
            mUploadStatus = itemView.findViewById(R.id.upload_status);
            mUploadStatus.setVisibility(View.INVISIBLE);
            mProgressHolder = itemView.findViewById(R.id.progress_holder);
        }

        public ImageView getSurvey() {
            return mSurvey;
        }

        public void setSurvey(ImageView survey) {
            mSurvey = survey;
        }

        public ImageView getDownload() {
            return mDownload;
        }

        public void setDownload(ImageView download) {
            mDownload = download;
        }

        public TextView getName() {
            return mName;
        }

        public void setName(TextView name) {
            mName = name;
        }

        public View getView() {
            return mView;
        }

        public View getProgressHolder() {
            return mProgressHolder;
        }

        public CircularProgressBar getSurveyProgress() {
            return mSurveyProgress;
        }

        public CircularProgressBar getUploadProgress() {
            return mUploadProgress;
        }

        public ImageView getUploadStatus() {
            return mUploadStatus;
        }
    }

    public class ViewHolderAddSurveyItem extends RecyclerView.ViewHolder {

        private ImageView mPhoto;

        private View mView;

        public ViewHolderAddSurveyItem(View itemView) {
            super(itemView);

            mView = itemView;
            mPhoto = itemView.findViewById(R.id.photo);
        }

        public ImageView getPhoto() {
            return mPhoto;
        }

        public void setPhoto(ImageView photo) {
            mPhoto = photo;
        }

        public View getView() {
            return mView;
        }

        public void setView(View view) {
            mView = view;
        }
    }

    public class AddSurvey implements ISurvey {

        private int mImage;

        public AddSurvey(int image) {
            mImage = image;
        }

        public int getImage() {
            return mImage;
        }

        public void setImage(int image) {
            mImage = image;
        }
    }
}
