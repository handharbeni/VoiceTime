package mhandharbeni.com.voicetime;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import mhandharbeni.com.voicetime.alarms.Alarm;
import mhandharbeni.com.voicetime.alarms.data.AsyncAlarmsTableUpdateHandler;
import mhandharbeni.com.voicetime.alarms.misc.AlarmController;
import mhandharbeni.com.voicetime.list.ScrollHandler;

/**
 * Created by Beni on 30/03/2018.
 */

public class AddAlarm extends AppCompatActivity implements ScrollHandler {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nodarurat)
    EditText nodarurat;

    @BindView(R.id.nodaruratsms)
    EditText nodaruratsms;

    @BindView(R.id.chckObat1)
    CheckBox chckObat1;

    @BindView(R.id.chckObat2)
    CheckBox chckObat2;

    @BindView(R.id.chckObat3)
    CheckBox chckObat3;

    @BindView(R.id.obat1)
    EditText obat1;

    @BindView(R.id.obat2)
    EditText obat2;

    @BindView(R.id.obat3)
    EditText obat3;

    @BindView(R.id.save)
    Button save;

    @BindView(R.id.btnObat1)
    AppCompatButton btnObat1;

    @BindView(R.id.btnObat2)
    AppCompatButton btnObat2;

    @BindView(R.id.btnObat3)
    AppCompatButton btnObat3;

    private SharedPreferences sharedPreferences;

    public String sNodarurat = "0",
            sNodaruratSMS = "0",
            sObat1 = "0",
            sObat2 = "0",
            sObat3 = "0";
    private Boolean sChckObat1 = false,
            sChckObat2 = false,
            sChckObat3 = false;

    public static String KEY_NODARURAT = "NODARURAT";
    public static String KEY_NODARURATSMS = "NODARURATSMS";
    public static String KEY_OBAT1 = "OBAT1";
    public static String KEY_OBAT2 = "OBAT2";
    public static String KEY_OBAT3 = "OBAT3";
    public static String KEY_CHKOBAT1 = "CHKOBAT1";
    public static String KEY_CHKOBAT2 = "CHKOBAT2";
    public static String KEY_CHKOBAT3 = "CHKOBAT3";

    private AsyncAlarmsTableUpdateHandler mAsyncUpdateHandler;
    private AlarmController mAlarmController;
    private View mSnackbarAnchor;

    Calendar calendar;
    private String TAG = AddAlarm.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settting_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        initModule();
        initData();
        initElement();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        save();
        finish();
    }


    private void initModule() {
        mSnackbarAnchor = findViewById(R.id.maincontent);

        sharedPreferences = getSharedPreferences(getString(R.string.sharedkey), Context.MODE_PRIVATE);
        mAlarmController = new AlarmController(getApplicationContext(), mSnackbarAnchor);
        mAsyncUpdateHandler = new AsyncAlarmsTableUpdateHandler(getApplicationContext(), mSnackbarAnchor, this, mAlarmController);
    }

    private void initData(){
        sNodarurat = sharedPreferences.getString(KEY_NODARURAT, sNodarurat);
        sNodaruratSMS = sharedPreferences.getString(KEY_NODARURATSMS, sNodaruratSMS);
        sChckObat1 = sharedPreferences.getBoolean(KEY_CHKOBAT1, sChckObat1);
        sChckObat2 = sharedPreferences.getBoolean(KEY_CHKOBAT2, sChckObat2);
        sChckObat3 = sharedPreferences.getBoolean(KEY_CHKOBAT3, sChckObat3);
        sObat1 = sharedPreferences.getString(KEY_OBAT1, sObat1);
        sObat2 = sharedPreferences.getString(KEY_OBAT2, sObat2);
        sObat3 = sharedPreferences.getString(KEY_OBAT3, sObat3);
    }
    private void initElement(){
        nodarurat.setText(sNodarurat);
        nodaruratsms.setText(sNodaruratSMS);
        chckObat1.setChecked(sChckObat1);
        chckObat2.setChecked(sChckObat2);
        chckObat3.setChecked(sChckObat3);
        obat1.setText(sObat1);
        obat2.setText(sObat2);
        obat3.setText(sObat3);
    }

    @OnClick(R.id.save)
    public void save(){
        sNodarurat = !nodarurat.getText().toString().equalsIgnoreCase("") ? nodarurat.getText().toString() : sNodarurat;
        sNodaruratSMS = !nodaruratsms.getText().toString().equalsIgnoreCase("") ? nodaruratsms.getText().toString() : sNodaruratSMS;
        sChckObat1 = chckObat1.isChecked();
        sChckObat2 = chckObat2.isChecked();
        sChckObat3 = chckObat3.isChecked();
        sObat1 = !obat1.getText().toString().equalsIgnoreCase("") ? obat1.getText().toString() : sObat1;
        sObat2 = !obat2.getText().toString().equalsIgnoreCase("") ? obat2.getText().toString() : sObat2;
        sObat3 = !obat3.getText().toString().equalsIgnoreCase("") ? obat3.getText().toString() : sObat3;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NODARURAT, sNodarurat);
        editor.putString(KEY_NODARURATSMS, sNodaruratSMS);
        editor.putBoolean(KEY_CHKOBAT1, sChckObat1);
        editor.putBoolean(KEY_CHKOBAT2, sChckObat2);
        editor.putBoolean(KEY_CHKOBAT3, sChckObat3);
        editor.putString(KEY_OBAT1, sObat1);
        editor.putString(KEY_OBAT2, sObat2);
        editor.putString(KEY_OBAT3, sObat3);
        editor.apply();
        editor.commit();
    }

    @OnClick({R.id.chckObat1,R.id.chckObat2,R.id.chckObat3})
    public void checkChange(CheckBox checkBox){
        switch (checkBox.getId()){
            case R.id.chckObat1:
                if (!obat1.getText().toString().equalsIgnoreCase("")){
                    String hourMinute = obat1.getText().toString();
                    int hour = Integer.valueOf(hourMinute.split(":")[0]);
                    int menit = Integer.valueOf(hourMinute.split(":")[1]);
                    if (checkBox.isChecked()){
                        Log.d(TAG, "checkChange: Checked");
                        setAlarm(hour, menit);
                    }else{
                        Log.d(TAG, "checkChange: unChecked");
                        cancelAlarm(hour, menit);
                    }
                }
                break;
            case R.id.chckObat2:
                if (!obat2.getText().toString().equalsIgnoreCase("")){
                    String hourMinute = obat2.getText().toString();
                    int hour = Integer.valueOf(hourMinute.split(":")[0]);
                    int menit = Integer.valueOf(hourMinute.split(":")[1]);
                    if (checkBox.isChecked()){
                        Log.d(TAG, "checkChange: Checked");
                        setAlarm(hour, menit);
                    }else{
                        Log.d(TAG, "checkChange: unChecked");
                        cancelAlarm(hour, menit);
                    }
                }
                break;
            case R.id.chckObat3:
                if (!obat2.getText().toString().equalsIgnoreCase("")){
                    String hourMinute = obat2.getText().toString();
                    int hour = Integer.valueOf(hourMinute.split(":")[0]);
                    int menit = Integer.valueOf(hourMinute.split(":")[1]);
                    if (checkBox.isChecked()){
                        Log.d(TAG, "checkChange: Checked");
                        setAlarm(hour, menit);
                    }else{
                        Log.d(TAG, "checkChange: unChecked");
                        cancelAlarm(hour, menit);
                    }
                }
                break;
        }
    }

    @OnClick({R.id.btnObat1, R.id.btnObat2, R.id.btnObat3})
    public void changeAlarm(Button button){
        switch (button.getId()){
            case R.id.btnObat1:
                showTimeDialog(obat1);
                break;
            case R.id.btnObat2:
                showTimeDialog(obat2);
                break;
            case R.id.btnObat3:
                showTimeDialog(obat3);
                break;
        }

    }

    public void showTimeDialog(final EditText editText){
        if (!editText.getText().toString().equalsIgnoreCase("0")){
            Log.d(TAG, "onTimeSet: "+editText.getText().toString());
            String hourx = editText.getText().toString().split(":")[0];
            String minutex = editText.getText().toString().split(":")[1];
            cancelAlarm(Integer.valueOf(hourx), Integer.valueOf(minutex));
        }

        new TimePickerDialog(getWindow().getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String hour = hourOfDay > 9 ? String.valueOf(hourOfDay): "0"+String.valueOf(hourOfDay);
                String menit = minute > 9 ? String.valueOf(minute): "0"+String.valueOf(minute);
                editText.setText(hour+":"+menit);
                if (editText.getId() == R.id.obat1){
                    /*obat1*/
                    if (chckObat1.isChecked()){
                        setAlarm(hourOfDay, minute);
                    }else{
                        cancelAlarm(hourOfDay, minute);
                    }
                }else if(editText.getId() == R.id.obat2){
                    /*obat2*/
                    if (chckObat2.isChecked()){
                        setAlarm(hourOfDay, minute);
                    }else{
                        cancelAlarm(hourOfDay, minute);
                    }
                }else if(editText.getId() == R.id.obat3){
                    /*obat3*/
                    if (chckObat3.isChecked()){
                        setAlarm(hourOfDay, minute);
                    }else{
                        cancelAlarm(hourOfDay, minute);
                    }
                }
            }
        }, 24, 60, true).show();
    }

    private void setAlarm(Integer hour, Integer minute){
        Alarm alarm = Alarm.builder()
                .hour(hour)
                .minutes(minute)
                .build();
        alarm.setEnabled(true);
        mAsyncUpdateHandler.asyncInsert(alarm);
    }

    private void cancelAlarm(Integer hour, Integer minute){
        Alarm alarm = Alarm.builder()
                .hour(hour)
                .minutes(minute)
                .build();
        alarm.setEnabled(false);
        mAsyncUpdateHandler.asyncDelete(alarm);
    }

    @Override
    public void setScrollToStableId(long id) {

    }

    @Override
    public void scrollToPosition(int position) {

    }
}
