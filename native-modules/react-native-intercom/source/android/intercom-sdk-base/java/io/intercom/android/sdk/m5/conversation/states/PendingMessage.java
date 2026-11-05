package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.models.Part;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001:\u0001\u001bB-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0005HÆ\u0003J\u000b\u0010\u0013\u001a\u0004\u0018\u00010\bHÆ\u0003J3\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\bHÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00052\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0017\u001a\u00020\u0018H×\u0001J\t\u0010\u0019\u001a\u00020\u001aH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\rR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001c",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/PendingMessage;",
         "",
         "part",
         "Lio/intercom/android/sdk/models/Part;",
         "isFailed",
         "",
         "isRetryable",
         "failedMediaUploadData",
         "Lio/intercom/android/sdk/m5/conversation/states/PendingMessage$FailedMediaUploadData;",
         "<init>",
         "(Lio/intercom/android/sdk/models/Part;ZZLio/intercom/android/sdk/m5/conversation/states/PendingMessage$FailedMediaUploadData;)V",
         "getPart",
         "()Lio/intercom/android/sdk/models/Part;",
         "()Z",
         "getFailedMediaUploadData",
         "()Lio/intercom/android/sdk/m5/conversation/states/PendingMessage$FailedMediaUploadData;",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "FailedMediaUploadData",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class PendingMessage {
   @NotNull
   private final Part part;
   private final boolean isFailed;
   private final boolean isRetryable;
   @Nullable
   private final PendingMessage$FailedMediaUploadData failedMediaUploadData;
   public static final int $stable;

   static { $stable = 8; }

   public PendingMessage(@NotNull Part var1, boolean var2, boolean var3, @Nullable PendingMessage$FailedMediaUploadData var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "part");
      this.part = var1;
      this.isFailed = var2;
      this.isRetryable = var3;
      this.failedMediaUploadData = var4;
   }

   // $VF: synthetic method
   public PendingMessage(Part var1, boolean var2, boolean var3, PendingMessage$FailedMediaUploadData var4, int var5, DefaultConstructorMarker var6) {
      this(var1, var2, (((var5 & 4) != 0) ? (boolean) (true) : var3), (((var5 & 8) != 0) ? (PendingMessage$FailedMediaUploadData) (null) : var4));
   }

   @NotNull
   public final Part getPart() {
      return this.part;
   }

   public final boolean isFailed() {
      return this.isFailed;
   }

   public final boolean isRetryable() {
      return this.isRetryable;
   }

   @Nullable
   public final PendingMessage$FailedMediaUploadData getFailedMediaUploadData() {
      return this.failedMediaUploadData;
   }

   @NotNull
   public final Part component1() {
      return this.part;
   }

   public final boolean component2() {
      return this.isFailed;
   }

   public final boolean component3() {
      return this.isRetryable;
   }

   @Nullable
   public final PendingMessage$FailedMediaUploadData component4() {
      return this.failedMediaUploadData;
   }

   @NotNull
   public final PendingMessage copy(@NotNull Part var1, boolean var2, boolean var3, @Nullable PendingMessage$FailedMediaUploadData var4) {
      Intrinsics.checkNotNullParameter(var1, "part");
      return new PendingMessage(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static PendingMessage copy$default(
      PendingMessage var0, Part var1, boolean var2, boolean var3, PendingMessage$FailedMediaUploadData var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.part;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.isFailed;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.isRetryable;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.failedMediaUploadData;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "PendingMessage(part="
         + this.part
         + ", isFailed="
         + this.isFailed
         + ", isRetryable="
         + this.isRetryable
         + ", failedMediaUploadData="
         + this.failedMediaUploadData
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.part.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isFailed);
      var1 = var1 * 31 + Boolean.hashCode(this.isRetryable);
      return var1 * 31 + (this.failedMediaUploadData == null ? 0 : this.failedMediaUploadData.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof PendingMessage)) {
         return false;
      } else {
         PendingMessage var2 = (PendingMessage)var1;
         if (!Intrinsics.areEqual(this.part, var2.part)) {
            return false;
         } else if (this.isFailed != var2.isFailed) {
            return false;
         } else {
            return this.isRetryable != var2.isRetryable ? false : Intrinsics.areEqual(this.failedMediaUploadData, var2.failedMediaUploadData);
         }
      }
   }
}
