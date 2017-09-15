package com.nearit.ui_bindings.permissions;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RestrictTo;

import com.nearit.ui_bindings.ExtraConstants;

/**
 * Created by Federico Boschini on 29/08/17.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class PermissionsRequestIntentExtras implements Parcelable {

    private final boolean enableTapToClose;
    private final boolean autoStartRadar;
    private final boolean invisibleLayoutMode;
    private final boolean noBeacon;
    private final boolean nonBlockingBeacon;
    private final int headerDrawable;
    private final boolean noHeader;

    PermissionsRequestIntentExtras(
            boolean enableTapToClose,
            boolean autoStartRadar,
            boolean invisibleLayoutMode,
            boolean noBeacon,
            boolean nonBlockingBeacon,
            int headerDrawable,
            boolean noHeader) {
        this.enableTapToClose = enableTapToClose;
        this.autoStartRadar = autoStartRadar;
        this.invisibleLayoutMode = invisibleLayoutMode;
        this.noBeacon = noBeacon;
        this.nonBlockingBeacon = nonBlockingBeacon;
        this.headerDrawable = headerDrawable;
        this.noHeader = noHeader;
    }

    /**
     * Extract PermissionsRequestIntentExtras from an Intent.
     */
    static PermissionsRequestIntentExtras fromIntent(Intent intent) {
        return intent.getParcelableExtra(ExtraConstants.EXTRA_FLOW_PARAMS);
    }

    /**
     * Extract PermissionsRequestIntentExtras from a Bundle.
     */
    public static PermissionsRequestIntentExtras fromBundle(Bundle bundle) {
        return bundle.getParcelable(ExtraConstants.EXTRA_FLOW_PARAMS);
    }

    /**
     * Create a bundle containing this PermissionsRequestIntentExtras object as {@link
     * ExtraConstants#EXTRA_FLOW_PARAMS}.
     */
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ExtraConstants.EXTRA_FLOW_PARAMS, this);
        return bundle;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(enableTapToClose ? 1 : 0);
        dest.writeInt(autoStartRadar ? 1 : 0);
        dest.writeInt(invisibleLayoutMode ? 1 : 0);
        dest.writeInt(noBeacon ? 1 : 0);
        dest.writeInt(nonBlockingBeacon ? 1 : 0);
        dest.writeInt(headerDrawable);
        dest.writeInt(noHeader ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PermissionsRequestIntentExtras> CREATOR = new Creator<PermissionsRequestIntentExtras>() {
        @Override
        public PermissionsRequestIntentExtras createFromParcel(Parcel in) {
            boolean enableTapToClose = in.readInt() != 0;
            boolean autoStartRadar = in.readInt() != 0;
            boolean invisibleLayoutMode = in.readInt() != 0;
            boolean noBeacon = in.readInt() != 0;
            boolean nonBlockingBeacon = in.readInt() != 0;
            int headerDrawable = in.readInt();
            boolean noHeader = in.readInt() != 0;

            return new PermissionsRequestIntentExtras(
                    enableTapToClose,
                    autoStartRadar,
                    invisibleLayoutMode,
                    noBeacon,
                    nonBlockingBeacon,
                    headerDrawable,
                    noHeader);
        }

        @Override
        public PermissionsRequestIntentExtras[] newArray(int size) {
            return new PermissionsRequestIntentExtras[size];
        }
    };

    boolean isEnableTapToClose() {
        return enableTapToClose;
    }

    boolean isAutoStartRadar() {
        return autoStartRadar;
    }

    boolean isInvisibleLayoutMode() {
        return invisibleLayoutMode;
    }

    boolean isNoBeacon() {
        return noBeacon;
    }

    boolean isNonBlockingBeacon() {
        return nonBlockingBeacon;
    }

    int getHeaderDrawable() {
        return headerDrawable;
    }

    boolean isNoHeader() {
        return noHeader;
    }
}
