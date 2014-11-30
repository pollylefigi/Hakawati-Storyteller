package ai.wit.eval.wit_eval;

/**
 * Created by Nekrothunder on 29/11/2014.
 */


        import java.util.ArrayList;
        import java.util.Iterator;

        import org.apache.http.NameValuePair;
        import org.apache.http.message.BasicNameValuePair;
        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import android.app.Activity;
        import android.os.Handler;
        import android.os.Message;
        import android.view.View;
        import android.widget.Toast;


public class ServerManager {

    private static String SERVER_URL="http://logtomobile.com/leonid/src/talk_xml.php";

    /**
     * carica la carta della categoria desiderata
     * @param outputActivity
     * @param textToSend

     */
    public static void callLeonidService(final MainActivity outputActivity,String textToSend){

        Handler handler;
        handler = new Handler(){

            @Override
            public void handleMessage(Message msg)
            {
                String errors="";
                switch (msg.what)
                {
                    case HttpPostThread.SUCCESS:

                        System.out.println(msg.toString());
                        String answer=msg.toString();
                         answer = answer.split("<odp>")[1];
                        System.out.println("***********"+answer);
                        answer=answer.split("</odp>")[0];

                        if (answer != null)
                        {
                            outputActivity.mLeonidAnswer=answer;
                            outputActivity.utterLeonidAnswer();
                        }
                        break;

                    case HttpPostThread.FAILURE:

                        errors=" "+msg.what;
                        System.out.println(msg.what);
                        // do some error handling
                        break;

                    default:
                        break;
                }/*fine switch*/
            }/*fine handleMessage*/

        };

        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("Pytanie", textToSend));
        pairs.add(new BasicNameValuePair("Submit","submit"));/*not needed*/
        HttpPostThread thread = new  HttpPostThread(SERVER_URL,pairs, handler);
        thread.start();

    }


}



