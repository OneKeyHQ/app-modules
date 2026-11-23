package cn.jiguang.api;

import android.content.Context;

public interface JAnalyticsAction {
    void dispatchResume(Context context);

    void dispatchPause(Context context);

    void dispatchStatus(Context context, String status);
}
