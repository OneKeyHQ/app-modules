package cn.jpush.android.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class DActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        finish();
    }
}
