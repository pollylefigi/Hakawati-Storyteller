package ai.wit.eval.wit_eval;

/**
 * Created by Nekrothunder on 29/11/2014.
 */
        import java.util.ArrayList;
        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.apache.http.message.BasicNameValuePair;
        import org.apache.http.params.BasicHttpParams;
        import org.apache.http.params.HttpConnectionParams;
        import org.apache.http.params.HttpParams;
        import org.apache.http.util.EntityUtils;
        import android.os.Handler;
        import android.os.Message;

public class HttpPostThread extends Thread {
    public static final int FAILURE = 0;
    public static final int SUCCESS = 1;
    public static final String VKEY = "FINDURB#V0";

    private final Handler handler;
    private String url;
    ArrayList<NameValuePair> pairs;

    public HttpPostThread(String Url, ArrayList<NameValuePair> pairs, final Handler handler)
    {
        this.url =Url;
        this.handler = handler;
        this.pairs = pairs;
        if(pairs==null){
            this.pairs = new ArrayList<NameValuePair>();
        }
    }


    @Override
    public void run()
    {
        try {

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 3000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            if(pairs!=null)
                post.setEntity(new UrlEncodedFormEntity(pairs));
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            String answer = EntityUtils.toString(entity);
            Message message = new Message();
            message.obj = answer;
            message.what = HttpPostThread.SUCCESS;
            handler.sendMessage(message);


        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(HttpPostThread.FAILURE);
        }

    }
}