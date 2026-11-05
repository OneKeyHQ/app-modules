package io.intercom.android.sdk.overlay;

import android.content.Context;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.m5.navigation.IntercomRootActivityLauncher;

@Deprecated
public class LauncherOpenBehaviour {
   public void openMessenger(Context var1) {
      Injector.get().getMetricTracker().clickedLauncher();
      IntercomRootActivityLauncher.INSTANCE.startHome(var1);
   }
}
