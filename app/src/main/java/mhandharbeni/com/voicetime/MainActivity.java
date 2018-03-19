package mhandharbeni.com.voicetime;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.voiceTime)
    AppCompatButton voiceTime;
    @BindView(R.id.emergencyCall)
    AppCompatButton emergencyCall;


    ArrayList<Locale> languages;

    String firstPatternEN = "Now is ";
    String firstPatterIN = "Sekarang Sudah Jam ";

    String timePatternSore = " Sore ";
    String timePatternSiang = " Siang ";
    String timePatternPagi = " Pagi ";
    String timePatternMalam = " Malam ";

    String completeString = "";

    private TextToSpeech mTts;
    private int MY_DATA_CHECK_CODE = 2008;
    private String TAG=MainActivity.class.getSimpleName();

    private String language = "en", region = "US";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initSupportLanguage();

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
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        callIntent.setData(Uri.parse("tel:081556617741"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
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
                // success, create the TTS instance
                mTts = new TextToSpeech(this, this);
            } else {
                // missing data, install it
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
        if (hour > 12){
            hourNow = hour-12;
            if (language.equalsIgnoreCase("en")){
                completeString += hourNow;
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
                completeString += hourNow;
            }else{
                if (hourNow < 9){
                    completeString += hourNow+" Pagi Lewat ";
                }else{
                    completeString += hourNow+" Siang Lewat ";
                }
            }
        }

        minuteNow = minutes;

        completeString += minuteNow;

        if (language.equalsIgnoreCase("en")){
            completeString += ampm;
        }else{
            completeString += " Menit";
        }
    }

    @Override
    public void onInit(int status) {

        mTts.setLanguage(new Locale(language, region));
        HashMap<String, String> myHashAlarm = new HashMap();
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                String.valueOf(AudioManager.STREAM_ALARM));
        mTts.speak(completeString, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
    }


    private void initSupportLanguage(){
        for (Locale locale:Locale.getAvailableLocales()){
            if (locale.getDisplayName().equalsIgnoreCase("Bahasa Indonesia")){
                language = "in";
                region = "ID";
            }
        }
    }
}
