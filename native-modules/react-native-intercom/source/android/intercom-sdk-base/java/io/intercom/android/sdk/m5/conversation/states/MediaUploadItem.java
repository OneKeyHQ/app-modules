package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0003\b\u0081\b\u0018\u00002\u00020\u0001:\u0001!B!\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\u0006\u0010\u0010\u001a\u00020\u0011J\u0006\u0010\u0012\u001a\u00020\u0011J\u0006\u0010\u0013\u001a\u00020\u0011J\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015J\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017J\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0007HÆ\u0003J'\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÇ\u0001J\u0013\u0010\u001c\u001a\u00020\u00112\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001e\u001a\u00020\u001fH×\u0001J\t\u0010 \u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\"",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem;",
         "",
         "instanceId",
         "",
         "mediaData",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "uploadState",
         "Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem$UploadState;",
         "<init>",
         "(Ljava/lang/String;Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem$UploadState;)V",
         "getInstanceId",
         "()Ljava/lang/String;",
         "getMediaData",
         "()Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "getUploadState",
         "()Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem$UploadState;",
         "isUploading",
         "",
         "isSuccess",
         "isError",
         "getUploadedBlock",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "getUploadError",
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadError;",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "UploadState",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class MediaUploadItem {
   @NotNull
   private final String instanceId;
   @NotNull
   private final MediaData$Media mediaData;
   @NotNull
   private final MediaUploadItem$UploadState uploadState;
   public static int $stable;

   static {}

   public MediaUploadItem(@NotNull String var1, @NotNull MediaData$Media var2, @NotNull MediaUploadItem$UploadState var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "instanceId");
      Intrinsics.checkNotNullParameter(var2, "mediaData");
      Intrinsics.checkNotNullParameter(var3, "uploadState");
      this.instanceId = var1;
      this.mediaData = var2;
      this.uploadState = var3;
   }

   // $VF: synthetic method
   public MediaUploadItem(String var1, MediaData$Media var2, MediaUploadItem$UploadState var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (String) (UUID.randomUUID().toString()) : var1), var2, var3);
   }

   @NotNull
   public final String getInstanceId() {
      return this.instanceId;
   }

   @NotNull
   public final MediaData$Media getMediaData() {
      return this.mediaData;
   }

   @NotNull
   public final MediaUploadItem$UploadState getUploadState() {
      return this.uploadState;
   }

   public final boolean isUploading() {
      return this.uploadState instanceof MediaUploadItem$UploadState$Uploading;
   }

   public final boolean isSuccess() {
      return this.uploadState instanceof MediaUploadItem$UploadState$Success;
   }

   public final boolean isError() {
      return this.uploadState instanceof MediaUploadItem$UploadState$Error;
   }

   @Nullable
   public final Block$Builder getUploadedBlock() {
      MediaUploadItem$UploadState var1 = this.uploadState;
      return (var1 instanceof MediaUploadItem$UploadState$Success ? (MediaUploadItem$UploadState$Success)var1 : null) != null
         ? (var1 instanceof MediaUploadItem$UploadState$Success ? (MediaUploadItem$UploadState$Success)var1 : null).getBlock()
         : null;
   }

   @Nullable
   public final MediaUploadRepository$UploadError getUploadError() {
      MediaUploadItem$UploadState var1 = this.uploadState;
      return (var1 instanceof MediaUploadItem$UploadState$Error ? (MediaUploadItem$UploadState$Error)var1 : null) != null
         ? (var1 instanceof MediaUploadItem$UploadState$Error ? (MediaUploadItem$UploadState$Error)var1 : null).getError()
         : null;
   }

   @NotNull
   public final String component1() {
      return this.instanceId;
   }

   @NotNull
   public final MediaData$Media component2() {
      return this.mediaData;
   }

   @NotNull
   public final MediaUploadItem$UploadState component3() {
      return this.uploadState;
   }

   @NotNull
   public final MediaUploadItem copy(@NotNull String var1, @NotNull MediaData$Media var2, @NotNull MediaUploadItem$UploadState var3) {
      Intrinsics.checkNotNullParameter(var1, "instanceId");
      Intrinsics.checkNotNullParameter(var2, "mediaData");
      Intrinsics.checkNotNullParameter(var3, "uploadState");
      return new MediaUploadItem(var1, var2, var3);
   }

   // $VF: synthetic method
   public static MediaUploadItem copy$default(MediaUploadItem var0, String var1, MediaData$Media var2, MediaUploadItem$UploadState var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.instanceId;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.mediaData;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.uploadState;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "MediaUploadItem(instanceId=" + this.instanceId + ", mediaData=" + this.mediaData + ", uploadState=" + this.uploadState + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.instanceId.hashCode();
      var1 = var1 * 31 + this.mediaData.hashCode();
      return var1 * 31 + this.uploadState.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof MediaUploadItem)) {
         return false;
      } else {
         MediaUploadItem var2 = (MediaUploadItem)var1;
         if (!Intrinsics.areEqual(this.instanceId, var2.instanceId)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.mediaData, var2.mediaData) ? false : Intrinsics.areEqual(this.uploadState, var2.uploadState);
         }
      }
   }
}
