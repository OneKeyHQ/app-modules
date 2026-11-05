package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\t\u0010\u0014\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/PendingMessage$FailedMediaUploadData;",
         "",
         "clientAssignedUUID",
         "",
         "mediaData",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "<init>",
         "(Ljava/lang/String;Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;)V",
         "getClientAssignedUUID",
         "()Ljava/lang/String;",
         "getMediaData",
         "()Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class PendingMessage$FailedMediaUploadData {
   @NotNull
   private final String clientAssignedUUID;
   @NotNull
   private final MediaData$Media mediaData;
   public static final int $stable;

   static { $stable = 8; }

   public PendingMessage$FailedMediaUploadData(@NotNull String var1, @NotNull MediaData$Media var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "clientAssignedUUID");
      Intrinsics.checkNotNullParameter(var2, "mediaData");
      this.clientAssignedUUID = var1;
      this.mediaData = var2;
   }

   @NotNull
   public final String getClientAssignedUUID() {
      return this.clientAssignedUUID;
   }

   @NotNull
   public final MediaData$Media getMediaData() {
      return this.mediaData;
   }

   @NotNull
   public final String component1() {
      return this.clientAssignedUUID;
   }

   @NotNull
   public final MediaData$Media component2() {
      return this.mediaData;
   }

   @NotNull
   public final PendingMessage$FailedMediaUploadData copy(@NotNull String var1, @NotNull MediaData$Media var2) {
      Intrinsics.checkNotNullParameter(var1, "clientAssignedUUID");
      Intrinsics.checkNotNullParameter(var2, "mediaData");
      return new PendingMessage$FailedMediaUploadData(var1, var2);
   }

   // $VF: synthetic method
   public static PendingMessage$FailedMediaUploadData copy$default(
      PendingMessage$FailedMediaUploadData var0, String var1, MediaData$Media var2, int var3, Object var4
   ) {
      if ((var3 & 1) != 0) {
         var1 = var0.clientAssignedUUID;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.mediaData;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "FailedMediaUploadData(clientAssignedUUID=" + this.clientAssignedUUID + ", mediaData=" + this.mediaData + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.clientAssignedUUID.hashCode();
      return var1 * 31 + this.mediaData.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof PendingMessage$FailedMediaUploadData)) {
         return false;
      }

      PendingMessage$FailedMediaUploadData var2 = (PendingMessage$FailedMediaUploadData)var1;
      return !Intrinsics.areEqual(this.clientAssignedUUID, var2.clientAssignedUUID) ? false : Intrinsics.areEqual(this.mediaData, var2.mediaData);
   }
}
