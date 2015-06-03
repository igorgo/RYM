package home.go.rym.async;


import android.app.Activity;
import android.os.AsyncTask;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import home.go.rym.R;
import home.go.rym.RymApplication;
import home.go.rym.orm.RymUser;
import home.go.rym.utils.Constants;

public class LoginTask extends AsyncTask<Void, Void, Void> {

    private final Activity activity;
    private Map<String,String> cookies = new HashMap<>();
    private MaterialDialog dialog;
    private String username;
    private String password;


    public LoginTask(Activity activity, String username, String password) {
        this.activity = activity;
        this.username = username;
        this.password = password;
    }

    private void login() {
        HttpsURLConnection connection;
        URL url;
        String params;
        try {
            url = new URL(Constants.HTTPS+Constants.URL_REQUEST);
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(Constants.POST);
            params = "user=" + username + "&password=" +password
                    + "&remember=true&maintain_session=true&action=Login&rym_ajax_req=1&request_token=";
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + Constants.UNICODE_CHARSET_NAME);
            OutputStream output = connection.getOutputStream();
            output.write(params.getBytes(Constants.UNICODE_CHARSET_NAME));
            if (connection.getResponseCode()==HttpsURLConnection.HTTP_OK) {
                cookies.clear();
                for (HttpCookie cookie: cookieManager.getCookieStore().get(URI.create(Constants.HTTPS + Constants
                        .HOST))) {
                    cookies.put(cookie.getName(),cookie.getValue());
                }
                setSelfUser();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override protected void onPreExecute() {
        dialog = new MaterialDialog.Builder(activity)
        .content(activity.getString(R.string.authentification))
        .progress(true,0)
        .show();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        login();
        return null;
    }

    @Override protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ((RymApplication)  activity.getApplication()).setCookies(cookies);
        dialog.dismiss();
    }

    private void setSelfUser() {
        String username = cookies.get("username");
        if (username != null) {
            List<RymUser> users = new Select()
                    .from(RymUser.class)
                    .where("Name = ?",username)
                    .execute();

            if (users.size() == 0) {
                RymUser user = new RymUser();
                user.name = username;
                user.self=1;
                user.friend = 0;
                user.favorite =0;
                user.save();
            } else {
                RymUser user = users.get(0);
                if (user.self != 1) {
                    new Update(RymUser.class)
                            .set("Self=0, Favorite=1")
                            .where("Self=1")
                            .execute();
                    user.self=1;
                    user.friend = 0;
                    user.favorite =0;
                    user.save();
                }
            }
        }
    }

}
