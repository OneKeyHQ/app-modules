package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\b\u0087\b\u0018\u00002\u00020\u0001:\u0001+BI\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\t\u0012\b\b\u0002\u0010\u000b\u001a\u00020\t\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r¢\u0006\u0004\b\u000e\u0010\u000fJ\t\u0010\u001d\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001e\u001a\u00020\u0005HÆ\u0003J\u000b\u0010\u001f\u001a\u0004\u0018\u00010\u0007HÆ\u0003J\t\u0010 \u001a\u00020\tHÆ\u0003J\t\u0010!\u001a\u00020\tHÆ\u0003J\t\u0010\"\u001a\u00020\tHÆ\u0003J\u000b\u0010#\u001a\u0004\u0018\u00010\rHÆ\u0003JS\u0010$\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\t2\b\b\u0002\u0010\u000b\u001a\u00020\t2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\rHÇ\u0001J\u0013\u0010%\u001a\u00020\t2\b\u0010&\u001a\u0004\u0018\u00010'H×\u0003J\t\u0010(\u001a\u00020)H×\u0001J\t\u0010*\u001a\u00020\u001aH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0016R\u0011\u0010\n\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0016R\u0013\u0010\f\u001a\u0004\u0018\u00010\r¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0014\u0010\u0019\u001a\u00020\u001a8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u001c¨\u0006,",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$BubbleMessageRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "partWrapper",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$BubbleMessageRow$PartWrapper;",
         "groupingPosition",
         "Lio/intercom/android/sdk/m5/conversation/states/GroupingPosition;",
         "failedMediaUploadData",
         "Lio/intercom/android/sdk/m5/conversation/states/PendingMessage$FailedMediaUploadData;",
         "isFailed",
         "",
         "isRetryable",
         "isPending",
         "senderMetadata",
         "Lio/intercom/android/sdk/m5/conversation/states/SenderMetadata;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/states/ContentRow$BubbleMessageRow$PartWrapper;Lio/intercom/android/sdk/m5/conversation/states/GroupingPosition;Lio/intercom/android/sdk/m5/conversation/states/PendingMessage$FailedMediaUploadData;ZZZLio/intercom/android/sdk/m5/conversation/states/SenderMetadata;)V",
         "getPartWrapper",
         "()Lio/intercom/android/sdk/m5/conversation/states/ContentRow$BubbleMessageRow$PartWrapper;",
         "getGroupingPosition",
         "()Lio/intercom/android/sdk/m5/conversation/states/GroupingPosition;",
         "getFailedMediaUploadData",
         "()Lio/intercom/android/sdk/m5/conversation/states/PendingMessage$FailedMediaUploadData;",
         "()Z",
         "getSenderMetadata",
         "()Lio/intercom/android/sdk/m5/conversation/states/SenderMetadata;",
         "key",
         "",
         "getKey",
         "()Ljava/lang/String;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "copy",
         "equals",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "PartWrapper",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ContentRow$BubbleMessageRow implements ContentRow {
   @NotNull
   private final ContentRow$BubbleMessageRow$PartWrapper partWrapper;
   @NotNull
   private final GroupingPosition groupingPosition;
   @Nullable
   private final PendingMessage$FailedMediaUploadData failedMediaUploadData;
   private final boolean isFailed;
   private final boolean isRetryable;
   private final boolean isPending;
   @Nullable
   private final SenderMetadata senderMetadata;
   public static final int $stable;

   static { $stable = 8; }

   public ContentRow$BubbleMessageRow(
      @NotNull ContentRow$BubbleMessageRow$PartWrapper var1,
      @NotNull GroupingPosition var2,
      @Nullable PendingMessage$FailedMediaUploadData var3,
      boolean var4,
      boolean var5,
      boolean var6,
      @Nullable SenderMetadata var7
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "partWrapper");
      Intrinsics.checkNotNullParameter(var2, "groupingPosition");
      this.partWrapper = var1;
      this.groupingPosition = var2;
      this.failedMediaUploadData = var3;
      this.isFailed = var4;
      this.isRetryable = var5;
      this.isPending = var6;
      this.senderMetadata = var7;
   }

   // $VF: synthetic method
   public ContentRow$BubbleMessageRow(
      ContentRow$BubbleMessageRow$PartWrapper var1,
      GroupingPosition var2,
      PendingMessage$FailedMediaUploadData var3,
      boolean var4,
      boolean var5,
      boolean var6,
      SenderMetadata var7,
      int var8,
      DefaultConstructorMarker var9
   ) {
      this(var1, var2, var3, var4, (((var8 & 16) != 0) ? (boolean) (true) : var5), (((var8 & 32) != 0) ? (boolean) (false) : var6), (((var8 & 64) != 0) ? (SenderMetadata) (null) : var7));
   }

   @NotNull
   public final ContentRow$BubbleMessageRow$PartWrapper getPartWrapper() {
      return this.partWrapper;
   }

   @NotNull
   public final GroupingPosition getGroupingPosition() {
      return this.groupingPosition;
   }

   @Nullable
   public final PendingMessage$FailedMediaUploadData getFailedMediaUploadData() {
      return this.failedMediaUploadData;
   }

   public final boolean isFailed() {
      return this.isFailed;
   }

   public final boolean isRetryable() {
      return this.isRetryable;
   }

   public final boolean isPending() {
      return this.isPending;
   }

   @Nullable
   public final SenderMetadata getSenderMetadata() {
      return this.senderMetadata;
   }

   @NotNull
   @Override
   public String getKey() {
      return "message_row_" + this.partWrapper.getPart().getId();
   }

   @NotNull
   public final ContentRow$BubbleMessageRow$PartWrapper component1() {
      return this.partWrapper;
   }

   @NotNull
   public final GroupingPosition component2() {
      return this.groupingPosition;
   }

   @Nullable
   public final PendingMessage$FailedMediaUploadData component3() {
      return this.failedMediaUploadData;
   }

   public final boolean component4() {
      return this.isFailed;
   }

   public final boolean component5() {
      return this.isRetryable;
   }

   public final boolean component6() {
      return this.isPending;
   }

   @Nullable
   public final SenderMetadata component7() {
      return this.senderMetadata;
   }

   @NotNull
   public final ContentRow$BubbleMessageRow copy(
      @NotNull ContentRow$BubbleMessageRow$PartWrapper var1,
      @NotNull GroupingPosition var2,
      @Nullable PendingMessage$FailedMediaUploadData var3,
      boolean var4,
      boolean var5,
      boolean var6,
      @Nullable SenderMetadata var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "partWrapper");
      Intrinsics.checkNotNullParameter(var2, "groupingPosition");
      return new ContentRow$BubbleMessageRow(var1, var2, var3, var4, var5, var6, var7);
   }

   // $VF: synthetic method
   public static ContentRow$BubbleMessageRow copy$default(
      ContentRow$BubbleMessageRow var0,
      ContentRow$BubbleMessageRow$PartWrapper var1,
      GroupingPosition var2,
      PendingMessage$FailedMediaUploadData var3,
      boolean var4,
      boolean var5,
      boolean var6,
      SenderMetadata var7,
      int var8,
      Object var9
   ) {
      if ((var8 & 1) != 0) {
         var1 = var0.partWrapper;
      }

      if ((var8 & 2) != 0) {
         var2 = var0.groupingPosition;
      }

      if ((var8 & 4) != 0) {
         var3 = var0.failedMediaUploadData;
      }

      if ((var8 & 8) != 0) {
         var4 = var0.isFailed;
      }

      if ((var8 & 16) != 0) {
         var5 = var0.isRetryable;
      }

      if ((var8 & 32) != 0) {
         var6 = var0.isPending;
      }

      if ((var8 & 64) != 0) {
         var7 = var0.senderMetadata;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7);
   }

   @NotNull
   @Override
   public String toString() {
      return "BubbleMessageRow(partWrapper="
         + this.partWrapper
         + ", groupingPosition="
         + this.groupingPosition
         + ", failedMediaUploadData="
         + this.failedMediaUploadData
         + ", isFailed="
         + this.isFailed
         + ", isRetryable="
         + this.isRetryable
         + ", isPending="
         + this.isPending
         + ", senderMetadata="
         + this.senderMetadata
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.partWrapper.hashCode();
      var1 = var1 * 31 + this.groupingPosition.hashCode();
      var1 = var1 * 31 + (this.failedMediaUploadData == null ? 0 : this.failedMediaUploadData.hashCode());
      var1 = var1 * 31 + Boolean.hashCode(this.isFailed);
      var1 = var1 * 31 + Boolean.hashCode(this.isRetryable);
      var1 = var1 * 31 + Boolean.hashCode(this.isPending);
      return var1 * 31 + (this.senderMetadata == null ? 0 : this.senderMetadata.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ContentRow$BubbleMessageRow)) {
         return false;
      } else {
         ContentRow$BubbleMessageRow var2 = (ContentRow$BubbleMessageRow)var1;
         if (!Intrinsics.areEqual(this.partWrapper, var2.partWrapper)) {
            return false;
         } else if (this.groupingPosition != var2.groupingPosition) {
            return false;
         } else if (!Intrinsics.areEqual(this.failedMediaUploadData, var2.failedMediaUploadData)) {
            return false;
         } else if (this.isFailed != var2.isFailed) {
            return false;
         } else if (this.isRetryable != var2.isRetryable) {
            return false;
         } else {
            return this.isPending != var2.isPending ? false : Intrinsics.areEqual(this.senderMetadata, var2.senderMetadata);
         }
      }
   }
}
