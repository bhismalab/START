package com.reading.start.presentation.mvp.presenters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.domain.entity.Children;
import com.reading.start.domain.entity.Languages;
import com.reading.start.domain.entity.Parent;
import com.reading.start.domain.entity.States;
import com.reading.start.general.TLog;
import com.reading.start.presentation.mvp.holders.ParentViewHolder;
import com.reading.start.presentation.mvp.models.ParentModel;
import com.reading.start.presentation.mvp.views.ParentView;
import com.reading.start.presentation.ui.UiConstants;
import com.reading.start.tests.TestLog;
import com.reading.start.utils.Validation;

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

public class ParentPresenter extends BasePresenter<ParentView, ParentModel, ParentViewHolder> {

    public static final String TAG = ParentPresenter.class.getSimpleName();

    private final DateFormat mFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());

    private Children mChild;

    private int mParentId;

    private int mStateIndex = -1;

    private int mStateId = -1;

    private int mLanguageIndex = -1;

    private int mLanguageId = -1;

    private int mPreferableContactIndex = 0;

    private Context mContext;

    public ParentPresenter(Context context, Children child, int parentId) {
        super(true);
        setModel(new ParentModel(getRealm()));
        mChild = child;
        mParentId = parentId;
        mContext = context;
    }

    public ParentPresenter(Context context, int childId, int parentId) {
        super(true);
        setModel(new ParentModel(getRealm()));
        mChild = getModel().getChild(childId);
        mParentId = parentId;
        mContext = context;
    }

    @Override
    public void onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getViewHolder() != null) {
            RxView.clicks(getViewHolder().getActionBar().findViewById(R.id.next_button)).subscribe(aVoid -> getView().onNextPressed());
            RxView.clicks(getViewHolder().getActionBar().findViewById(R.id.save_button)).subscribe(aVoid -> getView().onSaveParentPressed());
            RxView.clicks(getViewHolder().getActionBar().findViewById(R.id.back_button)).subscribe(aVoid -> getView().onBackPressed());

            if (getViewHolder().getMale().isChecked()) {
                getViewHolder().getMale().setPaintFlags(getViewHolder().getMale().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }

            if (getViewHolder().getFemale().isChecked()) {
                getViewHolder().getFemale().setPaintFlags(getViewHolder().getMale().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }

            if (getViewHolder().getParent().isChecked()) {
                getViewHolder().getParent().setPaintFlags(getViewHolder().getMale().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }

            if (getViewHolder().getGuardian().isChecked()) {
                getViewHolder().getGuardian().setPaintFlags(getViewHolder().getMale().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }

            RxView.clicks(getViewHolder().getMale()).subscribe(aVoid -> {
                getViewHolder().getMale().setPaintFlags(getViewHolder().getMale().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                getViewHolder().getFemale().setPaintFlags(0);
            });

            RxView.clicks(getViewHolder().getFemale()).subscribe(aVoid -> {
                getViewHolder().getFemale().setPaintFlags(getViewHolder().getFemale().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                getViewHolder().getMale().setPaintFlags(0);
            });

            RxView.clicks(getViewHolder().getParent()).subscribe(aVoid -> {
                getViewHolder().getParent().setPaintFlags(getViewHolder().getParent().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                getViewHolder().getGuardian().setPaintFlags(0);
            });

            RxView.clicks(getViewHolder().getGuardian()).subscribe(aVoid -> {
                getViewHolder().getGuardian().setPaintFlags(getViewHolder().getGuardian().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                getViewHolder().getParent().setPaintFlags(0);
            });

            if (mChild.isManaged()) {
                getViewHolder().getSkip().setVisibility(View.GONE);
            } else {
                if (mParentId == UiConstants.PARENT_ID_2) {
                    getViewHolder().getSkip().setVisibility(View.VISIBLE);
                } else {
                    getViewHolder().getSkip().setVisibility(View.GONE);
                }

                getViewHolder().getSkip().setOnClickListener(v -> {
                    if (getViewHolder().getSkip().isChecked()) {
                        disableViews();
                    } else {
                        resetFields();
                        enableViews();
                    }

                    validate();
                });
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

            RxView.clicks(getViewHolder().getLanguage()).subscribe(aVoid -> {
                final ArrayList<Languages> list = getModel().getLanguages();
                CharSequence[] items = new CharSequence[list.size()];

                for (int i = 0; i < list.size(); i++) {
                    items[i] = isHindiLanguage() ? list.get(i).getNameHindi() : list.get(i).getNameEnglish();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setItems(items, (dialog, item) -> {
                    mLanguageIndex = item;
                    Languages selectedLanguages = findLanguageByName(list, items[item].toString());

                    if (selectedLanguages != null) {
                        try {
                            mLanguageId = Integer.parseInt(selectedLanguages.getIdServer());
                        } catch (Exception e) {
                            TLog.e(TAG, e);
                        }

                        getViewHolder().getLanguage().setText(isHindiLanguage() ?
                                selectedLanguages.getNameHindi() : selectedLanguages.getNameEnglish());
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            });

            validateFields();

            if (mChild.isManaged()) {
                getViewHolder().getActionBar().findViewById(R.id.save_button).setVisibility(View.VISIBLE);

                if (mParentId == UiConstants.PARENT_ID_1) {
                    getViewHolder().getActionBar().findViewById(R.id.next_button).setVisibility(View.VISIBLE);
                } else {
                    getViewHolder().getActionBar().findViewById(R.id.next_button).setVisibility(View.GONE);
                }
            } else {
                if (mParentId == UiConstants.PARENT_ID_1) {
                    getViewHolder().getActionBar().findViewById(R.id.save_button).setVisibility(View.GONE);
                    getViewHolder().getActionBar().findViewById(R.id.next_button).setVisibility(View.VISIBLE);
                }

                if (mParentId == UiConstants.PARENT_ID_2) {
                    getViewHolder().getActionBar().findViewById(R.id.save_button).setVisibility(View.VISIBLE);
                    getViewHolder().getActionBar().findViewById(R.id.next_button).setVisibility(View.GONE);
                }
            }

            if (mParentId == UiConstants.PARENT_ID_1) {
                getViewHolder().getPreferableContact().setVisibility(View.VISIBLE);
            } else {
                getViewHolder().getPreferableContact().setVisibility(View.GONE);
            }

            RxView.clicks(getViewHolder().getPreferableContact()).subscribe(aVoid -> {
                final ArrayList<Integer> listId = getModel().getPreferableContact();
                CharSequence[] items = new CharSequence[listId.size()];

                for (int i = 0; i < listId.size(); i++) {
                    items[i] = mContext.getString(listId.get(i));
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setItems(items, (dialog, item) -> {
                    mPreferableContactIndex = item;
                    getViewHolder().getPreferableContact().setText(items[item]);
                });
                AlertDialog alert = builder.create();
                alert.show();
            });

            getViewHolder().getAddress().setText(mChild.getAddress());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateParentInfo();
    }

    public void resetFields() {
        getViewHolder().getName().setText(null);
        getViewHolder().getSurname().setText(null);
        getViewHolder().getPhone().setText(null);
        getViewHolder().getEmail().setText(null);
        getViewHolder().getAddress().setText(mChild.getAddress());
    }

    public void validateFields() {
        validateName();
        validateSurname();
        validateAddress();
        validatePhone();
        validateEmail();
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
                .getName()).subscribe(textViewAfterTextChangeEvent -> validate());
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
                .getSurname()).subscribe(textViewAfterTextChangeEvent -> validate());
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
                .getAddress()).subscribe(textViewAfterTextChangeEvent -> validate());
    }

    public void validatePhone() {
        getViewHolder().getPhone().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!TextUtils.isEmpty(getViewHolder().getPhone().getText()) && !Validation.isValidPhoneNumber(getViewHolder().getPhone().getText().toString())) {
                    getView().onShowToast(AppCore.getInstance().getString(R.string.validation_incorrect_phone_number));
                }
            }
        });
        RxTextView.afterTextChangeEvents(getViewHolder()
                .getPhone()).subscribe(textViewAfterTextChangeEvent -> validate());
    }

    public void validateEmail() {
        getViewHolder().getEmail().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (!TextUtils.isEmpty(getViewHolder().getPhone().getText()) && !Validation.isValidEmail(getViewHolder().getEmail().getText().toString())) {
                    getView().onShowToast(AppCore.getInstance().getString(R.string.validation_incorrect_email));
                }
            }
        });
        RxTextView.afterTextChangeEvents(getViewHolder()
                .getEmail()).subscribe(textViewAfterTextChangeEvent -> validate());
    }

    public boolean validate() {
        if ((!TextUtils.isEmpty(getViewHolder().getName().getText()) &&
                !TextUtils.isEmpty(getViewHolder().getSurname().getText()) &&
                //!TextUtils.isEmpty(getViewHolder().getDateOfBirth().getText()) &&
                !TextUtils.isEmpty(getViewHolder().getAddress().getText())
                && (TextUtils.isEmpty(getViewHolder().getPhone().getText()) || (!TextUtils.isEmpty(getViewHolder().getPhone().getText()) && Validation.isValidPhoneNumber(getViewHolder().getPhone().getText().toString())))
                && (TextUtils.isEmpty(getViewHolder().getEmail().getText()) || (!TextUtils.isEmpty(getViewHolder().getEmail().getText()) && Validation.isValidEmail(getViewHolder().getEmail().getText().toString()))))
                || getViewHolder().getSkip().isChecked()) {
            getViewHolder().getActionBar().findViewById(R.id.next_button).setEnabled(true);
            getViewHolder().getActionBar().findViewById(R.id.save_button).setEnabled(true);
            return true;
        } else {
            getViewHolder().getActionBar().findViewById(R.id.next_button).setEnabled(false);
            getViewHolder().getActionBar().findViewById(R.id.save_button).setEnabled(false);
            return false;
        }
    }

    public void saveChildren(Children children) {
        if (children != null) {
            final Observable<Children> observable = getModel().getSaveChildObservable(children);

            getSubscriptions().add(observable
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Children>() {
                        @Override
                        public void onNext(Children data) {
                            if (data != null) {
                                getView().onSave(true, data);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            getView().onSave(false, null);
                        }

                        @Override
                        public void onComplete() {
                        }
                    })
            );
        } else {
            getView().onSave(false, null);
        }
    }

    public void saveParentInfo() {
        Parent parent = mParentId == UiConstants.PARENT_ID_2 ? mChild.getParentSecond() : mChild.getParentFirst();

        String childRelationship = "";

        if (getViewHolder().getParent().isChecked()) {
            childRelationship = Constants.PARENT;
        } else if (getViewHolder().getGuardian().isChecked()) {
            childRelationship = Constants.GUARDIAN;
        }

        String name = getViewHolder().getName().getText().toString();
        String surname = getViewHolder().getSurname().getText().toString();
        String patronymic = "";
        String state = String.valueOf(mStateId);
        String preferableContact = getViewHolder().getPreferableContact().getText().toString();

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
            //TLog.e(TAG, e);
        }

        String spokenLanguage = String.valueOf(mLanguageId);
        String phone = getViewHolder().getPhone().getText().toString();
        String email = getViewHolder().getEmail().getText().toString();

        String addBy = AppCore.getInstance().getPreferences().getLoginWorker();
        String modBy = AppCore.getInstance().getPreferences().getLoginWorker();

        long addDateTime = (parent == null || parent.getAddDateTime() == 0)
                ? Calendar.getInstance().getTimeInMillis() : parent.getAddDateTime();
        long modDateTime = Calendar.getInstance().getTimeInMillis();
        boolean isDeleted = false;

        if (mParentId == UiConstants.PARENT_ID_2 && getViewHolder().getSkip().isChecked()) {
            childRelationship = "";
            name = "";
            surname = "";
            patronymic = "";
            state = "";
            address = "";
            gender = "";
            birthDate = 0;
            spokenLanguage = "";
            phone = "";
            email = "";
            addDateTime = 0;
            addBy = "";
            modDateTime = 0;
            modBy = "";
            isDeleted = false;
        }

        final Observable<Children> observable = getModel().getSaveParentObservable(mChild, mParentId,
                childRelationship, name, surname, patronymic, state, address, gender, birthDate,
                spokenLanguage, phone, email, addDateTime, addBy, modDateTime, modBy, isDeleted, preferableContact);

        getSubscriptions().add(observable
                .subscribeOn(AndroidSchedulers.mainThread())

                .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Children>() {
                    @Override
                    public void onNext(Children children) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        getView().onSaveParentSuccess(mChild.isManaged());
                    }
                }));
    }

    private void updateParentInfo() {
        Parent parent = mParentId == UiConstants.PARENT_ID_2 ? mChild.getParentSecond() : mChild.getParentFirst();

        if (parent != null) {
            getViewHolder().getName().setText(parent.getName());
            getViewHolder().getSurname().setText(parent.getSurname());

            if (parent.getGender() != null) {
                if (parent.getGender().equals(Constants.MALE)) {
                    getViewHolder().getMale().setChecked(true);
                    getViewHolder().getMale().setPaintFlags(getViewHolder().getMale().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                } else if (parent.getGender().equals(Constants.FEMALE)) {
                    getViewHolder().getFemale().setChecked(true);
                    getViewHolder().getFemale().setPaintFlags(getViewHolder().getFemale().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                }
            }

            if (parent.getChildRelationship() != null) {
                if (parent.getChildRelationship().equals(Constants.PARENT)) {
                    getViewHolder().getParent().setChecked(true);
                    getViewHolder().getParent().setPaintFlags(getViewHolder().getParent().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                } else if (parent.getChildRelationship().equals(Constants.GUARDIAN)) {
                    getViewHolder().getGuardian().setChecked(true);
                    getViewHolder().getGuardian().setPaintFlags(getViewHolder().getGuardian().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                }
            }

            try {
                if (parent.getBirthDate() > 0) {
                    String value = mFormat.format(new Date(parent.getBirthDate()));
                    getViewHolder().getDateOfBirth().setText(value);
                }
            } catch (Exception e) {
                TLog.e(TAG, e);
            }

            updateLanguage();
            updateState();
            updatePreferableContact();

            getViewHolder().getAddress().setText(parent.getAddress());
            getViewHolder().getPhone().setText(parent.getPhone());
            getViewHolder().getEmail().setText(parent.getEmail());
        } else {
            if (mChild != null) {
                int stateID = 0;

                try {
                    stateID = Integer.parseInt(mChild.getState());
                } catch (Exception e) {
                    TLog.e(TAG, e);
                }

                ArrayList<States> states = getModel().getStates();
                States state = findStateById(states, String.valueOf(stateID));

                if (state != null) {
                    mStateIndex = states.indexOf(state);
                    mStateId = Integer.parseInt(state.getIdServer());
                    getViewHolder().getState().setText(isHindiLanguage() ? state.getNameHindi() : state.getNameEnglish());
                }
            }

            ArrayList<Languages> list = getModel().getLanguages();
            Languages languages = findLanguageByName(list, Constants.DEFAULT_LANGUAGE);

            if (languages != null) {
                mLanguageIndex = list.indexOf(languages);
                mLanguageId = Integer.parseInt(languages.getIdServer());
                getViewHolder().getLanguage().setText(isHindiLanguage() ? languages.getNameHindi() : languages.getNameEnglish());
            }
        }
    }

    private void updateLanguage() {
        Parent parent = mParentId == UiConstants.PARENT_ID_2 ? mChild.getParentSecond() : mChild.getParentFirst();

        if (parent != null && parent.getSpokenLanguage() != null) {
            ArrayList<Languages> list = getModel().getLanguages();

            int languageId = -1;

            try {
                languageId = Integer.parseInt(parent.getSpokenLanguage());
            } catch (Exception e) {
                TLog.e(TAG, e);
            }

            if (languageId != -1) {
                Languages language = findLanguageById(list, String.valueOf(languageId));

                if (language != null) {
                    mLanguageIndex = languageId;
                    mLanguageId = Integer.parseInt(language.getIdServer());
                    getViewHolder().getLanguage().setText(isHindiLanguage() ? language.getNameHindi() : language.getNameEnglish());
                }
            } else {
                Languages language = findLanguageByName(list, Constants.DEFAULT_LANGUAGE);

                if (language != null) {
                    mLanguageIndex = languageId;
                    mLanguageId = Integer.parseInt(language.getIdServer());
                    getViewHolder().getLanguage().setText(isHindiLanguage() ? language.getNameHindi() : language.getNameEnglish());
                }
            }
        }
    }

    private void updateState() {
        Parent parent = mParentId == UiConstants.PARENT_ID_2 ? mChild.getParentSecond() : mChild.getParentFirst();

        if (parent != null && parent.getState() != null) {
            ArrayList<States> list = getModel().getStates();
            int stateId = -1;

            try {
                stateId = Integer.parseInt(parent.getState());
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

    private void updatePreferableContact() {
        Parent parent = mParentId == UiConstants.PARENT_ID_2 ? mChild.getParentSecond() : mChild.getParentFirst();

        if (parent != null) {
            int index = -1;
            ArrayList<Integer> listId = getModel().getPreferableContact();
            CharSequence[] items = new CharSequence[listId.size()];

            for (int i = 0; i < listId.size(); i++) {
                items[i] = mContext.getString(listId.get(i));
            }

            for (int i = 0; i < items.length; i++) {
                if (items[i].equals(parent.getPreferableContact())) {
                    index = i;
                    break;
                }
            }

            mPreferableContactIndex = index;

            if (mPreferableContactIndex != -1) {
                getViewHolder().getPreferableContact().setText(parent.getPreferableContact());
            } else {
                mPreferableContactIndex = 0;
                getViewHolder().getPreferableContact().setText(items[mPreferableContactIndex]);
            }
        }
    }

    private void disableViews() {
        getViewHolder().getName().setEnabled(false);
        getViewHolder().getSurname().setEnabled(false);
        getViewHolder().getMale().setEnabled(false);
        getViewHolder().getFemale().setEnabled(false);
        getViewHolder().getParent().setEnabled(false);
        getViewHolder().getGuardian().setEnabled(false);
        getViewHolder().getDateOfBirth().setEnabled(false);
        getViewHolder().getLanguage().setEnabled(false);
        getViewHolder().getState().setEnabled(false);
        getViewHolder().getPreferableContact().setEnabled(false);
        getViewHolder().getAddress().setEnabled(false);
        getViewHolder().getPhone().setEnabled(false);
        getViewHolder().getEmail().setEnabled(false);
    }

    private void enableViews() {
        getViewHolder().getName().setEnabled(true);
        getViewHolder().getSurname().setEnabled(true);
        getViewHolder().getMale().setEnabled(true);
        getViewHolder().getFemale().setEnabled(true);
        getViewHolder().getParent().setEnabled(true);
        getViewHolder().getGuardian().setEnabled(true);
        getViewHolder().getDateOfBirth().setEnabled(true);
        getViewHolder().getLanguage().setEnabled(true);
        getViewHolder().getState().setEnabled(true);
        getViewHolder().getPreferableContact().setEnabled(true);
        getViewHolder().getAddress().setEnabled(true);
        getViewHolder().getPhone().setEnabled(true);
        getViewHolder().getEmail().setEnabled(true);
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

    private Languages findLanguageByName(ArrayList<Languages> items, String name) {
        Languages result = null;

        if (items != null && name != null) {
            for (Languages item : items) {
                if (item.getNameEnglish().equals(name) || item.getNameHindi().equals(name)) {
                    result = item;
                    break;
                }
            }
        }

        return result;
    }

    private Languages findLanguageById(ArrayList<Languages> items, String id) {
        Languages result = null;

        if (items != null && id != null) {
            for (Languages item : items) {
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
