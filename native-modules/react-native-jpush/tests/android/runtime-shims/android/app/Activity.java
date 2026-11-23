package android.app;

import android.content.Context;

public class Activity extends Context {
    private final Application application;

    public Activity(Application application) {
        this.application = application;
    }

    @Override
    public Context getApplicationContext() {
        return application;
    }
}
