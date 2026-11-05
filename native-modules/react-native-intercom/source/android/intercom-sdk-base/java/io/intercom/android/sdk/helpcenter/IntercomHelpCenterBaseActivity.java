package io.intercom.android.sdk.helpcenter;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.activities.IntercomActivity;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.utilities.ContextLocaliser;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b!\u0018\u00002\u00020\u00012\u00020\u0002B\t\b\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0014J\u0010\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\bH\u0002J\u0012\u0010\u000b\u001a\u00020\u00062\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0014J\b\u0010\u000e\u001a\u00020\u0006H\u0014¨\u0006\u000f",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/IntercomHelpCenterBaseActivity;",
         "Landroidx/appcompat/app/AppCompatActivity;",
         "Lio/intercom/android/sdk/activities/IntercomActivity;",
         "<init>",
         "()V",
         "attachBaseContext",
         "",
         "newBase",
         "Landroid/content/Context;",
         "localizedContext",
         "context",
         "onCreate",
         "savedInstanceState",
         "Landroid/os/Bundle;",
         "onDestroy",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class IntercomHelpCenterBaseActivity extends AppCompatActivity implements IntercomActivity {
   public static int $stable;

   static {}

   @Override
   protected void attachBaseContext(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "newBase");
      super.attachBaseContext(this.localizedContext(var1));
   }

   private final Context localizedContext(Context var1) {
      AppConfig var2 = (AppConfig)Injector.get().getAppConfigProvider().get();
      Configuration var3 = new Configuration(var1.getResources().getConfiguration());
      Locale var4 = ContextLocaliser.convertToLocale(var2.getHelpCenterLocale());
      var3.setLocale(var4);
      Context var10000 = var1.createConfigurationContext(var3);
      Intrinsics.checkNotNullExpressionValue(var10000, "createConfigurationContext(...)");
      return var10000;
   }

   @Override
   protected void onCreate(@Nullable Bundle var1) {
      super.onCreate(var1);
      Injector.get().getActivityFinisher().register(this);
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      Injector.get().getActivityFinisher().unregister(this);
   }
}
