package com.reading.start.presentation.mvp.presenters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.domain.entity.Children;
import com.reading.start.domain.entity.States;
import com.reading.start.general.TLog;
import com.reading.start.presentation.mvp.holders.ChildViewHolder;
import com.reading.start.presentation.mvp.models.ChildModel;
import com.reading.start.presentation.mvp.views.ChildView;
import com.reading.start.tests.TestLog;
import com.reading.start.utils.BitmapUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

public class ChildPresenter extends BasePresenter<ChildView, ChildModel, ChildViewHolder> {

    public static final String TAG = ChildPresenter.class.getSimpleName();

    private Children mChild;

    private int mChildId = -1;

    private File mPhotoFile = null;

    private boolean mIsEdited = false;

    private boolean mIsInit = false;

    private int mStateIndex = -1;

    private int mStateId = -1;

    private int mHandIndex = 0;

    private Context mContext;

    public ChildPresenter(Context context, Children child) {
        super(true);
        setModel(new ChildModel(getRealm()));
        mChild = child;
        mContext = context;
    }

    public ChildPresenter(Context context, int childId) {
        super(true);
        setModel(new ChildModel(getRealm()));
        mChildId = childId;
        mChild = getModel().getChild(childId);
        mContext = context;
    }

    private DateFormat mFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());

    @Override
    public void onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getViewHolder() != null) {
            RxView.clicks(getViewHolder().getActionBar().findViewById(R.id.next_button)).subscribe(aVoid -> processNext());
            RxView.clicks(getViewHolder().getActionBar().findViewById(R.id.back_button))
                    .subscribe(aVoid -> getView().onBackPressed());
            RxView.clicks(getViewHolder().getActionBar().findViewById(R.id.next_button))
                    .subscribe(aVoid -> getView().onNextPressed());

            RxView.clicks(getViewHolder().getDiagnosis())
                    .subscribe(aVoid -> {
                        String doctor = "";
                        String diagnosis = "";
                        long diagnosisDate = 0;

                        if (mChild != null) {
                            doctor = mChild.getDiagnosisClinic();
                            diagnosis = mChild.getDiagnosis();
                            diagnosisDate = mChild.getDiagnosisDate();
                        }

                        getView().onChildDiagnosisPressed(doctor, diagnosis, diagnosisDate);
                    });

            RxView.clicks(getViewHolder().getActionBar().findViewById(R.id.save_button))
                    .subscribe(aVoid -> getView().onSave());
            RxView.clicks(getViewHolder().getChildPhoto())
                    .subscribe(aVoid -> getView().onTakePhoto());

            if (getViewHolder().getMale().isChecked()) {
                getViewHolder().getMale().setPaintFlags(getViewHolder().getMale().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }

            if (getViewHolder().getFemale().isChecked()) {
                getViewHolder().getFemale().setPaintFlags(getViewHolder().getMale().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }

            RxView.clicks(getViewHolder().getMale()).subscribe(aVoid -> {
                getViewHolder().getMale().setPaintFlags(getViewHolder().getMale().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                getViewHolder().getFemale().setPaintFlags(0);

                if (mIsInit) {
                    mIsEdited = true;
                    getView().onEdited(mIsEdited);
                }
            });

            RxView.clicks(getViewHolder().getFemale()).subscribe(aVoid -> {
                getViewHolder().getFemale().setPaintFlags(getViewHolder().getFemale().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                getViewHolder().getMale().setPaintFlags(0);

                if (mIsInit) {
                    mIsEdited = true;
                    getView().onEdited(mIsEdited);
                }
            });

            if (mChild.isManaged()) {
                getViewHolder().getActionBar().findViewById(R.id.save_button).setVisibility(View.VISIBLE);
            } else {
                getViewHolder().getActionBar().findViewById(R.id.save_button).setVisibility(View.GONE);
            }

            RxView.clicks(getViewHolder().getState()).subscribe(aVoid -> {
                final ArrayList<States> list = getModel().getStates();
                CharSequence[] items = new CharSequence[list.size()];

                for (int i = 0; i < list.size(); i++) {
                    items[i] = isHindiLanguage() ? list.get(i).getNameHindi() : list.get(i).getNameEnglish();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setItems(items, (dialog, item) -> {
                    mStateIndex = item;
                    States selectedStates = findStateByName(list, items[item].toString());

                    if (selectedStates != null) {
                        try {
                            mStateId = Integer.parseInt(selectedStates.getIdServer());
                        } catch (Exception e) {
                            TLog.e(TAG, e);
                        }

                        getViewHolder().getState().setText(isHindiLanguage() ?
                                selectedStates.getNameHindi() : selectedStates.getNameEnglish());
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            });

            RxView.clicks(getViewHolder().getHand()).subscribe(aVoid -> {
                final ArrayList<Integer> listId = getModel().getHandState();
                CharSequence[] items = new CharSequence[listId.size()];

                for (int i = 0; i < listId.size(); i++) {
                    items[i] = mContext.getString(listId.get(i));
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setItems(items, (dialog, item) -> {
                    mHandIndex = item;
                    getViewHolder().getHand().setText(items[item]);

                    if (mIsInit) {
                        mIsEdited = true;
                        getView().onEdited(mIsEdited);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            });

            validateFields();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mChild.getPhotoPath() != null) {
            mPhotoFile = mChild.getPhotoPath();
        }

        updateChildInfo();
        mIsInit = true;
    }

    public void validateFields() {
        validateName();
        validateSurname();
        validateAddress();
    }

    public void validateName() {
        getViewHolder().getName().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (TextUtils.isEmpty(getViewHolder().getName().getText().toString())) {
                    getView().onShowToast(AppCore.getInstance().getString(R.string.validation_incorrect_name));
                }
            }
        });

        RxTextView.afterTextChangeEvents(getViewHolder()
                .getName()).subscribe(textViewAfterTextChangeEvent -> {
            validate();

            if (mIsInit) {
                mIsEdited = true;
                getView().onEdited(mIsEdited);
            }
        });
    }

    public void validateSurname() {
        getViewHolder().getSurname().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (TextUtils.isEmpty(getViewHolder().getSurname().getText().toString())) {
                    getView().onShowToast(AppCore.getInstance().getString(R.string.validation_incorrect_surname));
                }
            }
        });

        RxTextView.afterTextChangeEvents(getViewHolder()
                .getSurname()).subscribe(textViewAfterTextChangeEvent -> {
            validate();

            if (mIsInit) {
                mIsEdited = true;
                getView().onEdited(mIsEdited);
            }
        });
    }

    public void validateAddress() {
        getViewHolder().getAddress().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (TextUtils.isEmpty(getViewHolder().getAddress().getText().toString())) {
                    getView().onShowToast(AppCore.getInstance().getString(R.string.validation_incorrect_address));
                }
            }
        });

        RxTextView.afterTextChangeEvents(getViewHolder()
                .getAddress()).subscribe(textViewAfterTextChangeEvent -> {
            validate();

            if (mIsInit) {
                mIsEdited = true;
                getView().onEdited(mIsEdited);
            }
        });
    }

    public boolean validate() {
        if (!TextUtils.isEmpty(getViewHolder().getName().getText()) &&
                !TextUtils.isEmpty(getViewHolder().getSurname().getText()) &&
                !TextUtils.isEmpty(getViewHolder().getDateOfBirth().getText()) &&
                !TextUtils.isEmpty(getViewHolder().getAddress().getText())) {

            getViewHolder().getActionBar().findViewById(R.id.next_button).setEnabled(true);
            getViewHolder().getActionBar().findViewById(R.id.save_button).setEnabled(true);
            return true;
        } else {
            getViewHolder().getActionBar().findViewById(R.id.next_button).setEnabled(false);
            getViewHolder().getActionBar().findViewById(R.id.save_button).setEnabled(false);
            return false;
        }
    }

    public void setPhotoFile(File photoFile) {
        mPhotoFile = photoFile;
    }

    private void processNext() {
        getView().onNextPressed();
    }

    private void updateChildInfo() {
        if (mPhotoFile != null) {
            getViewHolder().getChildPhoto().setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.with(AppCore.getInstance()).load(mPhotoFile)
                    .resize(0, Constants.PHOTO_HEIGHT).into(getViewHolder().getChildPhoto());
        } else if (mChild.getPhoto() != null && mChild.getPhoto().length() > 0) {
            Bitmap bitmap = BitmapUtils.bitmapFromBase64(mChild.getPhoto());

            if (bitmap != null) {
                getViewHolder().getChildPhoto().setScaleType(ImageView.ScaleType.CENTER_CROP);
                getViewHolder().getChildPhoto().setImageBitmap(bitmap);
            }
        }

        getViewHolder().getName().setText(mChild.getName());
        getViewHolder().getSurname().setText(mChild.getSurname());

        if (mChild.getGender().equals(Constants.MALE)) {
            getViewHolder().getMale().setChecked(true);
            getViewHolder().getMale().setPaintFlags(getViewHolder().getMale().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else if (mChild.getGender().equals(Constants.FEMALE)) {
            getViewHolder().getFemale().setChecked(true);
            getViewHolder().getFemale().setPaintFlags(getViewHolder().getFemale().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        try {
            if (mChild.getBirthDate() > 0) {
                String value = mFormat.format(new Date(mChild.getBirthDate()));
                getViewHolder().getDateOfBirth().setText(value);
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
        }

        updateState();
        updateHand();
        getViewHolder().getAddress().setText(mChild.getAddress());
    }

    private void updateState() {
        if (mChild.getState() != null) {
            ArrayList<States> list = getModel().getStates();
            int stateId = -1;

            try {
                stateId = Integer.parseInt(mChild.getState());
            } catch (Exception e) {
                TLog.e(TAG, e);
            }

            if (stateId != -1) {
                States state = findStateById(list, String.valueOf(stateId));

                if (state != null) {
                    mStateIndex = stateId;
                    mStateId = Integer.parseInt(state.getIdServer());
                    getViewHolder().getState().setText(isHindiLanguage() ? state.getNameHindi() : state.getNameEnglish());
                }
            } else {
                States state = findStateByName(list, Constants.DEFAULT_STATE);

                if (state != null) {
                    mStateIndex = stateId;
                    mStateId = Integer.parseInt(state.getIdServer());
                    getViewHolder().getState().setText(isHindiLanguage() ? state.getNameHindi() : state.getNameEnglish());
                }
            }
        }
    }

    private void updateHand() {
        if (mChild.getHand() != null) {
            int index = -1;
            ArrayList<Integer> listId = getModel().getHandState();
            CharSequence[] items = new CharSequence[listId.size()];

            for (int i = 0; i < listId.size(); i++) {
                items[i] = mContext.getString(listId.get(i));
            }

            for (int i = 0; i < items.length; i++) {
                if (items[i].equals(mChild.getHand())) {
                    index = i;
                    break;
                }
            }

            mHandIndex = index;

            if (mHandIndex != -1) {
                getViewHolder().getHand().setText(mChild.getHand());
            } else {
                mHandIndex = 0;
                getViewHolder().getHand().setText(items[mHandIndex]);
            }
        }
    }

    public void saveChildInfo(final boolean callback, Location location, File photoFile, String doctorName,
                              String diagnosis, long diagnosisDate) {
        String name = getViewHolder().getName().getText().toString();
        String surname = TextUtils.isEmpty(getViewHolder().getSurname().getText().toString())
                ? "" : getViewHolder().getSurname().getText().toString();
        String patronymic = "";

        String state = String.valueOf(mStateId);
        String hand = "";

        String handString = getViewHolder().getHand().getText().toString();
        if (handString.equals(mContext.getString(R.string.child_hand_right))) {
            hand = Constants.HAND_RIGHT;
        } else if (handString.equals(mContext.getString(R.string.child_hand_left))) {
            hand = Constants.HAND_LEFT;
        } else if (handString.equals(mContext.getString(R.string.child_hand_ambidexter))) {
            hand = Constants.HAND_AMBIDEXTER;
        }

        String address = getViewHolder().getAddress().getText().toString();
        String gender = "";

        if (getViewHolder().getMale().isChecked()) {
            gender = Constants.MALE;
        } else if (getViewHolder().getFemale().isChecked()) {
            gender = Constants.FEMALE;
        }

        long birthDate = 0;

        try {
            birthDate = mFormat.parse(getViewHolder().getDateOfBirth().getText().toString()).getTime();
        } catch (ParseException e) {
            TLog.e(TAG, e);
        }

        String latitude = "0.0";
        String longitude = "0.0";

        if (location != null) {
            try {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
            } catch (Exception e) {
                TLog.e(TAG, e);
            }
        }

        String addBy = AppCore.getInstance().getPreferences().getLoginWorker();
        String modBy = AppCore.getInstance().getPreferences().getLoginWorker();

        long addDatetime = mChild.getAddDateTime() == 0 ? Calendar.getInstance().getTimeInMillis() : mChild.getAddDateTime();
        long modDatetime = Calendar.getInstance().getTimeInMillis();
        boolean isDeleted = false;

        final Observable<Children> observable = getModel().getSaveChildObservable(mChild,
                name, surname, patronymic, state, hand, address, gender, birthDate, latitude,
                longitude, addDatetime, addBy, modDatetime, modBy, isDeleted, photoFile, doctorName,
                diagnosis, diagnosisDate);

        getSubscriptions().add(observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Children>() {
                    @Override
                    public void onNext(Children children) {
                        if (callback) {
                            getView().onSaveSuccess(mChild.isManaged());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    private States findStateByName(ArrayList<States> items, String name) {
        States result = null;

        if (items != null && name != null) {
            for (States item : items) {
                if (item.getNameEnglish().equals(name) || item.getNameHindi().equals(name)) {
                    result = item;
                    break;
                }
            }
        }

        return result;
    }

    private States findStateById(ArrayList<States> items, String id) {
        States result = null;

        if (items != null) {
            for (States item : items) {
                if (item.getIdServer().equals(id)) {
                    result = item;
                    break;
                }
            }
        }

        return result;
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
}
