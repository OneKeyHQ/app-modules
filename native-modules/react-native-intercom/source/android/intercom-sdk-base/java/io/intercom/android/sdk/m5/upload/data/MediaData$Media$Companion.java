package io.intercom.android.sdk.m5.upload.data;

import android.net.Uri;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005¨\u0006\u0006",
   d2 = {
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media$Companion;",
         "",
         "<init>",
         "()V",
         "empty",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media$Image;",
         "intercom-sdk-base_release"
   }
)
public final class MediaData$Media$Companion {
   // $VF: synthetic field
   static final MediaData$Media$Companion $$INSTANCE = new MediaData$Media$Companion();

   private MediaData$Media$Companion() {
   }

   @NotNull
   public final MediaData$Media$Image empty() {
      Uri var10000 = Uri.EMPTY;
      Intrinsics.checkNotNullExpressionValue(Uri.EMPTY, "EMPTY");
      Uri var1 = var10000;
      Map var2 = MapsKt.emptyMap();
      return new MediaData$Media$Image("", 0, 0, 0L, "", var1, var2);
   }
}
