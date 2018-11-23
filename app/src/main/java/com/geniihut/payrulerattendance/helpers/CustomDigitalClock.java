package com.geniihut.payrulerattendance.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.DigitalClock;
import android.widget.TextView;

import com.geniihut.payrulerattendance.R;

import java.util.Calendar;

/**
 * Created by macmini4 on 9/7/15.
 */
public class CustomDigitalClock extends android.support.v7.widget.AppCompatTextView {
    // FIXME: implement separate views for hours/minutes/seconds, so
    // proportional fonts don't shake rendering

    Calendar mCalendar;
    //    @SuppressWarnings("FieldCanBeLocal") // We must keep a reference to this observer
//    private FormatChangeObserver mFormatChangeObserver;
    public static final String FORMAT_LANDSCAPE = "MMM dd, yyyy hh:mm:ss aa";
    public static final String FORMAT_PORTRAIT = "MMM dd, yyyy \n hh:mm:ss aa";

    private Runnable mTicker;
    private Handler mHandler;

    private boolean mTickerStopped = false;

    String mFormat = FORMAT_LANDSCAPE;
    private boolean isRealTime = false;
    private boolean hasTime = false;

    protected long timeDifference = -1;

    public CustomDigitalClock(Context context) {
        super(context);
        initClock();
    }

    public CustomDigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock();
    }

    private void initClock() {
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }

//        mFormatChangeObserver = new FormatChangeObserver();
//        getContext().getContentResolver().registerContentObserver(
//                Settings.System.CONTENT_URI, true, mFormatChangeObserver);

//        setFormat();
    }

    @Override
    protected void onAttachedToWindow() {
//        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();
        setDefaultText();
        /**
         * requests a tick on the next hard-second boundary
         */

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        mTickerStopped = true;
    }

    private void setDefaultText() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setText(getResources().getString(R.string.date_default) + "  " + getResources().getString(R.string.time_default));
            mFormat = FORMAT_LANDSCAPE;
        } else {
            setText(getResources().getString(R.string.date_default) + "\n" + getResources().getString(R.string.time_default));
            mFormat = FORMAT_PORTRAIT;
        }
    }

    public void setDate(long ms) {
        mCalendar.setTimeInMillis(ms);
        setHasTime(true);
        timeDifference = Math.abs((mCalendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()));
        if (mTicker == null) {
            mTicker = new Runnable() {
                public void run() {
                    if (mTickerStopped) return;
//                mCalendar.setTimeInMillis(System.currentTimeMillis());
                    mCalendar.add(Calendar.SECOND, 1);
                    setText(DateFormat.format(mFormat, mCalendar));
                    invalidate();
                    long now = SystemClock.uptimeMillis();
                    long next = now + (1000 - now % 1000);
                    if (mHandler == null) mHandler = new Handler();
                    mHandler.postAtTime(mTicker, next);
                }
            };
            mTicker.run();
        }
    }

    public long getDate() {
        return mCalendar.getTime().getTime();
    }

    public void setFormat(String format) {
        mFormat = format;
        if (hasTime)
            setText(DateFormat.format(mFormat, mCalendar));
    }

    public boolean isRealTime() {
        return isRealTime;
    }

    public void setIsRealTime(boolean isRealTime) {
        this.isRealTime = isRealTime;
    }

    public boolean isHasTime() {
        return hasTime;
    }

    public void setHasTime(boolean hasTime) {
        this.hasTime = hasTime;
        if (!hasTime) {
            if(mHandler != null) mHandler.removeCallbacksAndMessages(null);
            mTicker = null;
            mTickerStopped = true;
            isRealTime = false;
            setDefaultText();
        } else {
            mTickerStopped = false;
        }
    }

    public boolean isNeededToSyncTime() {
        boolean flag = false;
        if (timeDifference != -1) {
            long currentTimeDifference = Math.abs((mCalendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()));
            if (currentTimeDifference > timeDifference + 30000) {
                setHasTime(false);
                flag = true;
            }
        }
        return flag;
    }

    //    private class FormatChangeObserver extends ContentObserver {
//        public FormatChangeObserver() {
//            super(new Handler());
//        }
//
//        @Override
//        public void onChange(boolean selfChange) {
//            //setFormat();
//        }
//    }

//    @Override
//    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
//        super.onInitializeAccessibilityEvent(event);
//        //noinspection deprecation
//        event.setClassName(DigitalClock.class.getName());
//    }
//
//    @Override
//    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
//        super.onInitializeAccessibilityNodeInfo(info);
//        //noinspection deprecation
//        info.setClassName(DigitalClock.class.getName());
//    }
}