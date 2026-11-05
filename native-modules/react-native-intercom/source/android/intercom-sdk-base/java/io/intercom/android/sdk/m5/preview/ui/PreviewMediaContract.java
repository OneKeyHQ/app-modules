package io.intercom.android.sdk.m5.preview.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.core.os.BundleCompat;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgs;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\b\u0007\u0018\u00002\u0019\u0012\u0004\u0012\u00020\u0002\u0012\u000f\u0012\r\u0012\t\u0012\u00070\u0004¢\u0006\u0002\b\u00050\u00030\u0001B\t\b\u0007¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0002H\u0016J \u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\tH\u0016¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/ui/PreviewMediaContract;",
         "Landroidx/activity/result/contract/ActivityResultContract;",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewArgs;",
         "",
         "Landroid/net/Uri;",
         "Lkotlin/jvm/JvmSuppressWildcards;",
         "<init>",
         "()V",
         "createIntent",
         "Landroid/content/Intent;",
         "context",
         "Landroid/content/Context;",
         "input",
         "parseResult",
         "resultCode",
         "",
         "intent",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class PreviewMediaContract extends ActivityResultContract {
   public static int $stable;

   static {}

   @NotNull
   public Intent createIntent(@NotNull Context var1, @NotNull IntercomPreviewArgs var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "input");
      return IntercomPreviewActivity.Companion.createIntent(var1, var2);
   }

   @NotNull
   public List parseResult(int var1, @Nullable Intent var2) {
      Intent var4 = var2;
      int var6 = 0;
      Intent var3 = var1 == -1 ? var4 : null;
      if (var3 != null) {
         List var13;
         label27: {
            Intent itx = var3;
            int var7 = 0;
            Bundle var10000 = itx.getExtras();
            if (var10000 != null) {
               Bundle var8 = var10000;
               int var9 = 0;
               var13 = BundleCompat.getParcelableArrayList(var8, "MEDIA_RESULT_URIS", Uri.class);
               if (var13 != null) {
                  var13 = var13;
                  break label27;
               }
            }

            var13 = CollectionsKt.emptyList();
         }

         List var10 = var13;
         if (var10 != null) {
            return var10;
         }
      }

      return CollectionsKt.emptyList();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Intent createIntent(Context var1, Object var2) {
      return this.createIntent(var1, (IntercomPreviewArgs)var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
