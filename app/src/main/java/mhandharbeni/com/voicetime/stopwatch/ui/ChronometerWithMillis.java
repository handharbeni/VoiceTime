package mhandharbeni.com.voicetime.stopwatch.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;

import mhandharbeni.com.voicetime.chronometer.BaseChronometer;

public class ChronometerWithMillis extends BaseChronometer {
    private static final String TAG = "ChronometerWithMillis";

    public ChronometerWithMillis(Context context) {
        this(context, null, 0);
    }

    public ChronometerWithMillis(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChronometerWithMillis(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public ChronometerWithMillis(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setShowCentiseconds(true, false);
    }
}
