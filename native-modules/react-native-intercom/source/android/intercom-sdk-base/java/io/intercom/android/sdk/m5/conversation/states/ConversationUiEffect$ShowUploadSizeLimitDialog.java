package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH×\u0003J\t\u0010\u000e\u001a\u00020\u000fH×\u0001J\t\u0010\u0010\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiEffect$ShowUploadSizeLimitDialog;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiEffect;",
         "uploadSizeLimitMB",
         "",
         "<init>",
         "(Ljava/lang/String;)V",
         "getUploadSizeLimitMB",
         "()Ljava/lang/String;",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ConversationUiEffect$ShowUploadSizeLimitDialog extends ConversationUiEffect {
   @NotNull
   private final String uploadSizeLimitMB;
   public static int $stable;

   static {}

   public ConversationUiEffect$ShowUploadSizeLimitDialog(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "uploadSizeLimitMB");
      super(null);
      this.uploadSizeLimitMB = var1;
   }

   @NotNull
   public final String getUploadSizeLimitMB() {
      return this.uploadSizeLimitMB;
   }

   @NotNull
   public final String component1() {
      return this.uploadSizeLimitMB;
   }

   @NotNull
   public final ConversationUiEffect$ShowUploadSizeLimitDialog copy(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "uploadSizeLimitMB");
      return new ConversationUiEffect$ShowUploadSizeLimitDialog(var1);
   }

   // $VF: synthetic method
   public static ConversationUiEffect$ShowUploadSizeLimitDialog copy$default(
      ConversationUiEffect$ShowUploadSizeLimitDialog var0, String var1, int var2, Object var3
   ) {
      if ((var2 & 1) != 0) {
         var1 = var0.uploadSizeLimitMB;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "ShowUploadSizeLimitDialog(uploadSizeLimitMB=" + this.uploadSizeLimitMB + ')';
   }

   @Override
   public int hashCode() {
      return this.uploadSizeLimitMB.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ConversationUiEffect$ShowUploadSizeLimitDialog)) {
         return false;
      }

      ConversationUiEffect$ShowUploadSizeLimitDialog var2 = (ConversationUiEffect$ShowUploadSizeLimitDialog)var1;
      return Intrinsics.areEqual(this.uploadSizeLimitMB, var2.uploadSizeLimitMB);
   }
}
