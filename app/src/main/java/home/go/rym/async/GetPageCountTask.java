package home.go.rym.async;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.orhanobut.logger.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
public class GetPageCountTask extends AsyncTask<Void, Void, Void> {
    private final Activity activity;
    private MaterialDialog dialog;
    private String url;
    private OnGetPageCountTask listener;
    int pageCount;

    public GetPageCountTask(Activity activity, String url, OnGetPageCountTask listener) {
        this.activity = activity;
        this.listener = listener;
        this.url = url;
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
            pageCount = 0;
            for (Element page : pages) {
                String txt = page.text();
                if (!TextUtils.isEmpty(txt) && TextUtils.isDigitsOnly(txt)) {
                    int itxt = Integer.parseInt(txt);
                    pageCount = (itxt>pageCount) ? itxt : pageCount;
                }
                Logger.d("Element data:%s", page.text());
            }
            Logger.d("pages:%s",pageCount);
            //Ln.d("pages:%s",pages);
            return pageCount;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        getPages();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (listener != null) {
            listener.OnGetPageCountTask(pageCount);
        }
    }

    public interface OnGetPageCountTask {
        void OnGetPageCountTask(int pages);
    }
}

