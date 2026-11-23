package cn.jiguang.internal;

import android.content.Context;
import android.os.Bundle;

public abstract class JCoreHelperAction {
    public abstract Object onEvent(Context context, String sdkType, int cmd, String action, Bundle bundle, Object... extras);

    public abstract Bundle directHandle(Context context, String sdkType, String action, Bundle bundle);

    public abstract Object onCommonMethod(Context context, String action, Object arg);
}
