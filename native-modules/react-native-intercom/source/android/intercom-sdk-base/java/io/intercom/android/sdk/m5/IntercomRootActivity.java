package io.intercom.android.sdk.m5;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.activity.compose.ComponentActivityKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.activities.IntercomBaseComponentActivity;
import io.intercom.android.sdk.m5.home.ui.helpers.InMemoryWebViewCacheKt;
import io.intercom.android.sdk.ui.theme.ThemeManager;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0014J\b\u0010\b\u001a\u00020\u0005H\u0014¨\u0006\t",
   d2 = {
         "Lio/intercom/android/sdk/m5/IntercomRootActivity;",
         "Lio/intercom/android/sdk/activities/IntercomBaseComponentActivity;",
         "<init>",
         "()V",
         "onCreate",
         "",
         "savedInstanceState",
         "Landroid/os/Bundle;",
         "onDestroy",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class IntercomRootActivity extends IntercomBaseComponentActivity {
   public static int $stable;

   static {}

   @Override
   protected void onCreate(@Nullable Bundle var1) {
      super.onCreate(var1);
      boolean var10000;
      switch (ThemeManager.INSTANCE.getCurrentThemeMode()) {
         case DARK:
            var10000 = true;
            break;
         case LIGHT:
            var10000 = false;
            break;
         case SYSTEM:
            var10000 = (this.getResources().getConfiguration().uiMode & 48) == 32;
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      boolean var2 = var10000;
      SystemBarStyle var3 = var2 ? SystemBarStyle.Companion.dark(0) : SystemBarStyle.Companion.light(0, 0);
      EdgeToEdge.enable(this, var3, var3);
      ComponentActivityKt.setContent$default(
         this, null, ComposableLambdaKt.composableLambdaInstance(1535831366, true, new IntercomRootActivity$onCreate$1(this)), 1, null
      );
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      InMemoryWebViewCacheKt.clearWebViewCache();
      if (this.isFinishing()) {
         Injector.get().getDataLayer().clearOpenResponse();
      }
   }
}
