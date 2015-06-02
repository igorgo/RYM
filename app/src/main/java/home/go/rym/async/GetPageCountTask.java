package home.go.rym.async;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.afollestad.materialdialogs.MaterialDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import home.go.rym.RymApplication;
import home.go.rym.utils.Constants;

/**
 * It's a part of project RYM
 * Created by igor-go (igor-go@parus.com.ua)
 * Copyright (C) 2015 Parus-Ukraine Corporation (www.parus.ua)
 */
public class GetPageCountTask {
    private final Activity activity;
    private SharedPreferences prefs;
    private Map<String,String> cookies = new HashMap<>();
    private MaterialDialog dialog;
    private String url;

    public GetPageCountTask(Activity activity, String url) {
        this.activity = activity;
        this.url = url;
        this.prefs = activity.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
    }

    private int getPages() {
        try {
            Document doc = Jsoup.connect(url)
                                .userAgent("Mozilla")
                                .referrer(url)
                                .cookies(((RymApplication) activity.getApplication()).getCookies())
                                .timeout(3000)
                                .get();
            Elements pages = doc.select(".navlinknum");
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
