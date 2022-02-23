package com.reading.start.presentation.ui.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.reading.start.AppPermissions;
import com.reading.start.R;
import com.reading.start.general.TLog;
import com.reading.start.presentation.ui.UiConstants;

/**
 * Contains logic for working with runtime/dangerous permissions.
 */
public abstract class BasePermissionActivity extends BaseLanguageActivity {

    private static final String TAG = BasePermissionActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMultiplePermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UiConstants.PERMISSION_REQUEST_CODE) {
            onAllowPermissionOk();
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == UiConstants.PERMISSION_REQUEST_CODE) {
            if (grantResults.length == AppPermissions.getPermissionList().length) {
                boolean result = true;

                for (int item : grantResults) {
                    if (item != PackageManager.PERMISSION_GRANTED) {
                        result = false;
                        break;
                    }
                }

                if (result) {
                    onAllowPermissionOk();
                } else {
                    if (!onPermissionDenied()) {
                        showPermissionErrorDialog();
                    }
                }
            } else {
                if (!onPermissionDenied()) {
                    showPermissionErrorDialog();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Request and check permissions for the application.
     */
    protected void requestMultiplePermissions() {
        if (AppPermissions.shouldShowRequestPermissionRationale(this)) {
            if (!onPermissionDenied()) {
                showPermissionDeniedErrorDialog();
            } else {
                ActivityCompat.requestPermissions(this, AppPermissions.getPermissionList(), UiConstants.PERMISSION_REQUEST_CODE);
            }
        } else {
            if (!AppPermissions.checkPermissionGranted(this)) {
                ActivityCompat.requestPermissions(this, AppPermissions.getPermissionList(), UiConstants.PERMISSION_REQUEST_CODE);
            }
        }
    }

    /**
     * Open application setting for swith on/off permissions.
     */
    private void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, UiConstants.PERMISSION_REQUEST_CODE);
    }

    /**
     * Show error when permission not allowed.
     */
    private void showPermissionErrorDialog() {
        runOnUiThread(() -> {
            try {
                if (!BasePermissionActivity.this.isFinishing()) {
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(BasePermissionActivity.this);
                    builder.onPositive((dialog, which) -> {
                        onAllowPermissionOk();
                        dialog.dismiss();
                    });
                    builder.onNegative((dialog, which) -> dialog.dismiss());
                    builder.positiveText(getString(R.string.button_allow));
                    builder.negativeText(getString(R.string.button_cancel));
                    builder.content(getString(R.string.dialog_permission_error_message));
                    builder.title(getString(R.string.dialog_permission_error_title));
                    builder.theme(Theme.LIGHT);
                    builder.show();
                }
            } catch (Exception e) {
                TLog.e(TAG, "showPermissionErrorDialog", e);
            }
        });
    }

    /**
     * Show dialog when permission denied.
     */
    private void showPermissionDeniedErrorDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!BasePermissionActivity.this.isFinishing()) {
                        MaterialDialog.Builder builder = new MaterialDialog.Builder(BasePermissionActivity.this);
                        builder.onPositive((dialog, which) -> {
                            dialog.dismiss();
                            openApplicationSettings();
                        });
                        builder.onNegative((dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        });
                        builder.positiveText(getString(R.string.button_open_settings));
                        builder.negativeText(getString(R.string.button_cancel));
                        builder.content(getString(R.string.dialog_permission_denied_error_message));
                        builder.title(getString(R.string.dialog_permission_error_title));
                        builder.theme(Theme.LIGHT);
                        builder.show();
                    }
                } catch (Exception e) {
                    TLog.e(TAG, "showPermissionDeniedErrorDialog", e);
                }
            }
        });
    }

    /**
     * Notify that all permissions allowed.
     */
    protected abstract void onAllowPermissionOk();

    /**
     * Notify that permission denied.
     */
    protected abstract boolean onPermissionDenied();
}
