package home.go.rym;

import com.activeandroid.ActiveAndroid;

public class RymApplication  extends com.activeandroid.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
