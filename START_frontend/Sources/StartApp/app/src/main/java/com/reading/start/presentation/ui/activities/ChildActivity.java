package com.reading.start.presentation.ui.activities;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.WindowManager;

import com.reading.start.AppCore;
import com.reading.start.R;
import com.reading.start.domain.entity.Children;
import com.reading.start.general.TLog;
import com.reading.start.presentation.ui.UiConstants;
import com.reading.start.presentation.ui.fragments.child.ChildFragment;
import com.reading.start.presentation.ui.fragments.child.ParentFragment;

/**
 * Screen that contains logic for create new or edit child info.
 */
public class ChildActivity extends BasePermissionActivity {
    public static final String TAG = ChildActivity.class.getSimpleName();

    private Children mChild;

    private int mChildId = -1;

    private Location mCurrentLocation = null;

    private LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            try {
                if (location != null) {
                    mCurrentLocation = location;
                }
            } catch (Exception e) {
                TLog.e(TAG, "onLocationChanged", e);
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // no need to implement
        }

        public void onProviderEnabled(String provider) {
            // no need to implement
        }

        public void onProviderDisabled(String provider) {
            // no need to implement
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(UiConstants.CHILD_ID)) {
            mChildId = getIntent().getExtras().getInt(UiConstants.CHILD_ID);
        } else {
            mChild = new Children();
        }

        openAddChildFragment();
    }

    public Children getChild() {
        return mChild;
    }

    public void setChild(Children child) {
        mChild = child;
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    /**
     * Open add child screen.
     */
    public void openAddChildFragment() {
        Fragment frag = getFragmentManager().findFragmentById(R.id.content_frame);
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();

        if (mChild == null) {
            bundle.putInt(UiConstants.CHILD_ID, mChildId);
        }

        if (frag != null && frag instanceof ChildFragment) {
            frag.setArguments(bundle);
            ft.attach(frag);
        } else {
            frag = new ChildFragment();
            frag.setArguments(bundle);
            ft.replace(R.id.content_frame, frag);
        }

        ft.commit();
    }

    /**
     * Open add parent screen.
     */
    public void openAddParentFragment(int parentId) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();

        if (mChild == null) {
            bundle.putInt(UiConstants.CHILD_ID, mChildId);
        }

        bundle.putInt(UiConstants.PARENT_ID, parentId);

        Fragment frag = new ParentFragment();
        frag.setArguments(bundle);
        ft.replace(R.id.content_frame, frag);
        ft.addToBackStack(ParentFragment.class.getSimpleName());
        ft.commit();
    }

    /**
     * Open MySurveys screen.
     */
    public void openMySurveysFragment() {
        setResult(UiConstants.CHILD_ACTIVITY_RESULT_NO_ACTION);
        finish();
    }

    @Override
    protected void onPause() {
        stopDetectLocation();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startDetectLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onAllowPermissionOk() {
        // no need to implement
    }

    @Override
    protected boolean onPermissionDenied() {
        return false;
    }

    /**
     * Start detect location
     */
    private void startDetectLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager = (LocationManager) AppCore.getInstance().getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
            }
        } catch (Exception e) {
            TLog.e(TAG, "startDetectLocation", e);
        }
    }

    /**
     * Stop detect location
     */
    private void stopDetectLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager = (LocationManager) AppCore.getInstance().getSystemService(Context.LOCATION_SERVICE);
                locationManager.removeUpdates(mLocationListener);
            }
        } catch (Exception e) {
            TLog.e(TAG, "startDetectLocation", e);
        }
    }
}
