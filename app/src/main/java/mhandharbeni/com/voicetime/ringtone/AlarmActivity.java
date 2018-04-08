package mhandharbeni.com.voicetime.ringtone;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.view.ViewGroup;

import mhandharbeni.com.voicetime.R;
import mhandharbeni.com.voicetime.alarms.Alarm;
import mhandharbeni.com.voicetime.alarms.misc.AlarmController;
import mhandharbeni.com.voicetime.ringtone.playback.AlarmRingtoneService;
import mhandharbeni.com.voicetime.ringtone.playback.RingtoneService;
import mhandharbeni.com.voicetime.util.TimeFormatUtils;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmActivity extends RingtoneActivity<Alarm> {
    private static final String TAG = "AlarmActivity";

    private AlarmController mAlarmController;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlarmController = new AlarmController(this, null);
        // TODO: If the upcoming alarm notification isn't present, verify other notifications aren't affected.
        // This could be the case if we're starting a new instance of this activity after leaving the first launch.
        mAlarmController.removeUpcomingAlarmNotification(getRingingObject());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void finish() {
        super.finish();
        // If the presently ringing alarm is about to be superseded by a successive alarm,
        // this, unfortunately, will cancel the missed alarm notification for the presently
        // ringing alarm.
        //
        // A workaround is to override onNewIntent() and post the missed alarm notification again,
        // AFTER calling through to its base implementation, because it calls finish().
        mNotificationManager.cancel(TAG, getRingingObject().getIntId());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // -------------- TOneverDO: precede super ---------------
        // Even though the base implementation calls finish() on this instance and starts a new
        // instance, this instance will still be alive with all of its member state intact at
        // this point. So this notification will still refer to the Alarm that was just missed.
        postMissedAlarmNote();
    }

    @Override
    protected Class<? extends RingtoneService> getRingtoneServiceClass() {
        return AlarmRingtoneService.class;
    }

    @Override
    protected CharSequence getHeaderTitle() {
        return getRingingObject().label();
    }

    @Override
    protected void getHeaderContent(ViewGroup parent) {
        // TODO: Consider applying size span on the am/pm label
        getLayoutInflater().inflate(R.layout.content_header_alarm_activity, parent, true);
    }

    @Override
    protected int getAutoSilencedText() {
        return R.string.alarm_auto_silenced_text;
    }

    @Override
    protected int getLeftButtonText() {
        return R.string.snooze;
    }

    @Override
    protected int getRightButtonText() {
        return R.string.dismiss;
    }

    @Override
    protected int getLeftButtonDrawable() {
        return R.drawable.ic_snooze_48dp;
    }

    @Override
    protected int getRightButtonDrawable() {
        return R.drawable.ic_dismiss_alarm_48dp;
    }

    @Override
    protected void onLeftButtonClick() {
        mAlarmController.snoozeAlarm(getRingingObject());
        // Can't call dismiss() because we don't want to also call cancelAlarm()! Why? For example,
        // we don't want the alarm, if it has no recurrence, to be turned off right now.
        stopAndFinish();
    }

    @Override
    protected void onRightButtonClick() {
        // TODO do we really need to cancel the intent and alarm?
        mAlarmController.cancelAlarm(getRingingObject(), false, true);
        stopAndFinish();
    }

    @Override
    protected Parcelable.Creator<Alarm> getParcelableCreator() {
        return Alarm.CREATOR;
    }

    // TODO: Consider changing the return type to Notification, and move the actual
    // task of notifying to the base class.
    @Override
    protected void showAutoSilenced() {
        super.showAutoSilenced();
        postMissedAlarmNote();
    }

    private void postMissedAlarmNote() {
        String alarmTime = TimeFormatUtils.formatTime(this,
                getRingingObject().hour(), getRingingObject().minutes());
        Notification note = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.missed_alarm))
                .setContentText(alarmTime)
                .setSmallIcon(R.drawable.ic_alarm_24dp)
                .build();
        mNotificationManager.notify(TAG, getRingingObject().getIntId(), note);
    }
}
