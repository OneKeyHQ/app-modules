package cn.jiguang.internal;

import android.content.Context;
import android.os.Bundle;

public abstract class JCoreHelperAction {
   public abstract Object onEvent(Context var1, String var2, int var3, String var4, Bundle var5, Object... var6);

   public abstract Bundle directHandle(Context var1, String var2, String var3, Bundle var4);

   public abstract Object onCommonMethod(Context var1, String var2, Object var3);
}
