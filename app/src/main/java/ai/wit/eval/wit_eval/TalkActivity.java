package ai.wit.eval.wit_eval;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
/**
 * Created by Nekrothunder on 29/11/2014.
 */


public class TalkActivity extends Activity implements OnInitListener{

    public static final int CONTROLLO_TTS=1;
    private TextToSpeech tts;
    private String mText;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_talk);
    }


    @SuppressWarnings("unused")
    public void onParlaButtonPressed(View v){

        Intent intent = new Intent();
        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, CONTROLLO_TTS);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==CONTROLLO_TTS){

            System.out.println("onActivityResult" +requestCode);
            if(resultCode==TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                //istanzio il tts:
                this.tts= new TextToSpeech(this,this);
                tts.setLanguage(Locale.ITALY);


            }
            else{
                Intent installIntent= new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);

            }
        }
    }

    public void saveFile(View v){
        String mytext="";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mytext = extras.getString("EXTRA_TEXT_TO_UTTER");

        }

        if(mytext!=""){
            final Calendar c= Calendar.getInstance();

            int mMonth=c.MONTH;
            int mDay=c.DAY_OF_MONTH;
            int mHour=c.HOUR;
            int mmMin=c.MINUTE;
            int mSec=c.SECOND;

            StringBuilder filename= new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mDay).append("-")
                    .append(mMonth + 1).append("-")
                    .append(mHour).append("-")
                    .append(mmMin).append("-")
                    .append(mSec).append(".wav");
            saveToFile( mytext,filename.toString());
        }
        else{
            Toast.makeText(this,"nothing to save",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try{
            tts.shutdown();
        }catch(Exception ex){


        }
    }
    private void saveToFile(String textToSpeak,String filename){
        HashMap<String, String> myHashRender = new HashMap();
        if(filename==null){
            filename = "/sdcard/myAppCache/testTTS.wav";
        }
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, textToSpeak);
        tts.synthesizeToFile(textToSpeak, myHashRender, "/sdcard/myAppCache/"+filename);
        Toast.makeText(this, "File saved!", Toast.LENGTH_SHORT).show();
    }

    public void onUtteranceCompleted(String uttId) {
        if (uttId == "end of wakeup message ID") {
            Toast.makeText(this,"playback completed", Toast.LENGTH_SHORT).show();
        }
    }

    public void onInit(int arg0) {
        // TODO Auto-generated method stub


        //get the text to pronounce:
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mText = extras.getString("EXTRA_TEXT_TO_UTTER");

        }
        else{

            mText="Sorry, I've nothing to say right now";
        }
        String mytext=mText;
        if(mytext!=""){
            tts.speak(mytext,TextToSpeech.QUEUE_FLUSH,null);
        }
        else{
           // tts.speak(mytext2,TextToSpeech.QUEUE_FLUSH,null);
        }
    }
}