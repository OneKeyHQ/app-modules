package io.intercom.android.sdk.m5.helpcenter;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider.Factory;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.api.MessengerApi;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.data.CommonRepository;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.metrics.MetricTracker;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J%\u0010\u0002\u001a\u0002H\u0003\"\b\b\u0000\u0010\u0003*\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u0002H\u00030\u0006H\u0016¢\u0006\u0002\u0010\u0007¨\u0006\b",
   d2 = {
         "io/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel$Companion$factory$1",
         "Landroidx/lifecycle/ViewModelProvider$Factory;",
         "create",
         "T",
         "Landroidx/lifecycle/ViewModel;",
         "modelClass",
         "Ljava/lang/Class;",
         "(Ljava/lang/Class;)Landroidx/lifecycle/ViewModel;",
         "intercom-sdk-base_release"
   }
)
public final class HelpCenterViewModel$Companion$factory$1 implements Factory {
   // $VF: synthetic field
   final HelpCenterApi $helpCenterApi;
   // $VF: synthetic field
   final String $place;

   HelpCenterViewModel$Companion$factory$1(HelpCenterApi var1, String var2) {
      this.$helpCenterApi = var1;
      this.$place = var2;
   }

   @Override
   public ViewModel create(Class var1) {
      Intrinsics.checkNotNullParameter(var1, "modelClass");
      IntercomDataLayer var2 = Injector.get().getDataLayer();
      HelpCenterApi var10002 = this.$helpCenterApi;
      AppConfig var10003 = (AppConfig)Injector.get().getAppConfigProvider().get();
      Intrinsics.checkNotNullExpressionValue(var10003, "get(...)");
      var10003 = var10003;
      MetricTracker var10004 = Injector.get().getMetricTracker();
      Intrinsics.checkNotNullExpressionValue(var10004, "getMetricTracker(...)");
      String var10005 = this.$place;
      Intrinsics.checkNotNull(var2);
      MessengerApi var10011 = Injector.get().getMessengerApi();
      Intrinsics.checkNotNullExpressionValue(var10011, "getMessengerApi(...)");
      return new HelpCenterViewModel(var10002, var10003, var10004, var10005, null, null, var2, new CommonRepository(var10011, var2), 48, null);
   }
}
