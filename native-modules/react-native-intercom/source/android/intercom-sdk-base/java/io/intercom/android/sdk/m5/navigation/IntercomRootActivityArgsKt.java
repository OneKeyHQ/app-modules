package io.intercom.android.sdk.m5.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.os.BundleCompat;
import io.intercom.android.sdk.m5.IntercomRootActivity;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000.\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\u001a\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0000\u001a7\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u00042\f\b\u0002\u0010\u000b\u001a\u0006\u0012\u0002\b\u00030\f2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0000¢\u0006\u0002\u0010\u000f\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0010",
   d2 = {
         "BUNDLE",
         "",
         "BUNDLE_ARGS",
         "getArgsForIntent",
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs;",
         "intent",
         "Landroid/content/Intent;",
         "getIntentForArgs",
         "context",
         "Landroid/content/Context;",
         "intercomRootActivityArgs",
         "activityName",
         "Ljava/lang/Class;",
         "intentFlags",
         "",
         "(Landroid/content/Context;Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs;Ljava/lang/Class;Ljava/lang/Integer;)Landroid/content/Intent;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nIntercomRootActivityArgs.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomRootActivityArgs.kt\nio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,107:1\n1#2:108\n*E\n"
)
public final class IntercomRootActivityArgsKt {
   @NotNull
   private static final String BUNDLE = "BUNDLE";
   @NotNull
   private static final String BUNDLE_ARGS = "BUNDLE_ARGS";

   @NotNull
   public static final IntercomRootActivityArgs getArgsForIntent(@NotNull Intent var0) {
      Intrinsics.checkNotNullParameter(var0, "intent");
      Bundle var1 = var0.getBundleExtra("BUNDLE");
      IntercomRootActivityArgs var10000;
      if (var1 != null) {
         var10000 = (IntercomRootActivityArgs)BundleCompat.getParcelable(var1, "BUNDLE_ARGS", IntercomRootActivityArgs.class);
         if (var10000 == null) {
            var10000 = IntercomRootActivityArgs$NoContent.INSTANCE;
         }
      } else {
         var10000 = IntercomRootActivityArgs$NoContent.INSTANCE;
      }

      return var10000;
   }

   @NotNull
   public static final Intent getIntentForArgs(@NotNull Context var0, @NotNull IntercomRootActivityArgs var1, @NotNull Class var2, @Nullable Integer var3) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "intercomRootActivityArgs");
      Intrinsics.checkNotNullParameter(var2, "activityName");
      Intent var4 = new Intent(var0, var2);
      Intent var5 = var4;
      int var6 = 0;
      Bundle var7 = new Bundle();
      var7.putParcelable("BUNDLE_ARGS", var1);
      var5.putExtra("BUNDLE", var7);
      if (var3 != null) {
         int var8 = var3.intValue();
         int var9 = 0;
         var5.setFlags(var8);
      }

      return var4;
   }

   // $VF: synthetic method
   public static Intent getIntentForArgs$default(Context var0, IntercomRootActivityArgs var1, Class var2, Integer var3, int var4, Object var5) {
      if ((var4 & 4) != 0) {
         var2 = IntercomRootActivity.class;
      }

      if ((var4 & 8) != 0) {
         var3 = 268435456;
      }

      return getIntentForArgs(var0, var1, var2, var3);
   }
}
