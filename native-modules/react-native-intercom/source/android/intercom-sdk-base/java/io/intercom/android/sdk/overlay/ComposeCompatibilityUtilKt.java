package io.intercom.android.sdk.overlay;

import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.platform.ComposeView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewTreeLifecycleOwner;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Participant;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000(\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0012\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\u0000\u001a\u000e\u0010\u0004\u001a\u00020\u0001*\u0004\u0018\u00010\u0003H\u0000\u001a\u001e\u0010\u0005\u001a\u00020\u0006*\u0004\u0018\u00010\u00032\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0000\u001a\u001e\u0010\u0005\u001a\u00020\u0006*\u0004\u0018\u00010\u00032\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\nH\u0000¨\u0006\r",
   d2 = {
         "isLegacyActivity",
         "",
         "composeView",
         "Landroidx/compose/ui/platform/ComposeView;",
         "hideForLegacyActivity",
         "addAvatarIconToCompose",
         "",
         "lastAdmin",
         "Lio/intercom/android/sdk/models/Participant;",
         "appConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "part",
         "Lio/intercom/android/sdk/models/Part;",
         "intercom-sdk-base_release"
   }
)
public final class ComposeCompatibilityUtilKt {
   public static final boolean isLegacyActivity(@Nullable ComposeView var0) {
      if (var0 == null) {
         return true;
      }

      boolean var1;
      try {
         LifecycleOwner var10000 = ViewTreeLifecycleOwner.get(var0);
         var1 = (var10000 != null ? var10000.getLifecycle() : null) == null;
         if (var1) {
            Injector.get().getMetricTracker().receivedInAppOnLegacyActivity();
         }

         var1 = var1;
      } catch (Exception var3) {
         Injector.get().getMetricTracker().receivedInAppOnLegacyActivity();
         var1 = true;
      }

      return var1;
   }

   public static final boolean hideForLegacyActivity(@Nullable ComposeView var0) {
      if (isLegacyActivity(var0)) {
         if (var0 != null) {
            var0.setVisibility(8);
         }

         return true;
      } else {
         return false;
      }
   }

   public static final void addAvatarIconToCompose(@Nullable ComposeView var0, @NotNull Participant var1, @NotNull AppConfig var2) {
      Intrinsics.checkNotNullParameter(var1, "lastAdmin");
      Intrinsics.checkNotNullParameter(var2, "appConfig");
      if (var0 != null && !isLegacyActivity(var0)) {
         ComposeView var3 = var0;
         ComposeView var4 = var3;
         int var5 = 0;
         var4.setContent(ComposableLambdaKt.composableLambdaInstance(421691537, true, new ComposeCompatibilityUtilKt$addAvatarIconToCompose$1$1(var2, var1)));
      }
   }

   public static final void addAvatarIconToCompose(@Nullable ComposeView var0, @NotNull Part var1, @NotNull AppConfig var2) {
      Intrinsics.checkNotNullParameter(var1, "part");
      Intrinsics.checkNotNullParameter(var2, "appConfig");
      if (var0 != null && !isLegacyActivity(var0)) {
         ComposeView var3 = var0;
         ComposeView var4 = var3;
         int var5 = 0;
         var4.setContent(ComposableLambdaKt.composableLambdaInstance(38990427, true, new ComposeCompatibilityUtilKt$addAvatarIconToCompose$2$1(var2, var1)));
      }
   }
}
