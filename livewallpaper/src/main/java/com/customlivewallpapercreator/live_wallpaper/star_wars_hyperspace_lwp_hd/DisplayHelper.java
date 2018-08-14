/*
 * Â© 2015 Custom Live Wallpaper Creator. All Rights Reserved.
 */

package com.customlivewallpapercreator.live_wallpaper.star_wars_hyperspace_lwp_hd;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class DisplayHelper {

    public final static int DISPLAY_ORIENTATION_LOCK_AUTO_ROTATE = 0;
    public final static int DISPLAY_ORIENTATION_LOCK_PORTRAIT = 1;
    public final static int DISPLAY_ORIENTATION_LOCK_LANDSCAPE = 2;

    private Point mDisplaySize;
    private int mDisplayOrientation;
    private int mDisplayOrientationLock;

    private Display mDisplay;

    public DisplayHelper(Context mContext, int orientationLock) {
        mDisplaySize = new Point();
        mDisplayOrientationLock = orientationLock;

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = wm.getDefaultDisplay();

        getLatestDisplaySizeReadings();
        getLatestDisplayOrientationReading();

        if (isOrientationLocked() && !isLockedOrientationSameAsDevice()) {
            int oldX = getWidth();
            mDisplaySize.x = getHeight();
            mDisplaySize.y = oldX;
        }
    }

    public void refresh() {
        getLatestDisplayOrientationReading();

        if (isOrientationLocked()) {
            return;
        }

        getLatestDisplaySizeReadings();
    }

    private void getLatestDisplaySizeReadings() {
        if (Build.VERSION.SDK_INT >= 17) {
            mDisplay.getRealSize(mDisplaySize);
        } else if (Build.VERSION.SDK_INT >= 13) {
            mDisplay.getSize(mDisplaySize);
        } else {
            mDisplaySize.x = mDisplay.getWidth();
            mDisplaySize.y = mDisplay.getHeight();
        }
    }

    private void getLatestDisplayOrientationReading() {
        mDisplayOrientation = mDisplay.getRotation();
    }

    public int getWidth() {
        return mDisplaySize.x;
    }

    public int getHeight() {
        return mDisplaySize.y;
    }

    public int getOrientation() {
        return mDisplayOrientation;
    }

    public boolean isPortrait() {
        if (mDisplayOrientation == Surface.ROTATION_0
            || mDisplayOrientation == Surface.ROTATION_180
        ) {
            return true;
        }

        return false;
    }

    public boolean isLandscape() {
        if (mDisplayOrientation == Surface.ROTATION_90
            || mDisplayOrientation == Surface.ROTATION_270
        ) {
            return true;
        }

        return false;
    }

    public int getOrientationAngle() {
        if (mDisplayOrientationLock == DISPLAY_ORIENTATION_LOCK_PORTRAIT) {
            if (mDisplayOrientation == Surface.ROTATION_90) {
                return -90;
            }

            if (mDisplayOrientation == Surface.ROTATION_270) {
                return 90;
            }
        }

        if (mDisplayOrientationLock == DISPLAY_ORIENTATION_LOCK_LANDSCAPE) {
            if (mDisplayOrientation == Surface.ROTATION_0) {
                return -90;
            }

            if (mDisplayOrientation == Surface.ROTATION_180) {
                return 90;
            }
        }

        return 0;
    }

    public boolean isOrientationLocked() {
        if (mDisplayOrientationLock != DISPLAY_ORIENTATION_LOCK_AUTO_ROTATE) {
            return true;
        }

        return false;
    }

    public boolean isLockedOrientationSameAsDevice() {
        if (mDisplayOrientationLock == DISPLAY_ORIENTATION_LOCK_PORTRAIT && isPortrait()) {
            return true;
        }

        if (mDisplayOrientationLock == DISPLAY_ORIENTATION_LOCK_LANDSCAPE && isLandscape()) {
            return true;
        }

        return false;
    }
}
