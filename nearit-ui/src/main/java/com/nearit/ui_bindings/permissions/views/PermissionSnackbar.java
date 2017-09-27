package com.nearit.ui_bindings.permissions.views;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nearit.ui_bindings.NearITUIBindings;
import com.nearit.ui_bindings.R;

/**
 * Created by Federico Boschini on 25/09/17.
 */

public class PermissionSnackbar extends RelativeLayout {

    public static final int NO_ICON = 0;
    ImageView btIcon, locIcon;
    TextView alertMessage;
    PermissionSnackbarButton okButton;
    String buttonText, alertMessageText;
    int btIconResId, locIconResId;
    final Context context;

    @Nullable
    private Activity activity;
    private int requestCode;

    public PermissionSnackbar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public PermissionSnackbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        obtainAttrs(attrs);
        init();
    }

    public PermissionSnackbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        obtainAttrs(attrs);
        init();
    }

    public void setActivity(@Nullable Activity activity, int requestCode) {
        this.activity = activity;
        this.requestCode = requestCode;
    }

    private void obtainAttrs(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.NearItUISnackbar,
                0, 0);
        try {
            buttonText = a.getString(R.styleable.NearItUISnackbar_snackbarButtonText);
            alertMessageText = a.getString(R.styleable.NearItUISnackbar_snackbarAlertText);
            btIconResId = a.getResourceId(R.styleable.NearItUISnackbar_snackbarBluetoothIcon, NO_ICON);
            locIconResId = a.getResourceId(R.styleable.NearItUISnackbar_snackbarLocationIcon, NO_ICON);
        } finally {
            a.recycle();
        }
    }

    private void init() {
        inflate(getContext(), R.layout.nearit_ui_layout_permission_snackbar, this);
        btIcon = (ImageView) findViewById(R.id.bluetooth_icon);
        locIcon = (ImageView) findViewById(R.id.location_icon);
        alertMessage = (TextView) findViewById(R.id.alert_message);
        okButton = (PermissionSnackbarButton) findViewById(R.id.ok_button);
        okButton.setClickable(true);

        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity != null) {
                    activity.startActivityForResult(
                            NearITUIBindings.getInstance(activity).createPermissionRequestIntentBuilder().invisibleLayoutMode().build()
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT),
                            requestCode
                    );
                }
            }
        });

        getContext().registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        getContext().registerReceiver(mReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        if (checkLocationPermission() && checkLocation() && checkBluetooth()) {
            this.setVisibility(GONE);
        } else {
            if (checkLocationPermission() && checkLocation()) {
                hideLocationIcon();
            } else {
                showLocationIcon();
            }
            if (checkBluetooth()) {
                hideBluetoothIcon();
            } else {
                showBluetoothIcon();
            }
        }
    }

    public void hideBluetoothIcon() {
        btIcon.setVisibility(GONE);
    }

    public void showBluetoothIcon() {
        btIcon.setVisibility(VISIBLE);
    }

    public void hideLocationIcon() {
        locIcon.setVisibility(GONE);
    }

    public void showLocationIcon() {
        locIcon.setVisibility(VISIBLE);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        okButton.setButtonText(buttonText);
        if (btIconResId != NO_ICON) {
            btIcon.setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(), btIconResId, null)
            );
        }
        if (locIconResId != NO_ICON) {
            locIcon.setImageDrawable(
                    ResourcesCompat.getDrawable(getResources(), locIconResId, null)
            );
        }
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) | locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean checkBluetooth() {
        return BluetoothAdapter.getDefaultAdapter() != null && BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            PermissionSnackbar.this.setVisibility(VISIBLE);

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (!checkBluetooth()) {
                    showBluetoothIcon();
                    if (checkLocation() && checkLocationPermission()) {
                        hideLocationIcon();
                    } else {
                        showLocationIcon();
                    }
                } else {
                    hideBluetoothIcon();
                    if (checkLocation() && checkLocationPermission()) {
                        PermissionSnackbar.this.setVisibility(GONE);
                    } else {
                        showLocationIcon();
                    }
                }
            }

            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(action)) {
                if (!checkLocation()) {
                    showLocationIcon();
                    if (!checkBluetooth()) {
                        showBluetoothIcon();
                    }
                } else {
                    if (checkLocationPermission()) {
                        hideLocationIcon();
                        if (checkBluetooth()) {
                            PermissionSnackbar.this.setVisibility(GONE);
                        } else {
                            showBluetoothIcon();
                        }
                    } else {
                        showLocationIcon();
                        if (checkBluetooth()) {
                            PermissionSnackbar.this.setVisibility(GONE);
                        } else {
                            showBluetoothIcon();
                        }

                    }
                }
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(mReceiver);
    }
}