package com.reading.start.presentation.ui.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.domain.entity.Children;
import com.reading.start.domain.entity.IChild;
import com.reading.start.utils.BitmapUtils;
import com.reading.start.utils.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying children.
 */
public class MySurveysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = MySurveysAdapter.class.getSimpleName();

    public interface OnSurveysItemClickListener {
        void onChildItemClick(Children child);

        void onAddItemClick();
    }

    private static final int FILL = 0;
    private static final int ADD = 1;

    private LinkedList<IChild> mChildList;

    private OnSurveysItemClickListener mListener;

    private SimpleDateFormat mDateFormatter;

    public MySurveysAdapter(LinkedList<IChild> items, OnSurveysItemClickListener listener) {
        mChildList = new LinkedList<>();
        addItems(items);
        mListener = listener;
        mDateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
    }

    @Override
    public int getItemViewType(int position) {
        if (mChildList.get(position) instanceof Children) {
            return FILL;
        } else if (mChildList.get(position) instanceof AddChild) {
            return ADD;
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
            case FILL: {
                view = inflater.inflate(R.layout.item_my_survey_child, parent, false);
                viewHolder = new ViewHolderChildItem(view);
                break;
            }
            case ADD: {
                view = inflater.inflate(R.layout.item_my_survey_add, parent, false);
                viewHolder = new ViewHolderAddChildItem(view);
                break;
            }
            default: {
                view = inflater.inflate(R.layout.item_my_survey_add, parent, false);
                viewHolder = new ViewHolderAddChildItem(view);
                break;
            }
        }

        int itemWidth = (int) ((double) parent.getMeasuredWidth() / (Utility.isNarrow(AppCore.getInstance()) ? Constants.MY_SURVEY_ITEMS_COLUMN_NARROW : Constants.MY_SURVEY_ITEMS_COLUMN_DEFAULT));
        view.setLayoutParams(new RecyclerView.LayoutParams(itemWidth, RecyclerView.LayoutParams.MATCH_PARENT));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case FILL:
                ViewHolderChildItem viewHolderChildItem = (ViewHolderChildItem) holder;
                configureViewHolderChildItem(viewHolderChildItem, position);
                break;
            case ADD:
                ViewHolderAddChildItem viewHolderAddChildItem = (ViewHolderAddChildItem) holder;
                configureViewHolderAddChildItem(viewHolderAddChildItem, position);
                break;
            default:
                ViewHolderAddChildItem viewHolder = (ViewHolderAddChildItem) holder;
                configureViewHolderAddChildItem(viewHolder, position);
                break;
        }
    }

    private void configureViewHolderChildItem(ViewHolderChildItem viewHolderChildItem, int position) {
        Children childItem = (Children) mChildList.get(position);

        if (childItem != null) {
            viewHolderChildItem.getName().setText(childItem.getName() + " " + childItem.getSurname());
            Bitmap bitmap = BitmapUtils.bitmapFromBase64(childItem.getPhoto());

            if (bitmap != null) {
                viewHolderChildItem.getPhoto().setScaleType(ImageView.ScaleType.CENTER_CROP);
                viewHolderChildItem.getPhoto().setImageBitmap(bitmap);
            } else {
                viewHolderChildItem.getPhoto().setScaleType(ImageView.ScaleType.FIT_CENTER);
                viewHolderChildItem.getPhoto().setImageResource(R.drawable.image_profile_photo_empty);
            }

            String dateValue = mDateFormatter.format(new Date(childItem.getBirthDate()));
            viewHolderChildItem.getBirthDate().setText(dateValue);
            viewHolderChildItem.getState().setText(childItem.getState());

            viewHolderChildItem.getView().findViewById(R.id.card_view)
                    .setOnClickListener((View v) -> mListener.onChildItemClick(childItem));
        }
    }

    private void configureViewHolderAddChildItem(ViewHolderAddChildItem viewHolderAddChildItem, int position) {
        AddChild addChildItem = (AddChild) mChildList.get(position);

        if (addChildItem != null) {
            viewHolderAddChildItem.getPhoto().setImageResource(addChildItem.getPhoto());
            viewHolderAddChildItem.getView().findViewById(R.id.card_view)
                    .setOnClickListener(view -> mListener.onAddItemClick());
        }
    }

    @Override
    public int getItemCount() {
        return mChildList.size();
    }

    public void addItems(List<IChild> items) {
        mChildList.add(new AddChild(R.drawable.button_add));
        mChildList.addAll(items);
    }

    public List<IChild> getChildList() {
        return mChildList;
    }

    public void addItem(IChild item) {
        mChildList.add(item);
    }

    public class ViewHolderChildItem extends RecyclerView.ViewHolder {

        private ImageView mPhoto;

        private TextView mName;

        private TextView mBirthDate;

        private TextView mState;

        private View mView;

        public ViewHolderChildItem(View itemView) {
            super(itemView);

            mView = itemView;
            mPhoto = itemView.findViewById(R.id.photo);
            mName = itemView.findViewById(R.id.name);
            mBirthDate = itemView.findViewById(R.id.birth_date);
            mState = itemView.findViewById(R.id.state);
        }

        public ImageView getPhoto() {
            return mPhoto;
        }

        public void setPhoto(ImageView photo) {
            mPhoto = photo;
        }

        public TextView getName() {
            return mName;
        }

        public void setName(TextView name) {
            mName = name;
        }

        public TextView getBirthDate() {
            return mBirthDate;
        }

        public TextView getState() {
            return mState;
        }

        public View getView() {
            return mView;
        }
    }

    public class ViewHolderAddChildItem extends RecyclerView.ViewHolder {

        private ImageView mPhoto;

        private View mView;

        public ViewHolderAddChildItem(View itemView) {
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

    private class AddChild implements IChild {

        private int mPhoto;

        public AddChild(int photo) {
            mPhoto = photo;
        }

        public int getPhoto() {
            return mPhoto;
        }

        public void setPhoto(int photo) {
            mPhoto = photo;
        }
    }
}
