package com.parser.blogio;

import android.content.Context;
import android.os.Handler;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlogConnector {
    private static BlogConnector sBlogConnector;
    private HttpClient mHttpClient;
    private AtomicBoolean mIsLoggedIn = new AtomicBoolean(false);


    private final String LOGIN_URL = "http://s13.ru/wp-login.php";

    private static final String COMMENT_POST_URL = "http://s13.ru/wp-comments-post.php";
    private final String THUMB_UP_URL = "http://s13.ru/wp-content/plugins/comment-rating/ck-processkarma.php?id=!id&action=add&path=s13.ru/wp-content/plugins/comment-rating/&imgIndex=1_16_";
    private final String THUMB_DOWN_URL = "http://s13.ru/wp-content/plugins/comment-rating/ck-processkarma.php?id=!id&action=subtract&path=s13.ru/wp-content/plugins/comment-rating/&imgIndex=1_16_";


    public enum QUERY_RESULT {
        OK,
        ACCESS_DENIED,
        ERROR
    }

    interface RequestListener {
        public void onRequestDone(QUERY_RESULT result, String errorMessage);
    }

    public static BlogConnector getBlogConnector() {
        if (sBlogConnector == null) {
            sBlogConnector = new BlogConnector();
        }
        return sBlogConnector;
    }

    BlogConnector() {
        mHttpClient = new DefaultHttpClient();
    }

    public boolean loggedIn() {
        return mIsLoggedIn.get();
    }

    public InputStream getInputStream(String url, String charSet) throws IOException {
        HttpGet rq = new HttpGet(url);
        if (charSet.length() > 0) {
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setContentCharset(params, charSet);
            rq.setParams(params);
        }
        addHeaders(rq);
        HttpResponse response = mHttpClient.execute(rq);
        return response.getEntity().getContent();
    }

    public void login(final String login, final String pwd, final RequestListener listener) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final boolean loggedIn = doLogin(login, pwd);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (loggedIn) {
                                mIsLoggedIn.set(true);
                                listener.onRequestDone(QUERY_RESULT.OK, "");
                            } else {
                                mIsLoggedIn.set(false);
                                listener.onRequestDone(QUERY_RESULT.ACCESS_DENIED, "");
                            }
                        }
                    });
                } catch
                        (final Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onRequestDone(QUERY_RESULT.ERROR, e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    private boolean doLogin(String login, String pwd) throws Exception {
        HttpGet httpGet = new HttpGet(LOGIN_URL);

        HttpResponse rp = mHttpClient.execute(httpGet);
        BufferedReader in = new BufferedReader(new InputStreamReader(rp
                .getEntity().getContent()));
        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        HttpPost httppost = new HttpPost(LOGIN_URL);
        List<BasicNameValuePair> nameValuePairs = new ArrayList<>();
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setHttpElementCharset(params, "UTF-8");
        httppost.setParams(params);
        mHttpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");

        addHeaders(httppost);
        nameValuePairs.add(new BasicNameValuePair("log", login));
        nameValuePairs.add(new BasicNameValuePair("pwd", pwd));
        nameValuePairs.add(new BasicNameValuePair("wp-submit",
                "%D0%92%D0%BE%D0%B9%D1%82%D0%B8"));
        nameValuePairs.add(new BasicNameValuePair("redirect_to",
                "http://s13.ru"));
        nameValuePairs.add(new BasicNameValuePair("testcookie", "0"));
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF8"));
        HttpResponse response = mHttpClient.execute(httppost);
        in = new BufferedReader(new InputStreamReader(
                response.getEntity().getContent()));
        sb = new StringBuilder();
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        in.close();
        String page = sb.toString();
        return page.toLowerCase().contains("logout");
    }

    private boolean doThumbUp(String idMessage) throws Exception {
        if (!mIsLoggedIn.get()) {
            return false;
        }

        String url = THUMB_UP_URL;
        url = url.replace("!id", idMessage);
        HttpGet rq = new HttpGet(url);

        addHeaders(rq);

        HttpResponse response = mHttpClient.execute(rq);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                response.getEntity().getContent()));
//        StringBuilder sb = new StringBuilder();
//        String line ;
//        String NL = System.getProperty("line.separator");
//        while ((line = in.readLine()) != null) {
//            sb.append(line + NL);
//        }
//
        in.close();
        return true;
    }

    private boolean doThumbDown(String idMessage) throws Exception{
        String url = THUMB_DOWN_URL;
        url = url.replace("!id", idMessage);
        HttpGet rq = new HttpGet(url);
        addHeaders(rq);
        HttpResponse response = mHttpClient.execute(rq);
        return true;
    }


    private boolean doAddComment(String commentText, String akismet, String postId) throws Exception {
        HttpPost httppost = new HttpPost(COMMENT_POST_URL);
        addHeaders(httppost);
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setHttpElementCharset(params, "UTF-8");
        httppost.setParams(params);
        mHttpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
        List<BasicNameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("comment",
                commentText));
        nameValuePairs
                .add(new BasicNameValuePair("submit",
                        "%D0%9E%D1%82%D0%BF%D1%80%D0%B0%D0%B2%D0%B8%D1%82%D1%8C"));
        nameValuePairs.add(new BasicNameValuePair("comment_post_ID",
                postId));
        nameValuePairs.add(new BasicNameValuePair(
                "akismet_comment_nonce", akismet));
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF8"));
        HttpResponse response = mHttpClient.execute(httppost);
        String rsp = response.getStatusLine().toString();
        if (rsp.contains("200")) {
            return true;
        } else {
            return false;
        }
    }

    private void addHeaders(HttpRequestBase request){
        request.addHeader("Host", "s13.ru");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.addHeader("Accept-Language", "ru,en-us;q=0.7,en;q=0.3");
        request.addHeader("Connection", "keep-alive");

//        httppost.addHeader("Host", "s13.ru");
//        httppost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1");
//        httppost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//        httppost.addHeader("Accept-Language", "ru,en-us;q=0.7,en;q=0.3");
//        httppost.addHeader("Connection", "keep-alive");
//
    }
}
