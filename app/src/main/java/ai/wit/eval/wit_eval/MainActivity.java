package ai.wit.eval.wit_eval;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import ai.wit.sdk.IWitListener;
import ai.wit.sdk.Wit;
import ai.wit.sdk.model.WitOutcome;


public class MainActivity extends ActionBarActivity implements IWitListener , TextToSpeech.OnInitListener {

    Wit _wit;

    public static final String EXTRA_URL="0";
    public static final int ANSWER_UTTERANCE_COMPLETED=1;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    public static final int CONTROLLO_TTS=1;
    private TextToSpeech tts;
    public String mText;

    public static  String mLeonidAnswer="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String accessToken = "6TXUHD66XNN65XVCQ6C5QWIZITUTX6WK";
        _wit = new Wit(accessToken, this);
        /**
         * Enabling the context location will add the GPS coordinates to the _context object to all
         * Wit requests (speech and text requests).
         * This can help the Wit API to resolve some entities like the Location entity*/
        //_wit.enableContextLocation(getApplicationContext());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * calls Wit when microphone button is pressed!Fully working uncomment this one if you need to use WIT.AI
     * @param v
     */

    public void toggle(View v) {
        try {

            _wit.toggleListening();
            ((TextView) findViewById(R.id.txtText)).setText("Talk to me");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /* calls Google speech to text when microphone button is pressed!
     *
     */
  /*  public void toggle(View v) {
        try {
           promptSpeechInput();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    @Override
    public void witDidGraspIntent(ArrayList<WitOutcome> witOutcomes, String messageId, Error error) {
        //add this again for debugging!!
        //TextView jsonView = (TextView) findViewById(R.id.jsonView);
       // jsonView.setMovementMethod(new ScrollingMovementMethod());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (error != null) {
           // jsonView.setText(error.getLocalizedMessage());
            System.out.println(error.getLocalizedMessage());
            return ;
        }
        String jsonOutput = gson.toJson(witOutcomes);
        //jsonView.setText(jsonOutput);
        System.out.println(jsonOutput);
        System.out.println(jsonOutput);

        /*
        try {

           //read json response and load content accordingly


         //comment for a while

            JSONArray jsonarray = new JSONArray(jsonOutput);
            JSONObject newssection=jsonarray.getJSONObject(0).getJSONObject("entities").getJSONArray("news_section").getJSONObject(0);
            String psection= newssection.getString("value");
            jsonView.append(psection);


            //load a webpage with news about psectiontopic
           String myurl="http://www.htmlfactory.us/aljazeera/news.html#"+psection;
            openNewsPage(myurl);
//test:





        } catch (JSONException e) {
            e.printStackTrace();
        }
    */

//get the question for leonid:
        try {


            JSONArray jsonarray = new JSONArray(jsonOutput);
            String textentered=jsonarray.getJSONObject(0).getString("_text");
            System.out.println(textentered);
            System.out.println(textentered);
            String cleanquestion = textentered.replace("what's", "what is");
            String cleanquestion2 = cleanquestion.replace("who's", "who is");
            String cleanquestion3 = cleanquestion2.replace("don't", "do not");
            String cleanquestion4 = cleanquestion3.replace("how's", "how is");
            String cleanquestion5 = cleanquestion4.replace("doesn't", "does not");
            String cleanquestion6 = cleanquestion5.replace("isn't", "is not");
            String cleanquestion7 = cleanquestion6.replace("that's", "that is");

            String cleanquestion8 = cleanquestion7.replace("couldn't", "could not");
            String cleanquestion9 = cleanquestion8.replace("wasn't", "was not");
            String cleanquestion10 = cleanquestion9.replace("wouldn't", "would not");
            String cleanquestion11 = cleanquestion10.replace("where's", "where is");


            //call leonid with the text entered:
            ((TextView) findViewById(R.id.txtText)).setText(cleanquestion3);
            this.sendToLeonid(cleanquestion11);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //ASK LEONID!WORKING
       // String  pquestion="Tell me something about Al Jazeera!";
       // this.sendToLeonid(pquestion);
       // ((TextView) findViewById(R.id.txtText)).setText("done!");


    }

    /**
     * calls Leonid service sending the question
     * @param pquestion
     */
    private void sendToLeonid(String pquestion){

        ServerManager.callLeonidService(this,pquestion);

    }

    /**
     * utters Leonid answer
     */
    public void utterLeonidAnswer(){
     System.out.println("*******LEONID SAYS "+mLeonidAnswer);
        ((TextView) findViewById(R.id.txtText)).setText(mLeonidAnswer);
        utterAnswer(mLeonidAnswer);

    }

    /**
     * utters a text
     * @param textToUtter
     */
    public void utterAnswer(String textToUtter){
        System.out.println("*************utterAnswer");
        mText=textToUtter;
        Intent intent = new Intent();
        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, CONTROLLO_TTS);

    }


   /**********************************   WIT   **********************************************/

    @Override
    public void witDidStartListening() {
        ((TextView) findViewById(R.id.txtText)).setText("Tap me and say something...");
    }

    @Override
    public void witDidStopListening() {
        ((TextView) findViewById(R.id.txtText)).setText("Processing...");
    }

    @Override
    public void witActivityDetectorStarted() {
        ((TextView) findViewById(R.id.txtText)).setText("Listening");
    }

    @Override
    public String witGenerateMessageId() {
        return null;
    }

    public static class PlaceholderFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.wit_button, container, false);
        }
    }

    /*****************   TEXT TO SPEECH *********************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==CONTROLLO_TTS){

            if(resultCode==TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                //istantiate tts:
                this.tts= new TextToSpeech(this,this);
                tts.setLanguage(Locale.ITALY);


            }
            else{
                Intent installIntent= new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);

            }
        }
        else if(resultCode==REQ_CODE_SPEECH_INPUT){
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                ((TextView) findViewById(R.id.txtText)).setText(result.get(0));
                System.out.println("*****************" + result.get(0));
                sendToLeonid(result.get(0));
            }
        }
        else{

            System.out.println("*****************"+resultCode);
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


    public void onInit(int arg0) {
        // TODO Auto-generated method stub


        //get the text to pronounce:
     /*   Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mText = extras.getString("EXTRA_TEXT_TO_UTTER");

        }
        else{

            mText="Sorry, I've nothing to say right now";
        }*/
        String mytext=mText;
        if(mytext!=""){
            tts.speak(mytext,TextToSpeech.QUEUE_FLUSH,null);
        }
        else{
            // tts.speak(mytext2,TextToSpeech.QUEUE_FLUSH,null);
        }
    }


    /***************************** google speech to text  *********************************************/
    /**
     * Showing google speech input dialog
     * */

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ITALY);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }





}