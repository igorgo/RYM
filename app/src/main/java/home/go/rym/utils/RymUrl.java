package home.go.rym.utils;

/**
 * It's a part of project RYM
 * Created by igorgo (igor-go@parus.com.ua)
 * Copyright (C) 2015 Parus-Ukraine Corporation (www.parus.ua)
 */
public class RymUrl {
    public static String recent(String user) {
        return Constants.HTTP + Constants.HOST + "/collection/" + user + "/recent/";
    }
}
