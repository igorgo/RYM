package home.go.rym;

import com.activeandroid.ActiveAndroid;

import java.util.HashMap;
import java.util.Map;

public class RymApplication  extends com.activeandroid.app.Application {
    private Map<String,String> cookies = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }


    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }
}
