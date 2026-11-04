package cn.jpush.android.ui;

import android.app.Activity;
import android.os.Bundle;

public class PopWinActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
    }

    public void startPushActivity(String action) {
        finish();
    }
}
