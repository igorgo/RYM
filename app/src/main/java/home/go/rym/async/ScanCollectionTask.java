package home.go.rym.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.afollestad.materialdialogs.MaterialDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import home.go.rym.R;
import home.go.rym.RymApplication;
import home.go.rym.orm.Artist;
import home.go.rym.orm.Collection;
import home.go.rym.orm.Months;
import home.go.rym.orm.Release;
import home.go.rym.orm.ReleaseType;
import home.go.rym.orm.RymUser;
import home.go.rym.utils.RymUtils;

/**
 * It's a part of project RYM
 * Created by igorgo (igor-go@parus.com.ua)
 * Copyright (C) 2015 Parus-Ukraine Corporation (www.parus.ua)
 */
public class ScanCollectionTask extends AsyncTask<Void, Integer, Void> {
    private final Activity activity;
    private String username;
    private int pageCount;
    private boolean preClean;
    private RymUser user;
    private MaterialDialog dialog;
    private String recentUrl;
    private String referer;

    public ScanCollectionTask(Activity activity, @Nullable String username, int pageCount, boolean preClean) {
        this.activity = activity;
        this.username = username;
        this.pageCount = pageCount;
        this.preClean = preClean;
        if (username == null) {
            user = new Select().from(RymUser.class).where("Self=1").executeSingle();
        } else {
            user = new Select().from(RymUser.class).where("Name=?",username).executeSingle();
        }
        recentUrl = RymUtils.buildRecentUrl(user.name);
        referer = recentUrl;
    }

    private void clearCollection () {
        if (preClean && user!=null) {
            new Delete().from(Collection.class).where("User=?",user.getId());
        }
    }

    @Override protected void onPreExecute() {
        super.onPreExecute();
        dialog = new MaterialDialog.Builder(activity)
                .content(activity.getString(R.string.querying_page_count))
                .progress(false,pageCount,true)
                .show();
    }

    @Override protected Void doInBackground(Void... voids) {
        clearCollection();
        if (user!=null) {
            for (int i = 0; i < pageCount; i++) {
                publishProgress(i + 1);
                scanPage(i + 1);
            }
        }
        return null;
    }

    @Override protected void onPostExecute(Void aVoid) {
        dialog.dismiss();
        super.onPostExecute(aVoid);
    }

    @Override protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        dialog.setProgress(values[0]);
    }

    private void scanPage(int pageNo) {
        String pageUrl = recentUrl+ String.valueOf(pageNo);
        Log.d("TTTTTTT","Scan  " + pageUrl);
        try {
            Document doc = Jsoup.connect(pageUrl)
                                .userAgent("Mozilla")
                                .referrer(referer)
                                .cookies(((RymApplication) activity.getApplication()).getCookies())
                                .timeout(5000)
                                .get();
            Log.d("TTTTTTT","Parse  " + pageUrl);
            Elements rows = doc.select("table.mbgen > tbody > tr");
            for (int i = 1; i < rows.size(); i++) {
                Collection collection = parseRow(rows.get(i));
                boolean needSave = true;
                if (!preClean) {
                    needSave = ! new Select().from(Collection.class).where("Release=? and User=?",collection.release
                            .getId(),user).exists();
                }
                if (needSave) collection.save();
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        referer = pageUrl;

    }

    private Collection parseRow(Element row) {
        Collection collection = new Collection();
        String day = row.select("div.date_element_day").first().text();
        String monthS = row.select("div.date_element_month").first().text();
        String year = row.select("div.date_element_year").first().text();
        String month = Months.valueOf(monthS).no();
        collection.entryDate = year + "." + month + "."+ day;
        Element rateImg = row.select("td.or_q_rating_date_s > img").first();
        if (rateImg == null) {
            collection.rate = 0;
        } else {
            collection.rate = RymUtils.rateStarsToInt(rateImg.attr("alt"));
        }
        collection.user = user;
        Element artistA = row.select("a.artist").first();
        Element albumA = row.select("a.album").first();
        String[] paths = albumA.attr("href").split("/");
        String artistUrl = paths[3];
        Artist artist = new Select().from(Artist.class).where("NameUrl=?", artistUrl).executeSingle();
        boolean checkRelease = true;
        if (artist == null) {
            artist = new Artist(artistA.text(),artistUrl);
            artist.save();
            checkRelease = false;
        }
        String releaseNameUrl = paths[4];
        String releaseType = paths[2];

        Release release = null;
        if (checkRelease){
            release = new Select().from(Release.class).where("Artist=? and NameUrl= ? and Type=?",artist
                    .getId(),releaseNameUrl, ReleaseType.valueOf(releaseType)).executeSingle();
        }
        if (release == null) {
            release = new Release();
            release.artist = artist;
            release.name = albumA.text();
            release.type = ReleaseType.valueOf(releaseType).ordinal();
            release.nameUrl = releaseNameUrl;
            try {
                release.year = Integer.parseInt(row.select("span.smallgray").text().substring(1,5));
            } catch (Exception e) {
                release.year = Integer.parseInt(null);
            }
            release.save();
        }
        collection.release = release;

        return collection;
    }

}
