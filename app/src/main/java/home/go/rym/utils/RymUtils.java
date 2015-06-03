package home.go.rym.utils;

/**
 * It's a part of project RYM
 * Created by igorgo (igor-go@parus.com.ua)
 * Copyright (C) 2015 Parus-Ukraine Corporation (www.parus.ua)
 */
public class RymUtils {
    public static String buildRecentUrl(String user) {
        return Constants.HTTP + Constants.HOST + "/collection/" + user + "/recent/";
    }

    public static int rateStarsToInt(String rate) {
        if (rate == null) {
            return 0;
        } else {
            return (int) (Float.parseFloat(rate.substring(0, 4)) * 2);
        }

    }
}
