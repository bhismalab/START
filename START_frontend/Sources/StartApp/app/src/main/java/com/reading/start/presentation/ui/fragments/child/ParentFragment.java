package com.reading.start.presentation.ui.fragments.child;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.databinding.FragmentParentBinding;
import com.reading.start.domain.entity.Children;
import com.reading.start.general.TLog;
import com.reading.start.presentation.mvp.holders.ParentViewHolder;
import com.reading.start.presentation.mvp.presenters.ParentPresenter;
import com.reading.start.presentation.mvp.views.ParentView;
import com.reading.start.presentation.ui.UiConstants;
import com.reading.start.presentation.ui.activities.ChildActivity;
import com.reading.start.presentation.ui.dialogs.DialogConsent;
import com.reading.start.presentation.ui.dialogs.DialogConsentSign;
import com.reading.start.presentation.ui.dialogs.DialogConsentSignSuccess;
import com.reading.start.presentation.ui.fragments.base.BaseActionBarFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Represent screen for add and edit parent info.
 */
public class ParentFragment extends BaseActionBarFragment implements ParentView {

    public static final String TAG = ParentFragment.class.getSimpleName();

    private ParentPresenter mPresenter;

    private FragmentParentBinding mBinding;

    private ParentViewHolder mViewHolder;

    private Context mContext;

    private DatePickerDialog mDatePickerDialog;

    private SimpleDateFormat mDateFormatter;

    private ChildActivity mActivity;

    private int mChildId = -1;

    private Children mChild;

    private int mIdParent;

    private boolean mIsNextPressed = false;

    private boolean mIsBackPressed = false;

    public ParentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mContext = inflater.getContext();
        mActivity = getChildActivity();

        if (getArguments() != null) {
            if (getArguments().containsKey(UiConstants.CHILD_ID)) {
                mChildId = getArguments().getInt(UiConstants.CHILD_ID);
            } else {
                mChild = mActivity.getChild();
            }

            mIdParent = getArguments().getInt(UiConstants.PARENT_ID);
        }

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_parent, container, false);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bg_small);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mBinding.root.setBackground(bitmapDrawable);

        //init presenter
        mViewHolder = new ParentViewHolder(mBinding.actionBar, mBinding.parentName,
                mBinding.parentSurname, mBinding.maleRadioButton, mBinding.femaleRadioButton,
                mBinding.parentRadioButton, mBinding.guardianRadioButton, mBinding.dateOfBirth,
                mBinding.language, mBinding.state, mBinding.parentAddress, mBinding.phone,
                mBinding.emailAddress, mBinding.actionBar.findViewById(R.id.checkbox_skip),
                mBinding.preferableContact);

        mPresenter = mChild == null ? new ParentPresenter(mActivity, mChildId, mIdParent)
                : new ParentPresenter(mActivity, mChild, mIdParent);
        mPresenter.init(this, mViewHolder);
        mPresenter.onCreate(inflater, container, savedInstanceState);

        updateTitle();

        // Date
        mDateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        updateDateTimeField();

        initActionBar(mBinding.actionBar);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.navigation_button).setVisibility(View.GONE);

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

    protected ChildActivity getChildActivity() {
        if (getActivity() instanceof ChildActivity) {
            return (ChildActivity) getActivity();
        } else {
            return null;
        }
    }

    /**
     * Updates title
     */
    private void updateTitle() {
        if (mIdParent == 1) {
            TextView titleActionBar = mBinding.actionBar.findViewById(R.id.text_actionbar);

            if (mChildId == -1) {
                titleActionBar.setText(R.string.action_bar_add_first_parent);
            } else {
                titleActionBar.setText(R.string.action_bar_edit_first_parent);
            }
        } else {
            TextView titleActionBar = mBinding.actionBar.findViewById(R.id.text_actionbar);

            if (mChildId == -1) {
                titleActionBar.setText(R.string.action_bar_add_second_parent);
            } else {
                titleActionBar.setText(R.string.action_bar_edit_second_parent);
            }
        }
    }

    /**
     * Updates date
     */
    private void updateDateTimeField() {
        mBinding.dateOfBirth.setOnClickListener(view -> {
            Calendar newCalendar = Calendar.getInstance();

            try {
                String value = mBinding.dateOfBirth.getText().toString();
                long defaultTime = 0;

                if (value == null || value.isEmpty()) {
                    defaultTime = Constants.DEFAULT_DATE_PARENT;
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
        newCalendar.setTimeInMillis(Constants.DEFAULT_DATE_PARENT);

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
            mPresenter.validate();
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        mDatePickerDialog.getDatePicker().setCalendarViewShown(false);
        mDatePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
    }

    /**
     * Raises when pressed next button
     */
    @Override
    public void onNextPressed() {
        mIsNextPressed = true;
        mIsBackPressed = false;
        mPresenter.saveParentInfo();
    }

    /**
     * Raises when pressed back
     */
    @Override
    public void onBackPressed() {
        mIsBackPressed = true;
        mPresenter.saveParentInfo();
    }

    /**
     * Raises when save button pressed
     */
    @Override
    public void onSaveParentPressed() {
        mIsNextPressed = false;
        mPresenter.saveParentInfo();
    }

    /**
     * Raises when parent save success
     */
    @Override
    public void onSaveParentSuccess(boolean isEdit) {
        if (mIsBackPressed) {
            super.onBackPressed();
        } else if (!mIsNextPressed && isEdit) {
            getChildActivity().openMySurveysFragment();
        } else {
            final ChildActivity activity = getChildActivity();

            if (activity != null) {
                if (mIdParent == UiConstants.PARENT_ID_1) {
                    activity.openAddParentFragment(UiConstants.PARENT_ID_2);
                } else {
                    showSignatureDialog();
                }
            }
        }

        mIsBackPressed = false;
    }

    /**
     * Raises when need save child when parents
     */
    @Override
    public void onSave(boolean success, final Children children) {
        if (success) {
            final ChildActivity activity = getChildActivity();

            if (activity != null) {
                getChildActivity().setChild(children);

                DialogConsentSignSuccess dialog = DialogConsentSignSuccess.getInstance(getString(R.string.consent_dialog_success_header_text),
                        getText(R.string.consent_dialog_success_message_text).toString(), new DialogConsentSignSuccess.DialogConsentSignSuccessListener() {

                            @Override
                            public void onMySurveys() {
                                Intent intent = new Intent();
                                intent.putExtra(UiConstants.CHILD_ID, children.getId());
                                getChildActivity().setResult(UiConstants.CHILD_ACTIVITY_RESULT_OPEN_MY_SURVEY, intent);
                                getChildActivity().finish();
                            }

                            @Override
                            public void onStartSurvey() {
                                Intent intent = new Intent();
                                intent.putExtra(UiConstants.CHILD_ID, children.getId());
                                getChildActivity().setResult(UiConstants.CHILD_ACTIVITY_RESULT_OPEN_START_SURVEY, intent);
                                getChildActivity().finish();
                            }
                        });

                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), TAG);
            }
        }
    }

    @Override
    public void onShowToast(String message) {
        if (isVisible() && isResumed()) {
            Toast.makeText(getChildActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show signature dialog
     */
    private void showSignatureDialog() {
        DialogConsent dialog = DialogConsent.getInstance(getString(R.string.consent_dialog_header_text), new DialogConsent.DialogConsentListener() {
            @Override
            public void onAccept() {
                boolean singleParent = false;
                String parentName1 = mChild.getParentFirst().getName();
                String parentName2 = null;

                if (mChild.getParentSecond() == null || mChild.getParentSecond().getName().isEmpty()) {
                    singleParent = true;
                } else {
                    parentName2 = mChild.getParentSecond().getName();
                }

                DialogConsentSign dialogConsentConfirm = DialogConsentSign.getInstance(getString(R.string.consent_sign_dialog_header_text),
                        singleParent, parentName1, parentName2,
                        new DialogConsentSign.DialogConsentSignListener() {
                            @Override
                            public void onSave(String signature_1_Base64, String signature_2_Base64) {
                                if (signature_1_Base64 != null) {
                                    if (mChild.getParentFirst() != null) {
                                        mChild.getParentFirst().setSignatureScan(signature_1_Base64);
                                    }
                                }

                                if (signature_2_Base64 != null) {
                                    if (mChild.getParentSecond() != null) {
                                        mChild.getParentSecond().setSignatureScan(signature_2_Base64);
                                    }
                                }

                                mPresenter.saveChildren(mChild);
                            }

                            @Override
                            public void onBack() {
                                showSignatureDialog();
                            }
                        });

                dialogConsentConfirm.setCancelable(false);
                dialogConsentConfirm.show(getFragmentManager(), TAG);
            }

            @Override
            public void onBack() {
                // no need to implement
            }
        });

        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), TAG);
    }
}
