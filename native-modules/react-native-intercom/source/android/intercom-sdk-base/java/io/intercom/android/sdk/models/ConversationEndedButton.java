package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\b\u0081\b\u0018\u00002\u00020\u0001:\u0002\u001d\u001eB+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0006HÆ\u0003J\u000b\u0010\u0015\u001a\u0004\u0018\u00010\bHÆ\u0003J3\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\bHÇ\u0001J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001a\u001a\u00020\u001bH×\u0001J\t\u0010\u001c\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u001f",
   d2 = {
         "Lio/intercom/android/sdk/models/ConversationEndedButton;",
         "",
         "message",
         "",
         "ctaTitle",
         "reason",
         "Lio/intercom/android/sdk/models/ConversationEndedButton$Reason;",
         "metadata",
         "Lio/intercom/android/sdk/models/ConversationEndedButton$Metadata;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/models/ConversationEndedButton$Reason;Lio/intercom/android/sdk/models/ConversationEndedButton$Metadata;)V",
         "getMessage",
         "()Ljava/lang/String;",
         "getCtaTitle",
         "getReason",
         "()Lio/intercom/android/sdk/models/ConversationEndedButton$Reason;",
         "getMetadata",
         "()Lio/intercom/android/sdk/models/ConversationEndedButton$Metadata;",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "Metadata",
         "Reason",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ConversationEndedButton {
   @SerializedName("title")
   @NotNull
   private final String message;
   @SerializedName("text")
   @NotNull
   private final String ctaTitle;
   @NotNull
   private final ConversationEndedButton$Reason reason;
   @Nullable
   private final ConversationEndedButton$Metadata metadata;
   public static int $stable;

   static {}

   public ConversationEndedButton(
      @NotNull String var1, @NotNull String var2, @NotNull ConversationEndedButton$Reason var3, @Nullable ConversationEndedButton$Metadata var4
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "message");
      Intrinsics.checkNotNullParameter(var2, "ctaTitle");
      Intrinsics.checkNotNullParameter(var3, "reason");
      this.message = var1;
      this.ctaTitle = var2;
      this.reason = var3;
      this.metadata = var4;
   }

   // $VF: synthetic method
   public ConversationEndedButton(
      String var1, String var2, ConversationEndedButton$Reason var3, ConversationEndedButton$Metadata var4, int var5, DefaultConstructorMarker var6
   ) {
      this(var1, var2, var3, (((var5 & 8) != 0) ? (ConversationEndedButton$Metadata) (null) : var4));
   }

   @NotNull
   public final String getMessage() {
      return this.message;
   }

   @NotNull
   public final String getCtaTitle() {
      return this.ctaTitle;
   }

   @NotNull
   public final ConversationEndedButton$Reason getReason() {
      return this.reason;
   }

   @Nullable
   public final ConversationEndedButton$Metadata getMetadata() {
      return this.metadata;
   }

   @NotNull
   public final String component1() {
      return this.message;
   }

   @NotNull
   public final String component2() {
      return this.ctaTitle;
   }

   @NotNull
   public final ConversationEndedButton$Reason component3() {
      return this.reason;
   }

   @Nullable
   public final ConversationEndedButton$Metadata component4() {
      return this.metadata;
   }

   @NotNull
   public final ConversationEndedButton copy(
      @NotNull String var1, @NotNull String var2, @NotNull ConversationEndedButton$Reason var3, @Nullable ConversationEndedButton$Metadata var4
   ) {
      Intrinsics.checkNotNullParameter(var1, "message");
      Intrinsics.checkNotNullParameter(var2, "ctaTitle");
      Intrinsics.checkNotNullParameter(var3, "reason");
      return new ConversationEndedButton(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static ConversationEndedButton copy$default(
      ConversationEndedButton var0, String var1, String var2, ConversationEndedButton$Reason var3, ConversationEndedButton$Metadata var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.message;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.ctaTitle;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.reason;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.metadata;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "ConversationEndedButton(message="
         + this.message
         + ", ctaTitle="
         + this.ctaTitle
         + ", reason="
         + this.reason
         + ", metadata="
         + this.metadata
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.message.hashCode();
      var1 = var1 * 31 + this.ctaTitle.hashCode();
      var1 = var1 * 31 + this.reason.hashCode();
      return var1 * 31 + (this.metadata == null ? 0 : this.metadata.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ConversationEndedButton)) {
         return false;
      } else {
         ConversationEndedButton var2 = (ConversationEndedButton)var1;
         if (!Intrinsics.areEqual(this.message, var2.message)) {
            return false;
         } else if (!Intrinsics.areEqual(this.ctaTitle, var2.ctaTitle)) {
            return false;
         } else {
            return this.reason != var2.reason ? false : Intrinsics.areEqual(this.metadata, var2.metadata);
         }
      }
   }
}
