package cn.jpush.android.api;

import android.app.ListActivity;

public class InstrumentedListActivity extends ListActivity {
    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }
}
