package io.intercom.android.sdk.m5.preview.ui;

import android.content.Context;
import android.content.Intent;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgs;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u001e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b¨\u0006\f",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/ui/IntercomPreviewActivity$Companion;",
         "",
         "<init>",
         "()V",
         "createIntent",
         "Landroid/content/Intent;",
         "context",
         "Landroid/content/Context;",
         "args",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewArgs;",
         "initialIndex",
         "",
         "intercom-sdk-base_release"
   }
)
public final class IntercomPreviewActivity$Companion {
   private IntercomPreviewActivity$Companion() {
   }

   @NotNull
   public final Intent createIntent(@NotNull Context var1, @NotNull IntercomPreviewArgs var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "args");
      Intent var3 = new Intent(var1, IntercomPreviewActivity.class);
      Intent var4 = var3;
      int var5 = 0;
      var4.putExtra("INTERCOM_PREVIEW_ARGS", var2);
      return var3;
   }

   @NotNull
   public final Intent createIntent(@NotNull Context var1, @NotNull IntercomPreviewArgs var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "args");
      Intent var4 = new Intent(var1, IntercomPreviewActivity.class);
      Intent var5 = var4;
      int var6 = 0;
      var5.putExtra("INTERCOM_PREVIEW_ARGS", IntercomPreviewArgs.copy$default(var2, null, null, null, false, var3, null, 47, null));
      return var4;
   }

   // $VF: synthetic method
   public IntercomPreviewActivity$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
