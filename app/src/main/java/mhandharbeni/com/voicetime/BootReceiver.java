package mhandharbeni.com.voicetime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * Created by Beni on 31/03/2018.
 */

public class BootReceiver extends BroadcastReceiver {
    private SharedPreferences sharedPreferences;

    private String sNodarurat = "0",
            sNodaruratSMS = "0",
            sObat1 = "0",
            sObat2 = "0",
            sObat3 = "0";
    private Boolean sChckObat1 = false,
            sChckObat2 = false,
            sChckObat3 = false;

    private static String KEY_NODARURAT = "NODARURAT";
    private static String KEY_NODARURATSMS = "NODARURATSMS";
    private static String KEY_OBAT1 = "OBAT1";
    private static String KEY_OBAT2 = "OBAT2";
    private static String KEY_OBAT3 = "OBAT3";
    private static String KEY_CHKOBAT1 = "CHKOBAT1";
    private static String KEY_CHKOBAT2 = "CHKOBAT2";
    private static String KEY_CHKOBAT3 = "CHKOBAT3";


    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            sharedPreferences = context.getSharedPreferences(context.getString(R.string.sharedkey), Context.MODE_PRIVATE);
            sChckObat1 = sharedPreferences.getBoolean(KEY_CHKOBAT1, sChckObat1);
            sChckObat2 = sharedPreferences.getBoolean(KEY_CHKOBAT2, sChckObat2);
            sChckObat3 = sharedPreferences.getBoolean(KEY_CHKOBAT3, sChckObat3);

            sObat1 = sharedPreferences.getString(KEY_OBAT1, sObat1);
            sObat2 = sharedPreferences.getString(KEY_OBAT2, sObat2);
            sObat3 = sharedPreferences.getString(KEY_OBAT3, sObat3);

            doSet();
        }
    }

    private void doSet(){
        if (sChckObat1){
            String hourMinute = sObat1;
            if (!hourMinute.equalsIgnoreCase("0")){
                int hour = Integer.valueOf(hourMinute.split(":")[0]);
                int menit = Integer.valueOf(hourMinute.split(":")[1]);
                setAlarm(hour, menit);
            }
        }else{
            String hourMinute = sObat1;
            if (!hourMinute.equalsIgnoreCase("0")) {
                int hour = Integer.valueOf(hourMinute.split(":")[0]);
                int menit = Integer.valueOf(hourMinute.split(":")[1]);
                cancelAlarm(hour, menit);
            }
        }

        if (sChckObat2){
            String hourMinute = sObat2;
            if (!hourMinute.equalsIgnoreCase("0")) {
                int hour = Integer.valueOf(hourMinute.split(":")[0]);
                int menit = Integer.valueOf(hourMinute.split(":")[1]);
                setAlarm(hour, menit);
            }
        }else{
            String hourMinute = sObat2;
            if (!hourMinute.equalsIgnoreCase("0")) {
                int hour = Integer.valueOf(hourMinute.split(":")[0]);
                int menit = Integer.valueOf(hourMinute.split(":")[1]);
                cancelAlarm(hour, menit);
            }
        }

        if (sChckObat3){
            String hourMinute = sObat3;
            if (!hourMinute.equalsIgnoreCase("0")) {
                int hour = Integer.valueOf(hourMinute.split(":")[0]);
                int menit = Integer.valueOf(hourMinute.split(":")[1]);
                setAlarm(hour, menit);
            }
        }else{
            String hourMinute = sObat3;
            if (!hourMinute.equalsIgnoreCase("0")) {
                int hour = Integer.valueOf(hourMinute.split(":")[0]);
                int menit = Integer.valueOf(hourMinute.split(":")[1]);
                cancelAlarm(hour, menit);
            }
        }

    }

    private void setAlarm(Integer hour, Integer minute){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, hour);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
        if (alarmMgr != null){
            alarmMgr.cancel(alarmIntent);
        }
    }

    private void cancelAlarm(Integer hour, Integer minute){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, hour);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

        alarmMgr.cancel(alarmIntent);
    }
}