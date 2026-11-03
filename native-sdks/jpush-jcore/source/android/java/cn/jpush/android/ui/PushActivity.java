package cn.jpush.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.local.ActionHelper;

public class PushActivity extends Activity {
    public static final String FROM_OTHER_WAY = "from_way";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionHelper.getInstance().handleNotificationIntent(getApplicationContext(), getIntent());
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ActionHelper.getInstance().handleNotificationIntent(getApplicationContext(), intent);
        finish();
    }

    public void showTitleBar() {
    }
}
