package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J\u000b\u0010\u0016\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u000b\u0010\u0017\u001a\u0004\u0018\u00010\bHÆ\u0003J5\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\bHÇ\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001c\u001a\u00020\u001dH×\u0001J\t\u0010\u001e\u001a\u00020\u0005H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR \u0010\u0006\u001a\u0004\u0018\u00010\u00058\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u000e\"\u0004\b\u0010\u0010\u0011R\u0018\u0010\u0007\u001a\u0004\u0018\u00010\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013¨\u0006\u001f",
   d2 = {
         "Lio/intercom/android/sdk/models/Header$Expanded$Footer;",
         "",
         "style",
         "Lio/intercom/android/sdk/models/Header$Expanded$Style;",
         "text",
         "",
         "color",
         "avatarDetails",
         "Lio/intercom/android/sdk/models/AvatarDetails;",
         "<init>",
         "(Lio/intercom/android/sdk/models/Header$Expanded$Style;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/models/AvatarDetails;)V",
         "getStyle",
         "()Lio/intercom/android/sdk/models/Header$Expanded$Style;",
         "getText",
         "()Ljava/lang/String;",
         "getColor",
         "setColor",
         "(Ljava/lang/String;)V",
         "getAvatarDetails",
         "()Lio/intercom/android/sdk/models/AvatarDetails;",
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
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class Header$Expanded$Footer {
   @SerializedName("style")
   @NotNull
   private final Header$Expanded$Style style;
   @SerializedName("text")
   @NotNull
   private final String text;
   @SerializedName("color")
   @Nullable
   private String color;
   @SerializedName("avatar_details")
   @Nullable
   private final AvatarDetails avatarDetails;
   public static final int $stable;

   static { $stable = 8; }

   public Header$Expanded$Footer(@NotNull Header$Expanded$Style var1, @NotNull String var2, @Nullable String var3, @Nullable AvatarDetails var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "style");
      Intrinsics.checkNotNullParameter(var2, "text");
      this.style = var1;
      this.text = var2;
      this.color = var3;
      this.avatarDetails = var4;
   }

   @NotNull
   public final Header$Expanded$Style getStyle() {
      return this.style;
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   @Nullable
   public final String getColor() {
      return this.color;
   }

   public final void setColor(@Nullable String var1) {
      this.color = var1;
   }

   @Nullable
   public final AvatarDetails getAvatarDetails() {
      return this.avatarDetails;
   }

   @NotNull
   public final Header$Expanded$Style component1() {
      return this.style;
   }

   @NotNull
   public final String component2() {
      return this.text;
   }

   @Nullable
   public final String component3() {
      return this.color;
   }

   @Nullable
   public final AvatarDetails component4() {
      return this.avatarDetails;
   }

   @NotNull
   public final Header$Expanded$Footer copy(@NotNull Header$Expanded$Style var1, @NotNull String var2, @Nullable String var3, @Nullable AvatarDetails var4) {
      Intrinsics.checkNotNullParameter(var1, "style");
      Intrinsics.checkNotNullParameter(var2, "text");
      return new Header$Expanded$Footer(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static Header$Expanded$Footer copy$default(
      Header$Expanded$Footer var0, Header$Expanded$Style var1, String var2, String var3, AvatarDetails var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.style;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.text;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.color;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.avatarDetails;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "Footer(style=" + this.style + ", text=" + this.text + ", color=" + this.color + ", avatarDetails=" + this.avatarDetails + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.style.hashCode();
      var1 = var1 * 31 + this.text.hashCode();
      var1 = var1 * 31 + (this.color == null ? 0 : this.color.hashCode());
      return var1 * 31 + (this.avatarDetails == null ? 0 : this.avatarDetails.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Header$Expanded$Footer)) {
         return false;
      } else {
         Header$Expanded$Footer var2 = (Header$Expanded$Footer)var1;
         if (this.style != var2.style) {
            return false;
         } else if (!Intrinsics.areEqual(this.text, var2.text)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.color, var2.color) ? false : Intrinsics.areEqual(this.avatarDetails, var2.avatarDetails);
         }
      }
   }
}
