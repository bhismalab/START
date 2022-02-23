package com.reading.start.presentation.ui.fragments.child;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.databinding.FragmentChildBinding;
import com.reading.start.domain.entity.Children;
import com.reading.start.general.TLog;
import com.reading.start.presentation.mvp.holders.ChildViewHolder;
import com.reading.start.presentation.mvp.presenters.ChildPresenter;
import com.reading.start.presentation.mvp.views.ChildView;
import com.reading.start.presentation.ui.UiConstants;
import com.reading.start.presentation.ui.activities.ChildActivity;
import com.reading.start.presentation.ui.dialogs.DialogChildDiagnosis;
import com.reading.start.presentation.ui.dialogs.DialogOkCancelGeneral;
import com.reading.start.presentation.ui.fragments.base.BaseActionBarFragment;
import com.reading.start.utils.Utility;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Represent screen for add and edit child info.
 */
public class ChildFragment extends BaseActionBarFragment implements ChildView {

    public static final String TAG = ChildFragment.class.getSimpleName();

    private ChildPresenter mPresenter;

    private FragmentChildBinding mBinding;

    private ChildViewHolder mViewHolder;

    private Context mContext;

    private DatePickerDialog mDatePickerDialog;

    private SimpleDateFormat mDateFormatter;

    private ChildActivity mActivity;

    private int mChildId = -1;

    private Children mChild;

    private String mCurrentPhotoPath;

    private File mCurrentPhotoFile;

    private boolean mIsNextPressed = false;

    private String mDoctorValue = null;

    private String mDiagnosisValue = null;

    private long mDiagnosisDate = 0;

    private boolean mIsEdited = false;

    public ChildFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mContext = inflater.getContext();
        mActivity = getChildActivity();

        if (getArguments() != null && getArguments().containsKey(UiConstants.CHILD_ID)) {
            mChildId = getArguments().getInt(UiConstants.CHILD_ID);
        } else {
            mChild = mActivity.getChild();
        }

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_child, container, false);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bg_small);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mBinding.root.setBackground(bitmapDrawable);

        //init presenter
        mViewHolder = new ChildViewHolder(mBinding.actionBar, mBinding.childPhoto,
                mBinding.buttonDiagnosis, mBinding.childName,
                mBinding.childSurname, mBinding.maleRadioButton, mBinding.femaleRadioButton,
                mBinding.dateOfBirth, mBinding.state, mBinding.childAddress, mBinding.hand);

        mPresenter = mChild == null ? new ChildPresenter(mActivity, mChildId) : new ChildPresenter(mActivity, mChild);
        mPresenter.init(this, mViewHolder);
        mPresenter.onCreate(inflater, container, savedInstanceState);

        // Date
        mDateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        updateDateTimeField();

        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);

        if (mChildId == -1) {
            title.setText(R.string.action_bar_add_child);
        } else {
            title.setText(R.string.action_bar_edit_child);
        }

        initActionBar(mBinding.actionBar);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.navigation_button).setVisibility(View.GONE);

        updatePhotoViewSize();

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
        updatePhotoViewSize();

        if (mPresenter != null) {
            mPresenter.onResume();
        }

        if (mChild != null && !mChild.isManaged() && ((mBinding.childName.getText().toString() != null && !mBinding.childName.getText().toString().isEmpty())
                || (mBinding.childSurname.getText().toString() != null && !mBinding.childSurname.getText().toString().isEmpty())
                || (mBinding.childAddress.getText().toString() != null && !mBinding.childAddress.getText().toString().isEmpty()))) {
            mIsEdited = true;
        }

        super.onResume();
    }

    protected ChildActivity getChildActivity() {
        if (getActivity() instanceof ChildActivity) {
            return (ChildActivity) getActivity();
        } else {
            return null;
        }
    }

    /**
     * Updates date.
     */
    private void updateDateTimeField() {
        mBinding.dateOfBirth.setOnClickListener(view -> {
            Calendar newCalendar = Calendar.getInstance();

            try {
                String value = mBinding.dateOfBirth.getText().toString();
                long defaultTime = 0;

                if (value == null || value.isEmpty()) {
                    defaultTime = Constants.DEFAULT_DATE_CHILD;
                } else {
                    defaultTime = mDateFormatter.parse(value).getTime();
                }

                newCalendar.setTimeInMillis(defaultTime);
            } catch (ParseException e) {
                TLog.e(TAG, e);
            }

            mDatePickerDialog.getDatePicker().updateDate(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            mDatePickerDialog.show();
        });

        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTimeInMillis(Constants.DEFAULT_DATE_CHILD);

        int theme;
        if (Build.VERSION.SDK_INT < 23) {
            theme = AlertDialog.THEME_HOLO_LIGHT;
        } else {
            theme = android.R.style.Theme_Holo_Dialog;
        }

        mDatePickerDialog = new DatePickerDialog(mContext, theme, (view, year, monthOfYear, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);
            mBinding.dateOfBirth.setText(mDateFormatter.format(newDate.getTime()));
            mIsEdited = true;
            mPresenter.validate();
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialog.getDatePicker().setCalendarViewShown(false);
        mDatePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
    }

    /**
     * Raises when pressed child diagnostic.
     */
    @Override
    public void onChildDiagnosisPressed(String doctorValue, String diagnosisValue, long diagnosisDate) {
        mIsNextPressed = false;
        mPresenter.saveChildInfo(false, getChildActivity().getCurrentLocation(), mCurrentPhotoFile,
                doctorValue, diagnosisValue, diagnosisDate);

        DialogChildDiagnosis dialog = DialogChildDiagnosis.getInstance(getString(R.string.action_bar_child_diagnosis),
                doctorValue, diagnosisValue, diagnosisDate,
                (doctorName, diagnosis, date) -> {
                    mIsEdited = true;
                    mDoctorValue = doctorName;
                    mDiagnosisValue = diagnosis;
                    mDiagnosisDate = date;
                    mPresenter.saveChildInfo(false, getChildActivity().getCurrentLocation(),
                            mCurrentPhotoFile, mDoctorValue, mDiagnosisValue, mDiagnosisDate);
                });

        Bundle args = dialog.getArguments();
        args.putString(UiConstants.DOCTOR_ID, doctorValue);
        args.putString(UiConstants.DIAGNOSIS_ID, diagnosisValue);
        args.putLong(UiConstants.ASSESSMENT_TIME_ID, diagnosisDate);

        dialog.setArguments(args);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), TAG);
    }

    /**
     * Raises when pressed next button
     */
    @Override
    public void onNextPressed() {
        mIsNextPressed = true;
        mPresenter.saveChildInfo(true, getChildActivity().getCurrentLocation(), mCurrentPhotoFile,
                mDoctorValue, mDiagnosisValue, mDiagnosisDate);
    }

    /**
     * Raises when pressed back button
     */
    @Override
    public void onBackPressed() {
        if (mIsEdited) {
            String title = (mChildId != -1) ? getResources().getString(R.string.child_dialog_edit_close_title)
                    : getResources().getString(R.string.child_dialog_add_close_title);

            DialogOkCancelGeneral dialog = DialogOkCancelGeneral.getInstance(title,
                    getResources().getText(R.string.child_dialog_edit_close_message).toString(), new DialogOkCancelGeneral.DialogListener() {
                        @Override
                        public void onOK() {
                            getChildActivity().openMySurveysFragment();
                        }

                        @Override
                        public void onCancel() {
                        }
                    });

            dialog.setCancelable(false);
            dialog.show(getFragmentManager(), TAG);
        } else {
            getChildActivity().openMySurveysFragment();
        }
    }

    /**
     * Raises when save success
     */
    @Override
    public void onSaveSuccess(boolean isEdit) {
        mIsEdited = false;
        final ChildActivity activity = getChildActivity();

        if (mIsNextPressed) {
            if (activity != null) {
                activity.openAddParentFragment(UiConstants.PARENT_ID_1);
            }
        } else if (isEdit) {
            getChildActivity().openMySurveysFragment();
        } else {
            if (activity != null) {
                activity.openAddParentFragment(UiConstants.PARENT_ID_1);
            }
        }
    }

    /**
     * Raise when pressed save button
     */
    @Override
    public void onSave() {
        mIsNextPressed = false;
        mPresenter.saveChildInfo(true, getChildActivity().getCurrentLocation(), mCurrentPhotoFile,
                mDoctorValue, mDiagnosisValue, mDiagnosisDate);
    }

    /**
     * Raises when pressed take photo button
     */
    @Override
    public void onTakePhoto() {
        mIsEdited = true;
        mIsNextPressed = false;
        mPresenter.saveChildInfo(false, getChildActivity().getCurrentLocation(), mCurrentPhotoFile, mDoctorValue, mDiagnosisValue, mDiagnosisDate);

        List<Intent> intentList = new ArrayList<>();
        addIntentsToList(AppCore.getInstance(), intentList, getIntentGetPhotoFromGallery());
        addIntentsToList(AppCore.getInstance(), intentList, getIntentGetPhotoFromCamera());

        Intent chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1), AppCore.getInstance().getString(R.string.take_photo));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, UiConstants.CHILD_ACTIVITY_TAKE_PHOTO);
    }

    /**
     * Show toast with message
     */
    @Override
    public void onShowToast(String message) {
        if (isVisible() && isResumed()) {
            Toast.makeText(getChildActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEdited(boolean value) {
        mIsEdited = value;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == UiConstants.CHILD_ACTIVITY_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
                String fileFromGallery = null;

                try {
                    if (data != null && data.getData() != null) {
                        Uri selectedImage = data.getData();
                        String[] filePath = {MediaStore.Images.Media.DATA};
                        Cursor c = AppCore.getInstance().getContentResolver().query(selectedImage, filePath, null, null, null);
                        c.moveToFirst();
                        int columnIndex = c.getColumnIndex(filePath[0]);
                        fileFromGallery = c.getString(columnIndex);
                        c.close();
                    }
                } catch (Exception e) {
                    TLog.e(TAG, e);
                }

                if (fileFromGallery == null) {
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);
                    File file = new File(imageUri.getPath());

                    if (file != null && file.exists()) {
                        mCurrentPhotoFile = file;
                        mPresenter.setPhotoFile(mCurrentPhotoFile);
                    }
                } else {
                    File file = new File(fileFromGallery);

                    if (file != null && file.exists()) {
                        mCurrentPhotoFile = file;
                        mPresenter.setPhotoFile(mCurrentPhotoFile);
                    }
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
        }
    }

    /**
     * Create temp file for photo
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat(Constants.IMAGE_FILE_FORMAT, Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = AppCore.getInstance().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void updatePhotoViewSize() {
        Size size = Utility.getDisplaySize(AppCore.getInstance());

        if (size != null) {
            int photoSize = size.getHeight() * Constants.PHOTO_SIZE_UI_PERCENT / 100;
            ImageView photo = mBinding.childPhoto;
            photo.setLayoutParams(new FrameLayout.LayoutParams(photoSize, photoSize));
            mBinding.getRoot().requestLayout();
        }
    }

    /**
     * Gets intent for call native photo gallery
     */
    private Intent getIntentGetPhotoFromGallery() {
        return new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    /**
     * Gets intent for call native camera application
     */
    private Intent getIntentGetPhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getChildActivity().getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                TLog.e(TAG, e);
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(AppCore.getInstance(), "com.reading.start.provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
        }

        return takePictureIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }

        return list;
    }
}
