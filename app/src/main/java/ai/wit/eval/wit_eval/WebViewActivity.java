package ai.wit.eval.wit_eval;
/**
 * Created by Nekrothunder on 27/11/2014.
 */


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Demonstrates how to embed a WebView in your activity. Also demonstrates how
 * to have javascript in the WebView call into the activity, and how the activity
 * can invoke javascript.
 * <p>
 * In this example, clicking on the android in the WebView will result in a call into
 * the activities code in {@link DemoJavaScriptInterface#clickOnAndroid()}. This code
 * will turn around and invoke javascript using the {@link WebView#loadUrl(String)}
 * method.
 * <p>
 * Obviously all of this could have been accomplished without calling into the activity
 * and then back into javascript, but this code is intended to show how to set up the
 * code paths for this sort of communication.
 *
 */
public class WebViewActivity extends Activity {

    private static final String LOG_TAG = "WebViewDemo";
    public static final String EXTRA_URL="0";

    private WebView mWebView;
    private String mUrl;


    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_webview);
        //prendo la webview dal layout:qui dentro mostrerò la pagina
        mWebView = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = mWebView.getSettings();
        //posso settare le diverse proprietà: a me interessa che sia abilitato il js:
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);

        //gli setto un mio Chromeclient implementato per loggare gli alert js:
        mWebView.setWebChromeClient(new MyWebChromeClient());
        //espongo una classe modo che il js della pagina possa chimare dei miei metodi:
        //nella pagina troverò che nel dom ho window.demo e sotto posso chiamare i metodio della classe java

        mWebView.addJavascriptInterface(new DemoJavaScriptInterface(), "demo");
        //carico la pagina:
        // mWebView.loadUrl("file:///android_asset/demo.html");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mUrl = extras.getString(EXTRA_URL);

            mWebView.loadUrl(mUrl);
        }
        else{


        }
    }
    //ecco la classe a disposizione di javascript:
    final class DemoJavaScriptInterface {

        DemoJavaScriptInterface() {
        }

        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * loadUrl on the UI thread.
         */
        public void clickOnAndroid() {
            mHandler.post(new Runnable() {
                public void run() {
                    //qui chiamo una funzione js direttamente da android:
                    //sempre con un loadUrl
                    mWebView.loadUrl("javascript:wave()");
                }
            });
        }
    }

    //questo lo estendo apposta perchè sugli alert mi logghi le cose che
    //sputa fuori il browser:è utile per il debugging
    /**
     * Provides a hook for calling "alert" from javascript. Useful for
     * debugging your javascript.
     */
    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d(LOG_TAG, message);
            result.confirm();
            return true;
        }
    }
}