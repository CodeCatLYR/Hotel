package com.tgcyber.hotelmobile.widget;

import android.view.View;

import com.tgcyber.hotelmobile.widget.ScrollDetectors.ScrollDetector;

public interface ScrollDetectorFactory {
    /**
     * Create new instance of {@link ScrollDetector} based on the parameter v
     *
     * @param v
     * @return
     */
    public ScrollDetector newScrollDetector(View v);
}
