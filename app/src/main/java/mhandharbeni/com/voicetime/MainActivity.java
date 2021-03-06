package mhandharbeni.com.voicetime;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, LocationListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.voiceTime)
    AppCompatButton voiceTime;
    @BindView(R.id.emergencyCall)
    AppCompatButton emergencyCall;


    ArrayList<Locale> languages;

    String firstPatternEN = "Now is,  ";
    String firstPatterIN = "Sekarang Sudah Jam  ";

    String completeString = "";

    private TextToSpeech mTts;
    private int MY_DATA_CHECK_CODE = 2008;
    private String TAG = MainActivity.class.getSimpleName();

    private String language = "en", region = "US";
    private SharedPreferences sharedPreferences;

    private LocationManager locationManager;

    private String latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPermission();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AddAlarm.class);
            startActivity(i);
        });

        initSupportLanguage();
        if (gpsIsEnable()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onCreate: PermissionGranted");
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
            }
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        initModule();
    }

    private void initModule() {
        sharedPreferences = getSharedPreferences(getString(R.string.sharedkey), Context.MODE_PRIVATE);
    }

    private void updateData(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private String getData(String key) {
        String defaultValue = getResources().getString(R.string.default_string);
        return sharedPreferences.getString(key, defaultValue);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.voiceTime)
    public void showTime() {

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("H:m:s a", new Locale(language, region));
        String datestring = dateFormat.format(date);

        fillWord(Integer.valueOf(splitString(datestring, ":", 0)),
                Integer.valueOf(splitString(datestring, ":", 1)),
                splitString(datestring, " ", 1));

        startVoices();
    }

    @OnClick(R.id.emergencyCall)
    public void showEmergencyPad() {
        sendMessage();

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        callIntent.setData(Uri.parse("tel:"+sharedPreferences.getString(AddAlarm.KEY_NODARURAT, "0")));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    private void sendMessage(){
        if (latitude == null && longitude == null){
            Toast.makeText(this, "Lokasi Belum Terdeteksi, Tunggu Sebentar", Toast.LENGTH_SHORT).show();
            return;
        }
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(sharedPreferences.getString(AddAlarm.KEY_NODARURATSMS, "0"), null, "Here's my location { Longitude : "+longitude+", Latitude : "+latitude+"}, i need you now", null, null);
    }

    private String splitString(String string, String regex, int index){
        return string.split(regex)[index];
    }

    private void startVoices(){

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    }
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                mTts = new TextToSpeech(this, this);
            } else {
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }
    private void fillWord(int hour, int minutes, String ampm){
        completeString = "";
        if (language.equalsIgnoreCase("en")){
            completeString += firstPatternEN;
        }else{
            completeString += firstPatterIN;
        }
        int hourNow;
        int minuteNow;
        if (hour == 0){
            if (language.equalsIgnoreCase("en")){
                completeString += " 12 " + ",";
            }else{
                completeString += "12 Malam Lewat ";
            }
        }else if (hour > 12){
            hourNow = hour-12;
            if (language.equalsIgnoreCase("en")){
                completeString += hourNow + ",";
            }else{
                if (hourNow > 6){
                    completeString += hourNow+" Malam Lewat ";
                }else{
                    completeString += hourNow+" Sore Lewat ";
                }
            }
        }else{
            hourNow = hour;
            if (language.equalsIgnoreCase("en")){
                completeString += hourNow + ",";
            }else{
                if (hourNow < 9){
                    completeString += hourNow+" Pagi Lewat ";
                }else{
                    completeString += hourNow+" Siang Lewat ";
                }
            }
        }
        minuteNow = minutes;
        completeString += " "+minuteNow+" ";
        if (language.equalsIgnoreCase("en")){
            completeString += ", "+ampm.toUpperCase();
        }else{
            completeString += " Menit";
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS){
            mTts.setLanguage(new Locale(language, region));
            HashMap<String, String> myHashAlarm = new HashMap();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                    String.valueOf(AudioManager.STREAM_ALARM));
            mTts.speak(completeString, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
        }else{
            HashMap<String, String> myHashAlarm = new HashMap();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                    String.valueOf(AudioManager.STREAM_ALARM));
            mTts.speak(completeString, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
            Toast.makeText(this, "Your Device Not Compatible With Text To Speech", Toast.LENGTH_SHORT).show();
        }
    }
    private Boolean gpsIsEnable(){
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            showGPSDisabledAlertToUser();
            return false;
        }
    }
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        (dialog, id) -> {
                            Intent callGPSSettingIntent = new Intent(
                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(callGPSSettingIntent);
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                (dialog, id) -> dialog.cancel());
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void initSupportLanguage(){
        for (Locale locale:Locale.getAvailableLocales()){
            if (locale.getDisplayName().equalsIgnoreCase("Bahasa Indonesia")){
                language = "in";
                region = "ID";
            }
        }
    }

    private void initPermission(){
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.SET_ALARM) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.SET_ALARM,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                }, 1);
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
//                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 5);
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 6);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        Log.d(TAG, "onLocationChanged: "+location.getLongitude()+"-"+location.getLatitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
