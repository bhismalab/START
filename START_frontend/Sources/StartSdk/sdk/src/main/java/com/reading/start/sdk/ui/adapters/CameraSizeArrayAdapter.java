package com.reading.start.sdk.ui.adapters;

import android.content.Context;
import android.hardware.Camera;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.reading.start.sdk.AppCore;
import com.reading.start.sdk.R;

import java.util.List;

public class CameraSizeArrayAdapter extends ArrayAdapter<Camera.Size> {
    public CameraSizeArrayAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    public CameraSizeArrayAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public CameraSizeArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull Camera.Size[] objects) {
        super(context, resource, objects);
    }

    public CameraSizeArrayAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull Camera.Size[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public CameraSizeArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Camera.Size> objects) {
        super(context, resource, objects);
    }

    public CameraSizeArrayAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<Camera.Size> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) AppCore.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.sdk_item_spinner, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.text1);

        Camera.Size size = getItem(position);
        String value = "Size(" + size.width + ", " + size.height + ")";
        textView.setText(value);

        return rowView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) AppCore.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.sdk_item_spinner_dropdown, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.text1);

        Camera.Size size = getItem(position);
        String value = "(" + size.width + ", " + size.height + ")";
        textView.setText(value);

        return rowView;
    }
}
