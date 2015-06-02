package home.go.rym.async;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import home.go.rym.utils.Constants;

public class LoginTask extends AsyncTask<Void, Void, Void> {

    private final WeakReference<Activity> activityReference;
    private final Activity activity;
    private SharedPreferences prefs;
    private String cookie;

    public LoginTask(Activity activity) {
        this.activityReference = new WeakReference<Activity>(activity);
        this.activity = activity;
        this.prefs = activity.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
    }

    private void login() {
        HttpsURLConnection connection;
        URL url;
        String params;
        try {
            url = new URL(Constants.HTTPS+Constants.URL_REQUEST);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(Constants.POST);
            params = "user=" + prefs.getString(Constants.PREF_USERNAME.toString(),"")
                    + "&password=" + prefs.getString(Constants.PREF_PASSWORD.toString(),"")
                    + "&remember=true&maintain_session=true&action=Login&rym_ajax_req=1&request_token=";
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + Constants.UNICODE_CHARSET_NAME);
            OutputStream output = connection.getOutputStream();
            output.write(params.getBytes(Constants.UNICODE_CHARSET_NAME));
            if (connection.getResponseCode()==HttpsURLConnection.HTTP_OK) {
                String sCookie = connection.getHeaderField("set-cookie");
                if(sCookie!=null && sCookie.length()>0){
                    cookie = sCookie;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        login();
        return null;
    }
}
