package io.intercom.android.sdk.m5.navigation.transitions;

import android.os.Bundle;
import androidx.core.os.BundleCompat;
import androidx.navigation.NavType;
import com.google.gson.Gson;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000'\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001J\u0019\u0010\u0003\u001a\u00020\u00022\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0096\u0002J\u0010\u0010\b\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\u0007H\u0016J \u0010\n\u001a\u00020\u000b2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u0002H\u0016J\u0010\u0010\f\u001a\u00020\u00022\u0006\u0010\r\u001a\u00020\u0007H\u0002¨\u0006\u000e",
   d2 = {
         "io/intercom/android/sdk/m5/navigation/transitions/TransitionStyleKt$TransitionArgNavType$1",
         "Landroidx/navigation/NavType;",
         "Lio/intercom/android/sdk/m5/navigation/transitions/TransitionArgs;",
         "get",
         "bundle",
         "Landroid/os/Bundle;",
         "key",
         "",
         "parseValue",
         "value",
         "put",
         "",
         "toTransitionArgs",
         "transitionArgs",
         "intercom-sdk-base_release"
   }
)
public final class TransitionStyleKt$TransitionArgNavType$1 extends NavType {
   TransitionStyleKt$TransitionArgNavType$1() {
      super(false);
   }

   public TransitionArgs get(Bundle var1, String var2) {
      Intrinsics.checkNotNullParameter(var1, "bundle");
      Intrinsics.checkNotNullParameter(var2, "key");
      TransitionArgs var10000 = (TransitionArgs)BundleCompat.getParcelable(var1, var2, TransitionArgs.class);
      if (var10000 == null) {
         var10000 = new TransitionArgs(null, null, null, null, 15, null);
      }

      return var10000;
   }

   public TransitionArgs parseValue(String var1) {
      Intrinsics.checkNotNullParameter(var1, "value");
      return this.toTransitionArgs(var1);
   }

   public void put(Bundle var1, String var2, TransitionArgs var3) {
      Intrinsics.checkNotNullParameter(var1, "bundle");
      Intrinsics.checkNotNullParameter(var2, "key");
      Intrinsics.checkNotNullParameter(var3, "value");
      var1.putParcelable(var2, var3);
   }

   private final TransitionArgs toTransitionArgs(String var1) {
      Object var10000 = new Gson().fromJson(var1, TransitionArgs.class);
      Intrinsics.checkNotNullExpressionValue(var10000, "fromJson(...)");
      return (TransitionArgs)var10000;
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void put(Bundle var1, String var2, Object var3) {
      this.put(var1, var2, (TransitionArgs)var3);
   }
}
