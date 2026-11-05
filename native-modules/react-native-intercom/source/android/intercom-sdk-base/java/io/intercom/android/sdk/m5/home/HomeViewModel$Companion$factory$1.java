package io.intercom.android.sdk.m5.home;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider.Factory;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.api.MessengerApi;
import io.intercom.android.sdk.m5.data.CommonRepository;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.home.data.HomeRepository;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J%\u0010\u0002\u001a\u0002H\u0003\"\b\b\u0000\u0010\u0003*\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u0002H\u00030\u0006H\u0016¢\u0006\u0002\u0010\u0007¨\u0006\b",
   d2 = {
         "io/intercom/android/sdk/m5/home/HomeViewModel$Companion$factory$1",
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
public final class HomeViewModel$Companion$factory$1 implements Factory {
   // $VF: synthetic field
   final Lifecycle $lifecycle;

   HomeViewModel$Companion$factory$1(Lifecycle var1) {
      this.$lifecycle = var1;
   }

   @Override
   public ViewModel create(Class var1) {
      Intrinsics.checkNotNullParameter(var1, "modelClass");
      IntercomDataLayer var2 = Injector.get().getDataLayer();
      HomeRepository var3 = new HomeRepository(null, null, 3, null);
      MessengerApi var10002 = Injector.get().getMessengerApi();
      Intrinsics.checkNotNullExpressionValue(var10002, "getMessengerApi(...)");
      Intrinsics.checkNotNull(var2);
      CommonRepository var4 = new CommonRepository(var10002, var2);
      IntercomDataLayer var5 = var2;
      return new HomeViewModel(this.$lifecycle, var3, var4, null, var5, null, 40, null);
   }
}
